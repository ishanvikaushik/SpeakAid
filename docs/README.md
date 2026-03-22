# SpeakAid Documentation

SpeakAid is an offline-first assistive Android application designed for neurodivergent users to support:

- Step-by-step daily routines
- Social interaction scripts
- Predictable transitions
- Audio-guided instructions

## Current Features

- **Mini Routines:** Step-by-step execution for daily tasks (e.g., Morning Routine, School Routine).
- **Social Scripts:** Pre-defined scripts for social interactions (e.g., "Say Hello", "Ask for Help").
- **Confetti Rewards:** Interactive confetti animation powered by the Konfetti library, appearing upon completion of routines or scripts.
- **Accessibility Settings:** 
    - **TTS Audio:** Toggleable Text-to-Speech guidance.
    - **Vibration Feedback:** Haptic feedback when navigating steps.
    - **Motion Settings:** Toggleable transition countdown (Reduced Motion).
- **Predictable Transitions:** 3-second visual countdown between steps to reduce anxiety.
- **Dynamic Content:** Steps and scripts loaded dynamically from a local SQLite database.
- **Progress Tracking:** Clear visual indication of progress (e.g., Step 1 / 4).
- **Navigation:** Simple Previous/Next controls.

## Tech Stack

- **Android (Java):** Core application framework.
- **SQLite:** Local storage for routines, steps, and scripts.
- **RecyclerView:** Efficient list rendering for routine and script selections.
- **TextToSpeech API:** Integrated audio guidance.
- **Konfetti Library (2.0.2):** High-performance particle system for rewards.
- **SharedPreferences:** Persistent storage for user settings.

## Structure

- **Activities:** UI screens for different application states.
- **DBHelper:** Centralized database management and seeding logic.
- **Adapters:** Custom adapters for list rendering (RoutineAdapter, ScriptAdapter).
- **Models:** Data representation for Routine, Script, and Step.
