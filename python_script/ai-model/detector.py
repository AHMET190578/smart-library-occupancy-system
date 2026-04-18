"""
Masa Tespiti + İnsan Sayımı - Birleşik Sistem (JSON Kayıtlı)
=============================================================
Kullanım:
    python detector.py

Gereksinimler:
    pip install ultralytics opencv-python flask

Dosya yapısı:
    detector.py
    models/
        table_best.pt
        human_best (1).pt
    logs/
        detections.json   <-- otomatik oluşturulur
"""

import cv2
import json
import threading
import os
from datetime import datetime
from flask import Flask, jsonify
from ultralytics import YOLO

# ─────────────────────────────────────────────
# AYARLAR
# ─────────────────────────────────────────────
TABLE_MODEL_PATH = "models/table_best.pt"
HUMAN_MODEL_PATH = "models/human_best(1).pt"
CAMERA_INDEX     = 0
CONFIDENCE       = 0.5
API_PORT         = 5000
JSON_LOG_PATH    = "logs/detections.json"   # Kayıt dosyası
SAVE_INTERVAL    = 5                        # Kaç saniyede bir kaydet
# ─────────────────────────────────────────────

app = Flask(__name__)

latest_result = {
    "table_count": 0,
    "human_count": 0,
    "tables": [],
    "humans": [],
    "status": "initializing"
}
result_lock = threading.Lock()


def ensure_log_dir():
    """logs/ klasörünü oluştur, yoksa"""
    os.makedirs(os.path.dirname(JSON_LOG_PATH), exist_ok=True)
    if not os.path.exists(JSON_LOG_PATH):
        with open(JSON_LOG_PATH, "w", encoding="utf-8") as f:
            json.dump([], f)
    print(f"[✓] JSON kayıt dosyası: {JSON_LOG_PATH}")


def save_to_json(data: dict):
    """Anlık sonucu timestamp ile JSON dosyasına ekle"""
    entry = {
        "timestamp": datetime.now().strftime("%Y-%m-%d %H:%M:%S"),
        "table_count": data["table_count"],
        "human_count": data["human_count"],
        "tables": data["tables"],
        "humans": data["humans"]
    }
    try:
        with open(JSON_LOG_PATH, "r", encoding="utf-8") as f:
            records = json.load(f)
        records.append(entry)
        with open(JSON_LOG_PATH, "w", encoding="utf-8") as f:
            json.dump(records, f, ensure_ascii=False, indent=2)
    except Exception as e:
        print(f"[!] JSON kayıt hatası: {e}")


def json_saver_loop():
    """Her SAVE_INTERVAL saniyede bir JSON'a kayıt atar"""
    import time
    while True:
        time.sleep(SAVE_INTERVAL)
        with result_lock:
            data = latest_result.copy()
        if data["status"] == "ok":
            save_to_json(data)
            print(f"[✓] Kaydedildi → masa:{data['table_count']} insan:{data['human_count']}")


def load_models():
    print("[*] Modeller yükleniyor...")
    try:
        table_model = YOLO(TABLE_MODEL_PATH)
        human_model = YOLO(HUMAN_MODEL_PATH)
        print("[✓] Modeller başarıyla yüklendi.")
        return table_model, human_model
    except Exception as e:
        print(f"[!] Model yükleme hatası: {e}")
        raise


def parse_detections(results, label: str) -> list:
    detections = []
    for r in results:
        for box in r.boxes:
            conf = float(box.conf[0])
            if conf < CONFIDENCE:
                continue
            x1, y1, x2, y2 = [int(v) for v in box.xyxy[0]]
            detections.append({
                "label": label,
                "confidence": round(conf, 3),
                "bbox": {"x1": x1, "y1": y1, "x2": x2, "y2": y2}
            })
    return detections


def camera_loop(table_model, human_model):
    global latest_result

    cap = cv2.VideoCapture(CAMERA_INDEX)
    if not cap.isOpened():
        print(f"[!] Kamera açılamadı (index={CAMERA_INDEX})")
        with result_lock:
            latest_result["status"] = "camera_error"
        return

    print(f"[✓] Kamera açıldı. API: http://localhost:{API_PORT}/detections")

    while True:
        ret, frame = cap.read()
        if not ret:
            continue

        table_results = table_model(frame, verbose=False)
        human_results = human_model(frame, verbose=False)

        tables = parse_detections(table_results, label="table")
        humans = parse_detections(human_results, label="human")

        with result_lock:
            latest_result = {
                "table_count": len(tables),
                "human_count": len(humans),
                "tables": tables,
                "humans": humans,
                "status": "ok"
            }

    cap.release()


# ─────────────────────────────────────────────
# API Endpoint'leri
# ─────────────────────────────────────────────

@app.route("/detections", methods=["GET"])
def get_detections():
    """Tüm tespitleri döndür (bbox dahil)"""
    with result_lock:
        return jsonify(latest_result)


@app.route("/summary", methods=["GET"])
def get_summary():
    """Sadece sayıları döndür"""
    with result_lock:
        return jsonify({
            "table_count": latest_result["table_count"],
            "human_count": latest_result["human_count"],
            "status": latest_result["status"]
        })


@app.route("/history", methods=["GET"])
def get_history():
    """JSON dosyasındaki tüm geçmiş kayıtları döndür"""
    try:
        with open(JSON_LOG_PATH, "r", encoding="utf-8") as f:
            records = json.load(f)
        return jsonify({"count": len(records), "records": records})
    except Exception as e:
        return jsonify({"error": str(e)}), 500


@app.route("/history/last", methods=["GET"])
def get_last():
    """JSON dosyasındaki son kaydı döndür"""
    try:
        with open(JSON_LOG_PATH, "r", encoding="utf-8") as f:
            records = json.load(f)
        if not records:
            return jsonify({"error": "Henüz kayıt yok"}), 404
        return jsonify(records[-1])
    except Exception as e:
        return jsonify({"error": str(e)}), 500


@app.route("/health", methods=["GET"])
def health():
    with result_lock:
        status = latest_result["status"]
    return jsonify({"status": status}), 200 if status == "ok" else 503


# ─────────────────────────────────────────────
# Başlatma
# ─────────────────────────────────────────────

if __name__ == "__main__":
    ensure_log_dir()

    table_model, human_model = load_models()

    cam_thread = threading.Thread(
        target=camera_loop,
        args=(table_model, human_model),
        daemon=True
    )
    cam_thread.start()

    saver_thread = threading.Thread(
        target=json_saver_loop,
        daemon=True
    )
    saver_thread.start()

    print(f"[*] API başlatılıyor → http://localhost:{API_PORT}")
    app.run(host="0.0.0.0", port=API_PORT, debug=False)