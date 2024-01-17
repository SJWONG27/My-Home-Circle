// ServiceListFragment.kt

package com.example.madassignment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class ServiceListFragment : Fragment(), ServiceAdapter.OnMakeAppointmentClickListener {
    private lateinit var servicesRecyclerView: RecyclerView
    private lateinit var serviceAdapter: ServiceAdapter
    private val servicesList = mutableListOf<CompanyServiceModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_service_list, container, false)
        servicesRecyclerView = view.findViewById(R.id.servicesRecyclerView)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val serviceId = arguments?.getInt("serviceId", 0) ?: 0

        // Fetch companies based on service ID
        fetchCompaniesByServiceId(serviceId)

        serviceAdapter = ServiceAdapter(servicesList, this)

        servicesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = serviceAdapter
        }

        val BtnAddService = view.findViewById<FloatingActionButton>(R.id.FBtnAddService)
        BtnAddService.setOnClickListener {
            // Navigate to the destination for adding a service
            val bundle = Bundle()
            val companyId = UUID.randomUUID().toString()
            bundle.putString("companyId", companyId)
            bundle.putInt("serviceId", serviceId)
            Navigation.findNavController(view).navigate(R.id.DestAddService, bundle)
        }
    }

    override fun onMakeAppointmentClick(view: View, company: CompanyServiceModel) {
        Log.d("ServiceListFragment", "Make appointment clicked for company: $company")

        // Fetch additional company details using the companyId
        fetchCompanyDetails(company.companyId)
    }

    private fun fetchCompaniesByServiceId(serviceId: Int) {
        val firestore = FirebaseFirestore.getInstance()
        val collection = firestore.collection("companies")

        // Query companies where the "serviceId" field matches the given serviceId
        collection.whereEqualTo("serviceId", serviceId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val companies = mutableListOf<CompanyServiceModel>()

                for (document in querySnapshot.documents) {
                    // Map Firestore document to your ServiceModel
                    val companyId = document.getString("companyId") ?: ""
                    val companyName = document.getString("companyName") ?: ""
                    val imageUrl = document.getString("profilePhotoUrl") ?: ""
                    val rating = document.getDouble("rating")?.toFloat() ?: 0.0f
                    val email = document.getString("companyEmail")

                    val company = CompanyServiceModel(companyId, companyName, imageUrl, rating, email)
                    companies.add(company)
                }

                // Update your RecyclerView adapter with the fetched companies
                serviceAdapter.updateData(companies)
            }
            .addOnFailureListener { e ->
                // Handle failure
                showToast("Failed to fetch companies. Error: ${e.message}")
            }
    }

    private fun fetchCompanyDetails(companyId: String) {
        val firestore = FirebaseFirestore.getInstance()
        val companyDocument = firestore.collection("companies").document(companyId)

        companyDocument.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // Retrieve additional company details
                    val companyName = documentSnapshot.getString("companyName") ?: ""
                    val profilePhotoUrl = documentSnapshot.getString("profilePhotoUrl") ?: ""

                    // Navigate to the AppointmentFragment with the company details
                    val bundle = Bundle().apply {
                        putString("companyId", companyId)
                        putString("companyName", companyName)
                        putString("profilePhotoUrl", profilePhotoUrl)
                    }

                    Navigation.findNavController(requireView()).navigate(R.id.scheduleFragment, bundle)
                } else {
                    showToast("Company details not found.")
                }
            }
            .addOnFailureListener { e ->
                // Handle failure
                showToast("Failed to fetch company details. Error: ${e.message}")
            }
    }


    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
