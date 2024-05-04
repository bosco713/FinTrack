package com.project.fintrack

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Color
import android.util.Log
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.room.Room
import com.androidplot.xy.*
import kotlinx.coroutines.runBlocking
import java.text.Format
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.text.FieldPosition
import java.text.ParsePosition

class SummaryReportActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_summary_report)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //
        val returnButton = findViewById<ImageButton>(R.id.summary_report_button_return)
        returnButton.setOnClickListener{
            Intent(this, MainActivity::class.java).also {
                startActivity(it)
            }
        }
        //
        val db = Room.databaseBuilder(MainActivity.applicationContext()
            , TransactionDatabase::class.java, "transaction.db").build()
        var transactions: List<TransactionData> = emptyList()
        runBlocking {
            var formatter = DateTimeFormatter.ofPattern("yyyy")
            val current = LocalDateTime.now().format(formatter).toInt()
            transactions = db.transactionDAO().loadAllByYear(current)
        }

        val income = mutableListOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
        val expense = mutableListOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
        var index: Int = 0
        var month: Int = 1
        var incomeAmount: Double = 0.0
        var expenseAmount: Double = 0.0
        while (month < 13 && index <= transactions.size) {
            if (index == transactions.size) {
                Log.d("Expense", "$month = $expenseAmount")
                income[month] = incomeAmount
                expense[month] = expenseAmount
                month++
                break
            }
            val currentMonth = (transactions[index].transactionId / 1000000) % 100
            if (currentMonth.toInt() == month) {
                if (transactions[index].isExpense) {
                    expenseAmount += transactions[index].transactionAmount
                } else {
                    incomeAmount += transactions[index].transactionAmount
                }
                index++
            } else {
                Log.d("Expense", "$month = $expenseAmount")
                income[month] = incomeAmount
                expense[month] = expenseAmount
                incomeAmount = 0.0
                expenseAmount = 0.0
                month++
            }
        }

        val domain = arrayOf<Number>(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)

        val seriesIncome: XYSeries = SimpleXYSeries(income, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Income")
        val seriesIncomeFormat = LineAndPointFormatter(Color.RED, Color.BLACK, null, null)
        val seriesExpense: XYSeries = SimpleXYSeries(expense, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Expense")
        val seriesExpenseFormat = LineAndPointFormatter(Color.BLUE, Color.BLACK, null, null)

        val plot = findViewById<XYPlot>(R.id.summary_report_xyPlot)
        plot.addSeries(seriesIncome, seriesIncomeFormat)
        plot.addSeries(seriesExpense, seriesExpenseFormat)

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
    }
}