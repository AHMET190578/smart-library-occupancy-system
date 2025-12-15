package com.backend.request;

import lombok.Data;

@Data
public class HistoryRequestDTO {
    private Long placeId;
    private Integer totalCapacity;
    private Integer occupiedSeats;
    private String cameraId;

}
