package com.project.fintrack

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import kotlinx.coroutines.launch

class SavingsGoalsActivity : AppCompatActivity() {
    private lateinit var goalsRecyclerView: RecyclerView
    private lateinit var adapter: SavingsGoalAdapter
    private lateinit var db: TransactionDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_savings_goals)

        db = Room.databaseBuilder(
            applicationContext,
            TransactionDatabase::class.java, "transaction.db"
        ).fallbackToDestructiveMigration().build()

        goalsRecyclerView = findViewById<RecyclerView>(R.id.goalsRecyclerView).apply {
            layoutManager = LinearLayoutManager(this@SavingsGoalsActivity)
        }

        adapter = SavingsGoalAdapter(applicationContext, mutableListOf()) { goal ->
            showEditDeleteDialog(goal)
        }
        goalsRecyclerView.adapter = adapter

        val addGoalButton = findViewById<Button>(R.id.addGoalButton)
        addGoalButton.setOnClickListener {
            startActivity(Intent(this, AddGoalActivity::class.java))
        }

        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        loadGoals()
    }

    private fun loadGoals() {
        lifecycleScope.launch {
            val goals = db.savingsGoalDAO().getAllGoals()
            Log.d("SavingsGoalsActivity", "Loading goals: ${goals.size} found")
            if (goals.isNotEmpty()) {
                runOnUiThread {
                    adapter.updateData(goals)
                }
            }
        }
    }

    private fun showEditDeleteDialog(goal: SavingsGoalData) {
        val options = arrayOf("Edit", "Delete")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose an option")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> startEditGoalActivity(goal)
                1 -> confirmAndDeleteGoal(goal)
            }
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }

    private fun startEditGoalActivity(goal: SavingsGoalData) {
        val intent = Intent(this, EditGoalActivity::class.java)
        intent.putExtra("GOAL_ID", goal.id)
        startActivity(intent)
    }

    private fun confirmAndDeleteGoal(goal: SavingsGoalData) {
        AlertDialog.Builder(this).apply {
            setTitle("Confirm Delete")
            setMessage("Are you sure you want to delete this goal?")
            setPositiveButton("Delete") { _, _ ->
                deleteGoal(goal)
            }
            setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            show()
        }
    }

    private fun deleteGoal(goal: SavingsGoalData) {
        lifecycleScope.launch {
            db.savingsGoalDAO().deleteGoal(goal)
            runOnUiThread {
                adapter.removeGoal(goal)
            }
            Toast.makeText(this@SavingsGoalsActivity, "Goal deleted successfully", Toast.LENGTH_SHORT).show()
        }
    }

}

