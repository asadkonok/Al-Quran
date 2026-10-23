package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AudioPlaybackState(
    val isPlaying: Boolean = false,
    val isLoading: Boolean = false,
    val surahId: Int? = null,
    val ayahNumber: Int? = null,
    val surahNameBangla: String = "",
    val surahNameArabic: String = "",
    val totalAyahs: Int = 0,
    val currentPositionMs: Int = 0,
    val durationMs: Int = 0,
    val reciterName: String = "মিশারী রশীদ আল-আফাসী",
    val errorMessage: String? = null
)

class QuranAudioPlayer(
    private val context: Context,
    private val onAyahChanged: ((surahId: Int, ayahNumber: Int) -> Unit)? = null
) {
    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())

    private val _playbackState = MutableStateFlow(AudioPlaybackState())
    val playbackState: StateFlow<AudioPlaybackState> = _playbackState.asStateFlow()

    var autoPlayNext: Boolean = true

    private val progressRunnable = object : Runnable {
        override fun run() {
            mediaPlayer?.let { player ->
                if (player.isPlaying) {
                    val pos = player.currentPosition
                    val dur = player.duration
                    _playbackState.value = _playbackState.value.copy(
                        currentPositionMs = pos,
                        durationMs = if (dur > 0) dur else _playbackState.value.durationMs
                    )
                    handler.postDelayed(this, 500)
                }
            }
        }
    }

    fun playAyah(
        surahId: Int,
        ayahNumber: Int,
        surahNameBangla: String,
        surahNameArabic: String,
        totalAyahs: Int
    ) {
        stop()
        val sStr = String.format("%03d", surahId)
        val aStr = String.format("%03d", ayahNumber)
        val audioUrl = "https://everyayah.com/data/Alafasy_128kbps/${sStr}${aStr}.mp3"

        _playbackState.value = AudioPlaybackState(
            isPlaying = false,
            isLoading = true,
            surahId = surahId,
            ayahNumber = ayahNumber,
            surahNameBangla = surahNameBangla,
            surahNameArabic = surahNameArabic,
            totalAyahs = totalAyahs,
            errorMessage = null
        )

        try {
            val player = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(audioUrl)
                setOnPreparedListener { mp ->
                    mp.start()
                    _playbackState.value = _playbackState.value.copy(
                        isPlaying = true,
                        isLoading = false,
                        durationMs = mp.duration
                    )
                    handler.post(progressRunnable)
                    onAyahChanged?.invoke(surahId, ayahNumber)
                }
                setOnCompletionListener {
                    handler.removeCallbacks(progressRunnable)
                    if (autoPlayNext && ayahNumber < totalAyahs) {
                        playAyah(surahId, ayahNumber + 1, surahNameBangla, surahNameArabic, totalAyahs)
                    } else {
                        _playbackState.value = _playbackState.value.copy(
                            isPlaying = false,
                            currentPositionMs = 0
                        )
                    }
                }
                setOnErrorListener { _, _, _ ->
                    handler.removeCallbacks(progressRunnable)
                    _playbackState.value = _playbackState.value.copy(
                        isPlaying = false,
                        isLoading = false,
                        errorMessage = "অডিও প্লেব্যাকের সময় ত্রুটি ঘটেছে"
                    )
                    true
                }
                prepareAsync()
            }
            mediaPlayer = player
        } catch (e: Exception) {
            e.printStackTrace()
            _playbackState.value = _playbackState.value.copy(
                isPlaying = false,
                isLoading = false,
                errorMessage = "অডিও সংযোগ ব্যর্থ হয়েছে"
            )
        }
    }

    fun togglePlayPause() {
        val state = _playbackState.value
        mediaPlayer?.let { player ->
            if (player.isPlaying) {
                player.pause()
                handler.removeCallbacks(progressRunnable)
                _playbackState.value = state.copy(isPlaying = false)
            } else {
                player.start()
                handler.post(progressRunnable)
                _playbackState.value = state.copy(isPlaying = true)
            }
        } ?: run {
            state.surahId?.let { sId ->
                state.ayahNumber?.let { aNum ->
                    playAyah(sId, aNum, state.surahNameBangla, state.surahNameArabic, state.totalAyahs)
                }
            }
        }
    }

    fun nextAyah() {
        val state = _playbackState.value
        val sId = state.surahId ?: return
        val aNum = state.ayahNumber ?: return
        if (aNum < state.totalAyahs) {
            playAyah(sId, aNum + 1, state.surahNameBangla, state.surahNameArabic, state.totalAyahs)
        }
    }

    fun previousAyah() {
        val state = _playbackState.value
        val sId = state.surahId ?: return
        val aNum = state.ayahNumber ?: return
        if (aNum > 1) {
            playAyah(sId, aNum - 1, state.surahNameBangla, state.surahNameArabic, state.totalAyahs)
        }
    }

    fun seekTo(positionMs: Int) {
        mediaPlayer?.seekTo(positionMs)
        _playbackState.value = _playbackState.value.copy(currentPositionMs = positionMs)
    }

    fun stop() {
        handler.removeCallbacks(progressRunnable)
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mediaPlayer = null
        _playbackState.value = _playbackState.value.copy(isPlaying = false, isLoading = false)
    }

    fun dismissPlayer() {
        stop()
        _playbackState.value = AudioPlaybackState()
    }
}
