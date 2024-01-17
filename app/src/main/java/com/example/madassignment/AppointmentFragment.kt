package com.example.madassignment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class AppointmentFragment : Fragment() {

    private lateinit var selectedDate: String
    private lateinit var selectedTime: String
    private lateinit var spinnerMorning: Spinner
    private lateinit var spinnerNoon: Spinner
    private lateinit var companyId: String
    private lateinit var companyName: String
    private lateinit var profilePhotoUrl: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_appointment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val IVAppoint1 = view.findViewById<ImageView>(R.id.IVAppoint1)
        val TVAppointCompanyName = view.findViewById<TextView>(R.id.TVAppointCompanyName)
        val datePicker = view.findViewById<DatePicker>(R.id.datePicker)
        spinnerMorning = view.findViewById(R.id.spinnerMorning)
        spinnerNoon = view.findViewById(R.id.spinnerNoon)
        val BtnSubmitAppointment = view.findViewById<Button>(R.id.BtnSubmitAppointment)

        // Set a default date and time (you may need to adjust this based on your UI)
        selectedDate = "${datePicker.year}-${datePicker.month + 1}-${datePicker.dayOfMonth}"
        selectedTime = "${spinnerMorning.selectedItem} ${spinnerNoon.selectedItem}"

        companyId = arguments?.getString("companyId") ?: ""
        companyName = arguments?.getString("companyName") ?: ""
        profilePhotoUrl = arguments?.getString("profilePhotoUrl") ?: ""

        // Update the selected date and time when the date picker changes
        datePicker.init(
            datePicker.year,
            datePicker.month,
            datePicker.dayOfMonth,
            object : DatePicker.OnDateChangedListener {
                override fun onDateChanged(view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
                    selectedDate = "$year-${monthOfYear + 1}-$dayOfMonth"
                }
            }
        )

        // Update the selected time when the spinners change
        spinnerMorning.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val morningTime = resources.getStringArray(R.array.morning_time_slots)[position]
                selectedTime = "$morningTime ${spinnerNoon.selectedItem}"
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle nothing selected if needed
            }
        }

        spinnerNoon.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val afternoonTime = resources.getStringArray(R.array.noon_time_slots)[position]
                selectedTime = "${spinnerMorning.selectedItem} $afternoonTime"
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                showToast("Please select at least one time.")
            }
        }

        // Set the company profile photo and name

        Glide.with(requireContext())
            .load(profilePhotoUrl)
            .placeholder(R.drawable.icon_company) // Replace with a placeholder image
            .error(com.google.android.material.R.drawable.mtrl_ic_error) // Replace with an error image
            .into(IVAppoint1)

        TVAppointCompanyName.text = companyName

        BtnSubmitAppointment.setOnClickListener {
            // Validate user input
            val validationMessages = mutableListOf<String>()

            // Check if the selected date is in the future
            val currentDate = Calendar.getInstance().time
            val selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(selectedDate)
            if (selectedDate != null && selectedDate.before(currentDate)) {
                validationMessages.add("Invalid date. Please select a date in the future or book 1 day earlier.")
            }

            // Check if both spinners have a selected time
            if (spinnerMorning.selectedItem != null && spinnerNoon.selectedItem != null &&
                spinnerMorning.selectedItem.toString() == "None" && spinnerNoon.selectedItem.toString() == "None") {
                validationMessages.add("Please select either morning or noon time.")
            }

            // Proceed with the booking if there are no validation errors
            if (validationMessages.isEmpty()) {
                storeAppointmentInFirestore()
                val toastMessage = "Appointment booked, Booking completed."
                showToast(toastMessage)
            }
        }


    }

    private fun storeAppointmentInFirestore() {
        val firestore = FirebaseFirestore.getInstance()
        val appointmentsCollection = firestore.collection("appointments")

        // Check if both spinners have a selected time
        if (spinnerMorning.selectedItem != null && spinnerNoon.selectedItem != null &&
            spinnerMorning.selectedItem.toString() != "None" && spinnerNoon.selectedItem.toString() != "None") {
            showToast("Please select either morning or noon time, not both.")
            return
        }

        // Determine which spinner has a valid time
        val selectedTime = if (spinnerMorning.selectedItem != null && spinnerMorning.selectedItem.toString() != "None") {
            "${spinnerMorning.selectedItem}"
        } else if (spinnerNoon.selectedItem != null && spinnerNoon.selectedItem.toString() != "None") {
            "${spinnerNoon.selectedItem}"
        } else {
            showToast("Please select a valid time.")
            return
        }

        // Create an Appointment object
        val appointment = Appointment(
            serviceId = 1, // Replace with the actual serviceId
            companyName = companyName, // Use the actual company ID from the arguments
            companyId = companyId,
            appointmentId = UUID.randomUUID().toString(), // Generate a unique appointmentId
            date = selectedDate,
            time = selectedTime,
        )

        // Add the appointment to the Firestore collection
        appointmentsCollection.add(appointment)
            .addOnSuccessListener {
                // Handle success, for example, show a popup with booking details
                showBookingDetailsPopup(appointment)
            }
            .addOnFailureListener { e ->
                // Handle failure
                showToast("Failed to confirm appointment. Error: ${e.message}")
            }
    }


    private fun showBookingDetailsPopup(appointment: Appointment) {
        // Implement your logic to show a popup with booking details
        // You can use a DialogFragment or any other UI element for this purpose
    }

    private fun showToast(message: String) {
        // Implement your logic to show a toast message
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
