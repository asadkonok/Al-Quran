package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("quran_settings_prefs", Context.MODE_PRIVATE)

    private val _arabicFontSize = MutableStateFlow(prefs.getFloat(KEY_ARABIC_FONT_SIZE, 28f))
    val arabicFontSize: StateFlow<Float> = _arabicFontSize.asStateFlow()

    private val _banglaFontSize = MutableStateFlow(prefs.getFloat(KEY_BANGLA_FONT_SIZE, 18f))
    val banglaFontSize: StateFlow<Float> = _banglaFontSize.asStateFlow()

    private val _darkModeSetting = MutableStateFlow(prefs.getString(KEY_DARK_MODE, "system") ?: "system")
    val darkModeSetting: StateFlow<String> = _darkModeSetting.asStateFlow()

    private val _showTranslation = MutableStateFlow(prefs.getBoolean(KEY_SHOW_TRANSLATION, true))
    val showTranslation: StateFlow<Boolean> = _showTranslation.asStateFlow()

    private val _showArabic = MutableStateFlow(prefs.getBoolean(KEY_SHOW_ARABIC, true))
    val showArabic: StateFlow<Boolean> = _showArabic.asStateFlow()

    private val _keepScreenOn = MutableStateFlow(prefs.getBoolean(KEY_KEEP_SCREEN_ON, true))
    val keepScreenOn: StateFlow<Boolean> = _keepScreenOn.asStateFlow()

    private val _autoPlayNext = MutableStateFlow(prefs.getBoolean(KEY_AUTO_PLAY_NEXT, true))
    val autoPlayNext: StateFlow<Boolean> = _autoPlayNext.asStateFlow()

    fun setArabicFontSize(size: Float) {
        prefs.edit().putFloat(KEY_ARABIC_FONT_SIZE, size).apply()
        _arabicFontSize.value = size
    }

    fun setBanglaFontSize(size: Float) {
        prefs.edit().putFloat(KEY_BANGLA_FONT_SIZE, size).apply()
        _banglaFontSize.value = size
    }

    fun setDarkModeSetting(mode: String) {
        prefs.edit().putString(KEY_DARK_MODE, mode).apply()
        _darkModeSetting.value = mode
    }

    fun setShowTranslation(show: Boolean) {
        prefs.edit().putBoolean(KEY_SHOW_TRANSLATION, show).apply()
        _showTranslation.value = show
    }

    fun setShowArabic(show: Boolean) {
        prefs.edit().putBoolean(KEY_SHOW_ARABIC, show).apply()
        _showArabic.value = show
    }

    fun setKeepScreenOn(keep: Boolean) {
        prefs.edit().putBoolean(KEY_KEEP_SCREEN_ON, keep).apply()
        _keepScreenOn.value = keep
    }

    fun setAutoPlayNext(auto: Boolean) {
        prefs.edit().putBoolean(KEY_AUTO_PLAY_NEXT, auto).apply()
        _autoPlayNext.value = auto
    }

    fun resetPreferences() {
        prefs.edit().clear().apply()
        _arabicFontSize.value = 28f
        _banglaFontSize.value = 18f
        _darkModeSetting.value = "system"
        _showTranslation.value = true
        _showArabic.value = true
        _keepScreenOn.value = true
        _autoPlayNext.value = true
    }

    companion object {
        private const val KEY_ARABIC_FONT_SIZE = "arabic_font_size"
        private const val KEY_BANGLA_FONT_SIZE = "bangla_font_size"
        private const val KEY_DARK_MODE = "dark_mode" // "system", "dark", "light"
        private const val KEY_SHOW_TRANSLATION = "show_translation"
        private const val KEY_SHOW_ARABIC = "show_arabic"
        private const val KEY_KEEP_SCREEN_ON = "keep_screen_on"
        private const val KEY_AUTO_PLAY_NEXT = "auto_play_next"
    }
}
