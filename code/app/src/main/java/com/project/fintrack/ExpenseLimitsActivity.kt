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

class ExpenseLimitsActivity : AppCompatActivity() {
        private lateinit var limitsRecyclerView: RecyclerView
        private lateinit var adapter: ExpenseLimitAdapter
        private lateinit var db: TransactionDatabase

        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_expense_limits)

                db = Room.databaseBuilder(
                        applicationContext,
                        TransactionDatabase::class.java, "transaction.db"
                ).fallbackToDestructiveMigration().build()

                limitsRecyclerView = findViewById<RecyclerView>(R.id.limitsRecyclerView).apply {
                        layoutManager = LinearLayoutManager(this@ExpenseLimitsActivity)
                }

                adapter = ExpenseLimitAdapter(applicationContext, mutableListOf()) { limit ->
                        showEditDeleteDialog(limit)
                }
                limitsRecyclerView.adapter = adapter

                val addLimitButton = findViewById<Button>(R.id.addLimitButton)
                addLimitButton.setOnClickListener {
                        startActivity(Intent(this, AddLimitActivity::class.java))
                }

                val backButton = findViewById<ImageButton>(R.id.backButton)
                backButton.setOnClickListener {
                        finish()
                }
        }

        override fun onResume() {
                super.onResume()
                loadLimits()
        }

        private fun loadLimits() {
                lifecycleScope.launch {
                        val limits = db.expenseLimitDAO().getAllLimits()
                        Log.d("ExpenseLimitsActivity", "Loading limits: ${limits.size} found")
                        if (limits.isNotEmpty()) {
                                runOnUiThread {
                                        adapter.updateData(limits)
                                }
                        }
                }
        }

        private fun showEditDeleteDialog(limit: ExpenseLimitData) {
                val options = arrayOf("Edit", "Delete")
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Choose an option")
                builder.setItems(options) { _, which ->
                        when (which) {
                                0 -> startEditLimitActivity(limit)
                                1 -> confirmAndDeleteLimit(limit)
                        }
                }
                builder.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                builder.create().show()
        }

        private fun startEditLimitActivity(limit: ExpenseLimitData) {
                val intent = Intent(this, EditLimitActivity::class.java)
                intent.putExtra("LIMIT_ID", limit.id)
                startActivity(intent)
        }

        private fun confirmAndDeleteLimit(limit: ExpenseLimitData) {
                AlertDialog.Builder(this).apply {
                        setTitle("Confirm Delete")
                        setMessage("Are you sure you want to delete this limit?")
                        setPositiveButton("Delete") { _, _ ->
                                deleteLimit(limit)
                        }
                        setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                        show()
                }
        }

        private fun deleteLimit(limit: ExpenseLimitData) {
                lifecycleScope.launch {
                        db.expenseLimitDAO().deleteLimit(limit)
                        runOnUiThread {
                                adapter.removeLimit(limit)
                        }
                        Toast.makeText(this@ExpenseLimitsActivity, "Limit deleted successfully", Toast.LENGTH_SHORT).show()
                }
        }

}

