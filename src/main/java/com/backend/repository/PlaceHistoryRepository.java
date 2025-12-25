package com.backend.repository;

import com.backend.models.Place;
import com.backend.models.PlaceHistory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface PlaceHistoryRepository extends JpaRepository<PlaceHistory, Long> {
    List<PlaceHistory> findByPlace(Place place);

    List<PlaceHistory> findByPlaceIdAndTimestampBetween(Long placeId, LocalDateTime start, LocalDateTime end);

    List<PlaceHistory> findTop50ByPlaceIdOrderByTimestampDesc(Long placeId);

    @Query("SELECT ph FROM PlaceHistory ph " +
            "WHERE ph.place = :place " +
            "AND ph.dayOfWeek = :dayOfWeek " +
            "AND ph.hourOfDay BETWEEN :startHour AND :endHour " +
            "AND ph.timestamp >= :since " +
            "ORDER BY ph.timestamp DESC")
    List<PlaceHistory> findHistoricalDataForPrediction(
            @Param("place") Place place,
            @Param("dayOfWeek") DayOfWeek dayOfWeek,
            @Param("startHour") Integer startHour,
            @Param("endHour") Integer endHour,
            @Param("since") LocalDateTime since
    );

    void deleteByTimestampBefore(LocalDateTime date);

    // Son kayıt
    List<PlaceHistory> findFirstByPlaceOrderByTimestampDesc(Place place);

}

