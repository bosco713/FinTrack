package com.project.fintrack

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [TransactionData::class, SavingsGoalData::class, ExpenseLimitData::class], version = 4)
abstract class TransactionDatabase : RoomDatabase() {
    abstract fun transactionDAO(): TransactionDAO
    abstract fun savingsGoalDAO(): SavingsGoalDAO
    abstract fun expenseLimitDAO(): ExpenseLimitDAO
}