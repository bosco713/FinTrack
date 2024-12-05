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

class GoalAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val db = Room.databaseBuilder(
            context,
            TransactionDatabase::class.java, "transaction.db"
        ).fallbackToDestructiveMigration().build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val goalId = intent.getLongExtra("goal_id", -1)
        val goalName = intent.getStringExtra("goal_name") ?: "Savings Goal"
        val goalAmount = intent.getDoubleExtra("goal_amount", 0.0)
        val goalDate = intent.getStringExtra("goal_date") ?: "2100-01-01"
        val notificationId = intent.getIntExtra("notification_id", 0)

        runBlocking(Dispatchers.Default) {
            val updatedGoal = SavingsGoalData(
                id = goalId,
                name = goalName,
                currentAmount = db.transactionDAO().calculateNetAmount(),
                targetAmount = goalAmount,
                endDate = goalDate
            )
            db.savingsGoalDAO().updateGoal(updatedGoal)
        }

        val mainIntent = Intent(context, MainActivity::class.java)
        mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        val planningIntent = Intent(context, PlanningActivity::class.java)

        val savingsIntent = Intent(context, SavingsGoalsActivity::class.java)
        savingsIntent.putExtra("GOAL_NAME", goalName)

        val stackBuilder = TaskStackBuilder.create(context).apply {
            addNextIntent(mainIntent)
            addNextIntent(planningIntent)
            addNextIntent(savingsIntent)
        }
        val resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notificationBuilder = NotificationCompat.Builder(context, "channel_id")
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle("Goal Due")
            .setContentText("$goalName ")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(resultPendingIntent)
            .setAutoCancel(true)

        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}
