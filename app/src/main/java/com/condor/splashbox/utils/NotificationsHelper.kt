package com.condor.splashbox.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.condor.splashbox.App
import com.condor.splashbox.R

class NotificationsHelper {

    companion object {
        fun showDownloadCompleteNotification(
            fileName: String,
            message: String,
            fileUri: Uri
        ) {
            val notificationManager = NotificationManagerCompat.from(App.appContext)

            val notificationBuilder = NotificationCompat.Builder(App.appContext, MESSAGE_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_download)
                .setContentTitle("$fileName.jpg")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(getViewPendingIntent(fileUri))
            notificationManager.notify(1, notificationBuilder.build())
        }

        fun showDownloadErrorNotification(fileName: String, message: String) {
            val notificationManager = NotificationManagerCompat.from(App.appContext)

            val notificationBuilder = NotificationCompat.Builder(App.appContext, MESSAGE_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_download)
                .setContentTitle("$fileName.jpg")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
            notificationManager.notify(1, notificationBuilder.build())
        }

        fun createDownloadNotificationsChannel() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = "SplashBox"
                val descriptionText = "Channel Description"
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(MESSAGE_CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                }
                val notificationManager: NotificationManager =
                    App.appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }

        private fun getViewPendingIntent(fileUri: Uri): PendingIntent {
            val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                setDataAndType(fileUri, "image/*")
            }
            val chooser = Intent.createChooser(viewIntent, "Open with")
            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
            return PendingIntent.getActivity(App.appContext, 0, chooser, flags)
        }

        private const val MESSAGE_CHANNEL_ID = "messages"
    }


}