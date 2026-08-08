package com.solerforge.lumeria.utils

import android.content.Context
import android.media.MediaPlayer

object MusicManager {
    private var mediaPlayer: MediaPlayer? = null
    private var currentMusicRes: Int? = null
    var isEnabled: Boolean = true
        set(value) {
            field = value
            if (!value) stopMusic()
        }

    fun playMusic(context: Context, musicRes: Int) {
        if (!isEnabled) return
        if (currentMusicRes == musicRes && mediaPlayer?.isPlaying == true) return

        stopMusic()
        
        try {
            mediaPlayer = MediaPlayer.create(context, musicRes)?.apply {
                isLooping = true
                start()
            }
            if (mediaPlayer != null) {
                currentMusicRes = musicRes
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun pauseMusic() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
        }
    }

    fun resumeMusic() {
        if (mediaPlayer != null && mediaPlayer?.isPlaying == false) {
            mediaPlayer?.start()
        }
    }

    fun stopMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        currentMusicRes = null
    }
}
