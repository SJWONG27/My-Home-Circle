package com.example.madassignment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.Toast
import androidx.navigation.Navigation
import kotlin.random.Random

class CreateCommunityFragment : Fragment() {
    private val TAG = "CreateCommunityFragment"

    private lateinit var communityViewModel: CommunityViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_community, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        communityViewModel = ViewModelProvider(requireActivity()).get(CommunityViewModel::class.java)

        val etGroupName = view.findViewById<EditText>(R.id.ETAddCommunity1)
        val etResidentialName = view.findViewById<EditText>(R.id.ETAddCommunity2)
        val btnCreateGroup = view.findViewById<Button>(R.id.BtnCreateGroup)

        btnCreateGroup.setOnClickListener {
            val groupName = etGroupName.text.toString().trim()
            val residentialName = etResidentialName.text.toString().trim()

            // Check if the user has already joined a community
            if (communityViewModel.isJoinedCommunity()) {
                showToast("You are already a member of a community.")
            } else {
                // Check if group name is not empty before creating the community
                if (groupName.isNotEmpty()) {
                    createCommunity(groupName, residentialName)
                } else {
                    // Show an error message or handle the case where the group name is empty
                    showToast("Group name cannot be empty")
                }
            }
        }
    }

    private fun generateCommunityCode(): String {
        val characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..6)
            .map { characters.random() }
            .joinToString("")
    }

    private fun createCommunity(groupName: String, residentialName: String) {
        val firestore = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId == null) {
            showToast("User not authenticated")
            return
        }

        // Check if the user is already a member of a community
        if (communityViewModel.isJoinedCommunity()) {
            showToast("You are already a member of a community.")
            return
        }

        // Generate a unique community ID
        val communityId = firestore.collection("communities").document().id

        // Create a unique chat collection for the community
        val chatCollection = "chats_$communityId"

        val communityCode = generateCommunityCode()

        val communityData = hashMapOf(
            "groupName" to groupName,
            "residentialName" to residentialName,
            "members" to mutableListOf(userId),
            "communityCode" to communityCode
        )

        firestore.collection("communities").document(communityId)
            .set(communityData)
            .addOnSuccessListener {
                Log.d(TAG, "Community information saved to Firestore")
                showToast("Community created! Code: $communityCode")

                // Automatically join the user to the community
                joinUserToCommunity(userId, communityId, groupName, communityCode, chatCollection)
                communityViewModel.groupName.value = groupName
                communityViewModel.groupCode.value = communityCode
                communityViewModel.notifyObservers()

                // Navigate back to the DestCommunity fragment
                view?.let {
                    Navigation.findNavController(it).navigate(R.id.DestCommunity)
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error saving community information to Firestore", e)
                showToast("Failed to create community")
            }
    }


    private fun joinUserToCommunity(
        userId: String,
        communityId: String,
        groupName: String,
        communityCode: String,
        chatCollection: String
    ) {
        val firestore = FirebaseFirestore.getInstance()

        // Update user document with communityId
        firestore.collection("users")
            .document(userId)
            .update("communityId", communityId)
            .addOnSuccessListener {
                // Add the user to the community members
                firestore.collection("communities")
                    .document(communityId)
                    .update("members", FieldValue.arrayUnion(userId))
                    .addOnSuccessListener {
                        // Update ViewModel with joined group details
                        communityViewModel.groupName.value = groupName
                        communityViewModel.groupCode.value = communityCode
                        communityViewModel.notifyObservers()

                        // Create a unique chat collection for the community
                        firestore.collection(chatCollection).document()

                        showToast("Joined group successfully!")
                        // You can add more UI updates here if needed
                    }
                    .addOnFailureListener { showToast("Failed to join group") }
            }
            .addOnFailureListener { showToast("Failed to join group") }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
