package com.example.madassignment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class ServiceFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_service, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()
        val btnService1 = view.findViewById<Button>(R.id.BtnPlumbing)
        val btnService2 = view.findViewById<Button>(R.id.BtnElectrical)
        val btnService3 = view.findViewById<Button>(R.id.BtnCleaning)
        val btnService4 = view.findViewById<Button>(R.id.BtnPainting)
        val btnService5 = view.findViewById<Button>(R.id.BtnAC)
        val btnService6 = view.findViewById<Button>(R.id.BtnPestControl)

        val onClickListener = View.OnClickListener { view ->
            val serviceId = when (view.id) {
                R.id.BtnPlumbing -> 1
                R.id.BtnElectrical -> 2
                R.id.BtnCleaning -> 3
                R.id.BtnPainting -> 4
                R.id.BtnAC -> 5
                R.id.BtnPestControl -> 6
                else -> 0 // Default value or handle other cases
            }

            val bundle = Bundle()
            bundle.putInt("serviceId", serviceId)

            // Navigate to ServiceListFragment with the selected serviceId
            navController.navigate(R.id.actionServicetoServiceList, bundle)
        }

        btnService1.setOnClickListener(onClickListener)
        btnService2.setOnClickListener(onClickListener)
        btnService3.setOnClickListener(onClickListener)
        btnService4.setOnClickListener(onClickListener)
        btnService5.setOnClickListener(onClickListener)
        btnService6.setOnClickListener(onClickListener)
    }
}
