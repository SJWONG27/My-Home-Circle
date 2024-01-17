package com.example.madassignment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*

class AddServiceFragment : Fragment() {

    private val PICK_FILE_REQUEST_CODE_1 = 101
    private val PICK_FILE_REQUEST_CODE_2 = 102
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private lateinit var profilePhotoUri: Uri
    private lateinit var supportDocumentsUri: Uri
    private val DEFAULT_ICON_RES_ID = R.drawable.icon_company
    private lateinit var ibUpload1: ImageButton
    private lateinit var ibUpload2: ImageButton
    private lateinit var companyId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_service, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ibUpload1 = view.findViewById<ImageButton>(R.id.IBUpload)
        ibUpload2 = view.findViewById<ImageButton>(R.id.IBUpload2)
        val btnServiceSubmit = view.findViewById<Button>(R.id.BtnServiceRegisterSubmit)

        ibUpload1.setOnClickListener {
            openFilePicker(PICK_FILE_REQUEST_CODE_1)
        }

        ibUpload2.setOnClickListener {
            openFilePicker(PICK_FILE_REQUEST_CODE_2)
        }

        // Get companyId from arguments
        companyId = arguments?.getString("companyId", "") ?: ""

        btnServiceSubmit.setOnClickListener {
            // Retrieve data from EditText fields
            val companyName = view.findViewById<EditText>(R.id.ETAddService1).text.toString()
            val representativeNRIC = view.findViewById<EditText>(R.id.ETAddService2).text.toString()
            val ssmID = view.findViewById<EditText>(R.id.ETAddService3).text.toString()
            val companyEmail = view.findViewById<EditText>(R.id.ETAddService4).text.toString()

            // Validate input data
            if (!isValidInput(companyName, representativeNRIC, ssmID, companyEmail)) {
                showToast("Invalid input. Please check your input data.")
                return@setOnClickListener
            }

            // Create a Company object
            val company = Company(companyName, representativeNRIC, ssmID, companyEmail)

            // Get serviceId from arguments
            val serviceId = arguments?.getInt("serviceId", 0)

            // Upload company data to Firebase Firestore
            uploadCompanyToFirestore(company, serviceId)
        }
    }

    private fun isValidInput(
        companyName: String,
        representativeNRIC: String,
        ssmID: String,
        companyEmail: String
    ): Boolean {
        // Add your specific validation rules here
        if (companyName.isEmpty() || representativeNRIC.length != 12 ||
            ssmID.length < 12 || companyEmail.isEmpty() || !companyEmail.contains("@")
        ) {
            return false
        }
        return true
    }

    private fun openFilePicker(requestCode: Int) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        startActivityForResult(intent, requestCode)
    }

    private fun updateImageButtonWithPreview(imageButton: ImageButton, fileUri: Uri?) {
        // Update the ImageButton with the preview of the selected photo or document
        if (fileUri != null) {
            // Load the image or document preview using your preferred library or method
            // For simplicity, we'll set a default icon here. Replace it with your logic.
            imageButton.setImageResource(R.drawable.icon_company)
        } else {
            // If no file is selected, set the default icon
            imageButton.setImageResource(DEFAULT_ICON_RES_ID)
        }
    }

    private fun uploadCompanyToFirestore(company: Company, serviceId: Int?) {
        // Add your Firebase Firestore collection name
        val collection = firestore.collection("companies")

        // Set the company data including the random rating
        val randomRating = Random().nextFloat() * (5.0f - 3.0f) + 3.0f // Random rating between 3 and 5

        // Correct the field assignments and update the order
        collection.document(companyId).set(
            mapOf(
                "companyId" to companyId,  // Add companyId here
                "companyName" to company.companyName,
                "profilePhotoUrl" to "", // Set default or handle it accordingly
                "supportDocumentsUrl" to "", // Set default or handle it accordingly
                "representativeIC" to company.representativeIC,
                "ssmID" to company.ssmID,
                "companyEmail" to company.companyEmail,
                "rating" to randomRating,
                "serviceId" to (serviceId ?: 0)
            )
        ).addOnSuccessListener {
            // Upload profile photo and supported documents
            uploadProfilePhoto(companyId)
            uploadSupportDocuments(companyId)

            // Navigate to the desired destination
            Navigation.findNavController(requireView()).navigate(R.id.DestService)

            // Show success message
            showToast("Company successfully created!")
        }.addOnFailureListener { e ->
            // Handle failure
            // You can add additional logic here if needed
            showToast("Failed to create company. Error: ${e.message}")
        }
    }





    private fun uploadProfilePhoto(companyId: String) {
        val storageRef: StorageReference = storage.reference.child("profile_photos/$companyId.jpg")

        storageRef.putFile(profilePhotoUri)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                storageRef.downloadUrl
            }
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    // Update the Company document in Firestore with the profile photo URL
                    updateCompanyProfilePhotoUrl(companyId, downloadUri.toString())
                } else {
                    // Handle failure
                    showToast("Failed to upload profile photo. Error: ${task.exception?.message}")
                }
            }
    }

    private fun uploadSupportDocuments(companyId: String) {
        val storageRef: StorageReference =
            storage.reference.child("support_documents/$companyId.pdf")

        storageRef.putFile(supportDocumentsUri)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                storageRef.downloadUrl
            }
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    // Update the Company document in Firestore with the support documents URL
                    updateCompanySupportDocumentsUrl(companyId, downloadUri.toString())
                } else {
                    // Handle failure
                    showToast("Failed to upload support documents. Error: ${task.exception?.message}")
                }
            }
    }

    private fun updateCompanyProfilePhotoUrl(companyId: String, photoUrl: String) {
        val documentRef = firestore.collection("companies").document(companyId)
        documentRef.update("profilePhotoUrl", photoUrl)
    }

    private fun updateCompanySupportDocumentsUrl(companyId: String, documentsUrl: String) {
        val documentRef = firestore.collection("companies").document(companyId)
        documentRef.update("supportDocumentsUrl", documentsUrl)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PICK_FILE_REQUEST_CODE_1 -> {
                    data?.data?.let { fileUri ->
                        profilePhotoUri = fileUri
                        // Update the selected photo preview in ImageButton 1
                        updateImageButtonWithPreview(ibUpload1, profilePhotoUri)
                    }
                }
                PICK_FILE_REQUEST_CODE_2 -> {
                    data?.data?.let { fileUri ->
                        supportDocumentsUri = fileUri
                        // Update the selected document preview in ImageButton 2
                        updateImageButtonWithPreview(ibUpload2, supportDocumentsUri)
                    }
                }
            }
        }
    }
}
