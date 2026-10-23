package com.example

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.navigation.BottomTab
import com.example.ui.navigation.Screen
import com.example.ui.screens.BookmarksScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.QuranReaderScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.SurahListScreen
import com.example.ui.screens.components.AudioMiniPlayer
import com.example.ui.theme.AlQuranTheme
import com.example.ui.theme.IslamicGold
import com.example.ui.viewmodel.QuranViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: QuranViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val darkModeSetting by viewModel.darkModeSetting.collectAsStateWithLifecycle()
            val keepScreenOn by viewModel.keepScreenOn.collectAsStateWithLifecycle()
            val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()

            // Dynamic Dark/Light Theme Determination
            val isDark = when (darkModeSetting) {
                "dark" -> true
                "light" -> false
                else -> isSystemInDarkTheme()
            }

            // Keep screen on when reading
            LaunchedEffect(keepScreenOn, currentScreen) {
                if (keepScreenOn && currentScreen is Screen.QuranReader) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                } else {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }
            }

            AlQuranTheme(darkTheme = isDark) {
                if (currentScreen is Screen.Splash) {
                    SplashScreen()
                } else {
                    MainAppScaffold(
                        viewModel = viewModel,
                        currentScreen = currentScreen
                    )
                }
            }
        }
    }
}

@Composable
fun MainAppScaffold(
    viewModel: QuranViewModel,
    currentScreen: Screen
) {
    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
    val audioState by viewModel.audioPlaybackState.collectAsStateWithLifecycle()

    // Handle back button on sub-screens
    BackHandler(enabled = currentScreen !is Screen.Home) {
        viewModel.navigateBack()
    }

    val isReader = currentScreen is Screen.QuranReader

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth().navigationBarsPadding()) {
                // Persistent Floating Audio Mini Player
                AudioMiniPlayer(
                    state = audioState,
                    onTogglePlayPause = { viewModel.toggleAudioPlayPause() },
                    onNext = { viewModel.nextAudioAyah() },
                    onPrevious = { viewModel.previousAudioAyah() },
                    onDismiss = { viewModel.dismissAudioPlayer() },
                    onOpenReader = { surahId, ayahNumber ->
                        viewModel.openSurah(surahId, ayahNumber)
                    }
                )

                // Bottom Navigation Bar (Hidden only in reader to maximize reading screen)
                AnimatedVisibility(
                    visible = !isReader,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 6.dp,
                        modifier = Modifier.testTag("bottom_nav_bar")
                    ) {
                        NavigationBarItem(
                            selected = activeTab == BottomTab.HOME,
                            onClick = { viewModel.navigateTab(BottomTab.HOME) },
                            icon = {
                                Icon(
                                    imageVector = if (activeTab == BottomTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                                    contentDescription = "হোম"
                                )
                            },
                            label = { Text("হোম", fontWeight = if (activeTab == BottomTab.HOME) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                            ),
                            modifier = Modifier.testTag("tab_home")
                        )

                        NavigationBarItem(
                            selected = activeTab == BottomTab.QURAN,
                            onClick = { viewModel.navigateTab(BottomTab.QURAN) },
                            icon = {
                                Icon(
                                    imageVector = if (activeTab == BottomTab.QURAN) Icons.Filled.MenuBook else Icons.Outlined.MenuBook,
                                    contentDescription = "কুরআন"
                                )
                            },
                            label = { Text("কুরআন", fontWeight = if (activeTab == BottomTab.QURAN) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                            ),
                            modifier = Modifier.testTag("tab_quran")
                        )

                        NavigationBarItem(
                            selected = activeTab == BottomTab.BOOKMARKS,
                            onClick = { viewModel.navigateTab(BottomTab.BOOKMARKS) },
                            icon = {
                                Icon(
                                    imageVector = if (activeTab == BottomTab.BOOKMARKS) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                    contentDescription = "বুকমার্ক"
                                )
                            },
                            label = { Text("বুকমার্ক", fontWeight = if (activeTab == BottomTab.BOOKMARKS) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                            ),
                            modifier = Modifier.testTag("tab_bookmarks")
                        )

                        NavigationBarItem(
                            selected = activeTab == BottomTab.SEARCH,
                            onClick = { viewModel.navigateTab(BottomTab.SEARCH) },
                            icon = {
                                Icon(
                                    imageVector = if (activeTab == BottomTab.SEARCH) Icons.Filled.Search else Icons.Outlined.Search,
                                    contentDescription = "অনুসন্ধান"
                                )
                            },
                            label = { Text("অনুসন্ধান", fontWeight = if (activeTab == BottomTab.SEARCH) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                            ),
                            modifier = Modifier.testTag("tab_search")
                        )

                        NavigationBarItem(
                            selected = activeTab == BottomTab.SETTINGS,
                            onClick = { viewModel.navigateTab(BottomTab.SETTINGS) },
                            icon = {
                                Icon(
                                    imageVector = if (activeTab == BottomTab.SETTINGS) Icons.Filled.Settings else Icons.Outlined.Settings,
                                    contentDescription = "সেটিংস"
                                )
                            },
                            label = { Text("সেটিংস", fontWeight = if (activeTab == BottomTab.SETTINGS) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                            ),
                            modifier = Modifier.testTag("tab_settings")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                is Screen.Home -> HomeScreen(viewModel = viewModel)
                is Screen.SurahList -> SurahListScreen(viewModel = viewModel)
                is Screen.QuranReader -> QuranReaderScreen(
                    viewModel = viewModel,
                    onBack = { viewModel.navigateBack() }
                )
                is Screen.Bookmarks -> BookmarksScreen(viewModel = viewModel)
                is Screen.Search -> SearchScreen(viewModel = viewModel)
                is Screen.Settings -> SettingsScreen(viewModel = viewModel)
                else -> HomeScreen(viewModel = viewModel)
            }
        }
    }
}
