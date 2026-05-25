package com.todoai.util

import com.todoai.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.todoai.TodoApplication

class TaskReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "Task due"
        val taskId = intent.getLongExtra("taskId", 0L)
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("todoai_reminders", "Task Reminders", NotificationManager.IMPORTANCE_DEFAULT)
            nm.createNotificationChannel(channel)
        }

        val openIntent = PendingIntent.getActivity(context, taskId.toInt(), Intent(context, TodoApplication::class.java), PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(context, "todoai_reminders")
//            .setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle(title)
            .setContentText("Your task is due now.")
            .setContentIntent(openIntent)
            .setAutoCancel(true)
            .build()

        nm.notify(taskId.toInt(), notification)
    }
}
