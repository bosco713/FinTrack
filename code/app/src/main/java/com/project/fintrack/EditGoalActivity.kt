package com.project.fintrack

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class EditGoalActivity : AppCompatActivity() {
    private lateinit var db: TransactionDatabase
    private lateinit var goalName: EditText
    private lateinit var goalAmount: EditText
    private lateinit var goalDate: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_goal)

        db = Room.databaseBuilder(
            applicationContext,
            TransactionDatabase::class.java, "transaction.db"
        ).fallbackToDestructiveMigration().build()

        val goalId = intent.getLongExtra("GOAL_ID", -1)
        if (goalId == -1L) {
            finish()
            return
        }

        goalName = findViewById(R.id.editGoalName)
        goalAmount = findViewById(R.id.editGoalAmount)
        goalDate = findViewById(R.id.editGoalDate)
        val saveButton = findViewById<Button>(R.id.saveGoalButton)

        loadGoalDetails(goalId)

        goalDate.apply {
            isFocusable = false
            isFocusableInTouchMode = false
            isCursorVisible = false
            setOnClickListener {
                showDatePickerDialog(this)
            }
        }

        saveButton.setOnClickListener {
            val name = goalName.text.toString()
            val amount = goalAmount.text.toString().toDoubleOrNull()
            val date = goalDate.text.toString()

            if (name.isBlank() || amount == null || date.isBlank()) {
                Toast.makeText(this, "Please fill all fields correctly", Toast.LENGTH_LONG).show()
            } else {
                saveGoalChanges(goalId)
                finish()
            }
        }

        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }
    }

    private fun loadGoalDetails(goalId: Long) {
        lifecycleScope.launch {
            val goal = db.savingsGoalDAO().getGoalById(goalId)
            goalName.setText(goal.name)
            goalAmount.setText(goal.targetAmount.toString())
            goalDate.setText(goal.endDate)
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

    private fun saveGoalChanges(goalId: Long) {
        lifecycleScope.launch {
            val updatedGoal = SavingsGoalData(
                id = goalId,
                name = goalName.text.toString(),
                currentAmount = db.transactionDAO().calculateNetAmount(),
                targetAmount = goalAmount.text.toString().toDouble(),
                endDate = goalDate.text.toString()
            )
            db.savingsGoalDAO().updateGoal(updatedGoal)
            updateNotification(goalId, updatedGoal.name, updatedGoal.targetAmount, updatedGoal.endDate)
            runOnUiThread {
                Toast.makeText(this@EditGoalActivity, "Goal updated successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }


    @SuppressLint("ScheduleExactAlarm")
    private fun updateNotification(goalId: Long, goalName: String, goalAmount:Double, newDate: String) {
        val intent = Intent(this, GoalAlarmReceiver::class.java).apply {
            putExtra("goal_id", goalId)
            putExtra("goal_name", goalName)
            putExtra("goal_amount", goalAmount)
            putExtra("goal_date", newDate)
            putExtra("notification_id", goalId.toInt())
        }
        val pendingIntent = PendingIntent.getBroadcast(this, goalId.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val targetCal = Calendar.getInstance().apply {
            time = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(newDate)!!
            set(Calendar.HOUR_OF_DAY, 24)
        }
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, targetCal.timeInMillis, pendingIntent)
    }

}
