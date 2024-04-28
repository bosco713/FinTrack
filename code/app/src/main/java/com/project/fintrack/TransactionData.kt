package com.project.fintrack

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transaction_table")
data class TransactionData (
    @PrimaryKey(autoGenerate = true)
    val transactionId: Long = 0,     // (Date, time)
    @ColumnInfo(name = "is_expense")
    val isExpense: Boolean,
    @ColumnInfo(name = "category")
    val transactionCategory: String,
    @ColumnInfo(name = "amount")
    val transactionAmount: Double
)
