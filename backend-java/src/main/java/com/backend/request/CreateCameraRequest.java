package com.backend.request;

import lombok.Data;

@Data
public class CreateCameraRequest {
    private String cameraId;
    private Long placeId;
    private String rtspUrl;
    private String position;
}
