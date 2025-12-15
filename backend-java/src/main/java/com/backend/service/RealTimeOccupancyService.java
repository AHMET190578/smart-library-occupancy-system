package com.backend.service;

import com.backend.models.Place;
import com.backend.models.PlaceHistory;
import com.backend.repository.PlaceHistoryRepository;
import com.backend.repository.PlaceRepository;
import com.backend.request.PlaceStatusDTO;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
@Slf4j
public class RealTimeOccupancyService {

    private final PlaceHistoryRepository placeHistoryRepository;
    private final PlaceRepository placeRepository;
    private final PlaceHistoryService placeHistoryService;

    private final Map<Long, PlaceStatusDTO> occupancyCache = new ConcurrentHashMap<>();

    private final Map<Long, LocalDateTime> lastDbSaveTime = new ConcurrentHashMap<>();

    private final Map<Long, Lock> placeLocks = new ConcurrentHashMap<>();

    private static final int DB_SAVE_INTERVAL_MINUTES = 15;
    private static final int CACHE_CLEANUP_HOURS = 24;

    @PostConstruct
    public void init() {
        log.info("RealTimeOccupancyService başlatılıyor DB Kayıt aralığı: {} dakika", DB_SAVE_INTERVAL_MINUTES);
    }

    public void updateOccupancy(Long placeId, Integer totalCapacity, Integer occupiedSeats) {
        if (placeId == null) {
            log.error("placeId cannot be null");
            return;
        }

        if (totalCapacity == null || totalCapacity <= 0) {
            log.warn("Invalid totalCapacity for placeId {}: {}", placeId, totalCapacity);
            return;
        }

        if (occupiedSeats == null || occupiedSeats < 0) {
            log.warn("Invalid occupiedSeats for placeId {}: {}", placeId, occupiedSeats);
            return;
        }

        if (occupiedSeats > totalCapacity) {
            log.warn("OccupiedSeats ({}) exceeds totalCapacity ({}) for placeId {}. Capping to totalCapacity.",
                    occupiedSeats, totalCapacity, placeId);
            occupiedSeats = totalCapacity;
        }

        Place place = placeRepository.findById(placeId).orElse(null);

        if (place == null) {
            log.warn("Güncelleme başarısız: ID {} olan yer (Place) bulunamadı.", placeId);
            return;

        }

        double occupancyRate = 0.0;
        if (totalCapacity != null && totalCapacity > 0) {
            occupancyRate = ((double) occupiedSeats / totalCapacity) * 100;
        }

        PlaceStatusDTO status = PlaceStatusDTO.builder()
                .id(place.getId())
                .name(place.getName())
                .type(place.getType().toString())
                .totalCapacity(totalCapacity)
                .occupiedSeats(occupiedSeats)
                .occupancyRate(occupancyRate)
                .isActive(true)
                .lastUpdated(LocalDateTime.now().toString())
                .build();

        occupancyCache.put(placeId, status);
        log.debug("Cache updated for placeId: {}", placeId);

        checkAndSaveToDb(placeId, totalCapacity, occupiedSeats);

    }

    public PlaceStatusDTO getLatestStatus(Long placeId) {

        return occupancyCache.get(placeId);
    }

    public Map<Long, PlaceStatusDTO> getAllStatuses() {
        return new ConcurrentHashMap<>(occupancyCache);
    }

    private Lock getLockForPlace(Long placeId) {
        return placeLocks.computeIfAbsent(placeId, id -> new ReentrantLock());
    }

    public void checkAndSaveToDb(Long placeId, Integer totalCapacity, Integer occupiedSeats) {
        Lock lock = getLockForPlace(placeId);
        lock.lock();
        try {
            LocalDateTime lastSave = lastDbSaveTime.get(placeId);

            if (lastSave == null) {
                try {
                    var historyList = placeHistoryService.getLatestHistory(placeId);
                    if (!historyList.isEmpty()) {
                        lastSave = historyList.get(0).getTimestamp();
                        log.info("Initialized lastSave from DB for placeId {}: {}", placeId, lastSave);
                    } else {
                        lastSave = LocalDateTime.MIN;
                    }
                    lastDbSaveTime.put(placeId, lastSave);
                } catch (Exception e) {
                    log.error("Error fetching latest history for placeId {}", placeId, e);
                    lastSave = LocalDateTime.MIN;
                }
            }

            if (lastSave.isBefore(LocalDateTime.now().minusMinutes(DB_SAVE_INTERVAL_MINUTES))) {
                placeHistoryService.savePlaceHistory(placeId, totalCapacity, occupiedSeats);
                lastDbSaveTime.put(placeId, LocalDateTime.now());
                log.info("Persisted history to DB for placeId: {}", placeId);
            }
        } finally {
            lock.unlock();
        }
    }

    @Scheduled(cron = "0 0 2 * * ?")
    public void InactivePlaces() {
        log.info("Starting cache cleanup");
        LocalDateTime cutoff = LocalDateTime.now().minusHours(CACHE_CLEANUP_HOURS);
        int removedCount = 0;

        var iterator = occupancyCache.entrySet().iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            PlaceStatusDTO status = entry.getValue();

            try {
                LocalDateTime lastUpdate = LocalDateTime.parse(status.getLastUpdated());

                if (lastUpdate.isBefore(cutoff)) {
                    Long placeId = entry.getKey();
                    iterator.remove();
                    lastDbSaveTime.remove(placeId);
                    lastDbSaveTime.remove(placeId);
                    placeLocks.remove(placeId);

                    removedCount++;
                    log.info("Removed inactive place from cache: placeId={}, lastUpdate={}",
                            placeId, lastUpdate);
                }
            } catch (Exception e) {
                log.warn("Error parsing lastUpdated for placeId: {}", entry.getKey(), e);
            }
        }
        log.info("Cache cleanup completed. Removed {} inactive places.", removedCount);
    }

    @Scheduled(fixedRate = 300000)
    public void logCacheStats() {
        log.info("Cache statistics - Active places: {}, Last DB saves tracked: {}",
                occupancyCache.size(), lastDbSaveTime.size());
    }

}
