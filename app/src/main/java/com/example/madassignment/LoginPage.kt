package com.example.madassignment

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginPage : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var forgotPasswordTextView: TextView
    private lateinit var registerTextView: TextView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loginpage)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Aligning IDs with XML layout file
        emailEditText = findViewById(R.id.ETLoginEmailAddress)
        passwordEditText = findViewById(R.id.ETLoginPassword)
        loginButton = findViewById(R.id.BtnLoginSignIn)
        forgotPasswordTextView = findViewById(R.id.BtnChangePassword)
        registerTextView = findViewById(R.id.BtnLoginRegisterNow)

        loginButton.setOnClickListener {
            // Get user input
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Perform user login using Firebase Auth
            loginUserWithFirebase(email, password)
        }

        // Set up the clickable text for navigation to RegisterPage
        val registerText = "Don't have an account? Register here."
        val spannableString = SpannableString(registerText)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Handle click, navigate to RegisterPage
                val intent = Intent(this@LoginPage, RegisterPage::class.java)
                startActivity(intent)
            }
        }

        spannableString.setSpan(clickableSpan, registerText.indexOf("Register"), registerText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        registerTextView.text = spannableString
        registerTextView.movementMethod = LinkMovementMethod.getInstance()

        // Set up the clickable text for initiating password reset
        val forgotPasswordText = "Forgot Password?"
        val forgotPasswordSpannable = SpannableString(forgotPasswordText)

        val forgotPasswordClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Handle click, initiate password reset
                val email = emailEditText.text.toString()
                sendPasswordResetEmail(email)
            }
        }

        forgotPasswordSpannable.setSpan(forgotPasswordClickableSpan, 0, forgotPasswordText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        forgotPasswordTextView.text = forgotPasswordSpannable
        forgotPasswordTextView.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun loginUserWithFirebase(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Login successful
                    Toast.makeText(this@LoginPage, "Login successful", Toast.LENGTH_SHORT).show()

                    // Redirect to the main app screen (modify this accordingly)
                    val intent = Intent(this@LoginPage, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this@LoginPage, "Login failed. Please check your credentials.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@LoginPage, "Password reset email sent. Check your inbox.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@LoginPage, "Failed to send password reset email. ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
