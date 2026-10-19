package com.whistlecounter.app

import android.app.Application
import com.whistlecounter.app.data.WhistlePrefs

class WhistleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        WhistlePrefs.load(this)
    }
}
