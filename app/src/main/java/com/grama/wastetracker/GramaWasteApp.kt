package com.grama.wastetracker

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GramaWasteApp : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val tractorChannel = NotificationChannel(
                TRACTOR_CHANNEL_ID,
                "Tractor Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts when waste collection tractor is nearby"
                enableVibration(true)
            }

            val reportChannel = NotificationChannel(
                REPORT_CHANNEL_ID,
                "Report Updates",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Updates on blackspot report status"
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(tractorChannel)
            notificationManager.createNotificationChannel(reportChannel)
        }
    }

    companion object {
        const val TRACTOR_CHANNEL_ID = "tractor_alerts"
        const val REPORT_CHANNEL_ID = "report_updates"
    }
}
