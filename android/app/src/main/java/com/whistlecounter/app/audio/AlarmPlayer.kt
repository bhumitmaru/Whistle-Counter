package com.whistlecounter.app.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.media.ToneGenerator
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * Plays the phone's own alarm sound on the alarm audio stream, with vibration
 * and a generated-tone fallback. Everything happens locally on the device.
 */
class AlarmPlayer(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private var toneGenerator: ToneGenerator? = null
    private var vibrator: Vibrator? = null
    private val handler = Handler(Looper.getMainLooper())

    private val fallbackBeep = object : Runnable {
        override fun run() {
            runCatching {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP2, 320)
            }
            handler.postDelayed(this, 700)
        }
    }

    fun start() {
        stop()

        if (!startSystemAlarm()) {
            startFallbackTone()
        }
        startVibration()
    }

    fun stop() {
        handler.removeCallbacks(fallbackBeep)

        runCatching {
            mediaPlayer?.let {
                if (it.isPlaying) it.stop()
                it.release()
            }
        }
        mediaPlayer = null

        runCatching { toneGenerator?.release() }
        toneGenerator = null

        runCatching { vibrator?.cancel() }
        vibrator = null
    }

    private fun startSystemAlarm(): Boolean {
        val uri = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: return false

        return try {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build(),
                )
                setDataSource(context, uri)
                isLooping = true
                prepare()
                setOnErrorListener { _, _, _ ->
                    startFallbackTone()
                    true
                }
                start()
            }
            true
        } catch (e: Exception) {
            runCatching { mediaPlayer?.release() }
            mediaPlayer = null
            false
        }
    }

    private fun startFallbackTone() {
        if (toneGenerator != null) return
        runCatching {
            toneGenerator = ToneGenerator(AudioManager.STREAM_ALARM, 90)
            handler.post(fallbackBeep)
        }
    }

    @Suppress("DEPRECATION")
    private fun startVibration() {
        val vib = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(VibratorManager::class.java)
            manager?.defaultVibrator
        } else {
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        } ?: return

        if (!vib.hasVibrator()) return
        vibrator = vib

        val pattern = longArrayOf(0, 700, 350, 700, 350, 700, 600)
        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vib.vibrate(VibrationEffect.createWaveform(pattern, 0))
            } else {
                @Suppress("DEPRECATION")
                vib.vibrate(pattern, 0)
            }
        }
    }
}
