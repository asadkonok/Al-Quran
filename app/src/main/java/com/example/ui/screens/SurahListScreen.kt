package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.components.SurahCard
import com.example.ui.screens.components.toBengaliDigits
import com.example.ui.theme.IslamicGold
import com.example.ui.viewmodel.QuranViewModel

@Composable
fun SurahListScreen(
    viewModel: QuranViewModel,
    modifier: Modifier = Modifier
) {
    val surahs by viewModel.filteredSurahs.collectAsStateWithLifecycle()
    val filter by viewModel.surahFilter.collectAsStateWithLifecycle()
    val searchQuery by viewModel.surahSearchQuery.collectAsStateWithLifecycle()
    val progressList by viewModel.allProgress.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Screen Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "সূরা তালিকা",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = "মোট ১১৪টি সূরা",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSurahSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("surah_list_search_input"),
                placeholder = {
                    Text(
                        text = "সূরার নাম, অর্থ বা নম্বর দিয়ে খুঁজুন...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "খুঁজুন",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSurahSearchQuery("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "পরিষ্কার করুন",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Filter Chips (All, Makki, Madani)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filter == "ALL",
                    onClick = { viewModel.setSurahFilter("ALL") },
                    label = { Text("সকল সূরা (১১৪)") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = androidx.compose.ui.graphics.Color.White
                    ),
                    modifier = Modifier.testTag("filter_all")
                )
                FilterChip(
                    selected = filter == "MAKKI",
                    onClick = { viewModel.setSurahFilter("MAKKI") },
                    label = { Text("মাক্কী (৮৬)") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = androidx.compose.ui.graphics.Color.White
                    ),
                    modifier = Modifier.testTag("filter_makki")
                )
                FilterChip(
                    selected = filter == "MADANI",
                    onClick = { viewModel.setSurahFilter("MADANI") },
                    label = { Text("মাদানী (২৮)") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = androidx.compose.ui.graphics.Color.White
                    ),
                    modifier = Modifier.testTag("filter_madani")
                )
            }
        }

        // Surah List
        if (surahs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "কোনো সূরা খুঁজে পাওয়া যায়নি",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(surahs, key = { it.id }) { surah ->
                    val progressEntity = progressList.find { it.surahId == surah.id }
                    val progressPercent = if (progressEntity != null && progressEntity.totalAyahs > 0) {
                        ((progressEntity.lastAyahRead.toFloat() / progressEntity.totalAyahs.toFloat()) * 100).toInt()
                    } else 0

                    SurahCard(
                        surah = surah,
                        progressPercent = progressPercent,
                        onClick = {
                            viewModel.openSurah(surah.id)
                        }
                    )
                }
            }
        }
    }
}
