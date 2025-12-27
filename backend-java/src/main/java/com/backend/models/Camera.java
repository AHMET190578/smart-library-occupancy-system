package com.backend.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "cameras")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Camera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Python için kamera kimliği (zorunlu)
    @Column(name = "camera_id", unique = true, nullable = false)
    private String cameraId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    // RTSP veya IP kamera adresi (zorunlu)
    @Column(name = "rtsp_url", nullable = false)
    private String rtspUrl;

    // Kafe / Kütüphane içindeki konum bilgisi
    private String position;

    // Python bu kameradan en son ne zaman veri gönderdi
    @Column(name = "last_update")
    private LocalDateTime lastUpdate;

    // Kamera aktif mi?
    @Column(name = "is_active")
    private Boolean isActive;

    // CRUD takibi için
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        isActive = true;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

