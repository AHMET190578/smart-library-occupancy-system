package com.backend.service;

import com.backend.models.Place;
import com.backend.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class PlaceService {
    private final PlaceRepository placeRepository;

    public List<Place> getAllPlace() {
        return placeRepository.findAll();
    }

    public List<Place> searchPlacesByName(String name) {
        return placeRepository.findByNameContainingIgnoreCase(name);
    }

    public Place getPlaceById(Long id) {
        return placeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Place not found: " + id));
    }

}
