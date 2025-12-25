import requests
import random
import sys


BASE_URL = "http://localhost:8080"
OCCUPANCY_URL = f"{BASE_URL}/api/occupancy"
PLACES_URL = f"{BASE_URL}/api/places"
CAMERAS_URL = f"{BASE_URL}/api/cameras"
TOTAL_CAPACITY = 200




def create_place(name, type_):
    payload = {
        "name": name,
        "type": type_
    }
    resp = requests.post(PLACES_URL, json=payload)
    if resp.status_code != 201:
        print(f"Place creation failed: {resp.status_code} {resp.text}")
        sys.exit(1)
    place_id = resp.json()["id"]
    print(f"Place created: {name} (ID: {place_id})")
    return place_id


def create_camera(camera_id, place_id, rtsp_url="rtsp://test", position="Entrance"):
    payload = {
        "cameraId": camera_id,
        "placeId": place_id,
        "rtspUrl": rtsp_url,
        "position": position
    }
    resp = requests.post(CAMERAS_URL, json=payload)
    if resp.status_code != 201:
        print(f"Camera creation failed: {resp.status_code} {resp.text}")
        sys.exit(1)
    print(f"Camera created: {camera_id}")
    return camera_id


def run_occupancy_test(place_id, camera_id):
    occupied_seats = random.randint(0, TOTAL_CAPACITY)
    payload = {
        "placeId": place_id,
        "totalCapacity": TOTAL_CAPACITY,
        "occupiedSeats": occupied_seats,
        "cameraId": camera_id
    }

    print("➡ Sending occupancy update...")
    post_resp = requests.post(f"{OCCUPANCY_URL}/update", json=payload)
    if post_resp.status_code != 200:
        print(f"POST failed: {post_resp.status_code} {post_resp.text}")
        sys.exit(1)
    print(" POST successful")

    print("➡ Fetching occupancy status...")
    get_resp = requests.get(f"{OCCUPANCY_URL}/{place_id}")
    if get_resp.status_code != 200:
        print(f"GET failed: {get_resp.status_code} {get_resp.text}")
        sys.exit(1)

    data = get_resp.json()
    assert data["occupiedSeats"] == occupied_seats, "Occupied seats mismatch"
    assert data["totalCapacity"] == TOTAL_CAPACITY, "Capacity mismatch"
    print("Occupancy GET validation successful")


if __name__ == "__main__":

    place_id = create_place("Library", "LIBRARY")

    camera_id = create_camera("CAM_01", place_id)

    run_occupancy_test(place_id, camera_id)

    print("\n🎉 All tests passed successfully!")
