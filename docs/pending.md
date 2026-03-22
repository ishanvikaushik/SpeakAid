# 📌 SpeakAid – Pending Tasks (MVP Checklist)

This document tracks all remaining features required before presenting the app to parents at the festival.

---

# 🔥 CORE GOAL
Ensure the app:
- Works smoothly on real devices
- Is easy to understand in seconds
- Actually helps children follow routines & social behavior

---

# ✅ 1. MINI ROUTINES (CORE FEATURE)

### Must Work
- [ ] Multiple routines (Morning, School, Bedtime)
- [ ] Steps display correctly
- [ ] Next / Previous navigation works perfectly
- [ ] Progress indicator (Step X / Y)
- [ ] Completion feedback (sound + vibration)

### 🔥 Important Improvements
- [ ] Resume last progress when app reopens
- [ ] No skipping / incorrect step behavior
- [ ] No duplicate steps in database

---

# ✅ 2. SOCIAL SCRIPTS (SECOND CORE FEATURE)

### Must Work
- [ ] Script list loads correctly
- [ ] Script player works step-by-step
- [ ] Text-to-Speech (TTS) works for each step
- [ ] Navigation (Next / Previous) works properly

### 🔥 Important Improvements
- [ ] Add "Repeat Step" button
- [ ] Ensure simple and clear wording
- [ ] Smooth transition between steps

---

# ✅ 3. SETTINGS (PARENT CONTROL)

### Must Work
- [ ] Sound toggle (ON/OFF)
- [ ] Vibration toggle (ON/OFF)
- [ ] Reduce motion toggle

### 🔥 Important Improvements
- [ ] Add speech speed control (Slow / Normal)
- [ ] Ensure settings persist after app restart

---

# ✅ 4. NAVIGATION (CRITICAL)

- [ ] Back button works on ALL screens
- [ ] No screen gets stuck
- [ ] Proper navigation flow:
    - Home → List → Player → Back → Home

---

# ✅ 5. DATA STABILITY

- [ ] No duplicate entries in database
- [ ] Default data inserted ONLY once
- [ ] Handle empty states safely (no crashes)

---

# ✅ 6. OFFLINE SUPPORT (MANDATORY)

- [ ] App works completely offline
- [ ] No internet dependency
- [ ] SQLite data loads reliably

---

# ✅ 7. PERFORMANCE

- [ ] No lag when clicking Next/Previous
- [ ] TTS starts quickly
- [ ] Smooth experience on low-end devices

---

# ⚠️ 8. CAREGIVER CONTROL (IMPORTANT FEATURE)

### Minimum Implementation
- [ ] Add Custom Routine
    - [ ] Enter routine name
    - [ ] Add steps manually
    - [ ] Save to database

---

# ✅ 9. FEEDBACK SYSTEM

- [ ] Completion sound (clap)
- [ ] Vibration feedback
- [ ] Clear "Completed" message

### 🔥 Improvement
- [ ] Different feedback for:
    - Normal step
    - Completion

---

# 🎯 10. DEMO FLOWS (FOR FESTIVAL)

Ensure these flows work perfectly:

### Flow 1: Routine Usage
- [ ] Child follows morning routine step-by-step

### Flow 2: Social Learning
- [ ] Child uses script (e.g., Ask for Help)

### Flow 3: Parent Interaction
- [ ] Parent can add or modify a routine

---

# 🚀 PRIORITY PLAN

## 🔥 Tier 1 (DO FIRST)
- [ ] Resume progress
- [ ] Repeat step button
- [ ] Fix navigation issues
- [ ] Remove bugs / crashes

## ⚡ Tier 2 (HIGH IMPACT)
- [ ] Add custom routines
- [ ] Speech speed control
- [ ] Clean database handling

## 💡 Tier 3 (OPTIONAL)
- [ ] Add icons for scripts
- [ ] Improve text clarity
- [ ] Minor UX improvements

---

# 🧠 FINAL CHECK BEFORE DEMO

- [ ] App works without internet
- [ ] No crashes during usage
- [ ] Navigation is smooth
- [ ] Parent understands app in < 1 minute

---

# 💬 NOTE

Focus on:
✔ Reliability  
✔ Simplicity  
✔ Real usability

Not on UI polish or animations.

---