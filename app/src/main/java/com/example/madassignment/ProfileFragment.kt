package com.example.madassignment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.madassignment.R

class ProfileFragment : Fragment() {

    private lateinit var textViewUserName: TextView
    private lateinit var textViewEmail: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var btnChangePassword: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        textViewUserName = view.findViewById(R.id.TVProfileUsername)
        textViewEmail = view.findViewById(R.id.TVProfile2)

        fetchUserProfile()

        btnChangePassword = view.findViewById(R.id.BtnChgPassword)
        btnChangePassword.setOnClickListener {
            showChangePasswordDialog()
        }

        val btnBackToLogin: Button = view.findViewById(R.id.BtnLogOut)
        btnBackToLogin.setOnClickListener {
            auth.signOut()
            val intent = Intent(activity, LoginPage::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            activity?.finish()
            showToast("Logged out successfully")
        }
        return view
    }

    private fun fetchUserProfile() {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val userId = currentUser.uid
            val userDocument = firestore.collection("users").document(userId)

            userDocument.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val userName = documentSnapshot.getString("name")
                        val userEmail = currentUser.email
                        textViewUserName.text = userName
                        textViewEmail.text = userEmail
                    } else {
                        showToast("User document not found in Firestore")
                    }
                }
                .addOnFailureListener { e ->
                    showToast("Failed to fetch user data from Firestore: ${e.message}")
                }
        } else {
            showToast("User not signed in")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showChangePasswordDialog() {
        val inflater = LayoutInflater.from(requireContext())
        val dialogView = inflater.inflate(R.layout.dialog_change_password, null)

        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Change Password")
            .setView(dialogView)
            .setPositiveButton("Change") { _, _ ->
                val etCurrentPassword = dialogView.findViewById<EditText>(R.id.etCurrentPassword)
                val etNewPassword = dialogView.findViewById<EditText>(R.id.etNewPassword)

                val currentPassword = etCurrentPassword.text.toString()
                val newPassword = etNewPassword.text.toString()

                val currentUser = FirebaseAuth.getInstance().currentUser
                if (currentUser != null) {
                    val credential = EmailAuthProvider.getCredential(currentUser.email!!, currentPassword)
                    currentUser.reauthenticate(credential)
                        .addOnSuccessListener {
                            currentUser.updatePassword(newPassword)
                                .addOnSuccessListener {
                                    showToast("Password changed successfully")
                                }
                                .addOnFailureListener { e ->
                                    showToast("Failed to change password: ${e.message}")
                                }
                        }
                        .addOnFailureListener { e ->
                            showToast("Failed to reauthenticate user: ${e.message}")
                        }
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        alertDialog.show()
    }
}