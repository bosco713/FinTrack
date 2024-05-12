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

class AddLimitActivity : AppCompatActivity() {
    private lateinit var db: TransactionDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_limit)

        db = Room.databaseBuilder(
            applicationContext,
            TransactionDatabase::class.java, "transaction.db"
        ).fallbackToDestructiveMigration().build()

        val limitName = findViewById<EditText>(R.id.editLimitName)
        val limitExpense = findViewById<EditText>(R.id.editLimitExpense)
        val limitDate = findViewById<EditText>(R.id.editLimitDate)
        val saveButton = findViewById<Button>(R.id.saveLimitButton)

        limitDate.apply {
            isFocusable = false
            isFocusableInTouchMode = false
            isCursorVisible = false
            setOnClickListener {
                showDatePickerDialog(this)
            }
        }

        saveButton.setOnClickListener {
            lifecycleScope.launch {
                val name = limitName.text.toString()
                val currentExpense = db.transactionDAO().calculateTotalExpense()
                val targetExpense = limitExpense.text.toString().toDoubleOrNull()
                val date = limitDate.text.toString()

                if (name.isBlank() || targetExpense == null || date.isBlank()) {
                    Toast.makeText(this@AddLimitActivity, "Please fill all fields correctly", Toast.LENGTH_LONG)
                        .show()
                } else {
                    saveLimit(name, currentExpense, targetExpense, date)
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

    private fun saveLimit(name: String, currentExpense:Double, targetExpense: Double, date: String) {
        val newLimit = ExpenseLimitData(name = name, currentExpense = currentExpense, targetExpense = targetExpense, endDate = date)
        lifecycleScope.launch {
            try {
                db.expenseLimitDAO().insertLimit(newLimit)
                val insertedLimit = db.expenseLimitDAO().getLastInsertedLimit()
                scheduleNotification(insertedLimit.id, insertedLimit.name, insertedLimit.targetExpense, insertedLimit.endDate)
                runOnUiThread {
                    Toast.makeText(this@AddLimitActivity, "Limit saved successfully!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@AddLimitActivity, "Failed to save limit: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleNotification(limitId: Long, limitName: String, limitExpense:Double, endDate: String) {
        val intent = Intent(this, LimitAlarmReceiver::class.java).apply {
            putExtra("limit_id", limitId)
            putExtra("limit_name", limitName)
            putExtra("limit_expense", limitExpense)
            putExtra("limit_date", endDate)
            putExtra("notification_id", limitId.toInt())
        }
        val pendingIntent = PendingIntent.getBroadcast(
            this, limitId.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val targetCal = Calendar.getInstance().apply {
            time = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(endDate)!!
            set(Calendar.HOUR_OF_DAY, 24)
        }
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, targetCal.timeInMillis, pendingIntent)
    }
}
