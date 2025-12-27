package com.backend.repository;

import java.util.List;
import com.backend.models.Place;
import com.backend.models.PlaceType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    List<Place> findByType(PlaceType type);
    List<Place> findByNameContainingIgnoreCase(String name);

}
