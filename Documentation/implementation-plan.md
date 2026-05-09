# Implementation Plan (16 Weeks)

The plan follows a modular, incremental, and test-driven development approach, ensuring that all functional requirements are implemented, integrated, and validated within the project timeline. The development is divided into sixteen weeks, aligned with academic milestones and FYP evaluation criteria.

| Week | Title | Description |
| :--- | :--- | :--- |
| **Week 1** | **Project Initialization & Architecture Design** | • Finalize technology stack (Python, FastAPI, PostgreSQL, Neo4j, ASR/TTS APIs).<br>• Design system architecture and module interaction diagrams.<br>• Define communication flow between Telephony, ASR, NLU, Dialogue Manager, and TTS.<br>• Set up Git repository and development environment. |
| **Week 2** | **Backend Framework & Database Setup** | • Initialize backend using FastAPI.<br>• Design and implement relational database schema.<br>• Create tables for users, calls, transcripts, logs, and knowledge base.<br>• Implement basic CRUD APIs for `system administration`. |
| **Week 3** | **Telephony Gateway Integration** | • Integrate inbound call handling via VoIP/PSTN gateway (simulated).<br>• Establish audio streaming pipeline.<br>• Implement secure session handling mechanisms.<br>• Test inbound call reception and audio forwarding. |
| **Week 4** | **ASR (Automatic Speech Recognition) Module** | • Integrate Urdu ASR engine (Google STT / Whisper).<br>• Support Urdu and Urdu-English code-switched speech.<br>• Apply basic noise reduction techniques.<br>• Store transcriptions in the database. |
| **Week 5** | **NLU & Intent Classification** | • Implement NLU pipeline using transformer-based models (mBERT/XLM-R).<br>• Define intent categories (Admissions, Fees, Merit, Complaints, etc.).<br>• Extract entities, sentiment, and urgency.<br>• Evaluate intent detection accuracy. |
| **Week 6** | **Dialogue Manager Development** | • Implement Dialogue Manager as the central decision-making unit.<br>• Define dialogue states and transitions.<br>• Implement clarification and fallback strategies.<br>• Maintain conversational context. |
| **Week 7** | **Knowledge Base (KB) Development** | • Create structured FAQ-based knowledge base.<br>• Store static university information (departments, schedules, contacts).<br>• Implement semantic search using embeddings.<br>• Integrate KB with Dialogue Manager. |
| **Week 8** | **Knowledge Graph & GraphRAG Integration** | • Design Knowledge Graph schema using Neo4j.<br>• Populate graph with interconnected academic data.<br>• Implement GraphRAG reasoning for contextual answers.<br>• Combine graph traversal with semantic search. |
| **Week 9** | **Response Generation Module** | • Design rule-based and AI-assisted response templates.<br>• Integrate GraphRAG outputs into response generation.<br>• Ensure contextual, consistent, and factual responses.<br>• Support multi-turn conversations. |
| **Week 10** | **Text-to-Speech (TTS) Module** | • Integrate Urdu TTS engine (ElevenLabs / Play.ht).<br>• Generate fluent and natural Urdu speech.<br>• Implement emotion-aware voice modulation.<br>• Optimize response latency. |
| **Week 11** | **Human Escalation Module** | • Define escalation rules based on urgency and sentiment.<br>• Transfer calls to human agents (simulated).<br>• Forward call transcripts and context securely.<br>• Log escalation events. |
| **Week 12** | **Security & Compliance Layer** | • Implement OTP-based verification (basic).<br>• Secure APIs using authentication tokens.<br>• Anonymize stored call data.<br>• Ensure PDPL/GDPR compliance at academic level. |
| **Week 13** | **Logging & Analytics Backend** | • Implement logging of calls, intents, and system responses.<br>• Compute performance metrics (accuracy, call volume).<br>• Prepare analytics APIs.<br>• Store anonymized transcripts for analysis. |
| **Week 14** | **Frontend Dashboard Development** | • Develop admin dashboard UI.<br>• Display call logs, transcripts, and analytics.<br>• Visualize system performance using charts and tables.<br>• Integrate frontend with backend APIs. |
| **Week 15** | **Integration & System Testing** | • Integrate all system modules end-to-end.<br>• Perform scenario-based testing.<br>• Validate Urdu conversational accuracy.<br>• Fix bugs and optimize system flow. |
| **Week 16** | **Deployment, Documentation & Evaluation** | • Deploy system locally or on cloud.<br>• Prepare final project documentation.<br>• Create demo video and presentation.<br>• Final evaluation against FYP objectives. |


<br>
<br>

<img width="864" height="518" alt="image" src="https://github.com/user-attachments/assets/a2f8728c-939b-4bc2-9091-10c0fa37c6b6" />
