package com.project.fintrack

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "savings_goal_table")
data class SavingsGoalData(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "goalId") val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "currentAmount") val currentAmount: Double,
    @ColumnInfo(name = "targetAmount") val targetAmount: Double,
    @ColumnInfo(name = "endDate") val endDate: String
)

