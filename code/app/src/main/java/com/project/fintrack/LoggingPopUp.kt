package com.project.fintrack

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup


class LoggingPopUp {
    fun incomeRadioButtonOnCheck(view: View?, context: Context?) {
        val incomeList = AppPreferences.incomeList
        val chipGroup = view?.findViewById<ChipGroup>(R.id.chipGroup_categories)
        chipGroup?.removeAllViews()
        if (incomeList != null) {
            for (index in incomeList.indices){
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
                    it.findViewById<Chip>(it.id)
                    Log.d("incomeButton.setOnClickListener", "Pressed, id = ${it.id}")
                }
                chipGroup?.addView(chipButton)
            }
        }
        chipGroup?.invalidate()
    }

    fun expenseRadioButtonOnCheck(view: View?, context: Context?) {
        val expenseList = AppPreferences.expenseList
        val chipGroup = view?.findViewById<ChipGroup>(R.id.chipGroup_categories)
        chipGroup?.removeAllViews()
        if (expenseList != null) {
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
                    it.findViewById<Chip>(it.id)
                    Log.d("expenseButton.setOnClickListener", "Pressed, id = ${it.id}")
                }
                chipGroup?.addView(chipButton)
            }
        }
        chipGroup?.invalidate()
    }
}