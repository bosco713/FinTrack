package com.project.fintrack

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingManageCategoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_setting_manage_category)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //
        val returnButton = findViewById<ImageButton>(R.id.setting_manageCategory_button_return)
        returnButton.setOnClickListener{
            Intent(this, MainActivity::class.java).also {
                startActivity(it)
            }
        }
        val llIncome = findViewById<LinearLayout>(R.id.setting_manageCategory_llIncomeList)
        val llExpense = findViewById<LinearLayout>(R.id.setting_manageCategory_llExpenseList)
//        var incomeList = AppPreferences.incomeList
//        var expenseList = AppPreferences.expenseList
        // For printing two lists
        if (AppPreferences.incomeList != null) {
            for (income: String in AppPreferences.incomeList!!) {
                val textView: TextView = TextView(this)
                textView.text = income
                llIncome.addView(textView)
            }
        }
        if (AppPreferences.expenseList != null) {
            for (expense: String in AppPreferences.expenseList!!) {
                val textView: TextView = TextView(this)
                textView.text = expense
                llExpense.addView(textView)
            }
        }

        val warningText = findViewById<TextView>(R.id.setting_manageCategory_warningText)
        val etCategoryName = findViewById<EditText>(R.id.setting_manageCategory_tietCategoryName)
        val addIncome = findViewById<Button>(R.id.setting_manageCategory_button_addIncome)
        val addExpense = findViewById<Button>(R.id.setting_manageCategory_button_addExpense)
        // Inserting income category
        addIncome.setOnClickListener {
            val text = etCategoryName.text
            val alreadyContains: Boolean? = AppPreferences.incomeList?.contains(text.toString())
            val emptyText: Boolean = text.toString() == ""
            if (alreadyContains == true || emptyText) {
                // add warning
                if (alreadyContains == true) {
                    warningText.text = "It is already inside the category list."
                }
                if (emptyText) {
                    warningText.text = "Your input is empty."
                }
            } else {
                warningText.text = ""
                var tempString: String = ""
                if (AppPreferences.incomeList != null) {
                    for (income: String in AppPreferences.incomeList!!) {
                        if (tempString == "") {
                            tempString = "$income"
                        } else {
                            tempString = tempString.plus(",$income")
                        }
                    }
                }
                tempString = tempString.plus(",$text")
                AppPreferences.incomeList = emptyList<String>().plus(tempString)
                val textView: TextView = TextView(this)
                textView.text = text
                llIncome.addView(textView)
                etCategoryName.text.clear()
            }
        }
        // Inserting expense category
        addExpense.setOnClickListener {
            val text = etCategoryName.text
            val alreadyContains: Boolean? = AppPreferences.expenseList?.contains(text.toString())
            val emptyText: Boolean = text.toString() == ""
            val warningText = findViewById<TextView>(R.id.setting_manageCategory_warningText)
            if (alreadyContains == true || emptyText) {
                // add warning
                if (alreadyContains == true) {
                    warningText.text = "It is already inside the category list."
                }
                if (emptyText) {
                    warningText.text = "Your input is empty."
                }
            } else {
                warningText.text = ""
                var tempString: String = ""
                if (AppPreferences.expenseList != null) {
                    for (expense: String in AppPreferences.expenseList!!) {
                        if (tempString == "") {
                            tempString = "$expense"
                        } else {
                            tempString = tempString.plus(",$expense")
                        }
                    }
                }
                tempString = tempString.plus(",$text")
                AppPreferences.expenseList = emptyList<String>().plus(tempString)
                val textView: TextView = TextView(this)
                textView.text = text
                llExpense.addView(textView)
                etCategoryName.text.clear()
            }
        }

        val deleteIncomeButton = findViewById<Button>(R.id.setting_manageCategory_button_deleteIncome)
        val deleteExpenseButton = findViewById<Button>(R.id.setting_manageCategory_button_deleteExpense)
        // Delete income category
        deleteIncomeButton.setOnClickListener {
            val size: Any = AppPreferences.incomeList?.size ?: Int
            val text = etCategoryName.text
            val alreadyContains: Boolean? = AppPreferences.incomeList?.contains(text.toString())
            val emptyText: Boolean = text.toString() == ""
            if (size == 1 || alreadyContains == false || emptyText) {
                if (size == 1) {
                    warningText.text = "You cannot further delete, \nplease keep at least one element"
                }
                if (alreadyContains == false) {
                    warningText.text = "It is not inside the category list."
                }
                if (emptyText) {
                    warningText.text = "Your input is empty."
                }
            } else {
                warningText.text = ""
                var tempString: String = ""
                if (AppPreferences.incomeList != null) {
                    for (income: String in AppPreferences.incomeList!!) {
                        if (income != text.toString()) {
                            if (tempString == "") {
                                tempString = "$income"
                            } else {
                                tempString = tempString.plus(",$income")
                            }
                        }
                    }
                }
                AppPreferences.incomeList = emptyList<String>().plus(tempString)
                llIncome.removeAllViews()
                if (AppPreferences.incomeList != null) {
                    for (income: String in AppPreferences.incomeList!!) {
                        val textView: TextView = TextView(this)
                        textView.text = income
                        llIncome.addView(textView)
                    }
                }
                etCategoryName.text.clear()
            }
        }
        // Deleting expense category
        deleteExpenseButton.setOnClickListener {
            val size: Any = AppPreferences.expenseList?.size ?: Int
            val text = etCategoryName.text
            val alreadyContains: Boolean? = AppPreferences.expenseList?.contains(text.toString())
            val emptyText: Boolean = text.toString() == ""
            if (size == 1 || alreadyContains == false || emptyText) {
                if (size == 1) {
                    warningText.text = "You cannot further delete, \nplease keep at least one element"
                }
                if (alreadyContains == false) {
                    warningText.text = "It is not inside the category list."
                }
                if (emptyText) {
                    warningText.text = "Your input is empty."
                }
            } else {
                warningText.text = ""
                var tempString: String = ""
                if (AppPreferences.expenseList != null) {
                    for (expense: String in AppPreferences.expenseList!!) {
                        if (expense != text.toString()) {
                            if (tempString == "") {
                                tempString = "$expense"
                            } else {
                                tempString = tempString.plus(",$expense")
                            }
                        }
                    }
                }
                AppPreferences.expenseList = emptyList<String>().plus(tempString)
                llExpense.removeAllViews()
                if (AppPreferences.expenseList != null) {
                    for (expense: String in AppPreferences.expenseList!!) {
                        val textView: TextView = TextView(this)
                        textView.text = expense
                        llExpense.addView(textView)
                    }
                }
                etCategoryName.text.clear()
            }
        }
        // Reset to default
        val defaultButton = findViewById<Button>(R.id.setting_manageCategory_resetDefault)
        defaultButton.setOnClickListener {
            AppPreferences.incomeList = emptyList<String>().plus("Salary,Pocket money,Investment")
            AppPreferences.expenseList = emptyList<String>().plus("Transportation,Food,Utility,Entertainment")
            // For printing two lists
            llIncome.removeAllViews()
            llExpense.removeAllViews()
            if (AppPreferences.incomeList != null) {
                for (income: String in AppPreferences.incomeList!!) {
                    val textView: TextView = TextView(this)
                    textView.text = income
                    llIncome.addView(textView)
                }
            }
            if (AppPreferences.expenseList != null) {
                for (expense: String in AppPreferences.expenseList!!) {
                    val textView: TextView = TextView(this)
                    textView.text = expense
                    llExpense.addView(textView)
                }
            }
        }
    }
}