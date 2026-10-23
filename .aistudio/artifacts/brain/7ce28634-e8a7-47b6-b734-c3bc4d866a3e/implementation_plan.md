# আল কুরআন (Al Quran Bangla) Implementation Plan

A comprehensive, production-grade Holy Quran application in Bengali and Arabic for Android built with Jetpack Compose, Room database, offline authentic Quran data, audio recitation player, bookmarks, last-read tracking, and customizable typography.

---

## User Review & Critical Decisions

> [!IMPORTANT]
> The workspace is natively set up as a modern Android Jetpack Compose project running on an active streaming Android emulator. To ensure maximum stability, offline speed, responsive performance on all Android devices, and instant interactive preview in the browser, the application is engineered in **Native Android with Jetpack Compose & Kotlin** using clean MVVM architecture.

- **Platform Target**: Native Android (Kotlin + Jetpack Compose + Material 3) optimized for smooth scrolling, RTL Arabic rendering, and zero-lag reading.
- **Quran Data & Translation**: Authentic Arabic script paired with verified Mawlana Muhiuddin Khan Bengali translation across all 114 Surahs with comprehensive metadata (Ayah count, Makki/Madani, transliteration, and meaning).
- **Audio Recitation**: Integrated audio player streaming recitations by Qari Mishary Rashid Alafasy with a persistent bottom mini-player, play/pause, Ayah highlight, and forward/previous controls.
- **Local Persistence (Room & DataStore)**: Device-local persistence using Room for Bookmarks, Ayah Notes, Reading Progress, and Last-Read tracking, alongside DataStore for font sizes and dark mode theme preferences.

---

## 1. Overview & Core Concept

**আল কুরআন (বাংলা অনুবাদসহ)** delivers a spiritual, distraction-free Quran reading and listening sanctuary for Bengali-speaking Muslims worldwide.

- **What It Does**:
  - Browse all 114 Surahs with search and filtration (by Arabic, Bengali, English transliteration, or revelation location).
  - Read Ayah-by-Ayah with crystalline RTL Arabic typography and faithful Bengali translation.
  - Automatically resume from the exact Last-Read Surah and Ayah with one tap on the home dashboard.
  - Track individual Surah reading completion and overall Quran reading progress percentage.
  - Bookmark favorite Ayahs, attach personal study reflections/notes, and copy or share verses with formatted Bengali and Arabic text.
  - Stream pristine Quranic audio recitation with synchronous Ayah highlighting and a system-aware bottom mini-player.
  - Customize Arabic font size, Bengali font size, and switch effortlessly between daylight parchment and soothing midnight dark themes.

---

## 2. User Experience & Visual Design

### Visual Identity & Theme
- **Theme Direction**: Divine Minimalist Islamic Elegance.
- **Color Palette**:
  - *Primary*: Deep Emerald (`#0D4B3E` / Dark: `#1B7A68`) representing tranquil Islamic geometry and tradition.
  - *Accent & Embellishments*: Warm Islamic Gold (`#D4AF37` / `#E6C86E`) for Ayah badges, progress rings, and active audio highlights.
  - *Backgrounds*: Parchment Pearl (`#FBF9F5`) in Light Mode; Deep Charcoal Obsidian (`#101716`) in Dark Mode with tinted surface elevations (`#172422`).
  - *Text Colors*: Deep Ink (`#192420`) and Muted Moss (`#506760`) for Bengali text; High-contrast crisp off-white (`#EAF2EE`) in Dark Mode.
- **Typography & Formatting**:
  - *Arabic Quran Text*: High-legibility Uthmani-style font rendering with right-to-left layout, generous line-height (`38.sp` default, adjustable from `24.sp` to `48.sp`), and zero clipping of diacritics (tashkeel).
  - *Bengali Translation*: Clear Unicode typography with distinct conjunct clarity, optimal line spacing (`24.sp` default, adjustable from `14.sp` to `28.sp`).
  - *Islamic Numerals*: Ornate Ayah markers displaying Bengali and Eastern Arabic numerals.

### Key Screens & Navigation Flow
```
┌────────────────────────────────────────────────────────┐
│                      Splash Screen                     │
│         ☪ আল কুরআন • বাংলা অনুবাদসহ পবিত্র কুরআন       │
└───────────────────────────┬────────────────────────────┘
                            │ (Auto-transition)
┌───────────────────────────▼────────────────────────────┐
│                    Main App Scaffold                   │
│   ┌────────────────────────────────────────────────┐   │
│   │               Top App Bar                      │   │
│   ├────────────────────────────────────────────────┤   │
│   │               Screen Content                   │   │
│   │  • Home Dashboard (Continue Reading, Daily)    │   │
│   │  • Surah List (114 Surahs, Search, Filters)    │   │
│   │  • Quran Reader (Ayah-by-Ayah, Audio, Actions) │   │
│   │  • Bookmarks & Notes (Categorized & Local)     │   │
│   │  • Settings (Font Sizes, Themes, Audio config) │   │
│   ├────────────────────────────────────────────────┤   │
│   │        Persistent Floating Audio Mini-Player   │   │
│   ├────────────────────────────────────────────────┤   │
│   │         Material 3 Bottom Navigation Bar       │   │
│   │   [হোম]    [কুরআন]    [বুকমার্ক]    [সেটিংস]   │   │
│   └────────────────────────────────────────────────┘   │
└────────────────────────────────────────────────────────┘
```

1. **Home Screen (হোম)**:
   - Greeting header with Islamic date / Assalamu Alaikum banner.
   - **Continue Reading Card (পড়া চালিয়ে যান)**: Highlights last-read Surah name, Ayah number, percentage complete, and one-tap resume button.
   - **Overall Reading Progress**: Visual bar showing percentage of Surahs and Ayahs completed.
   - **Quick Action Grid**: Quick navigation pills for "কুরআন পড়ুন" (Quran), "বুকমার্ক" (Bookmarks), "অনুসন্ধান" (Search), and "অডিও" (Audio).
   - **Daily Ayah Card (আজকের আয়াত)**: Curated verse with Arabic, Bengali translation, copy/share button, and instant audio recitation trigger.

2. **Surah List Screen (সূরা তালিকা)**:
   - Search bar filtering by Surah number, Bengali name (e.g. বাকারা), Arabic name (البقرة), or English name (Al-Baqarah).
   - Filter chips for "সকল সূরা" (All), "মাক্কী" (Meccan), and "মাদানী" (Medinan).
   - List items showing Surah number inside a custom geometric badge, Arabic calligraphy, Bengali name, meaning, verse count, and reading progress bar.

3. **Quran Reader Screen (কুরআন রিডার)**:
   - Surah metadata header with Bismillah calligraphic emblem (`بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ`) for appropriate Surahs.
   - Smooth `LazyColumn` for Ayah cards with index markers.
   - Interactive Ayah sheet:
     - 🔖 Bookmark / Unbookmark toggle
     - 📝 Add / Edit Personal Note dialog
     - 📋 Copy (Arabic only, Bangla only, or Arabic + Bangla)
     - ↗ Share formatted verse to external apps
     - ▶ Play Ayah audio recitation

4. **Bookmarks & Notes Screen (বুকমার্ক ও নোট)**:
   - Tabbed view: "বুকমার্ককৃত আয়াত" (Saved Ayahs) and "ব্যক্তিগত নোট" (Notes).
   - Tap to jump directly into the Reader at that exact Ayah.
   - Delete / edit actions with undo snackbars.

5. **Settings Screen (সেটিংস)**:
   - Appearance mode: Light, Dark, System default.
   - Interactive font size sliders with live preview card:
     - আরবি ফন্ট সাইজ (Arabic Font Size: 22sp - 46sp)
     - বাংলা ফন্ট সাইজ (Bengali Font Size: 14sp - 26sp)
   - Reading preferences: Show/hide Arabic text, show/hide translation, screen keep-awake toggle.
   - Audio Reciter selector (Mishary Rashid Alafasy, Abdul Basit, Al-Ghamadi).

---

## 3. Technical Architecture & Data Strategy

```
┌───────────────────────────────────────────────────────────────┐
│                       Jetpack Compose UI                      │
│   HomeScreen  |  SurahListScreen  |  QuranReaderScreen        │
│   BookmarksScreen  |  SettingsScreen  |  AudioMiniPlayerBar   │
└───────────────────────────────┬───────────────────────────────┘
                                │ StateFlow / Events
┌───────────────────────────────▼───────────────────────────────┐
│                   QuranViewModel & AudioPlayer                │
│   • UI States (Surahs, Reading, AudioState, Search)           │
│   • Android MediaPlayer / ExoPlayer engine for streaming      │
└───────────────────────────────┬───────────────────────────────┘
                                │ Repository Pattern
┌───────────────────────────────▼───────────────────────────────┐
│                        QuranRepository                        │
│   • QuranDataProvider (All 114 Surahs JSON asset & parser)    │
│   • Room Database (BookmarksDao, NotesDao, LastReadDao)       │
│   • UserPreferencesRepository (DataStore for Theme & Fonts)   │
└───────────────────────────────────────────────────────────────┘
```

### Data Layer
- **Quran Data Engine**:
  - Packaged structured asset `quran_bangla.json` providing comprehensive 114 Surahs with official metadata, Arabic text with full tashkeel, and authentic Bengali translations.
  - Fully offline: zero network dependency needed for reading, searching, or filtering all 114 Surahs.
- **Room Database Entities**:
  - `BookmarkEntity`: `surahId`, `ayahNumber`, `surahNameBangla`, `surahNameArabic`, `arabicText`, `banglaText`, `createdAt`.
  - `NoteEntity`: `surahId`, `ayahNumber`, `noteText`, `updatedAt`.
  - `LastReadEntity`: `id = 1`, `surahId`, `ayahNumber`, `surahNameBangla`, `progressPercent`, `updatedAt`.
  - `SurahProgressEntity`: `surahId`, `lastAyahRead`, `totalAyahs`, `isCompleted`.

---

## 4. Implementation Steps

1. **Project Setup & Dependencies**:
   - Verify Room dependencies and KSP compiler in `app/build.gradle.kts`.
   - Update `metadata.json` and `strings.xml` to set official title `আল কুরআন` and description.
2. **Data Assets & Parsing**:
   - Provide high-fidelity, verified Bengali Quran dataset spanning all 114 Surahs with metadata and Arabic Ayahs.
   - Implement `QuranDataProvider` and in-memory indexed search engine.
3. **Room Database Integration**:
   - Create Room Entities, DAOs, and `QuranDatabase`.
   - Implement `QuranRepository` providing reactive `Flow` feeds for bookmarks, notes, and reading progress.
4. **Audio Engine**:
   - Implement `QuranAudioPlayer` using Android MediaPlayer with online audio CDN streaming, playback status, duration, seeking, and Ayah-by-Ayah autoplay queue.
5. **Theme & Typography**:
   - Implement `Theme.kt` with Islamic emerald and gold palettes for both Light and Dark mode.
   - Configure dynamic typography scaling responsive to user preference sliders.
6. **Compose UI Screens**:
   - `HomeScreen`: Dashboard with continue reading banner, reading progress, quick actions, and daily Ayah.
   - `SurahListScreen`: 114 Surahs list, Makki/Madani badges, live search filter.
   - `QuranReaderScreen`: Ayah list, RTL Arabic text, Bengali translation, bottom actions sheet.
   - `BookmarksScreen`: Saved verses and notes management.
   - `SettingsScreen`: Font sliders with live preview, theme selector, audio preferences.
   - `AudioMiniPlayer`: Floating persistent bottom player bar.
7. **Verification & Build**:
   - Compile with `compile_applet` and verify seamless execution in the streaming emulator.
