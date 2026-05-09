import os
import uuid
import requests
from dotenv import load_dotenv
import re

# =========================
# ENV SETUP
# =========================
load_dotenv()
os.environ["COQUI_TOS_AGREED"] = "1"

from TTS.api import TTS

# =========================
# UPLIFT CONFIG
# =========================
UPLIFT_API_KEY = os.getenv("UPLIFT_API_KEY")
UPLIFT_URL = os.getenv("UPLIFT_URL")

# =========================
# XTTS LOAD
# =========================
tts = None

def load_model():
    global tts
    if tts is None:
        tts = TTS(model_name="tts_models/multilingual/multi-dataset/xtts_v2")


# =========================
# TEXT NORMALIZATION (CLEAN)
# =========================
def normalize_text(text: str) -> str:
    text = text.strip()
    text = text.replace("&", "and")
    text = re.sub(r'\s+', ' ', text)
    return text


def hybrid_format(text: str) -> str:
    text = re.sub(r'(FAST University)', r'... FAST University ...', text)
    text = re.sub(r'(admission|result|exam|fee)', r'... \\1', text, flags=re.IGNORECASE)
    return text


def improve_text_for_tts(text: str) -> str:
    text = text.strip()
    text = text.replace("dept", "department")
    text = text.replace("uni", "university")
    return " ".join(text.split())


# =========================
# LANGUAGE DETECTION
# =========================
def is_urdu(text: str) -> bool:
    return any('\u0600' <= c <= '\u06FF' for c in text)


def is_pure_urdu(text: str) -> bool:
    return all('\u0600' <= c <= '\u06FF' or c.isspace() for c in text)


def is_roman_urdu(text: str) -> bool:
    t = text.lower()
    urdu_words = ["mujhe", "ap", "ka", "hai", "mein", "kya", "kyun"]
    return any(word in t for word in urdu_words)


# =========================
# VOICE SELECTION (FIXED LOGIC)
# =========================
def choose_voice(text: str) -> str:
    t = text.lower()

    if any(w in t for w in ["exam", "result", "marks", "test"]):
        return "male"

    if any(w in t for w in ["admission", "fee", "apply", "registration"]):
        return "female"

    return "female"


# =========================
# UPLIFT TTS
# =========================
def uplift_tts(text: str):
    print("🔥 UPLIFT AI")

    if not UPLIFT_API_KEY or not UPLIFT_URL:
        print("❌ Missing Uplift credentials")
        return None

    try:
        response = requests.post(
            UPLIFT_URL,
            headers={
                "Authorization": f"Bearer {UPLIFT_API_KEY}",
                "Content-Type": "application/json"
            },
            json={
                "voiceId": "v_8eelc901",
                "text": text,
                "outputFormat": "MP3_22050_128"
            },
            timeout=20
        )

        if response.status_code != 200:
            print("❌ Uplift failed:", response.text)
            return None

        filename = f"audio/uplift_{uuid.uuid4().hex}.mp3"
        with open(filename, "wb") as f:
            f.write(response.content)

        return filename

    except Exception as e:
        print("❌ Uplift error:", e)
        return None


# =========================
# XTTS ENGINE (CLEAN)
# =========================
def xtts_tts(text: str, emotion="neutral"):
    load_model()

    text = improve_text_for_tts(text)

    voice = choose_voice(text)
    speaker = "voices/male.wav" if voice == "male" else "voices/female.wav"

    print(f"🎤 XTTS Voice Selected: {voice}")

    filename = f"audio/xtts_{uuid.uuid4().hex}.wav"

    tts.tts_to_file(
        text=text,
        speaker_wav=speaker,
        language="en",
        file_path=filename,
        temperature=0.62,
        length_penalty=1.05,
        repetition_penalty=2.2,
        speed=0.93
    )

    return filename


# =========================
# ROMAN URDU → URDU
# =========================
def roman_to_urdu(text: str):
    try:
        from openai import OpenAI
        client = OpenAI(api_key=os.getenv("OPENAI_API_KEY"))

        response = client.responses.create(
            model="gpt-4o-mini",
            input=f"Convert into Urdu script: {text}"
        )

        return response.output[0].content[0].text.strip()

    except:
        return text


# =========================
# SMART ROUTER (FINAL CLEAN)
# =========================
def smart_router(text: str, engine="auto"):

    print("\n===== ROUTER =====")
    print("TEXT:", text)

    t = text.lower()

    try:
        # -------------------------
        # MANUAL OVERRIDES
        # -------------------------
        if engine == "xtts":
            return xtts_tts(text)

        if engine == "uplift":
            return uplift_tts(text) or xtts_tts(text)

        # -------------------------
        # ROMAN URDU → UPLIFT
        # -------------------------
        if is_roman_urdu(text):
            urdu = roman_to_urdu(text)
            return uplift_tts(urdu)

        # -------------------------
        # PURE URDU → UPLIFT
        # -------------------------
        if is_pure_urdu(text):
            return uplift_tts(text)

        # -------------------------
        # XTTS ROUTES
        # -------------------------
        if any(w in t for w in ["admission", "fee", "apply", "registration"]):
            return xtts_tts(text)

        if any(w in t for w in ["exam", "result", "marks", "test"]):
            return xtts_tts(text)

        # -------------------------
        # DEFAULT → XTTS
        # -------------------------
        text = normalize_text(text)
        text = hybrid_format(text)

        return xtts_tts(text)

    except Exception as e:
        print("❌ Router error:", e)
        return xtts_tts(text)