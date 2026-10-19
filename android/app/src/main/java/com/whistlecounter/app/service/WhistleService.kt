package com.whistlecounter.app.service

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.PowerManager
import android.os.SystemClock
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import com.whistlecounter.app.audio.AlarmPlayer
import com.whistlecounter.app.audio.WhistleDetector
import com.whistlecounter.app.data.CounterRepository
import com.whistlecounter.app.data.WhistlePrefs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Owns the microphone and alarm while the app is in the background.
 *
 * Android requires a foreground service for continuous microphone access, so
 * this service shows a quiet persistent notification the whole time it listens.
 */
class WhistleService : Service() {

    private companion object {
        const val WAKELOCK_TIMEOUT_MS = 6 * 60 * 60 * 1000L
        const val AUTO_SILENCE_MS = 5 * 60 * 1000L
    }

    private val handler = Handler(Looper.getMainLooper())
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private lateinit var notifications: NotificationHelper
    private lateinit var alarmPlayer: AlarmPlayer

    private var detector: WhistleDetector? = null
    private var listeningWakeLock: PowerManager.WakeLock? = null
    private var alarmWakeLock: PowerManager.WakeLock? = null
    private var autoSilenceJob: Job? = null

    private val clearPulse = Runnable {
        CounterRepository.update { it.copy(isWhistleDetected = false) }
    }

    override fun onCreate() {
        super.onCreate()
        WhistlePrefs.load(this)
        notifications = NotificationHelper(this)
        notifications.createChannels()
        alarmPlayer = AlarmPlayer(this)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            CounterController.ACTION_START -> startListening()
            CounterController.ACTION_STOP -> stopListeningAndService()
            CounterController.ACTION_RESET -> resetEverything()
            CounterController.ACTION_DISMISS_ALARM -> dismissAlarm()
            CounterController.ACTION_LIMIT_CHANGED -> onLimitChanged()
            CounterController.ACTION_ALARM_TOGGLED -> onAlarmToggled()
            else -> {
                if (!CounterRepository.current.isServiceRunning) stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    private fun startListening() {
        if (detector?.isRunning() == true) return

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            failStart("Microphone permission is required to count whistles.")
            return
        }

        // Starting fresh always clears a previous alarm.
        autoSilenceJob?.cancel()
        alarmPlayer.stop()
        releaseAlarmWakeLock()
        notifications.cancelAlarm()

        val state = CounterRepository.current
        val safeState = if (state.whistleCount >= state.whistleLimit) {
            CounterRepository.update { it.copy(whistleCount = 0, isWhistleDetected = false) }
            CounterRepository.current
        } else {
            state
        }

        if (!enterForeground(notifications.buildListeningNotification(safeState.whistleCount, safeState.whistleLimit))) {
            return
        }

        CounterRepository.update {
            it.copy(
                isListening = true,
                isServiceRunning = true,
                isAlarmTriggered = false,
                isWhistleDetected = false,
                error = null,
            )
        }
        acquireListeningWakeLock()

        val newDetector = WhistleDetector(
            onLevel = { frequency, amplitude ->
                CounterRepository.updateLevel(frequency, amplitude)
            },
            onWhistle = {
                handler.post { onWhistleDetected() }
            },
            onError = { message ->
                handler.post { microphoneFailed(message) }
            },
        )
        detector = newDetector
        try {
            newDetector.start()
        } catch (e: Exception) {
            microphoneFailed(e.message ?: "Could not start the microphone.")
        }
    }

    private fun onWhistleDetected() {
        val state = CounterRepository.current
        if (!state.isListening || state.isAlarmTriggered) return

        val newCount = state.whistleCount + 1
        CounterRepository.update {
            it.copy(
                whistleCount = newCount,
                isWhistleDetected = true,
                currentFrequency = it.currentFrequency,
            )
        }
        notifications.notifyListening(newCount, state.whistleLimit)

        handler.removeCallbacks(clearPulse)
        handler.postDelayed(clearPulse, 320)

        if (newCount >= state.whistleLimit) {
            onTargetReached()
        }
    }

    private fun onTargetReached() {
        if (CounterRepository.current.isAlarmTriggered) return

        stopDetection()
        val state = CounterRepository.current
        CounterRepository.update {
            it.copy(
                isListening = false,
                isAlarmTriggered = true,
                isWhistleDetected = false,
                currentFrequency = null,
                currentAmplitude = 0f,
            )
        }
        notifications.cancelListening()
        acquireAlarmWakeLock()

        if (state.alarmEnabled) {
            alarmPlayer.start()
        }

        enterForeground(
            notifications.buildAlarmNotification(state.whistleCount, state.whistleLimit),
            id = NotificationHelper.ALARM_ID,
            foregroundType = ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK,
        )

        autoSilenceJob?.cancel()
        autoSilenceJob = scope.launch {
            delay(AUTO_SILENCE_MS)
            if (CounterRepository.current.isAlarmTriggered) dismissAlarm()
        }
    }

    private fun onLimitChanged() {
        val state = CounterRepository.current
        if (state.isAlarmTriggered && state.whistleCount < state.whistleLimit) {
            dismissAlarm()
            return
        }
        if (state.isListening) {
            notifications.notifyListening(state.whistleCount, state.whistleLimit)
            if (state.whistleCount >= state.whistleLimit) onTargetReached()
        }
    }

    private fun onAlarmToggled() {
        val state = CounterRepository.current
        if (!state.isAlarmTriggered) return
        if (state.alarmEnabled) alarmPlayer.start() else alarmPlayer.stop()
    }

    private fun dismissAlarm() {
        autoSilenceJob?.cancel()
        autoSilenceJob = null
        alarmPlayer.stop()
        releaseAlarmWakeLock()
        notifications.cancelAlarm()

        CounterRepository.update {
            it.copy(
                isAlarmTriggered = false,
                isListening = false,
                isServiceRunning = false,
                currentAmplitude = 0f,
                currentFrequency = null,
            )
        }

        stopForegroundAndSelf()
    }

    private fun stopListeningAndService() {
        stopDetection()
        autoSilenceJob?.cancel()
        autoSilenceJob = null
        alarmPlayer.stop()
        releaseAlarmWakeLock()
        notifications.cancelAlarm()
        notifications.cancelListening()

        CounterRepository.update {
            it.copy(
                isListening = false,
                isAlarmTriggered = false,
                isServiceRunning = false,
                isWhistleDetected = false,
                currentAmplitude = 0f,
                currentFrequency = null,
            )
        }

        stopForegroundAndSelf()
    }

    private fun resetEverything() {
        stopDetection()
        autoSilenceJob?.cancel()
        autoSilenceJob = null
        alarmPlayer.stop()
        releaseAlarmWakeLock()
        notifications.cancelAlarm()
        notifications.cancelListening()

        CounterRepository.update {
            it.copy(
                whistleCount = 0,
                isListening = false,
                isAlarmTriggered = false,
                isServiceRunning = false,
                isWhistleDetected = false,
                currentAmplitude = 0f,
                currentFrequency = null,
            )
        }

        stopForegroundAndSelf()
    }

    private fun stopDetection() {
        handler.removeCallbacks(clearPulse)
        detector?.stop()
        detector = null
        releaseListeningWakeLock()
    }

    private fun microphoneFailed(message: String) {
        stopDetection()
        alarmPlayer.stop()
        releaseAlarmWakeLock()
        notifications.cancelAlarm()
        notifications.cancelListening()
        notifications.notifyError(message)

        CounterRepository.update {
            it.copy(
                isListening = false,
                isAlarmTriggered = false,
                isServiceRunning = false,
                isWhistleDetected = false,
                currentAmplitude = 0f,
                currentFrequency = null,
                error = message,
            )
        }

        stopForegroundAndSelf()
    }

    private fun failStart(message: String) {
        notifications.notifyError(message)
        CounterRepository.update {
            it.copy(
                isListening = false,
                isServiceRunning = false,
                error = message,
                currentAmplitude = 0f,
                currentFrequency = null,
            )
        }
        stopForegroundAndSelf()
    }

    private fun enterForeground(
        notification: android.app.Notification,
        id: Int = NotificationHelper.LISTENING_ID,
        foregroundType: Int = ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE,
    ): Boolean {
        return try {
            val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) foregroundType else 0
            ServiceCompat.startForeground(this, id, notification, type)
            true
        } catch (e: Exception) {
            CounterRepository.update {
                it.copy(
                    error = "Background listening could not start: ${e.message ?: "unknown error"}",
                    isListening = false,
                    isServiceRunning = false,
                )
            }
            notifications.notifyError("Background listening could not start.")
            runCatching { stopSelf() }
            false
        }
    }

    private fun stopForegroundAndSelf() {
        runCatching {
            ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        }
        stopSelf()
    }

    private fun acquireListeningWakeLock() {
        val power = getSystemService(PowerManager::class.java) ?: return
        releaseListeningWakeLock()
        runCatching {
            listeningWakeLock = power.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "WhistleCounter::Listening",
            ).apply {
                setReferenceCounted(false)
                acquire(WAKELOCK_TIMEOUT_MS)
            }
        }
    }

    private fun releaseListeningWakeLock() {
        runCatching {
            listeningWakeLock?.let { if (it.isHeld) it.release() }
        }
        listeningWakeLock = null
    }

    @Suppress("DEPRECATION")
    private fun acquireAlarmWakeLock() {
        val power = getSystemService(PowerManager::class.java) ?: return
        releaseAlarmWakeLock()
        runCatching {
            alarmWakeLock = power.newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK or
                    PowerManager.ACQUIRE_CAUSES_WAKEUP or
                    PowerManager.ON_AFTER_RELEASE,
                "WhistleCounter::Alarm",
            ).apply {
                setReferenceCounted(false)
                acquire(15_000L)
            }
        }
    }

    private fun releaseAlarmWakeLock() {
        runCatching {
            alarmWakeLock?.let { if (it.isHeld) it.release() }
        }
        alarmWakeLock = null
    }

    override fun onDestroy() {
        handler.removeCallbacks(clearPulse)
        autoSilenceJob?.cancel()
        detector?.stop()
        detector = null
        alarmPlayer.stop()
        releaseListeningWakeLock()
        releaseAlarmWakeLock()
        CounterRepository.update {
            if (it.isServiceRunning || it.isListening) {
                it.copy(
                    isListening = false,
                    isServiceRunning = false,
                    isAlarmTriggered = false,
                    currentAmplitude = 0f,
                    currentFrequency = null,
                )
            } else {
                it
            }
        }
        scope.cancel()
        super.onDestroy()
    }
}
