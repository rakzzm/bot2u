# bot2u Android App

A native Android app for bot2u TTS (Text-to-Speech) with voice cloning capabilities.

## Architecture

The app follows Clean Architecture with three main layers:

- **Data Layer**: API models, Retrofit service, and repository
- **Domain Layer**: Use cases (simplified in this implementation)
- **UI Layer**: Jetpack Compose screens, ViewModels, and theme

## Project Structure

```
android/
├── app/
│   ├── src/main/
│   │   ├── java/com/bot2u/app/
│   │   │   ├── data/
│   │   │   │   ├── api/          # Retrofit API service
│   │   │   │   ├── model/        # API request/response models
│   │   │   │   └── repository/   # Data repository
│   │   │   ├── ui/
│   │   │   │   ├── theme/        # Compose theme
│   │   │   │   ├── TTSViewModel.kt
│   │   │   │   └── TTSScreen.kt
│   │   │   └── MainActivity.kt
│   │   └── res/                  # Android resources
│   └── build.gradle.kts
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

## Prerequisites

1. Android Studio Arctic Fox or later
2. Kotlin 1.9.21+
3. Gradle 8.2+
4. Android SDK 34+

## Setup

1. Open the `android` folder in Android Studio
2. Sync project with Gradle files
3. Build and run on an emulator or device

## Backend Setup

The Android app requires a FastAPI backend running the TTS library:

```bash
cd backend
pip install -r requirements.txt
python main.py
```

The backend runs on `http://localhost:8000` by default.

## Configuration

Update the API base URL in [`ChatterboxApi.kt`](app/src/main/java/com/bot2u/app/data/api/ChatterboxApi.kt):

```kotlin
const val BASE_URL = "http://YOUR_SERVER_IP:8000/"
```

For emulator use: `http://10.0.2.2:8000/`
For physical device: Use your computer's IP address

## Features

- Upload voice samples for voice cloning
- Enter text to synthesize speech
- Adjustable generation parameters (exaggeration, temperature, CFG weight)
- Audio playback of generated speech
- Modern Material 3 design with Jetpack Compose

## Dependencies

- Jetpack Compose with Material 3
- Retrofit for API calls
- Coroutines for async operations
- MediaPlayer for audio playback
