package com.condor.splashbox

import android.app.Application
import android.content.Context
import com.condor.splashbox.utils.NotificationsHelper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App: Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        NotificationsHelper.createDownloadNotificationsChannel()
    }

    companion object{
        lateinit var appContext: Context
    }
}