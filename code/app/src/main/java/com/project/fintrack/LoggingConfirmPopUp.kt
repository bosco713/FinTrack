package com.project.fintrack

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LoggingConfirmPopUp {
    @RequiresApi(Build.VERSION_CODES.O)     // Limitation?
    fun loggingConfirmButton(isExpense: Boolean, category: String, amount: Double) {
        val db = Room.databaseBuilder(MainActivity.applicationContext()
            , TransactionDatabase::class.java, "transaction.db").build()
        val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm")
        val current = LocalDateTime.now().format(formatter)
        Log.d("call constructor", "current = $current, isExpense = ${isExpense.toString()}, category = $category, amount = ${amount.toString()}")
        runBlocking(Dispatchers.Default) {
            db.transactionDAO().insertAll(TransactionData(current.toLong(), isExpense, category, amount))
        }

        // for debug
        runBlocking(Dispatchers.Default) {
            val allTransactionData: List<TransactionData> = db.transactionDAO().getAll()
            val index = 2
//            for (index in allTransactionDat)
            Log.d("database", "inserted without error, " +
                "\"current = ${allTransactionData[index].transactionId.toString()}, isExpense = ${allTransactionData[index].isExpense.toString()}" +
                ", category = ${allTransactionData[index].transactionCategory}, amount = ${allTransactionData[index].transactionAmount.toString()}")
        }
    }
}