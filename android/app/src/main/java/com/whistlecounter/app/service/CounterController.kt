package com.whistlecounter.app.service

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.whistlecounter.app.data.CounterRepository
import com.whistlecounter.app.data.WhistlePrefs

/**
 * Small facade the Compose UI uses to talk to the foreground service.
 * Commands are only sent while the service is actually running; otherwise
 * the local state is updated directly so the UI never waits on a dead service.
 */
object CounterController {
    const val ACTION_START = "com.whistlecounter.app.action.START"
    const val ACTION_STOP = "com.whistlecounter.app.action.STOP"
    const val ACTION_RESET = "com.whistlecounter.app.action.RESET"
    const val ACTION_DISMISS_ALARM = "com.whistlecounter.app.action.DISMISS_ALARM"
    const val ACTION_LIMIT_CHANGED = "com.whistlecounter.app.action.LIMIT_CHANGED"
    const val ACTION_ALARM_TOGGLED = "com.whistlecounter.app.action.ALARM_TOGGLED"

    fun start(context: Context) {
        val state = CounterRepository.current
        if (state.whistleCount >= state.whistleLimit) {
            CounterRepository.update { it.copy(whistleCount = 0, isWhistleDetected = false) }
        }
        val intent = Intent(context, WhistleService::class.java).setAction(ACTION_START)
        ContextCompat.startForegroundService(context, intent)
    }

    fun stop(context: Context) {
        if (CounterRepository.current.isServiceRunning) {
            sendCommand(context, ACTION_STOP)
        } else {
            CounterRepository.update {
                it.copy(isListening = false, currentFrequency = null, currentAmplitude = 0f)
            }
        }
    }

    fun reset(context: Context) {
        if (CounterRepository.current.isServiceRunning) {
            sendCommand(context, ACTION_RESET)
        } else {
            CounterRepository.update {
                it.copy(
                    whistleCount = 0,
                    isAlarmTriggered = false,
                    isWhistleDetected = false,
                    currentFrequency = null,
                    currentAmplitude = 0f,
                )
            }
        }
    }

    fun dismissAlarm(context: Context) {
        if (CounterRepository.current.isServiceRunning) {
            sendCommand(context, ACTION_DISMISS_ALARM)
        } else {
            CounterRepository.update { it.copy(isAlarmTriggered = false) }
        }
    }

    fun setLimit(context: Context, limit: Int) {
        val safe = limit.coerceIn(1, 20)
        WhistlePrefs.setLimit(context, safe)
        CounterRepository.update { it.copy(whistleLimit = safe) }
        if (CounterRepository.current.isServiceRunning) {
            sendCommand(context, ACTION_LIMIT_CHANGED)
        }
    }

    fun setAlarmEnabled(context: Context, enabled: Boolean) {
        WhistlePrefs.setAlarmEnabled(context, enabled)
        CounterRepository.update { it.copy(alarmEnabled = enabled) }
        if (CounterRepository.current.isServiceRunning) {
            sendCommand(context, ACTION_ALARM_TOGGLED)
        }
    }

    fun clearError() = CounterRepository.clearError()

    private fun sendCommand(context: Context, action: String) {
        val intent = Intent(context, WhistleService::class.java).setAction(action)
        runCatching { context.startService(intent) }
    }
}
