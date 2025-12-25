package com.backend.service;

import com.backend.models.Camera;
import com.backend.models.Place;
import com.backend.repository.CameraRepository;
import com.backend.repository.PlaceRepository;
import com.backend.request.CreateCameraRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    public Camera create(CreateCameraRequest req){
        Place place = placeRepository.findById(req.getPlaceId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Place not found: " + req.getPlaceId())
                );

        Camera camera = new Camera();
        camera.setCameraId(req.getCameraId());
        camera.setPlace(place);
        camera.setRtspUrl(req.getRtspUrl());
        camera.setPosition(req.getPosition());

        return cameraRepository.save(camera);


    }
}
