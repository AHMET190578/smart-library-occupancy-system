package com.backend.request;

import com.backend.models.PlaceType;
import lombok.Data;

@Data
public class CreatePlaceRequest {
    private String name;
    private PlaceType type;

}
