package com.project.fintrack

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class LimitAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val db = Room.databaseBuilder(
            context,
            TransactionDatabase::class.java, "transaction.db"
        ).fallbackToDestructiveMigration().build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val limitId = intent.getLongExtra("limit_id", -1)
        val limitName = intent.getStringExtra("limit_name") ?: "Expense Limit"
        val limitExpense = intent.getDoubleExtra("limit_expense", 0.0)
        val limitDate = intent.getStringExtra("limit_date") ?: "2100-01-01"
        val notificationId = intent.getIntExtra("notification_id", 0)

        runBlocking(Dispatchers.Default) {
            val updatedLimit = ExpenseLimitData(
                id = limitId,
                name = limitName,
                currentExpense = db.transactionDAO().calculateTotalExpense(),
                targetExpense = limitExpense,
                endDate = limitDate
            )
            db.expenseLimitDAO().updateLimit(updatedLimit)
        }

        val mainIntent = Intent(context, MainActivity::class.java)
        mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        val planningIntent = Intent(context, PlanningActivity::class.java)

        val expenseIntent = Intent(context, ExpenseLimitsActivity::class.java)
        expenseIntent.putExtra("LIMIT_NAME", limitName)

        val stackBuilder = TaskStackBuilder.create(context).apply {
            addNextIntent(mainIntent)
            addNextIntent(planningIntent)
            addNextIntent(expenseIntent)
        }
        val resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notificationBuilder = NotificationCompat.Builder(context, "channel_id")
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle("Limit Due")
            .setContentText("$limitName ")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(resultPendingIntent)
            .setAutoCancel(true)

        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}
