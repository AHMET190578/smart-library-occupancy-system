package com.backend.controller;

import com.backend.models.Camera;
import com.backend.request.CreateCameraRequest;
import com.backend.service.CameraService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cameras")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CameraController {

    private final CameraService cameraService;

    @PostMapping
    public ResponseEntity<Camera> createCamera(@RequestBody CreateCameraRequest req) {
        Camera saved = cameraService.create(req);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }


}
