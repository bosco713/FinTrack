package com.project.fintrack;

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

class MainActivity : AppCompatActivity() {

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
        var storeCheckedText = radioButtonExpense.text

        radioGroupTransactionType.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == radioButtonIncome.id) {
                loggingPopUp.incomeRadioButtonOnCheck(popupView, this)
                storeCheckedText = radioButtonIncome.text
            } else {
                loggingPopUp.expenseRadioButtonOnCheck(popupView, this)
                storeCheckedText = radioButtonExpense.text
            }
        }
        radioButtonExpense.isChecked = true
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
                loggingPopUp.confirmButtonOnClick(popUpConfirmationView, popupConfirmationWindow, storeCheckedText, etAmount)
            } else {
                // need to add warning sign here
            }
        }

        returnButton.setOnClickListener{
            popupWindow.dismiss()
        }
    }
}