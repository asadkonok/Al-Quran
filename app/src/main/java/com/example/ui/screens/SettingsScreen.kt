package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.ui.screens.components.toBengaliDigits
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.QuranArabicFontFamily
import com.example.ui.viewmodel.QuranViewModel

@Composable
fun SettingsScreen(
    viewModel: QuranViewModel,
    modifier: Modifier = Modifier
) {
    val arabicFontSize by viewModel.arabicFontSize.collectAsStateWithLifecycle()
    val banglaFontSize by viewModel.banglaFontSize.collectAsStateWithLifecycle()
    val darkModeSetting by viewModel.darkModeSetting.collectAsStateWithLifecycle()
    val showTranslation by viewModel.showTranslation.collectAsStateWithLifecycle()
    val showArabic by viewModel.showArabic.collectAsStateWithLifecycle()
    val keepScreenOn by viewModel.keepScreenOn.collectAsStateWithLifecycle()
    val autoPlayNext by viewModel.autoPlayNext.collectAsStateWithLifecycle()

    var showResetDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        item {
            Column {
                Text(
                    text = "সেটিংস",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "ফন্ট সাইজ, থিম ও পাঠের পছন্দসমূহ নির্ধারণ করুন",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Appearance / Theme
        item {
            SettingsCard(title = "থিম ও ডিসপ্লে") {
                Text(
                    text = "অ্যাপের থিম নির্বাচন করুন",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = darkModeSetting == "system",
                        onClick = { viewModel.setDarkModeSetting("system") },
                        label = { Text("সিস্টেম ডিফল্ট") },
                        modifier = Modifier.testTag("theme_system")
                    )
                    FilterChip(
                        selected = darkModeSetting == "light",
                        onClick = { viewModel.setDarkModeSetting("light") },
                        label = { Text("লাইট মোড") },
                        modifier = Modifier.testTag("theme_light")
                    )
                    FilterChip(
                        selected = darkModeSetting == "dark",
                        onClick = { viewModel.setDarkModeSetting("dark") },
                        label = { Text("ডার্ক মোড") },
                        modifier = Modifier.testTag("theme_dark")
                    )
                }
            }
        }

        // Font Size Controls & Live Preview
        item {
            SettingsCard(title = "ফন্ট সাইজ ও টাইপোগ্রাফি") {
                // Arabic Font Size
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "আরবি ফন্ট সাইজ",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${toBengaliDigits(arabicFontSize.toInt())} sp",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Slider(
                    value = arabicFontSize,
                    onValueChange = { viewModel.setArabicFontSize(it) },
                    valueRange = 22f..44f,
                    steps = 11,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.testTag("slider_arabic_font")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Bangla Font Size
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "বাংলা ফন্ট সাইজ",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${toBengaliDigits(banglaFontSize.toInt())} sp",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Slider(
                    value = banglaFontSize,
                    onValueChange = { viewModel.setBanglaFontSize(it) },
                    valueRange = 14f..26f,
                    steps = 6,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.testTag("slider_bangla_font")
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Live Preview Card
                Text(
                    text = "লাইভ প্রিভিউ:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(14.dp)
                ) {
                    Column {
                        Text(
                            text = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ",
                            fontFamily = QuranArabicFontFamily,
                            fontSize = arabicFontSize.sp,
                            lineHeight = (arabicFontSize * 1.5f).sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "শুরু করছি আল্লাহর নামে যিনি পরম করুণাময়, অতি দয়ালু।",
                            style = MaterialTheme.typography.bodyLarge,
                            fontSize = banglaFontSize.sp,
                            lineHeight = (banglaFontSize * 1.4f).sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Reading Content Toggles
        item {
            SettingsCard(title = "পাঠের পছন্দসমূহ") {
                ToggleRow(
                    title = "বাংলা অনুবাদ প্রদর্শন করুন",
                    subtitle = "আয়াত পাঠের সাথে বাংলা অর্থ দেখান",
                    checked = showTranslation,
                    onCheckedChange = { viewModel.setShowTranslation(it) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                ToggleRow(
                    title = "আরবি হরফ প্রদর্শন করুন",
                    subtitle = "মূল আরবি পাঠ প্রদর্শন করুন",
                    checked = showArabic,
                    onCheckedChange = { viewModel.setShowArabic(it) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                ToggleRow(
                    title = "পড়ার সময় স্ক্রিন সচল রাখুন",
                    subtitle = "পাঠকালে স্ক্রিনের আলো নিভে যাবে না",
                    checked = keepScreenOn,
                    onCheckedChange = { viewModel.setKeepScreenOn(it) }
                )
            }
        }

        // Audio Settings
        item {
            SettingsCard(title = "অডিও তেলাওয়াত") {
                Text(
                    text = "ডিফল্ট ক্বারী: মিশারী রশীদ আল-আফাসী (Mishary Rashid Alafasy)",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(12.dp))
                ToggleRow(
                    title = "পরবর্তী আয়াত অটো-প্লে",
                    subtitle = "একটি আয়াত শেষ হলে স্বয়ংক্রিয়ভাবে পরের আয়াত চলবে",
                    checked = autoPlayNext,
                    onCheckedChange = { viewModel.setAutoPlayNext(it) }
                )
            }
        }

        // Translation Source & Information
        item {
            SettingsCard(title = "অনুবাদ ও তথ্যসূত্র") {
                Text(
                    text = "বাংলা অনুবাদ: মাওলানা মুহিউদ্দীন খান",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "মদীনা মুনাওয়ারা কিং ফাহাদ কুরআন প্রিন্টিং কমপ্লেক্স দ্বারা অনুমোদিত ও বহুল সমাদৃত খাঁটি বঙ্গানুবাদ।",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "আরবি পাঠ: আল-কুরআনুল কারীম (উসমানী লিপি)",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Reset Settings
        item {
            OutlinedButton(
                onClick = { showResetDialog = true },
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reset_settings_button")
            ) {
                Text(text = "সেটিংস রিসেট করুন")
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("সেটিংস রিসেট করবেন?") },
            text = { Text("আপনার ফন্ট সাইজ এবং পাঠের পছন্দসমূহ ডিফল্ট অবস্থায় ফিরে যাবে।") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetPreferences()
                        showResetDialog = false
                    }
                ) {
                    Text("রিসেট", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }
}

@Composable
private fun SettingsCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(14.dp))
            content()
        }
    }
}

@Composable
private fun ToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}
