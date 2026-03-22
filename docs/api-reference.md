# API Reference

This document outlines the internal communication and data access layer for the SpeakAid application.

## Database (DBHelper)

### Methods
- `insertRoutine(String title)`: Inserts a new routine title into the `Routine` table.
- `insertStep(int routineId, String title, int order)`: Links a specific step to a routine.
- `insertScript(String title)`: Inserts a new social script title.
- `insertScriptStep(int scriptId, String title, int order)`: Links a specific step to a social script.
- `getRoutines()`: Returns a Cursor with all routines.
- `getScripts()`: Returns a Cursor with all social scripts.
- `getSteps(int routineId)`: Returns a Cursor with ordered steps for a specific routine.
- `getScriptSteps(int scriptId)`: Returns a Cursor with ordered steps for a specific script.

## Navigation & Intents

### `RoutinePlayerActivity`
**Input Extra:**
- `routineId` (int): The ID of the routine to play.

### `ScriptPlayerActivity`
**Input Extra:**
- `scriptId` (int): The ID of the script to play.

## SharedPreferences (Settings)

The following keys are stored in the `"settings"` SharedPreferences file:
- `sound` (boolean): Whether to play TTS audio for each step. (Default: `true`)
- `vibration` (boolean): Whether to trigger haptic feedback on navigation. (Default: `false`)
- `motion` (boolean): Whether to show the transition countdown between steps. (Default: `false`)
