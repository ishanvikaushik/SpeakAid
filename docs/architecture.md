# Architecture

The SpeakAid application follows a structured layered architecture designed for modularity and offline-first performance.

## Layers

1. **UI Layer**
    - **Activities:**
        - `MainActivity`: Central hub for navigation.
        - `RoutineListActivity` / `ScriptListActivity`: Display lists of available routines and scripts.
        - `RoutinePlayerActivity` / `ScriptPlayerActivity`: Handles the step-by-step playback logic.
        - `SettingsActivity`: Manages user accessibility preferences.
    - **Adapters:** 
        - `RoutineAdapter` and `ScriptAdapter` for handling RecyclerView data binding.
    - **XML Layouts:** Responsive layouts using `FrameLayout` (for layering animations) and `LinearLayout`.

2. **Data Layer**
    - **SQLite Database:** Local storage for all structured data.
    - **DBHelper:** Manages database creation, table updates, and data seeding (routines, scripts, and steps).
    - **SharedPreferences:** Persistent storage for simple key-value settings (sound, vibration, motion).

3. **Model Layer**
    - **Routine:** Represents a collection of daily tasks.
    - **Script:** Represents a collection of social interaction steps.
    - **Step:** The fundamental unit of content for both routines and scripts.

## Core Logic Flows

### Navigation Flow
`User Selection (List)` → `Intent (with ID)` → `Player Activity` → `Database Query (Steps)` → `UI Update`

### Completion & Reward Flow
`Last Step Reached` → `Next Clicked` → `Completion UI Update` → `Konfetti Trigger (PartyFactory)`

### Accessibility Flow
`User Change (SettingsActivity)` → `SharedPreferences Save` → `Player Activity (Read on Start)` → `Conditional Logic (TTS/Vibration/Countdown)`

## Technology Integration
- **Text-To-Speech:** Android `TextToSpeech` API is initialized in players and triggered on each step display.
- **Konfetti:** The `nl.dionsegijn.konfetti` library is integrated into the layout layer to provide high-performance rewards without blocking the UI thread.
