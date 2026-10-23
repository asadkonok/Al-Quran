# আল কুরআন (Al Quran Bangla) - Android Application

A complete, modern Holy Quran application in Bengali and Arabic built with **Jetpack Compose**, **Kotlin**, **Room SQLite database**, **Material 3**, and authentic offline Quran data.

---

## 🌟 Key Features

1. **Complete Quran Content (100% Offline)**:
   - All **114 Surahs** with complete Arabic text and authentic Bengali translation.
   - Verified translation by **Mawlana Muhiuddin Khan** (King Fahad Quran Complex approved).
   - High-fidelity **Uthmani Arabic script** with clear tashkeel and RTL alignment.
   - Comprehensive metadata for every Surah (Makki/Madani, Ayah count, Bengali meaning, transliteration).

2. **Ayah-by-Ayah Reading Sanctuary**:
   - Distinctive Islamic Ayah badges displaying Bengali numerals.
   - Adjustable Arabic font size (22sp – 44sp) using the authentic **Amiri** Quran font.
   - Adjustable Bengali font size (14sp – 26sp) using **Hind Siliguri** font with proper conjunct shaping.
   - Bismillah calligraphy header for applicable Surahs.
   - Quick jump to any Ayah number.

3. **Audio Recitation & Mini-Player**:
   - Crystal-clear audio recitation streamed from global CDN by **Qari Mishary Rashid Alafasy**.
   - Persistent bottom mini-player with play/pause, seek, previous/next Ayah, and progress indicator.
   - Continuous auto-play for seamless recitation listening.
   - Synchronized visual highlighting of the currently playing Ayah.

4. **Last-Read Position & Progress Tracking**:
   - Automatically tracks your exact reading position (Surah and Ayah number).
   - Dashboard card with one-tap "পড়া চালিয়ে যান" (Continue Reading) resumption.
   - Visual progress bars showing reading percentage per Surah and overall Quran completion.

5. **Local Bookmarks & Personal Reflections**:
   - Bookmark individual Ayahs with one tap.
   - Add personal study notes and reflections to any verse.
   - Dedicated "বুকমার্ক ও নোট" screen with fast search and instant jump-to-reader.
   - Stored securely on-device using Android **Room SQLite**.

6. **Full-Text Quran Search**:
   - Real-time search across Surah names (Bangla, Arabic, English), meanings, and Ayah Bengali translations.
   - One-tap suggested search topics (যেমন: নামাজ, রোজা, যাকাত, তাকওয়া, রহমত, ধৈর্য).

7. **Customization & Themes**:
   - Light Mode (Parchment & Warm White), Dark Mode (Obsidian Emerald), and System Default.
   - Toggles to show/hide Arabic text or Bengali translation.
   - Keep screen awake option during recitation and study.

---

## 📱 Architecture & Tech Stack

- **UI Layer**: Jetpack Compose, Material 3, Edge-to-Edge window insets.
- **State Management**: Android `ViewModel` + Kotlin `StateFlow` & `SharingStarted.WhileSubscribed`.
- **Local Persistence**: Android Room Database (`QuranDatabase`, `BookmarkEntity`, `NoteEntity`, `LastReadEntity`, `SurahProgressEntity`).
- **User Preferences**: `PreferencesManager` with reactive `StateFlow` for font scales and theme modes.
- **Audio Engine**: `QuranAudioPlayer` utilizing Android `MediaPlayer` streaming from EveryAyah CDN.
- **Typography**: Bundled local Google Fonts (`amiri.ttf` and `hind_siliguri.ttf`).

---

## 🛠️ Build & Release Instructions

### Generating Debug APK:
```bash
gradle :app:assembleDebug
```
The APK will be generated at:
`app/build/outputs/apk/debug/app-debug.apk`

### Generating Release Bundle (AAB for Google Play):
```bash
gradle :app:bundleRelease
```
The Android App Bundle will be generated at:
`app/build/outputs/bundle/release/app-release.aab`
