package com.backend.service;

import com.backend.models.Place;
import com.backend.models.PlaceHistory;
import com.backend.repository.PlaceHistoryRepository;
import com.backend.repository.PlaceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaceHistoryService {

    private final PlaceHistoryRepository placeHistoryRepository;

    private final PlaceRepository placeRepository;

    // Python'dan gelen veriyi kaydet
    @Transactional
    public  PlaceHistory savePlaceHistory(Long placeId, Integer totalCapacity, Integer occupiedSeats) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new EntityNotFoundException("place not found with id:" + placeId));

        PlaceHistory history = new PlaceHistory();
        history.setPlace(place);
        history.setTotalCapacity(totalCapacity);
        history.setOccupiedSeats(occupiedSeats);
        history.setTimestamp(LocalDateTime.now());

        return placeHistoryRepository.save(history);
    }

    // Belirli bir place için tüm geçmiş
    public List<PlaceHistory> getHistoryByPlace(Long placeId) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new EntityNotFoundException("place not found with id:" + placeId));
        return placeHistoryRepository.findByPlace(place);
    }
    // Belirli tarih aralığında geçmiş
    public List<PlaceHistory> getHistoryByDateRange(Long placeId, LocalDateTime start, LocalDateTime end) {
        return placeHistoryRepository.findByPlaceIdAndTimestampBetween(placeId, start, end);
    }

    // Prediction için geçmiş veri
    public List<PlaceHistory> getHistoricalDataForPrediction(
            Long placeId,
            DayOfWeek dayOfWeek,
            Integer startHour,
            Integer endHour,
            int daysBack
    ){
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new EntityNotFoundException("Place not found by id" + placeId));
        LocalDateTime since = LocalDateTime.now().minusDays(daysBack);

        return placeHistoryRepository.findHistoricalDataForPrediction(
                place, dayOfWeek, startHour, endHour, since
        );
    }

    // Son 50 kayıt
    public List<PlaceHistory> getRecentHistory(Long placeId){
        return placeHistoryRepository.findTop50ByPlaceIdOrderByTimestampDesc(placeId);
    }

    // Son durum
    public List<PlaceHistory> getLatestHistory(Long placeId){
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new EntityNotFoundException("Place not found by id" + placeId));
        return placeHistoryRepository.findFirstByPlaceOrderByTimestampDesc(place);

    }

    public PlaceHistory saveHistory(PlaceHistory history){
        return placeHistoryRepository.save(history);
    }

    @Transactional
    public void cleanOldHistory() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
        placeHistoryRepository.deleteByTimestampBefore(cutoffDate);
    }







}
