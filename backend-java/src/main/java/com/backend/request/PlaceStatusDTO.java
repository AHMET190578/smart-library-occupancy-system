package com.backend.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlaceStatusDTO {
    private Long id;
    private String name;
    private String type; // Kütüphane, Kafe vs.
    private Integer totalCapacity;
    private Integer occupiedSeats;
    private Double occupancyRate;
    private Boolean isActive; // place_history verisi güncel mi veya place aktif mi
    private String lastUpdated;
}
