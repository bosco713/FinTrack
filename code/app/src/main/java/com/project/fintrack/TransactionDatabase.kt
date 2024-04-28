package com.project.fintrack

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [TransactionData::class], version = 1)
abstract class TransactionDatabase: RoomDatabase() {
    abstract fun transactionDAO(): TransactionDAO
}