package com.whistlecounter.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.whistlecounter.app.MainActivity
import com.whistlecounter.app.R

class NotificationHelper(private val context: Context) {
    companion object {
        const val CHANNEL_LISTENING = "whistle_listening"
        const val CHANNEL_ALARM = "whistle_alarm"
        const val CHANNEL_ERROR = "whistle_errors"

        const val LISTENING_ID = 1001
        const val ALARM_ID = 1002
        const val ERROR_ID = 1003
    }

    private val manager = NotificationManagerCompat.from(context)

    fun createChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val system = context.getSystemService(NotificationManager::class.java) ?: return

        val listening = NotificationChannel(
            CHANNEL_LISTENING,
            context.getString(R.string.notification_channel_listening),
            NotificationManager.IMPORTANCE_LOW,
        ).apply {
            description = context.getString(R.string.notification_channel_listening_description)
            setShowBadge(false)
            setSound(null, null)
            enableVibration(false)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }

        val alarm = NotificationChannel(
            CHANNEL_ALARM,
            context.getString(R.string.notification_channel_alarm),
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = context.getString(R.string.notification_channel_alarm_description)
            setSound(null, null)
            enableVibration(false)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            setBypassDnd(false)
        }

        val errors = NotificationChannel(
            CHANNEL_ERROR,
            context.getString(R.string.notification_channel_error),
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            setShowBadge(false)
            setSound(null, null)
            enableVibration(false)
        }

        system.createNotificationChannels(listOf(listening, alarm, errors))
    }

    fun buildListeningNotification(count: Int, limit: Int): Notification {
        val stopIntent = PendingIntent.getService(
            context,
            21,
            Intent(context, WhistleService::class.java).setAction(CounterController.ACTION_STOP),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        return NotificationCompat.Builder(context, CHANNEL_LISTENING)
            .setSmallIcon(R.drawable.ic_stat_whistle)
            .setColor(ContextCompat.getColor(context, R.color.terracotta))
            .setContentTitle("Listening for whistles")
            .setContentText("$count of $limit counted")
            .setContentIntent(openAppIntent())
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setSilent(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .addAction(0, "Stop listening", stopIntent)
            .build()
    }

    fun notifyListening(count: Int, limit: Int) {
        notifyIfAllowed(LISTENING_ID, buildListeningNotification(count, limit))
    }

    fun buildAlarmNotification(count: Int, limit: Int): Notification {
        val dismissIntent = PendingIntent.getService(
            context,
            22,
            Intent(context, WhistleService::class.java).setAction(CounterController.ACTION_DISMISS_ALARM),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        return NotificationCompat.Builder(context, CHANNEL_ALARM)
            .setSmallIcon(R.drawable.ic_stat_whistle)
            .setColor(ContextCompat.getColor(context, R.color.terracotta))
            .setContentTitle("Target reached!")
            .setContentText("$count of $limit whistles counted. Tap to stop the alarm.")
            .setContentIntent(alarmAppIntent())
            .setFullScreenIntent(alarmAppIntent(), true)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .setAutoCancel(false)
            .setOnlyAlertOnce(true)
            .addAction(0, "Dismiss", dismissIntent)
            .build()
    }

    fun notifyError(message: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ERROR)
            .setSmallIcon(R.drawable.ic_stat_whistle)
            .setColor(ContextCompat.getColor(context, R.color.terracotta))
            .setContentTitle("Microphone unavailable")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setContentIntent(openAppIntent())
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        notifyIfAllowed(ERROR_ID, notification)
    }

    fun cancelAlarm() {
        manager.cancel(ALARM_ID)
    }

    fun cancelListening() {
        manager.cancel(LISTENING_ID)
    }

    private fun notifyIfAllowed(id: Int, notification: Notification) {
        if (!manager.areNotificationsEnabled()) return
        runCatching { manager.notify(id, notification) }
    }

    private fun openAppIntent(): PendingIntent = PendingIntent.getActivity(
        context,
        11,
        Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )

    private fun alarmAppIntent(): PendingIntent = PendingIntent.getActivity(
        context,
        12,
        Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(MainActivity.EXTRA_SHOW_ALARM, true)
        },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )
}
