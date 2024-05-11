package com.project.fintrack.authentication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.project.fintrack.R
import com.project.fintrack.databinding.ActivityRegisterBinding

class Register : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firebaseAuth = FirebaseAuth.getInstance()
        binding.btnRegister.setOnClickListener {

            val userName = binding.etUsername.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()
            // REGISTER ERROR STATUS
            if (userName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty() && password.count() >= 4 && confirmPassword == password) {

                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        findViewById<ProgressBar>(R.id.pbRegister).visibility = View.VISIBLE
                        Toast.makeText(this, "Registration Success!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, Login::class.java))
                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_LONG).show()
                    }
                }
            }
            else {
                if (userName.isEmpty()) {
                    binding.tvUsername.apply {
                        isErrorEnabled = true
                        error = "Please fill in your desired username"
                    }
                    binding.tvEmail.apply { isErrorEnabled = false }
                    binding.tvPassword.apply { isErrorEnabled = false }
                    binding.tvConfirmPassword.apply { isErrorEnabled = false }
                }

                else if (email.isEmpty()) {
                    binding.tvEmail.apply {
                        isErrorEnabled = true
                        error = "Please fill in email"
                    }
                    binding.tvUsername.apply { isErrorEnabled = false }
                    binding.tvPassword.apply { isErrorEnabled = false }
                    binding.tvConfirmPassword.apply { isErrorEnabled = false }
                }
                else if (password.isEmpty()) {
                    binding.tvPassword.apply {
                        isErrorEnabled = true
                        error = "Please fill in password"
                    }
                    binding.tvUsername.apply { isErrorEnabled = false }
                    binding.tvEmail.apply { isErrorEnabled = false }
                    binding.tvConfirmPassword.apply { isErrorEnabled = false }
                }

                else if (confirmPassword.isEmpty()) {
                    binding.tvConfirmPassword.apply {
                        isErrorEnabled = true
                        error = "Please confirm your password"
                    }
                    binding.tvUsername.apply { isErrorEnabled = false }
                    binding.tvEmail.apply { isErrorEnabled = false }
                    binding.tvPassword.apply { isErrorEnabled = false }
                }
                else if (password.count() < 4) {
                    binding.tvPassword.apply {
                        isErrorEnabled = true
                        error = "password too short"
                    }
                    binding.tvUsername.apply { isErrorEnabled = false }
                    binding.tvEmail.apply { isErrorEnabled = false }
                    binding.tvConfirmPassword.apply { isErrorEnabled = false }
                }
                else if (password != confirmPassword) {
                    binding.tvConfirmPassword.apply {
                        isErrorEnabled = true
                        error = "invalid confirm password"
                    }
                    binding.tvUsername.apply { isErrorEnabled = false }
                    binding.tvEmail.apply { isErrorEnabled = false }
                    binding.tvPassword.apply { isErrorEnabled = false }
                }
            }
            // END OF REGISTER ERROR STATUS
        }
        // TO LOGIN
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        btnLogin.setOnClickListener() {
            startActivity(Intent(this, Login::class.java))
        }
        // END OF TO LOGIN
    }
}