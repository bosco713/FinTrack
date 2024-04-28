package com.project.fintrack

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupWindow
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlin.properties.Delegates


class LoggingPopUp {
    private lateinit var checkedCategoryText: String
    var choseCategory by Delegates.notNull<Boolean>()
    fun incomeRadioButtonOnCheck(view: View?, context: Context?) {
        val incomeList = AppPreferences.incomeList
        val chipGroup = view?.findViewById<ChipGroup>(R.id.popup_logging_chipGroup_categories)
        chipGroup?.removeAllViews()
        if (incomeList != null) {
            choseCategory = false
            for (index in incomeList.indices) {
                val buttonStyle = R.style.Theme_ChipButton
                val chipButton: Chip = Chip(context, null, buttonStyle)
                chipButton.layoutParams = ConstraintLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                index.also { chipButton.id = it }
                chipButton.text = incomeList[index]
                chipButton.isCheckable = true
                chipButton.setOnClickListener {
                    checkedCategoryText = it.findViewById<Chip>(it.id).text.toString()
                    choseCategory = true
                    Log.d("incomeButton.setOnClickListener", "Pressed, id = ${it.id}")
                }
                chipGroup?.addView(chipButton)
            }
        }
        chipGroup?.invalidate()
    }

    fun expenseRadioButtonOnCheck(view: View?, context: Context?) {
        val expenseList = AppPreferences.expenseList
        val chipGroup = view?.findViewById<ChipGroup>(R.id.popup_logging_chipGroup_categories)
        chipGroup?.removeAllViews()
        if (expenseList != null) {
            choseCategory = false
            for (index in expenseList.indices){
                val buttonStyle = R.style.Theme_ChipButton
                val chipButton: Chip = Chip(context, null, buttonStyle)
                chipButton.layoutParams = ConstraintLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                index.also { chipButton.id = it }
                chipButton.text = expenseList[index]
                chipButton.isCheckable = true
                chipButton.setOnClickListener {
                    checkedCategoryText = it.findViewById<Chip>(it.id).text.toString()
                    choseCategory = true
                    Log.d("expenseButton.setOnClickListener", "Pressed, id = ${it.id}")
                }
                chipGroup?.addView(chipButton)
            }
        }
        chipGroup?.invalidate()
    }

    @RequiresApi(Build.VERSION_CODES.O) // limitation: OG popUpWindow need to be called inside here to dismiss
    fun confirmButtonOnClick(popUpConfirmationView: View?, popUpConfirmationWindow: PopupWindow
                             , isExpense: Boolean, transactionAmount: String, popUpWindow: PopupWindow) {

        val tvTransactionType = popUpConfirmationView?.findViewById<TextView>(R.id.popup_loggingConfirm_textView_transactionType)
        val tvTransactionCategory = popUpConfirmationView?.findViewById<TextView>(R.id.popup_loggingConfirm_textView_transactionCategory)
        val tvTransactionAmount = popUpConfirmationView?.findViewById<TextView>(R.id.popup_loggingConfirm_textView_transactionAmount)

        if (isExpense) {
            tvTransactionType?.text = ": ".plus("Expense")
        } else {
            tvTransactionType?.text = ": ".plus("Income")
        }
        tvTransactionCategory?.text = ": ".plus(checkedCategoryText)
        tvTransactionAmount?.text = ": $ ".plus(transactionAmount)

        val confirmTransaction = popUpConfirmationView?.findViewById<Button>(R.id.popup_loggingConfirm_button_confirm)
        val returnTransaction = popUpConfirmationView?.findViewById<Button>(R.id.popup_loggingConfirm_button_return)

        confirmTransaction?.setOnClickListener{// already validate data presence in MainActivity before passing here
            val loggingConfirmPopUp = LoggingConfirmPopUp()
            loggingConfirmPopUp.loggingConfirmButton(isExpense, checkedCategoryText, transactionAmount.toDouble())
            popUpConfirmationWindow.dismiss()
            popUpWindow.dismiss()
        }
        returnTransaction?.setOnClickListener{
            popUpConfirmationWindow.dismiss()
        }
    }
}