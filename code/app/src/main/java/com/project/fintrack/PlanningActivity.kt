package com.project.fintrack

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.project.fintrack.databinding.ActivityPlanningBinding

class PlanningActivity : AppCompatActivity() {
    //private lateinit var binding: ActivityPlanningBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_planning)

        val savingsGoalsButton = findViewById<Button>(R.id.savingsGoalsButton)
        savingsGoalsButton.setOnClickListener {
            val intent = Intent(this, SavingsGoalsActivity::class.java)
            startActivity(intent)
        }

        val expenseLimitsButton = findViewById<Button>(R.id.expenseLimitsButton)
        expenseLimitsButton.setOnClickListener {
            val intent = Intent(this, ExpenseLimitsActivity::class.java)
            startActivity(intent)
        }

        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btnReward).setOnClickListener() {
            startActivity(Intent(this, Reward::class.java))
        }
    }
}
