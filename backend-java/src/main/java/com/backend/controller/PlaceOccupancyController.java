package com.backend.controller;

import com.backend.models.PlaceHistory;
import com.backend.request.HistoryRequestDTO;
import com.backend.request.PlaceStatusDTO;
import com.backend.service.CameraService;
import com.backend.service.PlaceHistoryService;
import com.backend.service.RealTimeOccupancyService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@Slf4j
@RequestMapping("/api/occupancy")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PlaceOccupancyController {
    private final PlaceHistoryService placeHistoryService;
    private final CameraService cameraService;
    private final RealTimeOccupancyService realTimeOccupancyService;

    @PostMapping("/update")
    public ResponseEntity<String> updateOccupancy(@RequestBody HistoryRequestDTO request) {
        try {
            if (request.getPlaceId() == null) {
                log.warn("Update request received with null placeId");
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "PlaceId is required",
                        "timestamp", System.currentTimeMillis()).toString());

            }
            if (request.getCameraId() != null && !request.getCameraId().isEmpty()) {
                try {
                    cameraService.updateCameraLastSeen(request.getCameraId());
                    log.debug("camera last update: ", request.getCameraId());
                } catch (EntityNotFoundException e) {
                    log.warn("Camera not found: ", request.getCameraId());
                } catch (Exception e) {
                    log.warn("unexpected error ", request.getCameraId());

                }

            }
            realTimeOccupancyService.updateOccupancy(
                    request.getPlaceId(),
                    request.getTotalCapacity(),
                    request.getOccupiedSeats());
            log.info("occupancy update successfully", request.getPlaceId());

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Update received",
                    "placeId", request.getPlaceId(),
                    "timestamp", System.currentTimeMillis()).toString());

        } catch (Exception e) {
            log.error("Error proccessing occupancy update for placeId", request.getPlaceId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "Update failed: " + e.getMessage(),
                    "timestamp", System.currentTimeMillis()).toString());
        }
    }

    @GetMapping("/{placeId}")
    public ResponseEntity<PlaceStatusDTO> getPlaceStatus(@PathVariable Long placeId) {
        PlaceStatusDTO status = realTimeOccupancyService.getLatestStatus(placeId);
        if (status == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(status);
    }

    @GetMapping
    public ResponseEntity<Map<Long, PlaceStatusDTO>> getAllPlaces() {
        return ResponseEntity.ok(realTimeOccupancyService.getAllStatuses());
    }


}
