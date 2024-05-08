package com.project.fintrack

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
// How to use:
// runBlocking(Dispatchers.Default) {
//    db.transactionDAO().insertAll(TransactionData(current.toLong(), isExpense, category, amount))
// }
// call runBlocking first, and then call the Dao inside
@Dao
interface TransactionDAO {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(vararg transactionData: TransactionData)

    @Update
    suspend fun update(transactionData: TransactionData)

    @Delete
    suspend fun delete(transactionData: TransactionData)

    @Query("SELECT * FROM transaction_table")
    suspend fun getAll(): List<TransactionData>

    @Query("SELECT * FROM transaction_table WHERE transactionId/100000000 LIKE :year")
    suspend fun loadAllByYear(year: Int): List<TransactionData>

    @Query("SELECT * FROM transaction_table WHERE transactionId/1000000 LIKE :yearMonth")
    suspend fun loadAllByMonth(yearMonth: Int): List<TransactionData>

    @Query("SELECT * FROM transaction_table WHERE transactionId/10000 LIKE :yearMonthDate")
    suspend fun loadAllByDate(yearMonthDate: Long): List<TransactionData>

    @Query("SELECT * FROM transaction_table ORDER BY transactionId DESC LIMIT 10")
    suspend fun loadLastTenData(): List<TransactionData>

    @Query("SELECT * FROM transaction_table WHERE transactionId/100000000 LIKE :year AND is_expense LIKE :isExpense")
    suspend fun loadAllByYear(year: Int, isExpense: Boolean): List<TransactionData>

    @Query("SELECT * FROM transaction_table WHERE transactionId/1000000 LIKE :yearMonth AND is_expense LIKE :isExpense")
    suspend fun loadAllByMonth(yearMonth: Int, isExpense: Boolean): List<TransactionData>

    @Query("SELECT * FROM transaction_table WHERE transactionId/10000 LIKE :yearMonthDate AND is_expense LIKE :isExpense")
    suspend fun loadAllByDate(yearMonthDate: Long, isExpense: Boolean): List<TransactionData>

    @Query("SELECT * FROM transaction_table WHERE transactionId/100000000 LIKE :year AND is_expense LIKE :isExpense AND category LIKE :category")
    suspend fun loadAllByYear(year: Int, isExpense: Boolean, category: String): List<TransactionData>

    @Query("SELECT * FROM transaction_table WHERE transactionId/1000000 LIKE :yearMonth AND is_expense LIKE :isExpense AND category LIKE :category")
    suspend fun loadAllByMonth(yearMonth: Int, isExpense: Boolean, category: String): List<TransactionData>

    @Query("SELECT * FROM transaction_table WHERE transactionId/10000 LIKE :yearMonthDate AND is_expense LIKE :isExpense AND category LIKE :category")
    suspend fun loadAllByDate(yearMonthDate: Long, isExpense: Boolean, category: String): List<TransactionData>

    @Query("SELECT COALESCE(SUM(CASE WHEN is_expense THEN -COALESCE(amount, 0.0) ELSE COALESCE(amount, 0.0) END), 0.0) FROM transaction_table")
    suspend fun calculateNetAmount(): Double

    @Query("SELECT COALESCE(SUM(CASE WHEN is_expense THEN COALESCE(amount, 0.0) ELSE 0.0 END), 0.0) FROM transaction_table")
    suspend fun calculateTotalExpense(): Double
}
