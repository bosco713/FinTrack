package com.project.fintrack

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.room.Room
import kotlinx.coroutines.runBlocking

class SettingDeleteEntryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_setting_delete_entry)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //
        val returnButton = findViewById<ImageButton>(R.id.setting_deleteEntry_button_return)
        returnButton.setOnClickListener{
            Intent(this, MainActivity::class.java).also {
                startActivity(it)
            }
        }
        //
        val db = Room.databaseBuilder(MainActivity.applicationContext()
            , TransactionDatabase::class.java, "transaction.db").build()
        // for printing
        var transactionDatas: List<TransactionData> = emptyList()
        runBlocking {
            transactionDatas = db.transactionDAO().loadLastTenData()
        }
        val llList = findViewById<LinearLayout>(R.id.setting_deleteEntry_linearLayout)
        var id2index = mutableListOf<Long>()
        for (index: Int in transactionDatas.indices) {
            val horizontalLayout = LinearLayout(this)
            val idTextView = TextView(this)
            idTextView.text = "${transactionDatas[index].transactionId.toString()}"
            idTextView.width = 400
            val isExpTextView = TextView(this)
            isExpTextView.text = "${transactionDatas[index].isExpense.toString()}"
            isExpTextView.width = 200
            val categoryTextView = TextView(this)
            categoryTextView.text = "${transactionDatas[index].transactionCategory.toString()}"
            categoryTextView.width = 350
            val amountTextView = TextView(this)
            amountTextView.text = "${transactionDatas[index].transactionAmount.toString()}"
            amountTextView.width = 200
            horizontalLayout.addView(idTextView)
            horizontalLayout.addView(isExpTextView)
            horizontalLayout.addView(categoryTextView)
            horizontalLayout.addView(amountTextView)
            llList.addView(horizontalLayout)
            id2index = id2index.plus(transactionDatas[index].transactionId).toMutableList()
        }
        // Delete entry
        val deleteButton = findViewById<Button>(R.id.setting_deleteEntry_button_delete)
        val etID = findViewById<EditText>(R.id.setting_deleteEntry_etID)
        val warningText = findViewById<TextView>(R.id.setting_deleteEntry_warningText)
        deleteButton.setOnClickListener {
            val id = etID.text.toString()
            if (id == "") {
                warningText.text = "Your input is empty"
            } else if (transactionDatas.isEmpty()) {
                warningText.text = "No entry to be deleted"
            } else if (!id2index.contains(id.toLong())) {
                warningText.text = "Your input ID does not exist"
            } else {
                warningText.text = ""
                val index = id2index.indexOf(id.toLong())
                runBlocking {
                    db.transactionDAO().delete(transactionDatas[index])
                }
                // for printing
                transactionDatas = emptyList()
                runBlocking {
                    transactionDatas = db.transactionDAO().loadLastTenData()
                }
                llList.removeAllViews()
                for (index: Int in transactionDatas.indices) {
                    val horizontalLayout = LinearLayout(this)
                    val idTextView = TextView(this)
                    idTextView.text = "${transactionDatas[index].transactionId.toString()}"
                    idTextView.width = 400
                    val isExpTextView = TextView(this)
                    isExpTextView.text = "${transactionDatas[index].isExpense.toString()}"
                    isExpTextView.width = 200
                    val categoryTextView = TextView(this)
                    categoryTextView.text = "${transactionDatas[index].transactionCategory.toString()}"
                    categoryTextView.width = 350
                    val amountTextView = TextView(this)
                    amountTextView.text = "${transactionDatas[index].transactionAmount.toString()}"
                    amountTextView.width = 200
                    horizontalLayout.addView(idTextView)
                    horizontalLayout.addView(isExpTextView)
                    horizontalLayout.addView(categoryTextView)
                    horizontalLayout.addView(amountTextView)
                    llList.addView(horizontalLayout)
                }
                etID.text.clear()
            }
        }
    }
}