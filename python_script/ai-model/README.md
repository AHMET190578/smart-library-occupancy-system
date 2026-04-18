<<<<<<< HEAD
# Masa Tespiti + İnsan Sayımı API

Kameradan gerçek zamanlı masa ve insan tespiti yapan sistem.

---

## Kurulum

### 1. Repoyu klonla
```bash
git clone https://github.com/KULLANICI_ADI/REPO_ADI.git
cd REPO_ADI
```

### 2. Kütüphaneleri kur
```bash
pip install -r requirements.txt
```

### 3. Model dosyalarını ekle
Model dosyaları GitHub'da **bulunmaz**, ayrıca paylaşılır.  
İndirdiğin `.pt` dosyalarını `models/` klasörüne koy:

```
models/
├── table_best.pt
└── human_best (1).pt
```

### 4. Çalıştır
```bash
python detector.py
```

---

## API Kullanımı

| Endpoint | Açıklama |
|---|---|
| `GET /summary` | Anlık masa ve insan sayısı |
| `GET /detections` | Tüm tespitler + koordinatlar |
| `GET /history` | Tüm geçmiş kayıtlar |
| `GET /history/last` | Son kayıt |
| `GET /health` | Sistem durumu |

### Örnek istek (Python)
```python
import requests

response = requests.get("http://localhost:5000/summary")
data = response.json()

print(data["table_count"])  # Masa sayısı
print(data["human_count"])  # İnsan sayısı
```

### Örnek yanıt
```json
{
  "table_count": 3,
  "human_count": 5,
  "status": "ok"
}
```

---

## Notlar
- Kamera kaynağını değiştirmek için `detector.py` içindeki `CAMERA_INDEX` değerini güncelle
- Kayıt sıklığını değiştirmek için `SAVE_INTERVAL` değerini güncelle (saniye)
- Model dosyaları Google Drive üzerinden paylaşılmaktadır
=======
# Smart Library Occupancy System

A team project for detecting real-time seat occupancy in libraries and cafes.

## Tech Stack
- Backend: Java (Spring Boot)
- Image Processing: Python (YOLOv8, OpenCV)
- Frontend: Flutter

## Repository Structure
- backend-java/            → Java Spring Boot backend
- image-processing-python/ → Python YOLOv8 service
- frontend-flutter/        → Flutter mobile app
- docs/                    → API contracts & architecture docs

## Team Workflow
- Monorepo structure
- Feature-based branches
- Pull Request based development
>>>>>>> 6daca6d7b8b6583529f4d7d9f4ce9dccc6ee519f
