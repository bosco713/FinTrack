package com.project.fintrack

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface SavingsGoalDAO {
    @Insert
    suspend fun insertGoal(savingsGoal: SavingsGoalData)

    @Update
    suspend fun updateGoal(savingsGoal: SavingsGoalData)

    @Delete
    suspend fun deleteGoal(savingsGoal: SavingsGoalData)

    @Query("SELECT * FROM savings_goal_table ORDER BY endDate ASC")
    suspend fun getAllGoals(): List<SavingsGoalData>

    @Query("SELECT * FROM savings_goal_table WHERE goalId = :goalId")
    suspend fun getGoalById(goalId: Long): SavingsGoalData

    @Query("SELECT * FROM savings_goal_table ORDER BY goalId DESC LIMIT 1")
    suspend fun getLastInsertedGoal(): SavingsGoalData
}
