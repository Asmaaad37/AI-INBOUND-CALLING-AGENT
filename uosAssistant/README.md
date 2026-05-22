# UOS Assistance - Urdu Voice Assistant

A simple modern Android application built with Kotlin and Jetpack Compose that acts as an Urdu voice assistant.

## Features
- **Voice-to-Text**: Converts Urdu speech to text using Google Speech-to-Text.
- **REST API Integration**: Sends transcribed text to a custom REST API via Retrofit.
- **Text-to-Voice**: Converts API responses back to Urdu speech using Google Text-to-Speech.
- **RTL Support**: Native support for Urdu (Right-to-Left).
- **Clean UI**: Minimalistic design with a large microphone button and recording animations.
- **Dark/Light Mode**: Full support for Material 3 themes.

## Technical Stack
- **Kotlin**: Primary language.
- **Jetpack Compose**: Modern UI toolkit.
- **MVVM**: Architecture pattern.
- **Retrofit & OkHttp**: Networking.
- **Coroutines**: Asynchronous operations.
- **Material Design 3**: UI components.

## Setup Guide

### 1. Prerequisites
- Android Studio Iguana or newer.
- Android SDK 34 or newer.
- A physical device or emulator with Google Play Services (for STT/TTS).

### 2. Configuration
The app uses `local.properties` to manage secrets and a JSON file for Google Cloud credentials.

1. **Google Cloud Credentials**:
   - The service account JSON is located at .
   - Update `local.properties` with the following:
     ```properties
     BASE_URL=Default
     API_TOKEN=default
     GOOGLE_API_KEY=default
     GOOGLE_PROJECT_ID=default
     GOOGLE_CLIENT_EMAIL=default
     ```

### 3. Build & Run
1. Sync the project with Gradle files.
2. Build the project.
3. Run the app on your device.

## Architecture
- `ui/`: Compose screens and components.
- `viewmodel/`: Business logic and state management.
- `network/`: Retrofit API service and models.
- `speech/`: Speech recognition management.
- `tts/`: Text-to-Speech management.
- `config/`: Centralized configuration.
- `utils/`: Helper classes.

## Permissions
- `RECORD_AUDIO`: Required for voice input.
- `INTERNET`: Required for API requests.
