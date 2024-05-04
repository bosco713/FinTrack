package com.project.fintrack

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.room.Room
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SummaryDashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_summary_dashboard)
        // not sure what this part means
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.summary_dashboard_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //
        val returnButton = findViewById<ImageButton>(R.id.summary_dashboard_button_return)
        returnButton.setOnClickListener{
            Intent(this, MainActivity::class.java).also {
                startActivity(it)
            }
        }
        //
        val db = Room.databaseBuilder(MainActivity.applicationContext()
            , TransactionDatabase::class.java, "transaction.db").build()
        val spinner = findViewById<Spinner>(R.id.summary_dashboard_spinner)
        val income = findViewById<TextView>(R.id.summary_dashboard_totalIncome)
        val expense = findViewById<TextView>(R.id.summary_dashboard_totalExpense)
        val netIncome = findViewById<TextView>(R.id.summary_dashboard_netIncome)
        income.text = 0.0.toString()
        expense.text = 0.0.toString()
        netIncome.text = 0.0.toString()

        val date = findViewById<TextView>(R.id.summary_dashboard_date)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                var transactions: List<TransactionData> = emptyList()
                runBlocking {
                    when (position) {
                        0 -> {   // Daily
                            var formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
                            val current = LocalDateTime.now().format(formatter).toLong()
                            transactions = db.transactionDAO().loadAllByDate(current)
                            formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
                            date.text = LocalDateTime.now().format(formatter).toString()
                        }

                        1 -> {   // Monthly
                            var formatter = DateTimeFormatter.ofPattern("yyyyMM")
                            val current = LocalDateTime.now().format(formatter).toInt()
                            transactions = db.transactionDAO().loadAllByMonth(current)
                            formatter = DateTimeFormatter.ofPattern("yyyy")
                            val currentYear = LocalDateTime.now().format(formatter).toInt()
                            formatter = DateTimeFormatter.ofPattern("MM")
                            val currentMonth = LocalDateTime.now().format(formatter).toInt()
                            date.text = calculateDate(currentYear, currentMonth)
                        }

                        2 -> {   // Yearly
                            var formatter = DateTimeFormatter.ofPattern("yyyy")
                            val current = LocalDateTime.now().format(formatter).toInt()
                            transactions = db.transactionDAO().loadAllByYear(current)
                            formatter = DateTimeFormatter.ofPattern("yyyy")
                            val currentYear = LocalDateTime.now().format(formatter).toInt()
                            val temp = "$currentYear/01/31 - $currentYear/12/31"
                            date.text = temp
                        }
                    }
                }
                for (transaction: TransactionData in transactions)
                    Log.d("current_database", "inserted without error, " +
                            "\"current = ${transaction.transactionId.toString()}, isExpense = ${transaction.isExpense.toString()}" +
                            ", category = ${transaction.transactionCategory}, amount = ${transaction.transactionAmount.toString()}")
                var incomeValue : Double = 0.0
                var expenseValue : Double = 0.0
                for (transaction: TransactionData in transactions) {
                    if (transaction.isExpense) {
                        expenseValue += transaction.transactionAmount
                    } else {
                        incomeValue += transaction.transactionAmount
                    }
                }
                val netIncomeValue = incomeValue - expenseValue
                income.text = incomeValue.toString()
                expense.text = expenseValue.toString()
                netIncome.text = netIncomeValue.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }

    fun calculateDate(year: Int, month: Int): String {
        // find whether this year is a leap year
        var returnString : String
        var isLeapYear : Boolean
        if (year % 4 == 0) {
            if (year % 100 == 0) {
                if (year % 400 == 0) {  // divided by 4, 100, and 400
                    isLeapYear = true
                } else {
                    isLeapYear = false
                }
            } else {    // divided by 4 and not divided by 100
                isLeapYear = true
            }
        } else {    // not divided by 4
            isLeapYear = false
        }

        // find whether this month is big or small
        if (month == 2) {
            if (isLeapYear) {
                returnString = "$year/0$month/01 - $year/0$month/29"
            } else {
                returnString = "$year/0$month/01 - $year/0$month/28"
            }
        } else {
            if (month < 8) {
                if (month % 2 == 0) {
                    returnString = "$year/0$month/01 - $year/0$month/30"
                } else {
                    returnString = "$year/0$month/01 - $year/0$month/31"
                }
            } else if (month < 10) {
                if (month % 2 == 0) {
                    returnString = "$year/0$month/01 - $year/0$month/31"
                } else {
                    returnString = "$year/0$month/01 - $year/0$month/30"
                }
            } else {
                if (month % 2 == 0) {
                    returnString = "$year/$month/01 - $year/0$month/31"
                } else {
                    returnString = "$year/$month/01 - $year/0$month/30"
                }
            }
        }
        return returnString
    }
}