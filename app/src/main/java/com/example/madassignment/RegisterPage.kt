package com.example.madassignment

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

class RegisterPage : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var nameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registerpage)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        // Aligning IDs with XML layout file
        emailEditText = findViewById(R.id.ETRegisterEmailAddress)
        nameEditText = findViewById(R.id.ETRegisterUsername)
        passwordEditText = findViewById(R.id.ETRegisterCreatePassword)
        registerButton = findViewById(R.id.BtnRegister)

        registerButton.setOnClickListener {
            // Get user input
            val email = emailEditText.text.toString()
            val name = nameEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Validate email format
            if (!isValidEmail(email)) {
                Toast.makeText(
                    this@RegisterPage,
                    "Invalid email address format. Please enter a valid email.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Perform user registration using Firebase Auth
            registerUserWithFirebase(email, password, name)
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun registerUserWithFirebase(email: String, password: String, name: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val currentUser = auth.currentUser

                    // Update user profile with name
                    val profileUpdates = userProfileChangeRequest {
                        displayName = name
                    }

                    currentUser?.updateProfile(profileUpdates)?.addOnCompleteListener { profileUpdateTask ->
                        if (profileUpdateTask.isSuccessful) {
                            // Registration successful

                            // Store additional user information to Firestore
                            val userId = currentUser.uid
                            val user = hashMapOf(
                                "name" to name,
                                "email" to email
                                // Add other fields as needed
                            )

                            firestore.collection("users").document(userId)
                                .set(user)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        this@RegisterPage,
                                        "Registration successful",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    val intent = Intent(this@RegisterPage, LoginPage::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(
                                        this@RegisterPage,
                                        "Failed to save user information",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    e.printStackTrace()
                                }
                        } else {
                            // Failed to update profile
                            Toast.makeText(
                                this@RegisterPage,
                                "Failed to update user profile",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    // Registration failed
                    Toast.makeText(
                        this@RegisterPage,
                        "Registration failed. Please try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}
