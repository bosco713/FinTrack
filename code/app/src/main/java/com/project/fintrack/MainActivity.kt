package com.project.fintrack;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity
import android.view.LayoutInflater;
import android.widget.PopupWindow;
import android.view.View;
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppPreferences.setup(applicationContext, "types", MODE_PRIVATE);
    }

    fun loggingButtonOnClick(view: View?) {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater;
        val popupView = inflater.inflate(R.layout.activity_logging_pop_up, null);
        val loggingPopUp = LoggingPopUp()

        val width = LinearLayout.LayoutParams.WRAP_CONTENT;
        val height = LinearLayout.LayoutParams.WRAP_CONTENT;
        val focus = true;
        val popupWindow = PopupWindow(popupView, width, height, focus);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        val radioGroupTransactionType = popupView.findViewById<RadioGroup>(R.id.radioGroup_transactionTypes)
        val radioButtonIncome = popupView.findViewById<RadioButton>(R.id.radioButton_income)
        val radioButtonExpense = popupView.findViewById<RadioButton>(R.id.radioButton_expense)

        radioGroupTransactionType.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == radioButtonIncome.id) {
                loggingPopUp.incomeRadioButtonOnCheck(popupView, this)
            } else {
                loggingPopUp.expenseRadioButtonOnCheck(popupView, this)
            }
        }
        radioButtonExpense.isChecked = true
        // set initial value as most transaction should be expense
    }
}