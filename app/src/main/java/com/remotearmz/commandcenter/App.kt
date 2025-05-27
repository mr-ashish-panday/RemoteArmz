package com.remotearmz.commandcenter

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import com.remotearmz.commandcenter.data.database.AppDatabase
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {
    @Inject
    lateinit var database: AppDatabase

    override fun onCreate() {
        super.onCreate()
        // Initialize any global components here
    }
}
