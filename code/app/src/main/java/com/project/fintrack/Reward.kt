package com.project.fintrack

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isInvisible
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.google.android.material.button.MaterialButton
import com.project.fintrack.databinding.ActivityRewardBinding
import kotlinx.coroutines.launch

//import nl.dionsegijn.konfetti.KonfettiView
//import nl.dionsegijn.konfetti.models.Shape
//import nl.dionsegijn.konfetti.models.Size

class Reward : AppCompatActivity() {

    private lateinit var binding: ActivityRewardBinding
    private lateinit var db: TransactionDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRewardBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        db = Room.databaseBuilder(
            applicationContext,
            TransactionDatabase::class.java, "transaction.db"
        ).fallbackToDestructiveMigration().build()


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        var temp: String? = ""
        var temp2: String? = ""
        lifecycleScope.launch {
            try {

                val targetGoal = db.savingsGoalDAO().getLastInsertedGoal()
                temp = targetGoal.currentAmount.toString()
                temp2 = targetGoal.targetAmount.toString()
                var count: Int = 0
                runOnUiThread {
                    if (targetGoal.currentAmount >= targetGoal.targetAmount) {
                        if (count == 0) {
                            binding.tvReminder.visibility = View.INVISIBLE
                            binding.tvAch1.visibility = View.VISIBLE
                            binding.ivAch1.visibility = View.VISIBLE
                            display()
                            Toast.makeText(this@Reward, "Congratulations!! New Achievement!!", Toast.LENGTH_SHORT).show()
                            count += 1
                        }
                        else if (count == 1) {
                            //binding.tvReminder.visibility = View.INVISIBLE
                            binding.tvAch2.visibility = View.VISIBLE
                            binding.ivAch2.visibility = View.VISIBLE
                            display()
                            Toast.makeText(this@Reward, "Congratulations!! New Achievement!!", Toast.LENGTH_SHORT).show()
                            count += 1
                        }
                        else {
                            //binding.tvReminder.visibility = View.INVISIBLE
                            binding.tvAch3.visibility = View.VISIBLE
                            binding.ivAch3.visibility = View.VISIBLE
                            display()
                            Toast.makeText(this@Reward, "Congratulations!! New Achievement!!", Toast.LENGTH_SHORT).show()
                        }

                    }
                    else {
                        display()
                        Toast.makeText(this@Reward, "The goal hasn't reached yet, keep on fighting!!", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@Reward, "Failed to find target goal: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.btnCheckReward.setOnClickListener() {
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.reward_dialog)
            dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))


            val btnReceive = dialog.findViewById<MaterialButton>(R.id.mbtnReceive)
            val btnReturn = dialog.findViewById<MaterialButton>(R.id.mbtnReturn)
            val tvCurrent = dialog.findViewById<TextView>(R.id.tvCurrent)
            val tvGoal = dialog.findViewById<TextView>(R.id.tvGoal)
            tvCurrent.text = temp
            tvGoal.text = temp2

            btnReceive.setOnClickListener() {
                //if (tvCurrent.text.toString().toInt() >= tvGoal.text.toString().toInt()) {
                    Toast.makeText(this, "Reward received!", Toast.LENGTH_SHORT).show()
                //}
                //else {
                    //Toast.makeText(this, "Not enough for a reward for now", Toast.LENGTH_SHORT).show()
                //}

            }
            btnReturn.setOnClickListener() {
                dialog.dismiss()

            }
            dialog.show()
        }
        binding.backButton.setOnClickListener() {
            finish()
        }
        display()

    }

    private fun display() {
        if (binding.ivAch3.isInvisible) {
            if (binding.ivAch2.isInvisible) {
                if (binding.ivAch1.isInvisible) {
                    binding.tvLines.text = "You can do it!! Reach more goals to save more money!!"
                } else binding.tvLines.text = "Great job!! You've earned your first badge!! Keep it up!!"
            } else binding.tvLines.text = "2 dowm! One more left!!"
        } else binding.tvLines.text = "You are a master at financial management!!"
    }
}