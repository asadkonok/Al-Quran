package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioPlaybackState
import com.example.audio.QuranAudioPlayer
import com.example.data.local.BookmarkEntity
import com.example.data.local.LastReadEntity
import com.example.data.local.NoteEntity
import com.example.data.local.QuranDatabase
import com.example.data.local.SurahProgressEntity
import com.example.data.model.DailyAyah
import com.example.data.model.SearchResult
import com.example.data.model.SurahDetail
import com.example.data.model.SurahMeta
import com.example.data.repository.PreferencesManager
import com.example.data.repository.QuranRepository
import com.example.ui.navigation.BottomTab
import com.example.ui.navigation.Screen
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class QuranViewModel(application: Application) : AndroidViewModel(application) {

    private val db = QuranDatabase.getDatabase(application)
    val repository = QuranRepository(application, db.quranDao())
    val preferencesManager = PreferencesManager(application)

    // Navigation Stack
    private val screenStack = mutableListOf<Screen>(Screen.Splash)
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Splash)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _activeTab = MutableStateFlow(BottomTab.HOME)
    val activeTab: StateFlow<BottomTab> = _activeTab.asStateFlow()

    // Quran Surahs
    private val _surahs = MutableStateFlow<List<SurahMeta>>(emptyList())
    val surahs: StateFlow<List<SurahMeta>> = _surahs.asStateFlow()

    private val _surahFilter = MutableStateFlow("ALL") // "ALL", "MAKKI", "MADANI"
    val surahFilter: StateFlow<String> = _surahFilter.asStateFlow()

    private val _surahSearchQuery = MutableStateFlow("")
    val surahSearchQuery: StateFlow<String> = _surahSearchQuery.asStateFlow()

    val filteredSurahs: StateFlow<List<SurahMeta>> = combine(
        _surahs,
        _surahFilter,
        _surahSearchQuery
    ) { list, filter, query ->
        val q = query.trim().lowercase()
        list.filter { meta ->
            val matchesFilter = when (filter) {
                "MAKKI" -> meta.revelationType.contains("মাক্কী")
                "MADANI" -> meta.revelationType.contains("মাদানী")
                else -> true
            }
            val matchesQuery = if (q.isEmpty()) true else {
                meta.nameBangla.lowercase().contains(q) ||
                    meta.nameEnglish.lowercase().contains(q) ||
                    meta.nameArabic.contains(q) ||
                    meta.meaningBangla.lowercase().contains(q) ||
                    meta.id.toString() == q
            }
            matchesFilter && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Reader State
    private val _currentSurahDetail = MutableStateFlow<SurahDetail?>(null)
    val currentSurahDetail: StateFlow<SurahDetail?> = _currentSurahDetail.asStateFlow()

    private val _isReaderLoading = MutableStateFlow(false)
    val isReaderLoading: StateFlow<Boolean> = _isReaderLoading.asStateFlow()

    private val _readerInitialAyah = MutableStateFlow(1)
    val readerInitialAyah: StateFlow<Int> = _readerInitialAyah.asStateFlow()

    // Daily Ayah
    private val _dailyAyah = MutableStateFlow<DailyAyah?>(null)
    val dailyAyah: StateFlow<DailyAyah?> = _dailyAyah.asStateFlow()

    // Search Screen
    private val _dedicatedSearchQuery = MutableStateFlow("")
    val dedicatedSearchQuery: StateFlow<String> = _dedicatedSearchQuery.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _searchResults = MutableStateFlow<List<SearchResult>>(emptyList())
    val searchResults: StateFlow<List<SearchResult>> = _searchResults.asStateFlow()

    private var searchJob: Job? = null

    // Room Flows
    val bookmarks: StateFlow<List<BookmarkEntity>> = repository.allBookmarks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notes: StateFlow<List<NoteEntity>> = repository.allNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val lastRead: StateFlow<LastReadEntity?> = repository.lastRead
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allProgress: StateFlow<List<SurahProgressEntity>> = repository.allProgress
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Audio Engine
    val audioPlayer = QuranAudioPlayer(application) { surahId, ayahNumber ->
        // When audio advances, update progress
        _surahs.value.find { it.id == surahId }?.let { meta ->
            updateLastRead(surahId, ayahNumber, meta.nameBangla, meta.nameArabic, meta.ayahCount)
        }
    }
    val audioPlaybackState: StateFlow<AudioPlaybackState> = audioPlayer.playbackState

    // Settings / Preferences
    val arabicFontSize: StateFlow<Float> = preferencesManager.arabicFontSize
    val banglaFontSize: StateFlow<Float> = preferencesManager.banglaFontSize
    val darkModeSetting: StateFlow<String> = preferencesManager.darkModeSetting
    val showTranslation: StateFlow<Boolean> = preferencesManager.showTranslation
    val showArabic: StateFlow<Boolean> = preferencesManager.showArabic
    val keepScreenOn: StateFlow<Boolean> = preferencesManager.keepScreenOn
    val autoPlayNext: StateFlow<Boolean> = preferencesManager.autoPlayNext

    init {
        // Sync audio autoPlayNext with preferences
        viewModelScope.launch {
            autoPlayNext.collect { auto ->
                audioPlayer.autoPlayNext = auto
            }
        }

        // Load initial metadata and daily ayah
        viewModelScope.launch {
            _surahs.value = repository.getSurahsMeta()
            _dailyAyah.value = repository.getDailyAyah()

            // Splash transition
            delay(1200)
            if (_currentScreen.value is Screen.Splash) {
                navigateTo(Screen.Home)
            }
        }
    }

    // Navigation Methods
    fun navigateTo(screen: Screen) {
        if (screenStack.lastOrNull() != screen) {
            screenStack.add(screen)
        }
        _currentScreen.value = screen
        when (screen) {
            is Screen.Home -> _activeTab.value = BottomTab.HOME
            is Screen.SurahList -> _activeTab.value = BottomTab.QURAN
            is Screen.Bookmarks -> _activeTab.value = BottomTab.BOOKMARKS
            is Screen.Search -> _activeTab.value = BottomTab.SEARCH
            is Screen.Settings -> _activeTab.value = BottomTab.SETTINGS
            else -> {}
        }
    }

    fun navigateTab(tab: BottomTab) {
        _activeTab.value = tab
        when (tab) {
            BottomTab.HOME -> navigateTo(Screen.Home)
            BottomTab.QURAN -> navigateTo(Screen.SurahList)
            BottomTab.BOOKMARKS -> navigateTo(Screen.Bookmarks)
            BottomTab.SEARCH -> navigateTo(Screen.Search)
            BottomTab.SETTINGS -> navigateTo(Screen.Settings)
        }
    }

    fun navigateBack(): Boolean {
        if (screenStack.size > 1) {
            screenStack.removeAt(screenStack.size - 1)
            val prev = screenStack.last()
            _currentScreen.value = prev
            when (prev) {
                is Screen.Home -> _activeTab.value = BottomTab.HOME
                is Screen.SurahList -> _activeTab.value = BottomTab.QURAN
                is Screen.Bookmarks -> _activeTab.value = BottomTab.BOOKMARKS
                is Screen.Search -> _activeTab.value = BottomTab.SEARCH
                is Screen.Settings -> _activeTab.value = BottomTab.SETTINGS
                else -> {}
            }
            return true
        }
        return false
    }

    // Reader Methods
    fun openSurah(surahId: Int, initialAyah: Int = 1) {
        _readerInitialAyah.value = initialAyah
        _isReaderLoading.value = true
        navigateTo(Screen.QuranReader(surahId, initialAyah))

        viewModelScope.launch {
            val detail = repository.getSurahDetail(surahId)
            _currentSurahDetail.value = detail
            _isReaderLoading.value = false

            detail?.let {
                updateLastRead(it.id, initialAyah, it.nameBangla, it.nameArabic, it.ayahCount)
            }
        }
    }

    fun updateLastRead(
        surahId: Int,
        ayahNumber: Int,
        surahNameBangla: String,
        surahNameArabic: String,
        totalAyahs: Int
    ) {
        viewModelScope.launch {
            repository.updateLastRead(
                surahId = surahId,
                ayahNumber = ayahNumber,
                surahNameBangla = surahNameBangla,
                surahNameArabic = surahNameArabic,
                totalAyahs = totalAyahs
            )
        }
    }

    // Bookmarks & Notes
    fun toggleBookmark(
        surahId: Int,
        ayahNumber: Int,
        surahNameBangla: String,
        surahNameArabic: String,
        arabicText: String,
        banglaText: String
    ) {
        val isBookmarked = bookmarks.value.any { it.surahId == surahId && it.ayahNumber == ayahNumber }
        viewModelScope.launch {
            repository.toggleBookmark(
                surahId = surahId,
                ayahNumber = ayahNumber,
                surahNameBangla = surahNameBangla,
                surahNameArabic = surahNameArabic,
                arabicText = arabicText,
                banglaText = banglaText,
                isCurrentlyBookmarked = isBookmarked
            )
        }
    }

    fun saveNote(
        surahId: Int,
        ayahNumber: Int,
        surahNameBangla: String,
        arabicText: String,
        banglaText: String,
        noteText: String
    ) {
        viewModelScope.launch {
            repository.saveNote(
                surahId = surahId,
                ayahNumber = ayahNumber,
                surahNameBangla = surahNameBangla,
                arabicText = arabicText,
                banglaText = banglaText,
                noteText = noteText
            )
        }
    }

    fun deleteNote(surahId: Int, ayahNumber: Int) {
        viewModelScope.launch {
            repository.deleteNote(surahId, ayahNumber)
        }
    }

    // Surah List Filter & Search
    fun setSurahFilter(filter: String) {
        _surahFilter.value = filter
    }

    fun setSurahSearchQuery(query: String) {
        _surahSearchQuery.value = query
    }

    // Dedicated Search Screen
    fun setDedicatedSearchQuery(query: String) {
        _dedicatedSearchQuery.value = query
        searchJob?.cancel()
        if (query.trim().isEmpty()) {
            _searchResults.value = emptyList()
            _isSearching.value = false
            return
        }
        searchJob = viewModelScope.launch {
            delay(300) // debounce
            _isSearching.value = true
            val res = repository.searchQuran(query)
            _searchResults.value = res
            _isSearching.value = false
        }
    }

    // Audio Playback Actions
    fun playAyahAudio(
        surahId: Int,
        ayahNumber: Int,
        surahNameBangla: String,
        surahNameArabic: String,
        totalAyahs: Int
    ) {
        audioPlayer.playAyah(
            surahId = surahId,
            ayahNumber = ayahNumber,
            surahNameBangla = surahNameBangla,
            surahNameArabic = surahNameArabic,
            totalAyahs = totalAyahs
        )
    }

    fun toggleAudioPlayPause() {
        audioPlayer.togglePlayPause()
    }

    fun nextAudioAyah() {
        audioPlayer.nextAyah()
    }

    fun previousAudioAyah() {
        audioPlayer.previousAyah()
    }

    fun seekAudio(positionMs: Int) {
        audioPlayer.seekTo(positionMs)
    }

    fun dismissAudioPlayer() {
        audioPlayer.dismissPlayer()
    }

    // Settings actions
    fun setArabicFontSize(size: Float) {
        preferencesManager.setArabicFontSize(size)
    }

    fun setBanglaFontSize(size: Float) {
        preferencesManager.setBanglaFontSize(size)
    }

    fun setDarkModeSetting(mode: String) {
        preferencesManager.setDarkModeSetting(mode)
    }

    fun setShowTranslation(show: Boolean) {
        preferencesManager.setShowTranslation(show)
    }

    fun setShowArabic(show: Boolean) {
        preferencesManager.setShowArabic(show)
    }

    fun setKeepScreenOn(keep: Boolean) {
        preferencesManager.setKeepScreenOn(keep)
    }

    fun setAutoPlayNext(auto: Boolean) {
        preferencesManager.setAutoPlayNext(auto)
    }

    fun resetPreferences() {
        preferencesManager.resetPreferences()
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.stop()
    }
}
