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

class ExpenseLimitAdapter(
    context: Context,
    private var limits: MutableList<ExpenseLimitData>,
    private val onItemClick: (ExpenseLimitData) -> Unit
) : RecyclerView.Adapter<ExpenseLimitAdapter.LimitViewHolder>() {

    val db = Room.databaseBuilder(
        context,
        TransactionDatabase::class.java, "transaction.db"
    ).fallbackToDestructiveMigration().build()

    class LimitViewHolder(view: View, val onItemClick: (Int) -> Unit) : RecyclerView.ViewHolder(view) {
        val limitName: TextView = view.findViewById(R.id.limitName)
        val limitExpense: TextView = view.findViewById(R.id.limitExpense)
        val limitDate: TextView = view.findViewById(R.id.limitDate)
        val limitProgressBar: ProgressBar = view.findViewById(R.id.limitProgressBar)
        val limitProgressText: TextView = view.findViewById(R.id.limitProgressText)
        val limitItemContainer: LinearLayout = view.findViewById(R.id.limitItemContainer)
        val dueDatePassedNotice: TextView = view.findViewById(R.id.dueDatePassedNotice)

        init {
            view.setOnClickListener {
                onItemClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LimitViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.limit_item, parent, false)
        return LimitViewHolder(view) { position ->
            onItemClick(limits[position])
        }
    }

    override fun onBindViewHolder(holder: LimitViewHolder, position: Int) {
        runBlocking(Dispatchers.Default) {
            val limit = limits[position]
            holder.limitName.text = limit.name
            holder.limitExpense.text = "Expense: ${limit.targetExpense}"
            holder.limitDate.text = "Due: ${limit.endDate}"

            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dueDate = sdf.parse(limit.endDate)
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
                val progress = ((limit.currentExpense / limit.targetExpense) * 100).toInt()
                if (progress < 100) {
                    holder.limitItemContainer.setBackgroundColor(Color.parseColor("#B9F6CA"))
                    holder.dueDatePassedNotice.text = "Limit not exceeded!"
                    holder.dueDatePassedNotice.setTextColor(Color.parseColor("388E3C"))
                    holder.limitProgressText.setTextColor(Color.parseColor("388E3C"))
                } else {
                    holder.limitItemContainer.setBackgroundColor(Color.parseColor("#FFEBEE"))
                    holder.dueDatePassedNotice.text = "Limit exceeded"
                    holder.dueDatePassedNotice.setTextColor(Color.parseColor("D32F2F"))
                    holder.limitProgressText.setTextColor(Color.parseColor("D32F2F"))
                }
                holder.dueDatePassedNotice.visibility = View.VISIBLE
                holder.limitProgressBar.progress = progress
                holder.limitProgressText.text = "${progress}% of ${limit.targetExpense} (${limit.currentExpense}/${limit.targetExpense})"
            } else {
                val currentExpense = db.transactionDAO().calculateTotalExpense()
                val updatedLimit = ExpenseLimitData(
                    id = limit.id,
                    name = limit.name,
                    currentExpense = currentExpense,
                    targetExpense = limit.targetExpense,
                    endDate = limit.endDate
                )
                updateLimit(updatedLimit)
                val progress = ((limit.currentExpense / limit.targetExpense) * 100).toInt()
                holder.limitItemContainer.setBackgroundColor(Color.WHITE)
                holder.dueDatePassedNotice.visibility = View.GONE
                holder.limitProgressBar.progress = progress
                holder.limitProgressText.text = "${progress}% of ${limit.targetExpense} (${limit.currentExpense}/${limit.targetExpense})"
            }
        }
    }

    override fun getItemCount(): Int = limits.size

    fun addLimit(limit: ExpenseLimitData) {
        limits.add(limit)
        notifyItemInserted(limits.size - 1)
    }

    fun removeLimit(limit: ExpenseLimitData) {
        val position = limits.indexOf(limit)
        if (position > -1) {
            limits.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    private fun updateLimit(updatedLimit: ExpenseLimitData) {
        val position = limits.indexOfFirst { it.id == updatedLimit.id }
        if (position > -1) {
            limits[position] = updatedLimit
        }
    }

    fun updateData(newLimits: List<ExpenseLimitData>) {
        limits.clear()
        limits.addAll(newLimits)
        notifyDataSetChanged()
    }
}

