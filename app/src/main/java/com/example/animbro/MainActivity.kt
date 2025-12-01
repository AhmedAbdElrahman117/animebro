package com.example.animbro

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.animbro.anime.screens.ThemeManager
import com.example.animbro.navigation.AppNavigation
import com.example.animbro.ui.theme.AnimBroTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Setup notification channel
        notificationChannel()

        setContent {
            val themeManager = remember { ThemeManager(this) }
            val isDarkTheme by themeManager.themeFlow.collectAsState(initial = themeManager.isDarkMode())
            AnimBroTheme(darkTheme = isDarkTheme) {
                AppNavigation()
            }
        }
    }

    private fun notificationHanlder(): ActivityResultLauncher<String> {
        val handler = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                sendNotificationMain()
            }
        }
        return handler
    }

    private fun notificationChannel() {
        val channel =
            NotificationChannel("30", "Notifications", NotificationManager.IMPORTANCE_DEFAULT)
        channel.description = "Main Notifications"
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    @SuppressLint("MissingPermission")
    private fun sendNotificationMain() {
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.animebro_logo)
        val builder = NotificationCompat.Builder(this, "30")
        builder.setSmallIcon(R.drawable.animebro_logo)
            .setContentTitle("What's New")
            .setContentText("See if something came up !!")
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
        NotificationManagerCompat
            .from(this)
            .notify(10, builder.build())
    }
}