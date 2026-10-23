package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface QuranDao {

    // --- Bookmarks ---
    @Query("SELECT * FROM bookmarks ORDER BY createdAt DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Query("SELECT COUNT(*) > 0 FROM bookmarks WHERE surahId = :surahId AND ayahNumber = :ayahNumber")
    fun isBookmarked(surahId: Int, ayahNumber: Int): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE surahId = :surahId AND ayahNumber = :ayahNumber")
    suspend fun deleteBookmark(surahId: Int, ayahNumber: Int)

    // --- Notes ---
    @Query("SELECT * FROM notes ORDER BY updatedAt DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE surahId = :surahId AND ayahNumber = :ayahNumber LIMIT 1")
    fun getNote(surahId: Int, ayahNumber: Int): Flow<NoteEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity)

    @Query("DELETE FROM notes WHERE surahId = :surahId AND ayahNumber = :ayahNumber")
    suspend fun deleteNote(surahId: Int, ayahNumber: Int)

    // --- Last Read ---
    @Query("SELECT * FROM last_read WHERE id = 1 LIMIT 1")
    fun getLastRead(): Flow<LastReadEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setLastRead(lastRead: LastReadEntity)

    // --- Progress ---
    @Query("SELECT * FROM surah_progress")
    fun getAllProgress(): Flow<List<SurahProgressEntity>>

    @Query("SELECT * FROM surah_progress WHERE surahId = :surahId LIMIT 1")
    fun getProgress(surahId: Int): Flow<SurahProgressEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: SurahProgressEntity)
}
