package com.project.fintrack

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ExpenseLimitDAO {
    @Insert
    suspend fun insertLimit(expenseLimit: ExpenseLimitData)

    @Update
    suspend fun updateLimit(expenseLimit: ExpenseLimitData)

    @Delete
    suspend fun deleteLimit(expenseLimit: ExpenseLimitData)

    @Query("SELECT * FROM expense_limit_table ORDER BY endDate ASC")
    suspend fun getAllLimits(): List<ExpenseLimitData>

    @Query("SELECT * FROM expense_limit_table WHERE limitId = :limitId")
    suspend fun getLimitById(limitId: Long): ExpenseLimitData

    @Query("SELECT * FROM expense_limit_table ORDER BY limitId DESC LIMIT 1")
    suspend fun getLastInsertedLimit(): ExpenseLimitData
}
