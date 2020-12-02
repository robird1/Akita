package com.ulsee.shiba.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.ulsee.shiba.R
import com.ulsee.shiba.data.response.AttendRecord


class NotificationCenter {

    companion object {
        val shared = NotificationCenter()
    }

    private var id = 100

    fun show(
        ctx: Context,
        intent: Intent,
        title: String?,
        notification: AttendRecord?
    ) {
        Log.d("NotificationCenter", "[Enter] show2")
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            ctx, id /* Request code */, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val channelId = "channel_id_singleton"
        val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(ctx, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(ctx.getResources().getColor(R.color.colorPrimary))
                .setContentTitle(title)
                .setContentText(getContentText(notification!!))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)

//        val bitImage = getImageIcon(notification)
//        if (bitImage != null) {
//            val bigPicture = NotificationCompat.BigPictureStyle()
//            bigPicture.bigPicture(bitImage)
//            notificationBuilder.setLargeIcon(bitImage)
//            notificationBuilder.setStyle(bigPicture)
//        }

        val notificationManager =
            ctx.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(id /* ID of notification */, notificationBuilder.build())
        id++
    }

    private fun getContentText(notification: AttendRecord): String {
        return notification.timestamp + " Abnormal temperature detected. (" + notification.name + notification.bodyTemperature + ")"
    }
}

