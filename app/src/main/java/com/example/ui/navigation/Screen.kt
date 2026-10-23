package com.example.ui.navigation

sealed class Screen {
    object Splash : Screen()
    object Home : Screen()
    object SurahList : Screen()
    data class QuranReader(val surahId: Int, val initialAyah: Int = 1) : Screen()
    object Bookmarks : Screen()
    object Search : Screen()
    object Settings : Screen()
}

enum class BottomTab(val titleBangla: String) {
    HOME("হোম"),
    QURAN("কুরআন"),
    BOOKMARKS("বুকমার্ক"),
    SEARCH("অনুসন্ধান"),
    SETTINGS("সেটিংস")
}
