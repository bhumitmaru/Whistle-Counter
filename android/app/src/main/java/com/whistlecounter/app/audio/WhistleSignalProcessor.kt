package com.whistlecounter.app.audio

import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.sqrt

/**
 * Pure-Kotlin whistle detector.
 *
 * This mirrors the web app's Web Audio implementation: it watches the 2–4.5 kHz
 * band, requires a strong peak in that band that dominates the rest of the
 * spectrum, and applies a cooldown between counted whistles.
 *
 * It deliberately has no Android dependencies so the detection maths can be
 * verified with plain JVM unit tests.
 */
class WhistleSignalProcessor(
    private val sampleRate: Int = DEFAULT_SAMPLE_RATE,
    private val fftSize: Int = DEFAULT_FFT_SIZE,
    private val minFrequency: Int = DEFAULT_MIN_FREQUENCY,
    private val maxFrequency: Int = DEFAULT_MAX_FREQUENCY,
    private val bandEnergyThreshold: Float = DEFAULT_BAND_ENERGY_THRESHOLD,
    private val peakAmplitudeThreshold: Float = DEFAULT_PEAK_AMPLITUDE_THRESHOLD,
    private val cooldownMs: Long = DEFAULT_COOLDOWN_MS,
    private val smoothing: Float = DEFAULT_SMOOTHING,
) {
    companion object {
        const val DEFAULT_SAMPLE_RATE = 44_100
        const val DEFAULT_FFT_SIZE = 2_048
        const val DEFAULT_MIN_FREQUENCY = 2_000
        const val DEFAULT_MAX_FREQUENCY = 4_500
        const val DEFAULT_BAND_ENERGY_THRESHOLD = 0.15f
        const val DEFAULT_PEAK_AMPLITUDE_THRESHOLD = 0.30f
        const val DEFAULT_COOLDOWN_MS = 10_000L
        const val DEFAULT_SMOOTHING = 0.30f
    }

    data class Result(
        val frequency: Int?,
        val amplitude: Float,
        val bandRatio: Float,
        val whistleDetected: Boolean,
    )

    private val fft = Fft(fftSize)
    private val window = FloatArray(fftSize) { i ->
        (0.5 - 0.5 * cos(2.0 * Math.PI * i / (fftSize - 1))).toFloat()
    }
    private val real = FloatArray(fftSize)
    private val imag = FloatArray(fftSize)
    private val magnitudes = FloatArray(fftSize / 2)
    private val smoothed = FloatArray(fftSize / 2)

    private val binWidth = sampleRate.toDouble() / fftSize
    private val minBin = (minFrequency / binWidth).toInt().coerceAtLeast(1)
    private val maxBin = (maxFrequency / binWidth).toInt().coerceAtMost(fftSize / 2 - 1)

    private var hasSpectrum = false
    private var whistleActive = false
    private var lastWhistleAt = -(cooldownMs + 1)

    /**
     * Process one frame of 16-bit mono PCM.
     *
     * @param samples values in the signed 16-bit range.
     * @param length number of valid samples (must be <= fftSize).
     * @param nowMs monotonic clock in milliseconds, used for the cooldown.
     */
    fun process(samples: ShortArray, length: Int, nowMs: Long): Result {
        require(length in 1..fftSize) { "length must be between 1 and $fftSize" }

        for (i in 0 until fftSize) {
            val sample = if (i < length) samples[i] / 32768f else 0f
            real[i] = sample * window[i]
            imag[i] = 0f
        }
        fft.transform(real, imag)

        var totalPower = 0.0
        var bandPower = 0.0
        var peakMagnitude = 0f
        var peakBin = minBin

        for (bin in 1 until fftSize / 2) {
            val magnitude = 4f * sqrt(real[bin] * real[bin] + imag[bin] * imag[bin]) / fftSize
            magnitudes[bin] = magnitude
            totalPower += magnitude.toDouble() * magnitude

            if (bin in minBin..maxBin) {
                bandPower += magnitude.toDouble() * magnitude
                if (magnitude > peakMagnitude) {
                    peakMagnitude = magnitude
                    peakBin = bin
                }
            }
        }

        // Smoothing keeps a single noisy frame from producing phantom whistles.
        if (!hasSpectrum) {
            magnitudes.copyInto(smoothed)
            hasSpectrum = true
        } else {
            for (bin in 1 until fftSize / 2) {
                smoothed[bin] = smoothing * smoothed[bin] + (1f - smoothing) * magnitudes[bin]
            }
        }

        var smoothTotal = 0.0
        var smoothBand = 0.0
        var smoothPeak = 0f
        var smoothPeakBin = minBin
        for (bin in 1 until fftSize / 2) {
            val power = smoothed[bin].toDouble() * smoothed[bin]
            smoothTotal += power
            if (bin in minBin..maxBin) {
                smoothBand += power
                if (smoothed[bin] > smoothPeak) {
                    smoothPeak = smoothed[bin]
                    smoothPeakBin = bin
                }
            }
        }

        val bandRatio = if (smoothTotal > 1e-12) (smoothBand / smoothTotal).toFloat() else 0f
        val peakDb = 20f * log10(max(smoothPeak, 1e-9f))
        val normalizedAmplitude = ((peakDb + 100f) / 100f).coerceIn(0f, 1f)
        val frequency = (smoothPeakBin * binWidth).toInt()

        val isWhistle = bandRatio >= bandEnergyThreshold &&
            normalizedAmplitude >= peakAmplitudeThreshold &&
            smoothPeakBin in minBin..maxBin

        var counted = false
        if (isWhistle && !whistleActive && nowMs - lastWhistleAt >= cooldownMs) {
            whistleActive = true
            lastWhistleAt = nowMs
            counted = true
        } else if (!isWhistle) {
            whistleActive = false
        }

        return Result(
            frequency = frequency,
            amplitude = normalizedAmplitude,
            bandRatio = bandRatio,
            whistleDetected = counted,
        )
    }

    /** Exposed for tests: level of a bin in the most recent frame. */
    fun magnitudeAt(frequencyHz: Int): Float {
        val bin = (frequencyHz / binWidth).toInt().coerceIn(0, smoothed.size - 1)
        return smoothed[bin]
    }

    fun reset() {
        hasSpectrum = false
        whistleActive = false
        lastWhistleAt = Long.MIN_VALUE
        magnitudes.fill(0f)
        smoothed.fill(0f)
    }
}
