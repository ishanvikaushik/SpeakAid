# SpeakAid Documentation

SpeakAid is an offline-first assistive Android application designed for neurodivergent users to support daily living, social interactions, and emotional regulation.

## 🚀 Key Features

### 📋 Mini Routines
- **Step-by-Step Execution:** Breaks down complex daily tasks (e.g., Morning, School, Bedtime) into manageable steps.
- **Progress Tracking:** Clear visual indication of progress (e.g., Step 1/4).
- **Predictable Transitions:** Visual countdown between steps to reduce transition anxiety.

### 💬 Social Scripts
- **Social Practice:** Pre-defined scripts for common social interactions like "Say Hello" or "Ask for Help".
- **Audio Guidance:** Text-to-Speech (TTS) for every step to assist with verbalizing.

### 🗣️ AAC (Augmentative & Alternative Communication)
- **Visual Communication:** A dedicated "Communicate" module with easy-to-tap symbols (Food, Water, Toilet, Help, etc.).
- **Voice Output:** Instantly speaks complete sentences when a symbol is pressed (e.g., "I am thirsty").

### 🧩 Sensory Play (Zen Hub)
- **Zen Canvas:** A soothing "scratch and reveal" game with calming abstract patterns.
- **Fidget Spinner:** A physics-based digital spinner with realistic momentum and friction for tactile grounding.
- **Haptic Feedback:** Gentle vibrations for sensory satisfaction.

### 🔐 Caregiver Control (Parent Mode)
- **Passcode Protection:** Secure access to administrative features (Default: 1234, customizable in Settings).
- **Custom Content:** Caregivers can add their own custom routines and social scripts.
- **✨ AI Simplification:** Integrated **Gemini 1.5 Flash AI** to automatically paraphrase complex caregiver instructions into "Plain Language" for easier user comprehension.

### 🎨 Personalization & Accessibility
- **Theming Engine:** Support for four distinct themes (Classic, Soft Lavender, Ocean Breeze, Sunset Glow).
- **Accessibility Settings:** Toggle sound (TTS), haptic feedback (vibration), and reduced motion (skipping countdowns).
- **Consistent Navigation:** "Back" button headers across all modules for predictable flow.

## 🛠️ Tech Stack
- **Android (Java):** Core framework.
- **SQLite:** Local database for persistent storage of routines and scripts.
- **Google Generative AI SDK:** Integration with Gemini API for text simplification.
- **Konfetti Library:** Interactive reward system upon task completion.
- **Secrets Gradle Plugin:** Secure management of API keys via `local.properties`.
- **SharedPreferences:** Persistence for user settings and theme preferences.

## 📁 Project Structure
- **Activities:** UI screens for main modules (`MainActivity`, `CommunicateActivity`, `SensoryPlayActivity`, etc.).
- **DBHelper:** Centralized database layer managing `Routine`, `Script`, and `Step` tables.
- **ThemeHelper:** Utility for applying consistent branding across all screens.
- **Custom Views:** Performance-optimized views like `ScratchView` and `FidgetSpinnerView`.
- **Adapters:** Efficient list rendering for dynamic content.
