from TTS.api import TTS
import os

tts = TTS(model_name="tts_models/multilingual/multi-dataset/xtts_v2")

# choose voice
voice_file = r"voices\female.wav"   # or r"voices\male.wav"

# safety check
if not os.path.exists(voice_file):
    raise FileNotFoundError(f"Not found: {voice_file}")

text = "Hello, this is a test of the hybrid TTS system."

tts.tts_to_file(
    text=text,
    file_path="output.wav",
    speaker_wav=voice_file,
    language="en"
)

print("XTTS working ✔ Audio generated: output.wav")