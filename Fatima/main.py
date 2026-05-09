from fastapi import FastAPI
from pydantic import BaseModel
import re
import tts_engine

app = FastAPI()

# ---------------- LOAD MODEL ----------------
tts_engine.load_model()

# ---------------- CLEAN TEXT ----------------
def clean_text(text):
    text = text.strip()
    text = re.sub(r'([?.!,])', r'\1 ', text)
    text = re.sub(r'\s+', ' ', text)
    return text

# ---------------- REQUEST ----------------
class TTSRequest(BaseModel):
    text: str
    voice: str = "auto"
    engine: str = "auto"  # auto | openai | xtts | uplift

# ---------------- API ----------------
@app.post("/tts")
def generate_tts(req: TTSRequest):

    cleaned_text = clean_text(req.text)

    # ---------------- HYBRID CALL ----------------
    audio_file = tts_engine.smart_router(
        text=cleaned_text,
        engine=req.engine
    )

    # ---------------- RESPONSE ----------------
    return {
        "status": "success",
        "audio": audio_file,
        "text": cleaned_text,
        "engine_requested": req.engine,
        "note": "UpliftAI used automatically inside smart_router if Urdu detected"
    }