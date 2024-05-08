package com.project.fintrack

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import kotlinx.coroutines.launch
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.text.SimpleDateFormat
import java.util.*

class AddGoalActivity : AppCompatActivity() {
    private lateinit var db: TransactionDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_goal)

        db = Room.databaseBuilder(
            applicationContext,
            TransactionDatabase::class.java, "transaction.db"
        ).fallbackToDestructiveMigration().build()

        val goalName = findViewById<EditText>(R.id.editGoalName)
        val goalAmount = findViewById<EditText>(R.id.editGoalAmount)
        val goalDate = findViewById<EditText>(R.id.editGoalDate)
        val saveButton = findViewById<Button>(R.id.saveGoalButton)

        goalDate.apply {
            isFocusable = false
            isFocusableInTouchMode = false
            isCursorVisible = false
            setOnClickListener {
                showDatePickerDialog(this)
            }
        }

        saveButton.setOnClickListener {
            lifecycleScope.launch {
                val name = goalName.text.toString()
                val currentAmount = db.transactionDAO().calculateNetAmount()
                val targetAmount = goalAmount.text.toString().toDoubleOrNull()
                val date = goalDate.text.toString()

                if (name.isBlank() || targetAmount == null || date.isBlank()) {
                    Toast.makeText(this@AddGoalActivity, "Please fill all fields correctly", Toast.LENGTH_LONG)
                        .show()
                } else {
                    saveGoal(name, currentAmount, targetAmount, date)
                    finish()
                }
            }
        }

        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }
    }

    private fun showDatePickerDialog(dateInput: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, dayOfMonth ->
            dateInput.setText(String.format("%d-%02d-%02d", selectedYear, selectedMonth + 1, dayOfMonth))
        }, year, month, day)

        datePickerDialog.datePicker.minDate = calendar.timeInMillis

        datePickerDialog.show()
    }

    private fun saveGoal(name: String, currentAmount:Double, targetAmount: Double, date: String) {
        val newGoal = SavingsGoalData(name = name, currentAmount = currentAmount, targetAmount = targetAmount, endDate = date)
        lifecycleScope.launch {
            try {
                db.savingsGoalDAO().insertGoal(newGoal)
                val insertedGoal = db.savingsGoalDAO().getLastInsertedGoal()
                scheduleNotification(insertedGoal.id, insertedGoal.name, insertedGoal.targetAmount, insertedGoal.endDate)
                runOnUiThread {
                    Toast.makeText(this@AddGoalActivity, "Goal saved successfully!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@AddGoalActivity, "Failed to save goal: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleNotification(goalId: Long, goalName: String, goalAmount:Double, endDate: String) {
        val intent = Intent(this, GoalAlarmReceiver::class.java).apply {
            putExtra("goal_id", goalId)
            putExtra("goal_name", goalName)
            putExtra("goal_amount", goalAmount)
            putExtra("goal_date", endDate)
            putExtra("notification_id", goalId.toInt())
        }
        val pendingIntent = PendingIntent.getBroadcast(this, goalId.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val targetCal = Calendar.getInstance().apply {
            time = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(endDate)!!
            set(Calendar.HOUR_OF_DAY, 24)
        }
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, targetCal.timeInMillis, pendingIntent)
    }
}
