# Design Decisions

## Offline-First
The app is designed to function entirely without internet connectivity. All data (routines, scripts, and steps) is stored in a local SQLite database to ensure reliability in any environment.

## Accessibility-First UI
- **Large Elements:** Buttons and text sizes are increased to improve usability for individuals with motor or visual challenges.
- **Single-Task Focus:** Only one step is shown at a time to minimize cognitive load and focus attention.
- **Visual Hierarchy:** Progress indicators are clearly separated from the primary instructions.

## Predictable Transitions
A 3-second visual countdown is implemented between steps to provide predictability and reduce anxiety during transitions. This can be disabled via "Reduced Motion" for users who find it distracting.

## Multi-Modal Feedback
- **Audio Support:** Integrated Text-to-Speech (TTS) provides verbal instructions for every step.
- **Haptic Feedback:** Short vibration pulses provide physical confirmation during navigation.
- **Visual Rewards:** High-performance confetti animations provide positive reinforcement upon task completion.

## User-Centric Customization
Through the Settings menu, users (or caregivers) can tailor the experience by toggling sound, vibration, and motion features to match specific sensory profiles.

## Layout Choice (FrameLayout)
The use of `FrameLayout` for player activities allows for "layering," where the reward animation (`KonfettiView`) can be drawn on top of the interactive content without disrupting the UI flow or requiring complex layout weight management.
