# AI-INBOUND-CALLING-AGENT

AI Inbound Calling Agent delivers an automated, voice-driven solution that engages users in natural Urdu dialogue, interprets intent, and generates contextually appropriate responses. This repository contains code, documentation, and reports for the project.

**Project**

- **Overview**: Automated inbound calling system using speech-to-text, NLU, response generation, and text-to-speech modules.
- **Primary language**: Python (recommended Python 3.10+).

**Getting Started**

- **Prerequisites**: `git`, `python` (3.10+), PowerShell (Windows), optionally `conda`.
- **Fork the repo**: open the repository page on GitHub and click **Fork** to create a copy under your account.
- **Clone your fork (PowerShell)**:

```powershell
# replace <your-username> with your GitHub username
git clone https://github.com/<your-username>/AI-INBOUND-CALLING-AGENT.git
cd AI-INBOUND-CALLING-AGENT
git remote add upstream https://github.com/Asmaaad37/AI-INBOUND-CALLING-AGENT.git
git fetch upstream
git checkout -b my-side-branch
```

**Virtual environment & install**

- **Create & activate (PowerShell)**:

```powershell
python -m venv .venv
# If PowerShell blocks activation, run: Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
.\.venv\Scripts\Activate.ps1
```

- **Install dependencies** (after updating `Documentation/Requirements.txt`):

```powershell
pip install -r Documentation/Requirements.txt
```

**Project Structure**

- **`Asmaad/`**: main source code. Recommended subfolders:
  - `Asmaad/voice/`: speech modules (STT, TTS, audio utils).
  - `Asmaad/nlp/`: NLU, intent classification, dialogue manager.
  - `Asmaad/telephony/`: telephony integrations (Twilio, SIP adapters).
  - `Asmaad/api/`: REST/WebSocket server and API endpoints.
  - `Asmaad/tests/`: unit and integration tests.
- **`Documentation/`**: project docs and `Requirements.txt` (pip requirements).
- **`weeklyReport/`**: project progress reports and deliverables.
- **Other files**: `LICENSE`, high-level `README.md`, and supporting files.

**Module-to-Folder Mapping (recommended)**

- **Inbound Call Handling**: `Asmaad/telephony/`
- **Audio I/O & Recording**: `Asmaad/voice/io/`
- **Speech-to-Text (STT)**: `Asmaad/voice/stt/` (wrappers for cloud/local STT)
- **Text Processing / NLU**: `Asmaad/nlp/` (tokenization, intent models)
- **Response Generation / LLM**: `Asmaad/nlp/generation/`
- **Text-to-Speech (TTS)**: `Asmaad/voice/tts/`
- **API Server / Integration**: `Asmaad/api/`

**Dependencies (recommended)**
Note: `Documentation/Requirements.txt` in this repo is currently empty — add the packages below to that file, or use the example block.

- **Core runtime**: `python` 3.10+
- **Suggested pip packages** (example `Documentation/Requirements.txt` content):

```
# Web/API
fastapi
uvicorn[standard]

# Telephony
twilio

# AI / LLM
openai
transformers
torch

# Speech / audio
speechrecognition
sounddevice
soundfile
librosa
pyaudio   # Note: Windows users often need a wheel or to install portaudio first
pyttsx3

# Utilities
numpy
pandas
scikit-learn
python-dotenv

# Dev/test
pytest
black

```

- **Torch installation note**: For GPU support, install the appropriate `torch` build per your CUDA version. See https://pytorch.org for platform-specific commands.

**How to run (example)**

- Start the API server (adjust import path to your app's entrypoint):

```powershell
uvicorn Asmaad.api.main:app --reload --port 8000
```

- Run tests:

```powershell
pytest
```

**Contributing**

- **Branches**: create feature branches from `my-side-branch` or `main` depending on your workflow.
- **Pull Requests**: open PRs from your fork/feature branch to `Asmaaad37/AI-INBOUND-CALLING-AGENT` with a clear description of changes.

**Where to add new code**

- Add module code under `Asmaad/` following the recommended subfolders. Keep related code, tests, and small README snippets near each module to ease onboarding.

**Next steps for repository maintainers**

- Populate `Documentation/Requirements.txt` with the exact pinned package versions used in development.
- Add small `README.md` files inside `Asmaad/` subfolders describing module responsibilities and entrypoints.

**License**

- See the top-level `LICENSE` file for licensing details.
