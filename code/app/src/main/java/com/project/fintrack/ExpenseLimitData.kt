package com.project.fintrack

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expense_limit_table")
data class ExpenseLimitData(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "limitId") val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "currentExpense") val currentExpense: Double,
    @ColumnInfo(name = "targetExpense") val targetExpense: Double,
    @ColumnInfo(name = "endDate") val endDate: String
)

