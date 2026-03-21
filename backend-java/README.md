# Backend - Java Spring Boot

Java Spring Boot ile geliştirilmiş kütüphane/yer doluluk takip sistemi.

##  Proje Hakkında

Bu proje, gerçek zamanlı doluluk verilerini kamera ve yer konumları üzerinden takip etmek için REST API sağlar.

- Klasör: `backend-java`
- Çalışma: `Spring Boot` + `Spring Data JPA` + `H2`/`PostgreSQL`/`MySQL` desteği
- Temel model: `Place`, `Camera`, `PlaceHistory`, `PlaceType`

##  Proje Yapısı

- `src/main/java/com/backend/controller`
  - `CameraController`, `PlaceController`, `PlaceOccupancyController`
- `src/main/java/com/backend/models`
  - `Camera`, `Place`, `PlaceHistory`, `PlaceType`
- `src/main/java/com/backend/repository`
  - `CameraRepository`, `PlaceRepository`, `PlaceHistoryRepository`
- `src/main/java/com/backend/service`
  - `CameraService`, `PlaceService`, `PlaceHistoryService`, `RealTimeOccupancyService`
- `src/main/java/com/backend/request`
  - `CreateCameraRequest`, `CreatePlaceRequest`, `HistoryRequestDTO`, `PlaceStatusDTO`

##  Kurulum ve Çalıştırma

1. Repoyu klonlayın ve backend klasörüne girin:
   ```bash
   cd smart-library-occupancy-system/backend-java
   ```
2. Yapıyı derleyin:
   ```bash
   ./mvnw clean install
   ```
3. Uygulamayı başlatın:
   ```bash
   ./mvnw spring-boot:run
   ```

##  Test

- Testleri çalıştırmak için:
  ```bash
  ./mvnw test
  ```
- Test sınıfı: `src/test/java/com/backend/BackendApplicationTests.java`

##  Konfigürasyon

`src/main/resources/application.yml` ve `src/main/resources/application-test.yml` dosyalarında veri tabanı ve server ayarları bulunur.

Örnek H2 konfigürasyonu:
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

##  API Örnekleri

- `POST /api/places` - Yer oluşturma
- `GET /api/places` - Tüm yerleri listeleme
- `PUT /api/places/{id}/status` - Yer durumu (doluluk) güncelleme
- `POST /api/cameras` - Kamera ekleme
- `GET /api/occupancy/history` - Doluluk geçmişi

> Not: Endpoint isimleri controller içindeki `@RequestMapping` ayarınıza göre değişebilir.



##  Hata ayıklama

- Port çakışması: `application.yml` içindeki `server.port` değerini kontrol edin.
- Veritabanı bağlantısı: `spring.datasource` ayarları doğrulanmalı.
- `mvnw` çalışmıyorsa `mvn` kullanın.

---

Bu README, `backend-java` klasöründe çalışacak şekilde hazırlandı. Değişiklik isterseniz başka bir yapı önerisinde de yardımcı olabilirim.
