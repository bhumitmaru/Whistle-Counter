package com.whistlecounter.app.audio

/**
 * In-place iterative radix-2 Cooley-Tukey FFT.
 *
 * The input arrays must have a power-of-two length. [transform] fills [imag]
 * with zeros before transforming, so callers only need to supply real samples.
 */
internal class Fft(private val size: Int) {
    init {
        require(size > 1 && size and (size - 1) == 0) { "FFT size must be a power of two" }
    }

    private val cosTable = FloatArray(size / 2) {
        kotlin.math.cos(-2.0 * Math.PI * it / size).toFloat()
    }
    private val sinTable = FloatArray(size / 2) {
        kotlin.math.sin(-2.0 * Math.PI * it / size).toFloat()
    }
    private val reversed = IntArray(size) { Integer.reverse(it) ushr (32 - Integer.numberOfTrailingZeros(size)) }

    fun transform(real: FloatArray, imag: FloatArray) {
        require(real.size == size && imag.size == size)

        for (i in 0 until size) {
            val j = reversed[i]
            if (j > i) {
                val tr = real[i]
                real[i] = real[j]
                real[j] = tr
                val ti = imag[i]
                imag[i] = imag[j]
                imag[j] = ti
            }
        }

        var length = 2
        while (length <= size) {
            val half = length / 2
            val step = size / length
            var start = 0
            while (start < size) {
                var k = 0
                for (i in start until start + half) {
                    val j = i + half
                    val cos = cosTable[k]
                    val sin = sinTable[k]
                    val tre = real[j] * cos - imag[j] * sin
                    val tim = real[j] * sin + imag[j] * cos
                    real[j] = real[i] - tre
                    imag[j] = imag[i] - tim
                    real[i] += tre
                    imag[i] += tim
                    k += step
                }
                start += length
            }
            length = length shl 1
        }
    }
}
