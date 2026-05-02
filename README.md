# AI Inbound Calling Agent

> An intelligent voice-driven system for automated inbound call management with natural language understanding in Urdu

![Python Version](https://img.shields.io/badge/python-3.10+-blue.svg)
![License](https://img.shields.io/badge/license-Apache%202.0-green.svg)
![Status](https://img.shields.io/badge/status-In%20Development-orange.svg)

## Table of Contents

- [Abstract](#abstract)
- [Project Objectives](#project-objectives)
- [System Architecture](#system-architecture)
- [Key Features](#key-features)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Installation & Setup](#installation--setup)
- [Usage](#usage)
- [Module Documentation](#module-documentation)
- [System Flow](#system-flow)
- [Experimental Results](#experimental-results)
- [Contributing Guidelines](#contributing-guidelines)
- [Future Enhancements](#future-enhancements)
- [License](#license)
- [References](#references)

## Abstract

The **AI Inbound Calling Agent** is an automated telecommunication system designed to handle inbound calls through an intelligent conversational interface. The system combines state-of-the-art speech processing, natural language understanding, and knowledge retrieval technologies to facilitate seamless human-agent interaction in Urdu language. Leveraging Graph Retrieval Augmented Generation (GraphRAG) powered by Neo4j and GPT-4o-mini, the system can comprehend caller intent, retrieve contextually relevant information from academic and institutional knowledge graphs, and generate appropriate, contextually-aware responses. This project demonstrates the practical application of advanced NLP techniques in a real-world telecommunications domain.

## Project Objectives

1. **Develop a multilingual voice interface** capable of processing and responding to inbound calls in Urdu with natural conversational flow
2. **Implement intelligent intent recognition** using deep learning models to classify caller intentions and route appropriately
3. **Create a knowledge graph system** integrating academic, departmental, and institutional data for context-aware response generation
4. **Integrate GraphRAG technology** for enhanced information retrieval from structured and unstructured sources
5. **Build a scalable API architecture** supporting concurrent call handling and real-time processing
6. **Ensure system reliability and performance** with comprehensive error handling and logging mechanisms

## System Architecture

The system follows a modular microservices-based architecture:

```
┌─────────────────────────────────────────────────────────────┐
│                    INBOUND CALL REQUEST                      │
└────────────────┬────────────────────────────────────────────┘
                 │
    ┌────────────▼────────────┐
    │   Telephony Interface   │ (Twilio/SIP)
    └────────────┬────────────┘
                 │
    ┌────────────▼──────────────┐
    │  Speech-to-Text (STT)     │ (Audio Processing)
    └────────────┬──────────────┘
                 │
    ┌────────────▼──────────────────┐
    │  Natural Language Understanding │ (Intent Classification)
    └────────────┬──────────────────┘
                 │
    ┌────────────▼───────────────┐
    │  Knowledge Graph Retrieval  │ (Neo4j + GraphRAG)
    └────────────┬───────────────┘
                 │
    ┌────────────▼──────────────────┐
    │  Response Generation (LLM)    │ (GPT-4o-mini)
    └────────────┬──────────────────┘
                 │
    ┌────────────▼────────────────┐
    │   Text-to-Speech (TTS)      │ (Urdu Audio Synthesis)
    └────────────┬────────────────┘
                 │
    ┌────────────▼─────────────────┐
    │    Audio Output to Caller    │
    └─────────────────────────────┘
```

## Key Features

- **Multilingual Support**: Native support for Urdu language processing with extensibility for additional languages
- **GraphRAG Integration**: Leverages graph-based knowledge retrieval combined with LLM for accurate, context-aware responses
- **Real-time Processing**: Low-latency call handling with concurrent request support
- **Knowledge Management**: Automated curriculum, department, and academic data extraction and graph indexing
- **Web Data Ingestion**: Intelligent web scraping with PDF extraction for knowledge base population
- **RESTful API**: FastAPI-based interface for integration with existing telephony systems
- **Comprehensive Logging**: Detailed audit trails for debugging and performance analysis

## Technology Stack

| Component             | Technology                           | Purpose                                    |
| --------------------- | ------------------------------------ | ------------------------------------------ |
| **Runtime**           | Python 3.10+                         | Core language and execution environment    |
| **API Framework**     | FastAPI, Uvicorn                     | RESTful API and ASGI server                |
| **Telephony**         | Twilio SDK                           | Call handling and PSTN integration         |
| **Speech Processing** | SpeechRecognition, pyttsx3           | STT and TTS modules                        |
| **NLP/LLM**           | OpenAI (GPT-4o-mini)                 | Intent recognition and response generation |
| **Knowledge Graph**   | Neo4j                                | Structured data storage and querying       |
| **GraphRAG**          | neo4j-graphrag                       | Graph-based retrieval augmented generation |
| **Data Processing**   | Pandas, NumPy                        | Data manipulation and analysis             |
| **Web Scraping**      | BeautifulSoup4, Requests, pdfplumber | Content extraction and indexing            |
| **Testing**           | Pytest                               | Unit and integration testing               |
| **Code Quality**      | Black                                | Code formatting and style consistency      |

## Project Structure

```
AI-INBOUND-CALLING-AGENT/
│
├── Asmaad/                          # Main source code directory
│   ├── voice/                       # Speech processing modules
│   │   ├── stt/                     # Speech-to-Text implementations
│   │   ├── tts/                     # Text-to-Speech implementations
│   │   └── io/                      # Audio I/O utilities
│   │
│   ├── nlp/                         # Natural Language Processing
│   │   ├── intent/                  # Intent classification models
│   │   ├── generation/              # Response generation modules
│   │   └── dialogue/                # Dialogue management
│   │
│   ├── telephony/                   # Call handling and integration
│   │   ├── twilio/                  # Twilio-specific adapters
│   │   └── handlers/                # Call event handlers
│   │
│   ├── knowledge_graph/             # Graph database operations
│   │   ├── entities/                # Entity definitions
│   │   ├── queries/                 # Neo4j query builders
│   │   └── indexing/                # Data indexing utilities
│   │
│   ├── api/                         # API endpoints and server
│   │   ├── main.py                  # FastAPI application entrypoint
│   │   ├── routes/                  # API route definitions
│   │   └── schemas/                 # Pydantic models
│   │
│   └── tests/                       # Unit and integration tests
│
├── code/                            # Utility scripts and notebooks
│   ├── WebScraper.py                # Website content extraction
│   ├── GraphRag.py                  # GraphRAG configuration
│   ├── create_curriculum.py         # Curriculum data initialization
│   ├── update_curriculum.py         # Curriculum data updates
│   ├── GraphRag.ipynb               # GraphRAG experimentation
│   └── prospectus.ipynb             # Data analysis notebooks
│
├── Asmaad/                          # Documentation and data
│   ├── Documents/                   # Academic data files (CSV)
│   │   ├── Curriculum.csv
│   │   ├── Department.csv
│   │   ├── Faculty.csv
│   │   ├── Programs.csv
│   │   └── ...
│   │
│   ├── Docs_extraction/             # Extracted document content
│   └── Extra_Docs/                  # Additional documentation
│
├── Documentation/                   # Project documentation
│   ├── Requirements.txt             # Python dependencies
│   └── README.md files              # Module-specific documentation
│
├── weeklyReport/                    # Project progress reports
│
├── Diagram.pdf                      # System architecture diagram
├── LICENSE                          # Apache 2.0 License
└── README.md                        # This file
```

## Prerequisites

- **System Requirements**:
  - Windows/Linux/macOS operating system
  - 8 GB RAM (minimum), 16 GB recommended
  - Python 3.10 or higher

- **External Services**:
  - Neo4j Database (v4.4+) - local or cloud instance
  - OpenAI API key (for GPT-4o-mini access)
  - Twilio account (for telephony integration)

- **Software**:
  - `git` version control system
  - `pip` package manager (included with Python)
  - PowerShell (Windows) or Bash (Linux/macOS)
  - Virtual environment management tool (venv or conda)

## Installation & Setup

### 1. Clone Repository

```bash
# Clone the repository
git clone https://github.com/Asmaaad37/AI-INBOUND-CALLING-AGENT.git
cd AI-INBOUND-CALLING-AGENT

# Add upstream remote for syncing
git remote add upstream https://github.com/Asmaaad37/AI-INBOUND-CALLING-AGENT.git
git fetch upstream
```

### 2. Create Virtual Environment

**On Windows (PowerShell):**

```powershell
python -m venv .venv

# If execution policy blocks activation, run:
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass

# Activate virtual environment
.\.venv\Scripts\Activate.ps1
```

**On Linux/macOS (Bash):**

```bash
python3 -m venv .venv
source .venv/bin/activate
```

### 3. Install Dependencies

```bash
# Upgrade pip to latest version
pip install --upgrade pip

# Install project dependencies
pip install -r Documentation/Requirements.txt
```

### 4. Configure Environment Variables

Create a `.env` file in the project root:

```bash
# Neo4j Configuration
NEO4J_URI=bolt://localhost:7687
NEO4J_USERNAME=neo4j
NEO4J_PASSWORD=your_password

# OpenAI Configuration
OPENAI_API_KEY=your_api_key

# Twilio Configuration (optional)
TWILIO_ACCOUNT_SID=your_account_sid
TWILIO_AUTH_TOKEN=your_auth_token
TWILIO_PHONE_NUMBER=+1234567890

# Application Configuration
API_HOST=127.0.0.1
API_PORT=8000
DEBUG=False
```

### 5. Initialize Knowledge Graph (Optional)

```bash
# Populate Neo4j with academic data
python code/create_curriculum.py

# Or update existing curriculum
python code/update_curriculum.py
```

## Usage

### Starting the API Server

```bash
# Development mode with auto-reload
uvicorn Asmaad.api.main:app --reload --host 127.0.0.1 --port 8000

# Production mode
uvicorn Asmaad.api.main:app --host 0.0.0.0 --port 8000 --workers 4
```

Access the API documentation at: `http://localhost:8000/docs`

### Running Tests

```bash
# Run all tests with verbose output
pytest -v

# Run specific test file
pytest tests/test_stt.py -v

# Run with coverage report
pytest --cov=Asmaad tests/
```

### Web Scraping for Knowledge Base

```bash
python code/WebScraper.py \
  --url https://example.com \
  --output scraped_data.csv \
  --max-pages 50 \
  --depth 3 \
  --delay 2.0
```

### Example API Calls

**Process Inbound Call:**

```bash
curl -X POST "http://localhost:8000/api/v1/call/process" \
  -H "Content-Type: application/json" \
  -d '{
    "caller_id": "+923001234567",
    "audio_url": "s3://bucket/audio.wav",
    "language": "ur"
  }'
```

**Query Knowledge Graph:**

```bash
curl -X GET "http://localhost:8000/api/v1/knowledge/search?query=programs" \
  -H "Accept: application/json"
```

## Module Documentation

### Voice Processing (`Asmaad/voice/`)

Handles audio input/output and conversion:

- **STT Module**: Converts Urdu speech to text using cloud and local models
- **TTS Module**: Synthesizes Urdu text responses to speech
- **Audio I/O**: Manages recording, playback, and audio preprocessing

### NLP Engine (`Asmaad/nlp/`)

Natural language understanding and response generation:

- **Intent Classification**: Deep learning models for caller intent recognition
- **Dialogue Manager**: Context-aware conversation flow management
- **Response Generation**: LLM-based response synthesis with knowledge retrieval

### Knowledge Graph (`Asmaad/knowledge_graph/`)

Structured information storage and retrieval:

- **Entity Management**: Academic entities (Department, Program, Course, etc.)
- **Graph Queries**: Optimized Neo4j query patterns
- **GraphRAG Integration**: Hybrid retrieval-augmentation pipeline

### Telephony Integration (`Asmaad/telephony/`)

Call handling and telecommunications:

- **Twilio Adapter**: Bidirectional Twilio integration
- **Call State Machine**: Call lifecycle management
- **Event Handlers**: SIP/PSTN event processing

### API Layer (`Asmaad/api/`)

FastAPI-based REST interface:

- **Call Management Endpoints**: `/api/v1/call/*`
- **Knowledge Retrieval**: `/api/v1/knowledge/*`
- **System Status**: `/api/v1/health`

## System Flow

### Call Processing Pipeline

1. **Incoming Call** → Twilio webhook receives call event
2. **Audio Capture** → Stream caller audio to STT module
3. **Intent Recognition** → NLP engine classifies caller intent
4. **Context Retrieval** → GraphRAG queries knowledge graph
5. **Response Generation** → LLM generates appropriate response
6. **TTS Conversion** → Text response converted to Urdu speech
7. **Audio Playback** → Response streamed back to caller
8. **Logging & Analysis** → Call metadata and transcript stored

## Experimental Results

_This section to be populated with benchmarks and performance metrics_

- **STT Accuracy**: To be measured against Urdu speech corpus
- **Intent Classification F1-Score**: Classification performance metrics
- **Response Latency**: End-to-end processing time measurements
- **System Throughput**: Concurrent calls handled

See `weeklyReport/` directory for detailed progress reports.

## Contributing Guidelines

We welcome contributions! Please follow these guidelines:

1. **Fork the Repository**: Create your own fork on GitHub
2. **Create Feature Branch**: `git checkout -b feature/your-feature-name`
3. **Code Standards**:
   - Follow PEP 8 style guidelines
   - Format code using Black: `black Asmaad/`
   - Write docstrings for all functions and classes
   - Add type hints where applicable

4. **Testing**: Ensure all tests pass

   ```bash
   pytest --cov=Asmaad tests/
   ```

5. **Commit Messages**: Use clear, descriptive commit messages

   ```
   feat: Add GraphRAG integration
   fix: Resolve STT timeout issues
   docs: Update installation instructions
   ```

6. **Submit Pull Request**: Open a PR to the main branch with detailed description

7. **Code Review**: Address feedback from reviewers

## Future Enhancements

- [ ] Multi-language support (English, Arabic, Punjabi)
- [ ] Enhanced voice biometric authentication
- [ ] Advanced sentiment analysis for caller satisfaction
- [ ] Integration with institutional CRM systems
- [ ] Support for callback and queue management
- [ ] Real-time call transcription and summarization
- [ ] Mobile application for agent escalation
- [ ] Machine learning model optimization for edge deployment
- [ ] Advanced analytics and reporting dashboard

## License

This project is licensed under the **Apache License 2.0** - see the [LICENSE](LICENSE) file for detailed terms and conditions.

### Citation

If you use this project in your research or work, please cite:

```bibtex
@misc{aiinboundcalling2026,
  title={AI Inbound Calling Agent: Intelligent Voice-Based Service Automation},
  author={Asmaad and Contributors},
  year={2026},
  publisher={GitHub},
  howpublished={\url{https://github.com/Asmaaad37/AI-INBOUND-CALLING-AGENT}}
}
```

## References

1. Brown, T. B., et al. (2020). "Language Models are Few-Shot Learners." _arXiv preprint arXiv:2005.14165_
2. Devlin, J., Chang, M. W., Lee, K., & Toutanova, K. (2019). "BERT: Pre-training of Deep Bidirectional Transformers for Language Understanding." NAACL-HLT.
3. Neo4j GraphRAG Documentation: https://neo4j.com/docs/graphrag/
4. Twilio Voice API Documentation: https://www.twilio.com/docs/voice
5. OpenAI API Documentation: https://platform.openai.com/docs

---

**Last Updated**: May 2026  
**Project Status**: Active Development  
**Maintainer**: Asmaad

For questions or issues, please open a GitHub Issue or contact the development team.
