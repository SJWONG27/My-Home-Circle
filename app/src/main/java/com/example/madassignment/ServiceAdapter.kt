// ServiceAdapter.kt

package com.example.madassignment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ServiceAdapter(
    private var companies: List<CompanyServiceModel>,
    private val onMakeAppointmentClickListener: OnMakeAppointmentClickListener
) : RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_service, parent, false)
        return ServiceViewHolder(view, onMakeAppointmentClickListener)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        val company = companies[position]

        // Call bind to initialize currentCompany
        holder.bind(company)

        // Set the company name
        holder.usernameTextView.text = company.companyName
        Log.d("ServiceAdapter", "Data updated. New companies: ${company.companyName}")

        // Set the company rating
        holder.serviceRatingBar.rating = company.rating

        // Load and display the profile photo using Glide
        Glide.with(holder.itemView.context)
            .load(company.profilePhotoUrl)
            .placeholder(R.drawable.icon_company) // Replace with a placeholder image
            .error(com.google.android.material.R.drawable.mtrl_ic_error) // Replace with an error image
            .into(holder.userPhotoImageView)
    }

    override fun getItemCount(): Int {
        return companies.size
    }

    fun updateData(newCompanies: List<CompanyServiceModel>) {
        companies = newCompanies
        notifyDataSetChanged()
        Log.d("ServiceAdapter", "Data updated. New companies: $newCompanies")
    }

    class ServiceViewHolder(
        itemView: View,
        private val onMakeAppointmentClickListener: OnMakeAppointmentClickListener
    ) : RecyclerView.ViewHolder(itemView) {

        private lateinit var currentCompany: CompanyServiceModel

        val userPhotoImageView: ImageView = itemView.findViewById(R.id.userPhotoImageView)
        val usernameTextView: TextView = itemView.findViewById(R.id.TVItemCompanyName)
        val serviceRatingBar: RatingBar = itemView.findViewById(R.id.serviceRatingBar)
        val BtnMakeAppointment: Button = itemView.findViewById(R.id.BtnMakeAppointment)
        val ratingTv: TextView = itemView.findViewById(R.id.TVrating)
        val emailTV: TextView = itemView.findViewById(R.id.TVemail)

        init {
            Log.d("ServiceAdapter", "ViewHolder initialized")

            // Set up the click listener for the button
            BtnMakeAppointment.setOnClickListener {
                if (::currentCompany.isInitialized) {
                    Log.d("ServiceAdapter", "Make appointment clicked for company: $currentCompany")
                    onMakeAppointmentClickListener.onMakeAppointmentClick(itemView, currentCompany)
                } else {
                    Log.e("ServiceAdapter", "Make appointment clicked, but currentCompany is not initialized")
                    // Handle the case where currentCompany is not initialized
                }
            }
        }

        fun bind(company: CompanyServiceModel) {
            Log.d("ServiceAdapter", "bind called for company: $company")
            currentCompany = company

            // Bind other data to views
            usernameTextView.text = company.companyName
            serviceRatingBar.rating = company.rating
            ratingTv.text = String.format("%.1f", company.rating)
            emailTV.text = company.email

            // Load and display the profile photo using Glide or another image loading library
            Glide.with(itemView.context)
                .load(company.profilePhotoUrl)
                .placeholder(R.drawable.icon_company) // Replace with a placeholder image
                .error(com.google.android.material.R.drawable.mtrl_ic_error) // Replace with an error image
                .into(userPhotoImageView)
        }
    }

    interface OnMakeAppointmentClickListener {
        fun onMakeAppointmentClick(view: View, company: CompanyServiceModel)
    }
}
