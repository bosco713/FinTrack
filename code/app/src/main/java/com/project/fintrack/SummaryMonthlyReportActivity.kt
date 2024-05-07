package com.project.fintrack

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.room.Room
import com.androidplot.xy.LineAndPointFormatter
import com.androidplot.xy.PanZoom
import com.androidplot.xy.SimpleXYSeries
import com.androidplot.xy.XYGraphWidget
import com.androidplot.xy.XYPlot
import com.androidplot.xy.XYSeries
import kotlinx.coroutines.runBlocking
import java.text.FieldPosition
import java.text.Format
import java.text.ParsePosition
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SummaryMonthlyReportActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_summary_monthly_report)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //
        val returnButton = findViewById<ImageButton>(R.id.summary_monthly_report_button_return)
        returnButton.setOnClickListener{
            Intent(this, MainActivity::class.java).also {
                startActivity(it)
            }
        }
        //
        val db = Room.databaseBuilder(MainActivity.applicationContext()
            , TransactionDatabase::class.java, "transaction.db").build()
        var transactions: List<TransactionData> = emptyList()
        //
        var currentYear : Int;
        var currentMonth : Int;
        runBlocking {
            var formatter = DateTimeFormatter.ofPattern("yyyyMM")
            val current = LocalDateTime.now().format(formatter).toInt()
            currentYear = current / 100;
            currentMonth = current % 100;
            transactions = db.transactionDAO().loadAllByMonth(current)
        }
        var isLeapYear : Boolean
        if (currentYear % 4 == 0) {
            if (currentYear % 100 == 0) {
                if (currentYear % 400 == 0) {  // divided by 4, 100, and 400
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
        var income = emptyList<Double>()
        var expense = emptyList<Double>()
        var netIncome = emptyList<Double>()
        var numDate = 0
        var domain = emptyArray<Number>()
        if (currentMonth == 2) {
            if (isLeapYear) {
                income = mutableListOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                    0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                    0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
                expense = mutableListOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                    0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                    0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
                netIncome = mutableListOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                    0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                    0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
                numDate = 29
                domain = arrayOf<Number>(1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
                    11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
                    21, 22, 23, 24, 25, 26, 27, 28, 29)
            } else {
                income = mutableListOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                    0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                    0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
                expense = mutableListOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                    0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                    0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
                netIncome = mutableListOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                    0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                    0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
                numDate = 28
                domain = arrayOf<Number>(1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
                    11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
                    21, 22, 23, 24, 25, 26, 27, 28)
            }
        } else {
            if (currentMonth == 1 || currentMonth == 3 || currentMonth == 5 || currentMonth == 7 || currentMonth == 8
                || currentMonth == 10 || currentMonth == 12) {
                income = mutableListOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                    0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                    0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
                expense = mutableListOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                    0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                    0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
                netIncome = mutableListOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                    0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                    0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
                numDate = 31
                domain = arrayOf<Number>(1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
                    11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
                    21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31)
            } else {
                income = mutableListOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                    0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                    0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
                expense = mutableListOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                    0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                    0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
                netIncome = mutableListOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                    0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                    0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
                numDate = 30
                domain = arrayOf<Number>(1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
                    11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
                    21, 22, 23, 24, 25, 26, 27, 28, 29, 30)
            }
        }
        var index: Int = 0
        var day: Int = 1
        var incomeAmount: Double = 0.0
        var expenseAmount: Double = 0.0
        while (day <= numDate && index <= transactions.size) {
            if (index == transactions.size) {
                Log.d("Expense", "$day = $expenseAmount")
                income[day-1] = incomeAmount
                expense[day-1] = expenseAmount
                netIncome[day-1] = incomeAmount - expenseAmount
                day++
                break
            }
            val currentDay = transactions[index].transactionId/10000 % 100
            if (currentDay.toInt() == day) {
                if (transactions[index].isExpense) {
                    expenseAmount += transactions[index].transactionAmount
                } else {
                    incomeAmount += transactions[index].transactionAmount
                }
                index++
            } else {
                Log.d("Expense", "$day = $expenseAmount")
                income[day-1] = incomeAmount
                expense[day-1] = expenseAmount
                netIncome[day-1] = incomeAmount - expenseAmount
                incomeAmount = 0.0
                expenseAmount = 0.0
                day++
            }
        }

        val seriesIncome: XYSeries = SimpleXYSeries(income, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Income")
        val seriesIncomeFormat = LineAndPointFormatter(Color.RED, Color.BLACK, null, null)
        val seriesExpense: XYSeries = SimpleXYSeries(expense, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Expense")
        val seriesExpenseFormat = LineAndPointFormatter(Color.BLUE, Color.BLACK, null, null)
        val seriesNetIncome: XYSeries = SimpleXYSeries(netIncome, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Net income")
        val seriesNetIncomeFormat = LineAndPointFormatter(Color.GREEN, Color.BLACK, null, null)

        val plot = findViewById<XYPlot>(R.id.summary_monthly_report_xyPlot)
        plot.clear()
        plot.addSeries(seriesIncome, seriesIncomeFormat)
        plot.addSeries(seriesExpense, seriesExpenseFormat)
        plot.addSeries(seriesNetIncome, seriesNetIncomeFormat)

        plot.graph.getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).format = object : Format() {
            override fun format(
                obj: Any?,
                toAppendTo: StringBuffer,
                pos: FieldPosition
            ): StringBuffer {
                val i = Math.round((obj as Number).toFloat())
                return toAppendTo.append(domain[i])
            }

            override fun parseObject(source: String?, pos: ParsePosition): Any? {
                return null
            }
        }
        PanZoom.attach(plot)
        val button = findViewById<Button>(R.id.summary_monthly_report_toMonth)
        button.setOnClickListener {
            Intent(this, SummaryYearlyReportActivity::class.java).also {
                startActivity(it)
            }
        }
    }
}