package com.example.madassignment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView


class CommunityFragment : Fragment() {

    private lateinit var communityViewModel: CommunityViewModel
    private lateinit var etCommunityCode: EditText
    private lateinit var tvJoinGroupName: TextView
    private lateinit var tvJoinGroupCode: TextView
    private lateinit var joinGroupDetailsLayout: LinearLayout
    private lateinit var BtnCommunityChat: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Use ViewModelProvider.AndroidViewModelFactory for shared ViewModelStore
        communityViewModel = ViewModelProvider(
            requireActivity().viewModelStore,
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
        ).get(CommunityViewModel::class.java)
        return inflater.inflate(R.layout.fragment_community, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etCommunityCode = view.findViewById(R.id.ETCommunity1)
        tvJoinGroupName = view.findViewById(R.id.TVJoinGroupName)
        tvJoinGroupCode = view.findViewById(R.id.TVJoinGroupCode)
        joinGroupDetailsLayout = view.findViewById(R.id.joinGroupDetailsLayout)

        // Inside onViewCreated method
        val btnReport = view.findViewById<Button>(R.id.BtnReport)
        btnReport.setOnClickListener {
            // Use the Navigation component to navigate to the specified destination
            Navigation.findNavController(requireView()).navigate(R.id.DestReportComplaint)
        }

        val btnAddCommunity = view.findViewById<FloatingActionButton>(R.id.FBtnCreateGroup)
        btnAddCommunity.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.createCommunityFragment)
        }

        val btnJoinGroup = view.findViewById<Button>(R.id.BtnJoinGroup)
        btnJoinGroup.setOnClickListener {
            val communityCode = etCommunityCode.text.toString().trim()
            if (communityCode.isNotEmpty()) {
                joinGroup(communityCode)
            } else {
                showToast("Please enter a valid community code")
            }
        }

        val btnLeaveGroup = view.findViewById<Button>(R.id.btnLeaveGroup)
        btnLeaveGroup.setOnClickListener {
            leaveGroup(joinGroupDetailsLayout)
        }

        // Check if the user is a member of a community and update UI
        updateUIIfJoinedCommunity()

        BtnCommunityChat = view.findViewById<Button>(R.id.BtnCommunityChat)
        BtnCommunityChat.setOnClickListener {
            navigateToCommunityChats()
        }

    }

    private fun navigateToCommunityChats() {
        // Use the Navigation component to navigate to the specified destination
        Navigation.findNavController(requireView()).navigate(R.id.DestCommunityChats);
    }

    private fun updateUIIfJoinedCommunity(groupName: String?, groupCode: String?) {
        if (groupName != null && groupCode != null) {
            // User has joined a community before, update UI with saved details
            tvJoinGroupName.text = "Group Name: $groupName"
            tvJoinGroupCode.text = "Group Code: $groupCode"
            joinGroupDetailsLayout.visibility = View.VISIBLE

            // Enable the button since the user has a community
            BtnCommunityChat.isEnabled = true
        } else {
            // User is not a member of any community
            // Display "None" in the top two text views
            tvJoinGroupName.text = "Group Name: None"
            tvJoinGroupCode.text = "Group Code: None"
            joinGroupDetailsLayout.visibility = View.GONE

            // Disable the button since the user doesn't have a community
            BtnCommunityChat.isEnabled = false
        }
    }

    private fun updateUIIfJoinedCommunity() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            val firestore = FirebaseFirestore.getInstance()

            // Check if the user is a member of a community
            firestore.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener { userDocumentSnapshot ->
                    val communityId = userDocumentSnapshot.getString("communityId")

                    if (communityId != null) {
                        // Fetch group details from Firestore
                        firestore.collection("communities")
                            .document(communityId)
                            .get()
                            .addOnSuccessListener { communityDocumentSnapshot ->
                                val groupName = communityDocumentSnapshot.getString("groupName")
                                val groupCode = communityDocumentSnapshot.getString("communityCode")

                                if (groupName != null && groupCode != null) {
                                    // User has joined a community before, update UI with saved details
                                    tvJoinGroupName.text = "Group Name: $groupName"
                                    tvJoinGroupCode.text = "Group Code: $groupCode"
                                    joinGroupDetailsLayout.visibility = View.VISIBLE
                                } else {
                                    // Handle the case where group details are missing
                                    showToast("Error: Group details not found.")
                                    BtnCommunityChat.isEnabled = false;
                                }
                            }
                            .addOnFailureListener {
                                showToast("Failed to fetch group details.")
                            }
                    } else {
                        // User is not a member of any community
                        // Display "None" in the top two text views
                        tvJoinGroupName.text = "Group Name: None"
                        tvJoinGroupCode.text = "Group Code: None"
                        joinGroupDetailsLayout.visibility = View.GONE
                        BtnCommunityChat.isEnabled = false
                    }
                }
                .addOnFailureListener {
                    showToast("Failed to check community membership.")
                }
        } else {
            showToast("User not authenticated")
        }
    }

    private fun leaveGroup(joinGroupDetailsLayout: LinearLayout) {
        val firestore = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId == null) {
            showToast("User not authenticated")
            return
        }

        // Check if the user is a member of a community
        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { userDocumentSnapshot ->
                val communityId = userDocumentSnapshot.getString("communityId")

                // Check if communityId is not null before proceeding
                if (communityId != null) {
                    // Remove the user from the community members
                    firestore.collection("communities")
                        .document(communityId)
                        .update("members", FieldValue.arrayRemove(userId))
                        .addOnSuccessListener {
                            // Update the user document to leave the community
                            firestore.collection("users")
                                .document(userId)
                                .update("communityId", FieldValue.delete())
                                .addOnSuccessListener {
                                    // Update ViewModel
                                    clearCommunityDetails()

                                    // Make the joinGroupDetailsLayout invisible
                                    joinGroupDetailsLayout.visibility = View.GONE

                                    showToast("Left the community successfully!")
                                    BtnCommunityChat.isEnabled = false

                                    // Update UI to reflect the user is not a member of any community
                                    updateUIIfJoinedCommunity()
                                }
                                .addOnFailureListener { showToast("Failed to leave community") }
                        }
                        .addOnFailureListener { showToast("Failed to leave community") }
                } else {
                    showToast("You are not a member of any community.")
                }
            }
            .addOnFailureListener { showToast("Failed to leave community") }
    }





    private fun clearCommunityDetails() {
        saveCommunityDetailsToViewModel(null, null)
    }

    private fun saveCommunityDetailsToViewModel(groupName: String?, groupCode: String?) {
        communityViewModel.groupName.value = groupName
        communityViewModel.groupCode.value = groupCode
    }



    private fun joinGroup(communityCode: String) {
        val firestore = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId == null) {
            showToast("User not authenticated")
            return
        }

        // Check if the user is already a member of a community
        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { userDocumentSnapshot ->
                val userCommunityId = userDocumentSnapshot.getString("communityId")

                if (userCommunityId != null) {
                    showToast("You are already a member of a community.")
                } else {
                    // Check if the entered community code exists
                    firestore.collection("communities")
                        .whereEqualTo("communityCode", communityCode)
                        .get()
                        .addOnSuccessListener { communityQuerySnapshot ->
                            if (!communityQuerySnapshot.isEmpty) {
                                // Get the first matching community
                                val communityDocument = communityQuerySnapshot.documents[0]
                                val communityId = communityDocument.id
                                val groupName = communityDocument.getString("groupName")
                                val communityMembers =
                                    communityDocument.get("members") as? List<String>

                                // Ensure communityMembers is not null
                                if (communityMembers != null) {
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
                                                    // Create a unique chat collection for the community
                                                    val chatCollection = "chats_$communityId"
                                                    firestore.collection(chatCollection).document()

                                                    // Update UI with joined group details
                                                    tvJoinGroupName.text = "Group Name: $groupName"
                                                    tvJoinGroupCode.text = "Group Code: $communityCode"

                                                    // Show success message
                                                    showToast("Joined group successfully!")
                                                    BtnCommunityChat.isEnabled = true

                                                    // Make the joinGroupDetailsLayout visible
                                                    joinGroupDetailsLayout.visibility = View.VISIBLE
                                                }
                                                .addOnFailureListener { showToast("Failed to join group") }
                                        }
                                        .addOnFailureListener { showToast("Failed to join group") }
                                } else {
                                    showToast("Invalid community. Unable to retrieve members.")
                                }
                            } else {
                                showToast("Community with code $communityCode not found.")
                            }
                        }
                        .addOnFailureListener { showToast("Failed to join group") }
                }
            }
            .addOnFailureListener { showToast("Failed to join group") }
    }


    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
