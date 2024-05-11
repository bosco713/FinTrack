package com.project.fintrack.authentication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.project.fintrack.Information
import com.project.fintrack.MainActivity
import com.project.fintrack.R
import com.project.fintrack.databinding.ActivityLoginBinding

class Login : AppCompatActivity() {
    // FIREBASE CLIENT CONNECTION
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setTheme(R.style.Theme_Project)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // AUTHENTICATION ACCESS
        firebaseAuth = FirebaseAuth.getInstance()
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please input both your username and password.", Toast.LENGTH_LONG).show()
            }
            else if (password.count() > 15) {
                Toast.makeText(this, "Password should be less than 15 alphanumerical characters", Toast.LENGTH_LONG).show()
            }
            else {
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        findViewById<ProgressBar>(R.id.pbLoading).visibility = View.VISIBLE
                        Toast.makeText(this, "Signed In!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                    } else {
                        Toast.makeText(this, "invalid name or password", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        // END OF AUTHENTICATION ACCESS

        val btnRegister = findViewById<Button>(R.id.btnRegister)
        btnRegister.setOnClickListener() {
            startActivity(Intent(this, Register::class.java))
        }

        findViewById<TextView>(R.id.tvEmailURL).setOnClickListener() {
            startActivity(Intent(this, Information::class.java))
        }

    }
    // user
    private fun saveData() {
        val etUserName = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val username = etUserName.text.toString()
    }
}