package com.backend.controller;


import com.backend.models.Place;
import com.backend.request.CreatePlaceRequest;
import com.backend.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/places")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PlaceController {
    private final PlaceService placeService;

    @PostMapping
    public ResponseEntity<Place> createPlace(@RequestBody CreatePlaceRequest req) {
        Place saved = placeService.create((req));
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
