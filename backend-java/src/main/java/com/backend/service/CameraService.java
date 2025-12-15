package com.backend.service;

import com.backend.models.Camera;
import com.backend.models.Place;
import com.backend.repository.CameraRepository;
import com.backend.repository.PlaceHistoryRepository;
import com.backend.repository.PlaceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CameraService {
    public final CameraRepository cameraRepository;
    public final PlaceRepository placeRepository;

    public List<Camera> getActiveCameras(){
        return cameraRepository.findAll();
    }

    public List<Camera> getCamerasByPlace(Long placeId){
        return cameraRepository.findByPlaceId(placeId);
    }

    public Camera getCameraById(Long cameraId) {
        return cameraRepository.findById(cameraId)
                .orElseThrow(() -> new EntityNotFoundException("Camera not found with id: " + cameraId));
    }

    public void updateCameraLastSeen(String cameraId) {
        Camera camera = cameraRepository.findByCameraId(cameraId)
                .orElseThrow(() -> new EntityNotFoundException("Camera not found: " + cameraId));

        camera.setLastUpdate(LocalDateTime.now());
        cameraRepository.save(camera);
    }
}
