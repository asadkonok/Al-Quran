package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks", primaryKeys = ["surahId", "ayahNumber"])
data class BookmarkEntity(
    val surahId: Int,
    val ayahNumber: Int,
    val surahNameBangla: String,
    val surahNameArabic: String,
    val arabicText: String,
    val banglaText: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notes", primaryKeys = ["surahId", "ayahNumber"])
data class NoteEntity(
    val surahId: Int,
    val ayahNumber: Int,
    val surahNameBangla: String,
    val arabicText: String,
    val banglaText: String,
    val noteText: String,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "last_read")
data class LastReadEntity(
    @PrimaryKey val id: Int = 1,
    val surahId: Int,
    val ayahNumber: Int,
    val surahNameBangla: String,
    val surahNameArabic: String,
    val totalAyahs: Int,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "surah_progress")
data class SurahProgressEntity(
    @PrimaryKey val surahId: Int,
    val lastAyahRead: Int,
    val totalAyahs: Int,
    val isCompleted: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)
