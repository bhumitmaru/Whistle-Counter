package com.whistlecounter.app.data

import android.content.Context

object WhistlePrefs {
    private const val FILE = "whistle_counter"
    private const val KEY_LIMIT = "whistle_limit"
    private const val KEY_ALARM = "alarm_enabled"

    fun load(context: Context) {
        val prefs = context.applicationContext.getSharedPreferences(FILE, Context.MODE_PRIVATE)
        val limit = prefs.getInt(KEY_LIMIT, 3).coerceIn(1, 20)
        val alarm = prefs.getBoolean(KEY_ALARM, true)
        CounterRepository.update { it.copy(whistleLimit = limit, alarmEnabled = alarm) }
    }

    fun setLimit(context: Context, limit: Int) {
        val safe = limit.coerceIn(1, 20)
        context.applicationContext.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .edit().putInt(KEY_LIMIT, safe).apply()
    }

    fun setAlarmEnabled(context: Context, enabled: Boolean) {
        context.applicationContext.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_ALARM, enabled).apply()
    }
}
