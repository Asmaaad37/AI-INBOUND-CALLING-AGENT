import os
import requests
from dotenv import load_dotenv

load_dotenv()

UPLIFT_API_KEY = os.getenv("UPLIFT_API_KEY")
UPLIFT_URL = os.getenv("UPLIFT_URL")

def uplift_tts(text: str):
    headers = {
        "Authorization": f"Bearer {UPLIFT_API_KEY}",
        "Content-Type": "application/json"
    }

    payload = {
    "voiceId": "v_8eelc901",
    "text": text,
    "outputFormat": "MP3_22050_128"
}

    try:
        response = requests.post(UPLIFT_URL, json=payload, headers=headers, timeout=20)

        if response.status_code != 200:
            print("UpliftAI failed:", response.text)
            return None

        data = response.json()

        # CASE 1: URL
        if "audio_url" in data:
            return data["audio_url"]

        # CASE 2: base64/audio field
        if "audio" in data:
            return data["audio"]

        return None

    except Exception as e:
        print("UpliftAI error:", str(e))
        return None