package com.project.fintrack;

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater;
import android.widget.PopupWindow;
import android.view.View;
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.room.Room
import kotlinx.coroutines.runBlocking
import java.sql.Time
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    init {
        instance = this
    }

    companion object {
        private var instance: MainActivity? = null
        fun applicationContext() : Context {
            return instance!!.applicationContext
        }

        const val CHANNEL_NAME = "FinTrack_notification"
        const val CHANNEL_DESCRIPTION = "For savings goals"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val name = CHANNEL_NAME
        val descriptionText = CHANNEL_DESCRIPTION
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("channel_id", name, importance).apply {
            description = descriptionText
        }

        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppPreferences.setup(applicationContext, "types", MODE_PRIVATE);
//        for debug
//
//        val sharedPref = getSharedPreferences("types", MODE_PRIVATE)
//        val editor = sharedPref.edit()
//        editor.apply{
//            clear()
//            putString("INCOME", "Salary,Pocket money,Investment")
//            putString("EXPENSE", "Transportation,Food,Utility,Entertainment")
//            apply()
//        }
//
//        buildingRandomDatasetForTesting()


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        val planningButton = findViewById<Button>(R.id.activity_main_planningButton)
        planningButton.setOnClickListener {
            val intent = Intent(this, PlanningActivity::class.java)
            startActivity(intent)
        }
    }

    fun summaryButtonOnClick(view: View?) {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater;
        val popupView = inflater.inflate(R.layout.popup_summary, null);

        val width = LinearLayout.LayoutParams.WRAP_CONTENT;
        val height = LinearLayout.LayoutParams.WRAP_CONTENT;
        val focus = true;
        val popupWindow = PopupWindow(popupView, width, height, focus);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        val dashboardButton = popupView.findViewById<Button>(R.id.popup_summary_dashboard)
        val reportButton = popupView.findViewById<Button>(R.id.popup_summary_report)

        dashboardButton.setOnClickListener {
            Intent(this, SummaryDashboardActivity::class.java).also {
                startActivity(it)
            }
        }
        reportButton.setOnClickListener {
            Intent(this, SummaryYearlyReportActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loggingButtonOnClick(view: View?) {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater;
        val popupView = inflater.inflate(R.layout.popup_logging, null);
        val loggingPopUp = LoggingPopUp()

        val width = LinearLayout.LayoutParams.WRAP_CONTENT;
        val height = LinearLayout.LayoutParams.WRAP_CONTENT;
        val focus = true;
        val popupWindow = PopupWindow(popupView, width, height, focus);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        val radioGroupTransactionType = popupView.findViewById<RadioGroup>(R.id.popup_logging_radioGroup_transactionTypes)
        val radioButtonIncome = popupView.findViewById<RadioButton>(R.id.popup_logging_radioButton_income)
        val radioButtonExpense = popupView.findViewById<RadioButton>(R.id.popup_logging_radioButton_expense)
        var isExpense: Boolean

        radioGroupTransactionType.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == radioButtonIncome.id) {
                loggingPopUp.incomeRadioButtonOnCheck(popupView, this)
                isExpense = false
            } else {
                loggingPopUp.expenseRadioButtonOnCheck(popupView, this)
                isExpense = true
            }
        }
        radioButtonExpense.isChecked = true
        isExpense = true
        // set initial value as most transaction should be expense

        // For the confirm button and return button
        val confirmButton = popupView.findViewById<Button>(R.id.popup_logging_button_confirm)
        val returnButton = popupView.findViewById<Button>(R.id.popup_logging_button_return)

        confirmButton.setOnClickListener{
            val etAmount = popupView.findViewById<EditText>(R.id.popup_logging_editTextDecimal_inputPrice).text.toString()
            if ((etAmount != "") and (loggingPopUp.choseCategory)) {
                Log.d("etAmount", "etAmount = $etAmount")
                val popUpConfirmationView = inflater.inflate(R.layout.popup_logging_confirm, null);
                val width = LinearLayout.LayoutParams.WRAP_CONTENT;
                val height = LinearLayout.LayoutParams.WRAP_CONTENT;
                val focus = true;
                val popupConfirmationWindow = PopupWindow(popUpConfirmationView, width, height, focus);
                popupConfirmationWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                loggingPopUp.confirmButtonOnClick(popUpConfirmationView, popupConfirmationWindow, isExpense, etAmount, popupWindow)
            } else {
                val textViewWarning = popupView.findViewById<TextView>(R.id.popup_logging_textView_warning)
                textViewWarning.text = "Please enter again, there are missing data in your input."
            }
        }

        returnButton.setOnClickListener{
            popupWindow.dismiss()
        }
    }

    fun settingButtonOnClick(view: View?) {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater;
        val popupView = inflater.inflate(R.layout.popup_setting, null);

        val width = LinearLayout.LayoutParams.WRAP_CONTENT;
        val height = LinearLayout.LayoutParams.WRAP_CONTENT;
        val focus = true;
        val popupWindow = PopupWindow(popupView, width, height, focus);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        val manageCategoryButton = popupView.findViewById<Button>(R.id.setting_button_manageCategory)
        val deleteEntryButton = popupView.findViewById<Button>(R.id.setting_button_deleteEntry)
        manageCategoryButton.setOnClickListener {
            Intent(this, SettingManageCategoryActivity::class.java).also {
                startActivity(it)
            }
        }
        deleteEntryButton.setOnClickListener {
            Intent(this, SettingDeleteEntryActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun buildingRandomDatasetForTesting() {
        val startingMonth = 1
        val endingMonth = 5
        val startingDate = 1
        val endingDate = 31
        val startingHour = 0
        val endingHour = 23
        val startingMinute = 0
        val endingMinute = 59
        val incomeList = listOf("Salary", "Pocket money" ,"Investment")
        val expenseList = listOf("Transportation", "Food", "Utility", "Entertainment")
        val idList = mutableListOf<Long>()
        val db = Room.databaseBuilder(MainActivity.applicationContext()
            , TransactionDatabase::class.java, "transaction.db").build()
        val maxNumData = 200
        var count = 0
        while (count < maxNumData) {
            val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
            Random(LocalDateTime.now().format(formatter).toInt())
            val year = 2024
            val month = Random.nextInt(startingMonth, endingMonth+1)
            var date = Random.nextInt(startingDate, endingDate+1)
            val hour = Random.nextInt(startingHour, endingHour+1)
            val minute = Random.nextInt(startingMinute, endingMinute+1)   // limitation
            if (month == 2) {
                if (date > 29) {
                    date = 29
                }
            } else if (month == 4) {
                date = 30
            }
            val id: Long = year.toLong() * 100000000 + month.toLong() * 1000000 + date.toLong() * 10000 + hour * 100 + minute
            if (idList.contains(id)) {
                continue
            } else {
                idList.add(id)
            }
            var isExpense: Boolean = true
            if (count % 2 == 1) {
                isExpense = false
            }
            val category: String
            if (isExpense) {
                category = expenseList[Random.nextInt(0, 3)]
            } else {
                category = incomeList[Random.nextInt(0, 2)]
            }
            val amount = String.format("%.1f", Random.nextDouble(10.0, 100.0)).toDouble()
            runBlocking {
                db.transactionDAO().insertAll(TransactionData(id, isExpense, category, amount))
            }
            count++
        }
    }
}
