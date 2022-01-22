package com.nullvoid.crimson.customs

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import com.nullvoid.crimson.R

class SirenUtil {

    companion object {

        private var mediaPlayer: MediaPlayer? = null
        private var audioManager: AudioManager? = null

        fun start(context: Context) {
            audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            if (mediaPlayer == null) mediaPlayer = MediaPlayer.create(context, R.raw.siren)
            mediaPlayer?.isLooping = true
            mediaPlayer?.setVolume(1f, 1f)
            audioManager?.getStreamMaxVolume(AudioManager.STREAM_MUSIC)?.let {
                audioManager?.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    it, 0
                )
            }
            mediaPlayer?.start()
        }

        fun stop() {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        }

    }
}