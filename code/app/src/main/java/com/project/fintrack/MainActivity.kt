package com.project.fintrack;

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

class MainActivity : AppCompatActivity() {

    init {
        instance = this
    }

    companion object {
        private var instance: MainActivity? = null
        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppPreferences.setup(applicationContext, "types", MODE_PRIVATE);
//        val sharedPref = getSharedPreferences("types", MODE_PRIVATE)
//        val editor = sharedPref.edit()
//        editor.apply{
//            clear()
//            putString("INCOME", "Salary,Pocket money,Investment")
//            putString("EXPENSE", "Transportation,Food,Utility,Entertainment")
//            apply()
//        }
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
            Intent(this, SummaryReportActivity::class.java).also {
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
}