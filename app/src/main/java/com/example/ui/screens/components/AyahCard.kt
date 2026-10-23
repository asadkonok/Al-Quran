package com.example.ui.screens.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Ayah
import com.example.ui.theme.AudioPlayingGlow
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.QuranArabicFontFamily

@Composable
fun AyahCard(
    ayah: Ayah,
    surahId: Int,
    surahNameBangla: String,
    surahNameArabic: String,
    totalAyahs: Int,
    isBookmarked: Boolean,
    isPlaying: Boolean,
    arabicFontSize: Float,
    banglaFontSize: Float,
    showArabic: Boolean,
    showTranslation: Boolean,
    onToggleBookmark: () -> Unit,
    onPlayAudio: () -> Unit,
    onAddNote: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val cardBgColor by animateColorAsState(
        targetValue = if (isPlaying) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
        } else {
            MaterialTheme.colorScheme.surface
        },
        label = "cardBgColor"
    )

    val borderStroke = if (isPlaying) {
        BorderStroke(1.5.dp, AudioPlayingGlow)
    } else {
        BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("ayah_card_${ayah.ayahNumber}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isPlaying) 4.dp else 1.dp),
        border = borderStroke
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header row: Ayah Badge & Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Ayah badge with Bengali numeral
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(IslamicGold.copy(alpha = 0.15f))
                            .border(1.dp, IslamicGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = toBengaliDigits(ayah.ayahNumber),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "আয়াত ${toBengaliDigits(ayah.ayahNumber)}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Quick action icons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Play audio
                    IconButton(
                        onClick = onPlayAudio,
                        modifier = Modifier.size(36.dp).testTag("play_ayah_${ayah.ayahNumber}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "তেলাওয়াত শুনুন",
                            tint = if (isPlaying) AudioPlayingGlow else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Bookmark
                    IconButton(
                        onClick = onToggleBookmark,
                        modifier = Modifier.size(36.dp).testTag("bookmark_ayah_${ayah.ayahNumber}")
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = if (isBookmarked) "বুকমার্ক সরানো" else "বুকমার্ক করুন",
                            tint = if (isBookmarked) IslamicGold else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Note
                    IconButton(
                        onClick = onAddNote,
                        modifier = Modifier.size(36.dp).testTag("note_ayah_${ayah.ayahNumber}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.EditNote,
                            contentDescription = "নোট যোগ করুন",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Copy
                    IconButton(
                        onClick = {
                            copyAyahText(context, surahNameBangla, ayah)
                        },
                        modifier = Modifier.size(36.dp).testTag("copy_ayah_${ayah.ayahNumber}")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ContentCopy,
                            contentDescription = "কপি করুন",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Share
                    IconButton(
                        onClick = {
                            shareAyah(context, surahNameBangla, ayah)
                        },
                        modifier = Modifier.size(36.dp).testTag("share_ayah_${ayah.ayahNumber}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "শেয়ার করুন",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Arabic Quran text
            if (showArabic) {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = ayah.arabic,
                    fontFamily = QuranArabicFontFamily,
                    fontSize = arabicFontSize.sp,
                    lineHeight = (arabicFontSize * 1.55f).sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Bangla Translation
            if (showTranslation) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = ayah.bangla,
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = banglaFontSize.sp,
                        lineHeight = (banglaFontSize * 1.45f).sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Left,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

fun toBengaliDigits(number: Int): String {
    val bnDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
    return number.toString().map { char ->
        if (char in '0'..'9') bnDigits[char - '0'] else char
    }.joinToString("")
}

private fun copyAyahText(context: Context, surahName: String, ayah: Ayah) {
    val textToCopy = "সূরা $surahName — আয়াত ${ayah.ayahNumber}\n\n${ayah.arabic}\n\n${ayah.bangla}\n\n(আল কুরআন)"
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Quran Ayah", textToCopy)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "কপি করা হয়েছে", Toast.LENGTH_SHORT).show()
}

private fun shareAyah(context: Context, surahName: String, ayah: Ayah) {
    val shareText = "সূরা $surahName — আয়াত ${ayah.ayahNumber}\n\n${ayah.arabic}\n\n${ayah.bangla}\n\nআল কুরআন (বাংলা অনুবাদসহ)"
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareText)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "আয়াত শেয়ার করুন")
    context.startActivity(shareIntent)
}
