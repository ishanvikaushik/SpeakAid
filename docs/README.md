# SpeakAid Documentation

SpeakAid is an offline-first assistive Android application designed for neurodivergent users to support:

- Step-by-step daily routines
- Social interaction scripts
- Predictable transitions
- Audio-guided instructions

## Current Features

- Mini routines (step-by-step execution)
- Dynamic steps from SQLite database
- Progress tracking (Step x / y)
- Previous/Next navigation
- Transition countdown system
- Text-to-Speech (TTS) for steps

## Tech Stack

- Android (Java)
- SQLite (local storage)
- RecyclerView (UI lists)
- TextToSpeech API

## Structure

- Activities: UI screens
- DBHelper: database layer
- Adapter: list rendering
- Models: Routine, Step