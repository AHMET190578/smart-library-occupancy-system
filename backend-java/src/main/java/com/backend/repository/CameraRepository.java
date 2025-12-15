package com.backend.repository;

import com.backend.models.Camera;
import com.backend.models.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.ScopedValue;
import java.util.List;
import java.util.Optional;

public interface CameraRepository extends JpaRepository<Camera, Long> {
    List<Camera> findByPlace(Place place);

    List<Camera> findByPlaceId(Long placeId);

    Optional<Camera> findByCameraId(String cameraId);


}
