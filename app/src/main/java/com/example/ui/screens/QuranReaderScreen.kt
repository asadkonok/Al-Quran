package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Ayah
import com.example.data.model.SurahDetail
import com.example.ui.screens.components.AyahCard
import com.example.ui.screens.components.toBengaliDigits
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.MadaniBadge
import com.example.ui.theme.MakkiBadge
import com.example.ui.theme.QuranArabicFontFamily
import com.example.ui.viewmodel.QuranViewModel
import kotlinx.coroutines.launch

@Composable
fun QuranReaderScreen(
    viewModel: QuranViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onBack()
    }

    val surahDetail by viewModel.currentSurahDetail.collectAsStateWithLifecycle()
    val isLoading by viewModel.isReaderLoading.collectAsStateWithLifecycle()
    val bookmarks by viewModel.bookmarks.collectAsStateWithLifecycle()
    val audioState by viewModel.audioPlaybackState.collectAsStateWithLifecycle()
    val initialAyah by viewModel.readerInitialAyah.collectAsStateWithLifecycle()

    val arabicFontSize by viewModel.arabicFontSize.collectAsStateWithLifecycle()
    val banglaFontSize by viewModel.banglaFontSize.collectAsStateWithLifecycle()
    val showArabic by viewModel.showArabic.collectAsStateWithLifecycle()
    val showTranslation by viewModel.showTranslation.collectAsStateWithLifecycle()

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var showJumpDialog by remember { mutableStateOf(false) }
    var noteTargetAyah by remember { mutableStateOf<Ayah?>(null) }
    var noteInputText by remember { mutableStateOf("") }

    // Scroll to initial ayah when detail loads
    LaunchedEffect(surahDetail, initialAyah) {
        surahDetail?.let {
            if (initialAyah > 1 && initialAyah <= it.ayahs.size) {
                // Header is index 0, so ayah N is at index N
                coroutineScope.launch {
                    listState.animateScrollToItem(initialAyah)
                }
            }
        }
    }

    // Auto-scroll when audio advances
    LaunchedEffect(audioState.ayahNumber) {
        val playingAyah = audioState.ayahNumber
        if (playingAyah != null && audioState.surahId == surahDetail?.id) {
            coroutineScope.launch {
                listState.animateScrollToItem(playingAyah)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("reader_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "ফিরে যান",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                surahDetail?.let { detail ->
                    Column {
                        Text(
                            text = "সূরা ${detail.nameBangla}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "${detail.nameArabic} • ${toBengaliDigits(detail.ayahCount)} আয়াত",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Play Surah from beginning
                surahDetail?.let { detail ->
                    IconButton(
                        onClick = {
                            viewModel.playAyahAudio(
                                surahId = detail.id,
                                ayahNumber = 1,
                                surahNameBangla = detail.nameBangla,
                                surahNameArabic = detail.nameArabic,
                                totalAyahs = detail.ayahCount
                            )
                        },
                        modifier = Modifier.testTag("reader_play_all_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayCircle,
                            contentDescription = "সম্পূর্ণ সূরা শুনুন",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                // Jump to Ayah button
                IconButton(
                    onClick = { showJumpDialog = true },
                    modifier = Modifier.testTag("reader_jump_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.FormatListNumbered,
                        contentDescription = "আয়াতে যান",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

        if (isLoading || surahDetail == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = IslamicGold)
            }
        } else {
            val detail = surahDetail!!

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                // Surah Header Card
                item {
                    SurahHeaderCard(detail = detail)
                }

                // Ayahs
                itemsIndexed(detail.ayahs, key = { _, ayah -> ayah.ayahNumber }) { index, ayah ->
                    val isBookmarked = bookmarks.any { it.surahId == detail.id && it.ayahNumber == ayah.ayahNumber }
                    val isPlaying = audioState.isPlaying && audioState.surahId == detail.id && audioState.ayahNumber == ayah.ayahNumber

                    AyahCard(
                        ayah = ayah,
                        surahId = detail.id,
                        surahNameBangla = detail.nameBangla,
                        surahNameArabic = detail.nameArabic,
                        totalAyahs = detail.ayahCount,
                        isBookmarked = isBookmarked,
                        isPlaying = isPlaying,
                        arabicFontSize = arabicFontSize,
                        banglaFontSize = banglaFontSize,
                        showArabic = showArabic,
                        showTranslation = showTranslation,
                        onToggleBookmark = {
                            viewModel.toggleBookmark(
                                surahId = detail.id,
                                ayahNumber = ayah.ayahNumber,
                                surahNameBangla = detail.nameBangla,
                                surahNameArabic = detail.nameArabic,
                                arabicText = ayah.arabic,
                                banglaText = ayah.bangla
                            )
                        },
                        onPlayAudio = {
                            viewModel.playAyahAudio(
                                surahId = detail.id,
                                ayahNumber = ayah.ayahNumber,
                                surahNameBangla = detail.nameBangla,
                                surahNameArabic = detail.nameArabic,
                                totalAyahs = detail.ayahCount
                            )
                        },
                        onAddNote = {
                            noteTargetAyah = ayah
                            noteInputText = ""
                        }
                    )

                    // Track read position as user scrolls
                    LaunchedEffect(index) {
                        viewModel.updateLastRead(
                            surahId = detail.id,
                            ayahNumber = ayah.ayahNumber,
                            surahNameBangla = detail.nameBangla,
                            surahNameArabic = detail.nameArabic,
                            totalAyahs = detail.ayahCount
                        )
                    }
                }
            }
        }
    }

    // Jump to Ayah Dialog
    if (showJumpDialog && surahDetail != null) {
        var jumpAyahInput by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showJumpDialog = false },
            title = { Text("নির্দিষ্ট আয়াতে যান") },
            text = {
                Column {
                    Text(
                        text = "আয়াত নম্বর লিখুন (১ থেকে ${toBengaliDigits(surahDetail!!.ayahCount)}):",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = jumpAyahInput,
                        onValueChange = { jumpAyahInput = it },
                        singleLine = true,
                        placeholder = { Text("যেমন: ২৫৫") }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val num = jumpAyahInput.toIntOrNull()
                        if (num != null && num in 1..surahDetail!!.ayahCount) {
                            coroutineScope.launch {
                                listState.animateScrollToItem(num)
                            }
                        }
                        showJumpDialog = false
                    }
                ) {
                    Text("যান")
                }
            },
            dismissButton = {
                TextButton(onClick = { showJumpDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }

    // Add Personal Note Dialog
    noteTargetAyah?.let { target ->
        AlertDialog(
            onDismissRequest = { noteTargetAyah = null },
            title = { Text("ব্যক্তিগত নোট যুক্ত করুন") },
            text = {
                Column {
                    Text(
                        text = "সূরা ${surahDetail?.nameBangla} — আয়াত ${target.ayahNumber}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = noteInputText,
                        onValueChange = { noteInputText = it },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        placeholder = { Text("এই আয়াত নিয়ে আপনার ভাবনা বা নোট লিখুন...") }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (noteInputText.isNotBlank() && surahDetail != null) {
                            viewModel.saveNote(
                                surahId = surahDetail!!.id,
                                ayahNumber = target.ayahNumber,
                                surahNameBangla = surahDetail!!.nameBangla,
                                arabicText = target.arabic,
                                banglaText = target.bangla,
                                noteText = noteInputText.trim()
                            )
                        }
                        noteTargetAyah = null
                    }
                ) {
                    Text("সংরক্ষণ")
                }
            },
            dismissButton = {
                TextButton(onClick = { noteTargetAyah = null }) {
                    Text("বাতিল")
                }
            }
        )
    }
}

@Composable
private fun SurahHeaderCard(detail: SurahDetail) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Arabic Calligraphy
            Text(
                text = detail.nameArabic,
                fontFamily = QuranArabicFontFamily,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Bangla Name
            Text(
                text = "সূরা ${detail.nameBangla}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (detail.meaningBangla.isNotEmpty()) {
                Text(
                    text = "অর্থ: ${detail.meaningBangla}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Metadata Chips
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                val isMakki = detail.revelationType.contains("মাক্কী")
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isMakki) MakkiBadge.copy(alpha = 0.15f) else MadaniBadge.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = detail.revelationType,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isMakki) MakkiBadge else MadaniBadge
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${toBengaliDigits(detail.ayahCount)} আয়াত",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Bismillah Banner for Surahs other than At-Tawbah (9)
            if (detail.bismillahPre && detail.id != 9) {
                Spacer(modifier = Modifier.height(18.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(IslamicGold.copy(alpha = 0.12f))
                        .border(1.dp, IslamicGold.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ",
                        fontFamily = QuranArabicFontFamily,
                        fontSize = 26.sp,
                        color = IslamicGold,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
