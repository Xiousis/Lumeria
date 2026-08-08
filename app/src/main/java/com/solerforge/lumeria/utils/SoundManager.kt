package com.solerforge.lumeria.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.solerforge.lumeria.R

object SoundManager {
    private var soundPool: SoundPool? = null
    private val soundMap = mutableMapOf<Int, Int>()
    var isEnabled: Boolean = true

    fun init(context: Context) {
        if (soundPool != null) return

        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(10) // Increased for more simultaneous sounds
            .setAudioAttributes(attributes)
            .build()

        // Preload sounds
        loadSound(context, R.raw.hit_001)
        loadSound(context, R.raw.hit_002)
        loadSound(context, R.raw.select_sound)
        loadSound(context, R.raw.monster_hurt)
    }

    private fun loadSound(context: Context, resId: Int) {
        soundPool?.let {
            val soundId = it.load(context, resId, 1)
            soundMap[resId] = soundId
        }
    }

    fun playSound(resId: Int) {
        if (!isEnabled) return
        val soundId = soundMap[resId]
        if (soundId != null) {
            soundPool?.play(soundId, 1.0f, 1.0f, 0, 0, 1.0f)
        }
    }
}
