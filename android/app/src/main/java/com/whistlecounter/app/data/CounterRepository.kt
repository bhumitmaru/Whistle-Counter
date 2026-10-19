package com.whistlecounter.app.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Single source of truth shared by the UI and the foreground service.
 * Both live in the same process, so a plain in-memory StateFlow is enough,
 * and it keeps the UI snappy without database or IPC overhead.
 */
data class CounterUiState(
    val isListening: Boolean = false,
    val isServiceRunning: Boolean = false,
    val whistleCount: Int = 0,
    val currentFrequency: Int? = null,
    val currentAmplitude: Float = 0f,
    val isWhistleDetected: Boolean = false,
    val isAlarmTriggered: Boolean = false,
    val alarmEnabled: Boolean = true,
    val whistleLimit: Int = 3,
    val error: String? = null,
)

object CounterRepository {
    private val _state = MutableStateFlow(CounterUiState())
    val state: StateFlow<CounterUiState> = _state.asStateFlow()

    val current: CounterUiState
        get() = _state.value

    fun update(transform: (CounterUiState) -> CounterUiState) {
        _state.update(transform)
    }

    fun updateLevel(frequency: Int?, amplitude: Float) {
        _state.update {
            it.copy(
                currentFrequency = frequency,
                currentAmplitude = amplitude.coerceIn(0f, 1f),
            )
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    fun resetRuntime() {
        _state.update {
            it.copy(
                isListening = false,
                isServiceRunning = false,
                isWhistleDetected = false,
                currentFrequency = null,
                currentAmplitude = 0f,
            )
        }
    }
}
