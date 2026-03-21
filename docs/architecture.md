# Architecture

The app follows a simple layered architecture:

## Layers

1. UI Layer
    - Activities (MainActivity, RoutineListActivity, RoutinePlayerActivity)
    - XML layouts

2. Data Layer
    - SQLite database
    - DBHelper class

3. Model Layer
    - Routine
    - Step

## Flow

User → Activity → Adapter → DBHelper → SQLite → UI Update