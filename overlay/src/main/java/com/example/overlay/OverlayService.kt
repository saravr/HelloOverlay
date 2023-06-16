package com.example.overlay

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class OverlayService : Service() {
    private lateinit var overlayWindow: OverlayWindow
    private val items = mutableListOf<String>()
    private var itemList = MutableStateFlow<List<String>>(listOf())

    override fun onBind(intent: Intent): IBinder? {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onCreate() {
        super.onCreate()
        // create the custom or default notification based on the android version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startOverlayForeground()
        } else {
            startForeground(1, Notification())
        }

        // create an instance of Window class and display the content on screen
        overlayWindow = OverlayWindow(this, itemList)
        overlayWindow.open()

        CoroutineScope(Dispatchers.Main).launch {
            repeat(1000) {
                items.add("Element $it")
                itemList.emit(items.toMutableList())
                delay(1000)
            }
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        overlayWindow.close()
    }

    // for android version >=O we need to create custom notification stating
    // foreground service is running
    @RequiresApi(Build.VERSION_CODES.O)
    private fun startOverlayForeground() {
        val notificationChannelId = "example.permanence"
        val channelName = "Background Service"
        val chan = NotificationChannel(
            notificationChannelId,
            channelName,
            NotificationManager.IMPORTANCE_MIN
        )
        val manager = (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
        manager.createNotificationChannel(chan)
        val notificationBuilder = NotificationCompat.Builder(this, notificationChannelId)
        val notification = notificationBuilder.setOngoing(true)
            .setContentTitle("Service running")
            .setContentText("Displaying over other apps") // this is important, otherwise the notification will show the way
            // you want i.e. it will show some default notification
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(2, notification)
    }
}
