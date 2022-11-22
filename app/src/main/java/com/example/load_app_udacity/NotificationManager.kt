package com.example.load_app_udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
private const val noti_id: Int = 0
fun NotificationManager.cancelNotifications() {
    cancelAll()
}
fun NotificationManager.sendNotification(
    applicationContext: Context,
    finaleName: String,
    messageBody: String,
    status: String
) {
    val contentIntent = Intent(applicationContext, DetailActivity::class.java)
    contentIntent.apply {
        putExtra("REPOSITORY_NAME", finaleName)
        putExtra("STATUS", status)
    }
    val contentPendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        PendingIntent.getActivity(
            applicationContext,
            noti_id,
            contentIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    } else {
        PendingIntent.getActivity(
            applicationContext,
            noti_id,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT,
        )
    }
    val downloadImage = BitmapFactory.decodeResource(
        applicationContext.resources,
        R.drawable.ic_download
    )
    val bigPictureStyle = NotificationCompat.BigPictureStyle()
        .bigPicture(downloadImage)
        .bigLargeIcon(null)
    val notiBuilder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.notification_channel_id)
    )
        .setSmallIcon(R.drawable.ic_download)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(messageBody)
        .setContentIntent(contentPendingIntent)
        .setStyle(bigPictureStyle)
        .setLargeIcon(downloadImage)
        .addAction(
            R.drawable.ic_download,
            "Go to status page",
            contentPendingIntent
        )
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
    notify(noti_id, notiBuilder.build())
}

