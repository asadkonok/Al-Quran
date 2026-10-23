package com.example.data.repository

import android.content.Context
import com.example.data.local.BookmarkEntity
import com.example.data.local.LastReadEntity
import com.example.data.local.NoteEntity
import com.example.data.local.QuranDao
import com.example.data.local.SurahProgressEntity
import com.example.data.model.Ayah
import com.example.data.model.DailyAyah
import com.example.data.model.SearchResult
import com.example.data.model.SurahDetail
import com.example.data.model.SurahMeta
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Calendar

class QuranRepository(
    private val context: Context,
    private val quranDao: QuranDao
) {
    private var cachedSurahsMeta: List<SurahMeta>? = null
    private val surahCache = mutableMapOf<Int, SurahDetail>()
    private var cachedDailyAyahs: List<DailyAyah>? = null

    // Room reactive streams
    val allBookmarks: Flow<List<BookmarkEntity>> = quranDao.getAllBookmarks()
    val allNotes: Flow<List<NoteEntity>> = quranDao.getAllNotes()
    val lastRead: Flow<LastReadEntity?> = quranDao.getLastRead()
    val allProgress: Flow<List<SurahProgressEntity>> = quranDao.getAllProgress()

    fun isBookmarked(surahId: Int, ayahNumber: Int): Flow<Boolean> {
        return quranDao.isBookmarked(surahId, ayahNumber)
    }

    suspend fun getSurahsMeta(): List<SurahMeta> = withContext(Dispatchers.IO) {
        cachedSurahsMeta?.let { return@withContext it }
        val list = mutableListOf<SurahMeta>()
        try {
            val jsonString = readAssetFile("quran/surahs_meta.json")
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    SurahMeta(
                        id = obj.getInt("id"),
                        nameArabic = obj.getString("nameArabic"),
                        nameBangla = obj.getString("nameBangla"),
                        nameEnglish = obj.getString("nameEnglish"),
                        meaningBangla = obj.optString("meaningBangla", ""),
                        revelationType = obj.getString("revelationType"),
                        ayahCount = obj.getInt("ayahCount"),
                        bismillahPre = obj.optBoolean("bismillahPre", true)
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        cachedSurahsMeta = list
        list
    }

    suspend fun getSurahDetail(surahId: Int): SurahDetail? = withContext(Dispatchers.IO) {
        surahCache[surahId]?.let { return@withContext it }
        try {
            val jsonString = readAssetFile("quran/surahs/$surahId.json")
            val obj = JSONObject(jsonString)
            val ayahsArray = obj.getJSONArray("ayahs")
            val ayahsList = mutableListOf<Ayah>()
            for (i in 0 until ayahsArray.length()) {
                val aObj = ayahsArray.getJSONObject(i)
                val cleanArabic = aObj.getString("arabic")
                    .replace("\ufeff", "")
                    .trim()
                ayahsList.add(
                    Ayah(
                        ayahNumber = aObj.getInt("ayahNumber"),
                        arabic = cleanArabic,
                        bangla = aObj.getString("bangla").trim()
                    )
                )
            }
            val detail = SurahDetail(
                id = obj.getInt("id"),
                nameArabic = obj.getString("nameArabic"),
                nameBangla = obj.getString("nameBangla"),
                nameEnglish = obj.getString("nameEnglish"),
                meaningBangla = obj.optString("meaningBangla", ""),
                revelationType = obj.getString("revelationType"),
                ayahCount = obj.getInt("ayahCount"),
                bismillahPre = obj.optBoolean("bismillahPre", true),
                ayahs = ayahsList
            )
            surahCache[surahId] = detail
            detail
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getDailyAyah(): DailyAyah = withContext(Dispatchers.IO) {
        if (cachedDailyAyahs == null) {
            val list = mutableListOf<DailyAyah>()
            try {
                val jsonString = readAssetFile("quran/daily_ayahs.json")
                val arr = JSONArray(jsonString)
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    list.add(
                        DailyAyah(
                            surahId = o.getInt("surahId"),
                            surahNameBangla = o.getString("surahNameBangla"),
                            surahNameArabic = o.getString("surahNameArabic"),
                            ayahNumber = o.getInt("ayahNumber"),
                            arabic = o.getString("arabic"),
                            bangla = o.getString("bangla"),
                            contextTitle = o.getString("contextTitle")
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            cachedDailyAyahs = list
        }
        val list = cachedDailyAyahs ?: emptyList()
        if (list.isNotEmpty()) {
            val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
            list[dayOfYear % list.size]
        } else {
            DailyAyah(
                surahId = 1,
                surahNameBangla = "আল-ফাতিহা",
                surahNameArabic = "سُورَةُ ٱلْفَاتِحَةِ",
                ayahNumber = 1,
                arabic = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ",
                bangla = "শুরু করছি আল্লাহর নামে যিনি পরম করুণাময়, অতি দয়ালু।",
                contextTitle = "আল্লাহর দয়া ও করুণা"
            )
        }
    }

    suspend fun searchQuran(query: String): List<SearchResult> = withContext(Dispatchers.IO) {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return@withContext emptyList()
        val results = mutableListOf<SearchResult>()
        val metas = getSurahsMeta()

        // 1. Search Surah names first
        for (meta in metas) {
            if (meta.nameBangla.lowercase().contains(q) ||
                meta.nameEnglish.lowercase().contains(q) ||
                meta.nameArabic.contains(q) ||
                meta.meaningBangla.lowercase().contains(q) ||
                meta.id.toString() == q
            ) {
                val detail = getSurahDetail(meta.id)
                val firstAyah = detail?.ayahs?.firstOrNull()
                results.add(
                    SearchResult(
                        surahId = meta.id,
                        surahNameBangla = meta.nameBangla,
                        surahNameArabic = meta.nameArabic,
                        ayahNumber = 1,
                        arabic = firstAyah?.arabic ?: "",
                        bangla = firstAyah?.bangla ?: meta.meaningBangla,
                        matchSnippet = "সূরা ${meta.nameBangla} (${meta.meaningBangla})"
                    )
                )
            }
        }

        // 2. Search inside Ayahs across popular/key Surahs or matching queried keywords
        val searchLimit = 50
        for (meta in metas) {
            if (results.size >= searchLimit) break
            val detail = getSurahDetail(meta.id) ?: continue
            for (ayah in detail.ayahs) {
                if (results.size >= searchLimit) break
                if (ayah.bangla.lowercase().contains(q) || ayah.arabic.contains(q)) {
                    // Avoid duplicate if already added as first ayah match
                    val alreadyAdded = results.any { it.surahId == meta.id && it.ayahNumber == ayah.ayahNumber }
                    if (!alreadyAdded) {
                        results.add(
                            SearchResult(
                                surahId = meta.id,
                                surahNameBangla = meta.nameBangla,
                                surahNameArabic = meta.nameArabic,
                                ayahNumber = ayah.ayahNumber,
                                arabic = ayah.arabic,
                                bangla = ayah.bangla,
                                matchSnippet = ayah.bangla
                            )
                        )
                    }
                }
            }
        }

        results
    }

    // Room operations
    suspend fun toggleBookmark(
        surahId: Int,
        ayahNumber: Int,
        surahNameBangla: String,
        surahNameArabic: String,
        arabicText: String,
        banglaText: String,
        isCurrentlyBookmarked: Boolean
    ) = withContext(Dispatchers.IO) {
        if (isCurrentlyBookmarked) {
            quranDao.deleteBookmark(surahId, ayahNumber)
        } else {
            quranDao.insertBookmark(
                BookmarkEntity(
                    surahId = surahId,
                    ayahNumber = ayahNumber,
                    surahNameBangla = surahNameBangla,
                    surahNameArabic = surahNameArabic,
                    arabicText = arabicText,
                    banglaText = banglaText
                )
            )
        }
    }

    suspend fun saveNote(
        surahId: Int,
        ayahNumber: Int,
        surahNameBangla: String,
        arabicText: String,
        banglaText: String,
        noteText: String
    ) = withContext(Dispatchers.IO) {
        quranDao.insertNote(
            NoteEntity(
                surahId = surahId,
                ayahNumber = ayahNumber,
                surahNameBangla = surahNameBangla,
                arabicText = arabicText,
                banglaText = banglaText,
                noteText = noteText,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun deleteNote(surahId: Int, ayahNumber: Int) = withContext(Dispatchers.IO) {
        quranDao.deleteNote(surahId, ayahNumber)
    }

    suspend fun updateLastRead(
        surahId: Int,
        ayahNumber: Int,
        surahNameBangla: String,
        surahNameArabic: String,
        totalAyahs: Int
    ) = withContext(Dispatchers.IO) {
        quranDao.setLastRead(
            LastReadEntity(
                id = 1,
                surahId = surahId,
                ayahNumber = ayahNumber,
                surahNameBangla = surahNameBangla,
                surahNameArabic = surahNameArabic,
                totalAyahs = totalAyahs,
                updatedAt = System.currentTimeMillis()
            )
        )
        // Also update progress
        val isCompleted = ayahNumber >= totalAyahs
        quranDao.saveProgress(
            SurahProgressEntity(
                surahId = surahId,
                lastAyahRead = ayahNumber,
                totalAyahs = totalAyahs,
                isCompleted = isCompleted,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    private fun readAssetFile(fileName: String): String {
        val inputStream = context.assets.open(fileName)
        val reader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
        val sb = StringBuilder()
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            sb.append(line)
        }
        reader.close()
        return sb.toString()
    }
}
