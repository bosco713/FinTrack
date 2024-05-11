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

class EditLimitActivity : AppCompatActivity() {
    private lateinit var db: TransactionDatabase
    private lateinit var limitName: EditText
    private lateinit var limitExpense: EditText
    private lateinit var limitDate: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_limit)

        db = Room.databaseBuilder(
            applicationContext,
            TransactionDatabase::class.java, "transaction.db"
        ).fallbackToDestructiveMigration().build()

        val limitId = intent.getLongExtra("LIMIT_ID", -1)
        if (limitId == -1L) {
            finish()
            return
        }

        limitName = findViewById(R.id.editLimitName)
        limitExpense = findViewById(R.id.editLimitExpense)
        limitDate = findViewById(R.id.editLimitDate)
        val saveButton = findViewById<Button>(R.id.saveLimitButton)

        loadLimitDetails(limitId)

        limitDate.apply {
            isFocusable = false
            isFocusableInTouchMode = false
            isCursorVisible = false
            setOnClickListener {
                showDatePickerDialog(this)
            }
        }

        saveButton.setOnClickListener {
            val name = limitName.text.toString()
            val expense = limitExpense.text.toString().toDoubleOrNull()
            val date = limitDate.text.toString()

            if (name.isBlank() || expense == null || date.isBlank()) {
                Toast.makeText(this, "Please fill all fields correctly", Toast.LENGTH_LONG).show()
            } else {
                saveGoalChanges(limitId)
                finish()
            }
        }

        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }
    }

    private fun loadLimitDetails(limitId: Long) {
        lifecycleScope.launch {
            val limit = db.expenseLimitDAO().getLimitById(limitId)
            limitName.setText(limit.name)
            limitExpense.setText(limit.targetExpense.toString())
            limitDate.setText(limit.endDate)
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

    private fun saveGoalChanges(limitId: Long) {
        lifecycleScope.launch {
            val updatedLimit = ExpenseLimitData(
                id = limitId,
                name = limitName.text.toString(),
                currentExpense = db.transactionDAO().calculateTotalExpense(),
                targetExpense = limitExpense.text.toString().toDouble(),
                endDate = limitDate.text.toString()
            )
            db.expenseLimitDAO().updateLimit(updatedLimit)
            updateNotification(limitId, updatedLimit.name, updatedLimit.targetExpense, updatedLimit.endDate)
            runOnUiThread {
                Toast.makeText(this@EditLimitActivity, "Limit updated successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }


    @SuppressLint("ScheduleExactAlarm")
    private fun updateNotification(limitId: Long, limitName: String, limitExpense:Double, newDate: String) {
        val intent = Intent(this, LimitAlarmReceiver::class.java).apply {
            putExtra("limit_id", limitId)
            putExtra("limit_name", limitName)
            putExtra("limit_expense", limitExpense)
            putExtra("limit_date", newDate)
            putExtra("notification_id", limitId.toInt())
        }
        val pendingIntent = PendingIntent.getBroadcast(
            this, limitId.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val targetCal = Calendar.getInstance().apply {
            time = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(newDate)!!
            set(Calendar.HOUR_OF_DAY, 24)
        }
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, targetCal.timeInMillis, pendingIntent)
    }

}
