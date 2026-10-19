package com.whistlecounter.app.audio

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.PI
import kotlin.math.sin

class WhistleSignalProcessorTest {

    private val sampleRate = 44_100
    private val frameSize = 2_048

    private fun tone(frequencyHz: Double, amplitude: Double = 0.5): ShortArray {
        return ShortArray(frameSize) { i ->
            (amplitude * Short.MAX_VALUE * sin(2.0 * PI * frequencyHz * i / sampleRate)).toInt().toShort()
        }
    }

    private fun silence(): ShortArray = ShortArray(frameSize)

    private fun noise(amplitude: Double = 0.2): ShortArray {
        val random = java.util.Random(42)
        return ShortArray(frameSize) {
            ((random.nextDouble() * 2.0 - 1.0) * amplitude * Short.MAX_VALUE).toInt().toShort()
        }
    }

    @Test
    fun `a loud 3 kHz tone is counted after a few frames`() {
        val processor = WhistleSignalProcessor(sampleRate = sampleRate)
        val signal = tone(3_000.0)
        var detected = false
        var time = 1_000L

        repeat(6) {
            val result = processor.process(signal, frameSize, time)
            time += 100
            if (result.whistleDetected) detected = true
        }

        assertTrue("expected the 3 kHz tone to be detected", detected)
    }

    @Test
    fun `a tone below the whistle band is not counted`() {
        val processor = WhistleSignalProcessor(sampleRate = sampleRate)
        val signal = tone(300.0, amplitude = 0.9)
        var detected = false
        var time = 1_000L

        repeat(10) {
            if (processor.process(signal, frameSize, time).whistleDetected) detected = true
            time += 100
        }

        assertFalse("a low hum must not count as a whistle", detected)
    }

    @Test
    fun `broadband noise is rejected`() {
        val processor = WhistleSignalProcessor(sampleRate = sampleRate)
        val signal = noise(amplitude = 0.4)
        var detected = false
        var time = 1_000L

        repeat(10) {
            if (processor.process(signal, frameSize, time).whistleDetected) detected = true
            time += 100
        }

        assertFalse("white noise must not count as a whistle", detected)
    }

    @Test
    fun `silence never counts`() {
        val processor = WhistleSignalProcessor(sampleRate = sampleRate)
        val signal = silence()
        var detected = false

        repeat(10) {
            if (processor.process(signal, frameSize, it * 100L).whistleDetected) detected = true
        }

        assertFalse(detected)
    }

    @Test
    fun `cooldown stops a sustained tone from counting repeatedly`() {
        val processor = WhistleSignalProcessor(sampleRate = sampleRate, cooldownMs = 10_000)
        val signal = tone(3_000.0)
        var count = 0
        var time = 1_000L

        // One second of audio while the cooker keeps whistling.
        repeat(10) {
            if (processor.process(signal, frameSize, time).whistleDetected) count++
            time += 100
        }

        assertTrue("the initial whistle should count", count >= 1)
        assertTrue("a sustained tone should be counted once, not ten times", count <= 2)
    }

    @Test
    fun `a new whistle counts after the cooldown expires`() {
        val processor = WhistleSignalProcessor(sampleRate = sampleRate, cooldownMs = 10_000)
        val signal = tone(3_000.0)
        var time = 1_000L

        var first = false
        repeat(6) {
            if (processor.process(signal, frameSize, time).whistleDetected) first = true
            time += 100
        }
        assertTrue(first)

        // Silence lets the smoothed spectrum decay and the detector re-arm.
        repeat(14) {
            processor.process(silence(), frameSize, time)
            time += 100
        }

        // Still inside the cooldown window.
        var early = false
        repeat(6) {
            if (processor.process(signal, frameSize, time).whistleDetected) early = true
            time += 100
        }
        assertFalse("cooldown should still be active", early)

        // After 10 seconds a fresh whistle is allowed.
        time += 10_000
        var later = false
        repeat(6) {
            if (processor.process(signal, frameSize, time).whistleDetected) later = true
            time += 100
        }
        assertTrue("a new whistle after cooldown should count", later)
    }
}
