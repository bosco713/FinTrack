package com.project.fintrack

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*

class SavingsGoalAdapter(
    context: Context,
    private var goals: MutableList<SavingsGoalData>,
    private val onItemClick: (SavingsGoalData) -> Unit
) : RecyclerView.Adapter<SavingsGoalAdapter.GoalViewHolder>() {

    val db = Room.databaseBuilder(
        context,
        TransactionDatabase::class.java, "transaction.db"
    ).fallbackToDestructiveMigration().build()

    class GoalViewHolder(view: View, val onItemClick: (Int) -> Unit) : RecyclerView.ViewHolder(view) {
        val goalName: TextView = view.findViewById(R.id.goalName)
        val goalAmount: TextView = view.findViewById(R.id.goalAmount)
        val goalDate: TextView = view.findViewById(R.id.goalDate)
        val goalProgressBar: ProgressBar = view.findViewById(R.id.goalProgressBar)
        val goalProgressText: TextView = view.findViewById(R.id.goalProgressText)
        val goalItemContainer: LinearLayout = view.findViewById(R.id.goalItemContainer)
        val dueDatePassedNotice: TextView = view.findViewById(R.id.dueDatePassedNotice)

        init {
            view.setOnClickListener {
                onItemClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.goal_item, parent, false)
        return GoalViewHolder(view) { position ->
            onItemClick(goals[position])
        }
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        runBlocking(Dispatchers.Default) {
            val goal = goals[position]
            holder.goalName.text = goal.name
            holder.goalAmount.text = "Amount: ${goal.targetAmount}"
            holder.goalDate.text = "Due: ${goal.endDate}"

            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dueDate = sdf.parse(goal.endDate)
            val calendar = Calendar.getInstance()

            if (dueDate != null) {
                calendar.time = dueDate
                calendar.add(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            }

            val adjustedDueDate = calendar.time
            val currentDate = Calendar.getInstance().time

            if (currentDate.after(adjustedDueDate)) {
                val progress = ((goal.currentAmount / goal.targetAmount) * 100).toInt()
                if (progress >= 100) {
                    holder.goalItemContainer.setBackgroundColor(Color.parseColor("#B9F6CA"))
                    holder.dueDatePassedNotice.text = "Goal achieved!"
                    holder.dueDatePassedNotice.setTextColor(Color.parseColor("388E3C"))
                    holder.goalProgressText.setTextColor(Color.parseColor("388E3C"))
                } else {
                    holder.goalItemContainer.setBackgroundColor(Color.parseColor("#FFEBEE"))
                    holder.dueDatePassedNotice.text = "Goal not achieved"
                    holder.dueDatePassedNotice.setTextColor(Color.parseColor("D32F2F"))
                    holder.goalProgressText.setTextColor(Color.parseColor("D32F2F"))
                }
                holder.dueDatePassedNotice.visibility = View.VISIBLE
                holder.goalProgressBar.progress = progress
                holder.goalProgressText.text = "${progress}% of ${goal.targetAmount} (${goal.currentAmount}/${goal.targetAmount})"
            } else {
                val currentAmount = db.transactionDAO().calculateNetAmount()
                val updatedGoal = SavingsGoalData(
                    id = goal.id,
                    name = goal.name,
                    currentAmount = currentAmount,
                    targetAmount = goal.targetAmount,
                    endDate = goal.endDate
                )
                updateGoal(updatedGoal)
                val progress = ((goal.currentAmount / goal.targetAmount) * 100).toInt()
                holder.goalItemContainer.setBackgroundColor(Color.WHITE)
                holder.dueDatePassedNotice.visibility = View.GONE
                holder.goalProgressBar.progress = progress
                holder.goalProgressText.text = "${progress}% of ${goal.targetAmount} (${goal.currentAmount}/${goal.targetAmount})"
            }
        }
    }

    override fun getItemCount(): Int = goals.size

    fun addGoal(goal: SavingsGoalData) {
        goals.add(goal)
        notifyItemInserted(goals.size - 1)
    }

    fun removeGoal(goal: SavingsGoalData) {
        val position = goals.indexOf(goal)
        if (position > -1) {
            goals.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    private fun updateGoal(updatedGoal: SavingsGoalData) {
        val position = goals.indexOfFirst { it.id == updatedGoal.id }
        if (position > -1) {
            goals[position] = updatedGoal
        }
    }

    fun updateData(newGoals: List<SavingsGoalData>) {
        goals.clear()
        goals.addAll(newGoals)
        notifyDataSetChanged()
    }
}

