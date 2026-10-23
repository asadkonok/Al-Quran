package com.example.data.model

data class SurahMeta(
    val id: Int,
    val nameArabic: String,
    val nameBangla: String,
    val nameEnglish: String,
    val meaningBangla: String,
    val revelationType: String,
    val ayahCount: Int,
    val bismillahPre: Boolean = true
)

data class Ayah(
    val ayahNumber: Int,
    val arabic: String,
    val bangla: String
)

data class SurahDetail(
    val id: Int,
    val nameArabic: String,
    val nameBangla: String,
    val nameEnglish: String,
    val meaningBangla: String,
    val revelationType: String,
    val ayahCount: Int,
    val bismillahPre: Boolean,
    val ayahs: List<Ayah>
)

data class SearchResult(
    val surahId: Int,
    val surahNameBangla: String,
    val surahNameArabic: String,
    val ayahNumber: Int,
    val arabic: String,
    val bangla: String,
    val matchSnippet: String
)

data class DailyAyah(
    val surahId: Int,
    val surahNameBangla: String,
    val surahNameArabic: String,
    val ayahNumber: Int,
    val arabic: String,
    val bangla: String,
    val contextTitle: String
)
