    package com.backend.models;

    import jakarta.persistence.*;
    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;
    import org.springframework.cglib.core.Local;

    import java.time.LocalDateTime;
    import java.time.DayOfWeek;

    @Entity
    @Table(name = "place_history", indexes = {
            @Index(name = "idx_place_timestamp", columnList = "place_id, timestamp")
    })
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class PlaceHistory {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "place_id", nullable = false)
        private Place place;

        @Column(name = "timestamp", nullable = false)
        private LocalDateTime timestamp;

        // python data

        @Column(name = "total_capacity", nullable = false)
        private Integer totalCapacity;

        @Column(name = "occupied_seats", nullable = false)
        private Integer occupiedSeats;

        @Column(name = "occupancy_rate")
        private Double occupancyRate;


        @Column(name = "day_of_week")
        private DayOfWeek dayOfWeek;

        @Column(name = "hour_of_day")
        private Integer hourOfDay;

        @Column(name = "is_weekend")
        private Boolean isWeekend;



        @PrePersist
        protected void onCreate() {

            if (this.timestamp == null) {
                this.timestamp = LocalDateTime.now();
            }

            this.dayOfWeek = timestamp.getDayOfWeek();
            this.hourOfDay = timestamp.getHour();
            this.isWeekend = (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY);

            if (totalCapacity != null && occupiedSeats != null && totalCapacity > 0) {
                this.occupancyRate = ((double) occupiedSeats / totalCapacity) * 100;
            }
        }



    }
