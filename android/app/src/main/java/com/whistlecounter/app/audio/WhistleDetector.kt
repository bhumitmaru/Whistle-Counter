package com.whistlecounter.app.audio

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Process
import android.os.SystemClock
import kotlin.math.max

/**
 * Reads live microphone audio and feeds fixed-size frames to the detector.
 *
 * The foreground service owns this class, which is what lets detection keep
 * running while the screen is off or another app is open.
 */
class WhistleDetector(
    private val onLevel: (frequency: Int?, amplitude: Float) -> Unit,
    private val onWhistle: () -> Unit,
    private val onError: (String) -> Unit,
) {
    companion object {
        private const val SAMPLE_RATE = 44_100
        private const val FFT_SIZE = 2_048
        private const val LEVEL_UPDATE_INTERVAL_MS = 45L
    }

    private val processor = WhistleSignalProcessor(sampleRate = SAMPLE_RATE, fftSize = FFT_SIZE)

    @Volatile
    private var running = false

    @Volatile
    private var audioRecord: AudioRecord? = null

    private var thread: Thread? = null

    @SuppressLint("MissingPermission") // Permission is checked by the service before starting.
    fun start() {
        if (running) return
        running = true
        thread = Thread({ recordLoop() }, "whistle-audio").apply {
            priority = Thread.MAX_PRIORITY
            start()
        }
    }

    fun stop() {
        running = false
        runCatching { audioRecord?.stop() }
        runCatching { thread?.join(800) }
        thread = null
        runCatching { audioRecord?.release() }
        audioRecord = null
        processor.reset()
    }

    fun isRunning(): Boolean = running

    private fun recordLoop() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO)

        val minBuffer = AudioRecord.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
        )
        if (minBuffer <= 0) {
            running = false
            onError("This phone could not create a microphone audio buffer.")
            return
        }

        val record = try {
            AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                max(minBuffer, FFT_SIZE * 2 * 4),
            )
        } catch (e: Exception) {
            running = false
            onError(e.message ?: "Could not open the microphone.")
            return
        }

        if (record.state != AudioRecord.STATE_INITIALIZED) {
            record.release()
            running = false
            onError("The microphone could not be initialised.")
            return
        }

        audioRecord = record
        val frame = ShortArray(FFT_SIZE)
        var lastLevelUpdate = 0L

        try {
            record.startRecording()
            if (record.recordingState != AudioRecord.RECORDSTATE_RECORDING) {
                running = false
                onError("The microphone did not start recording.")
                return
            }

            while (running) {
                val read = record.read(frame, 0, frame.size, AudioRecord.READ_BLOCKING)
                if (read > 0) {
                    val result = processor.process(frame, read, SystemClock.elapsedRealtime())
                    val now = SystemClock.elapsedRealtime()
                    if (now - lastLevelUpdate >= LEVEL_UPDATE_INTERVAL_MS) {
                        lastLevelUpdate = now
                        onLevel(result.frequency, result.amplitude)
                    }
                    if (result.whistleDetected) onWhistle()
                } else if (read < 0) {
                    if (running) onError("The microphone stopped responding.")
                    running = false
                    return
                }
            }
        } catch (e: SecurityException) {
            if (running) onError("Microphone permission was removed while listening.")
            running = false
        } catch (e: Exception) {
            if (running) onError(e.message ?: "Microphone listening failed.")
            running = false
        } finally {
            runCatching { record.stop() }
        }
    }
}
