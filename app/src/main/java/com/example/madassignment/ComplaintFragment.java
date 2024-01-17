package com.example.madassignment;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ComplaintFragment extends Fragment {

    Button BtnInProgress;
    Button BtnCompleted;
    private ComplaintAdapter adapter;
    private FirebaseFirestore firestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_complaint, container, false);
        firestore = FirebaseFirestore.getInstance();

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        adapter = new ComplaintAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Button btnInProgress = view.findViewById(R.id.BtnInProgress);
        Button btnCompleted = view.findViewById(R.id.BtnCompleted);

        BtnInProgress = view.findViewById(R.id.BtnInProgress);
        BtnCompleted = view.findViewById(R.id.BtnCompleted);

        handleChoiceClick(btnInProgress);
        fetchComplaintsFromDatabase("Inprogress", view);

        FloatingActionButton addComplaintButton = view.findViewById(R.id.BtnAddComplaint);
        addComplaintButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_complaintFragment_to_addComplaintFragment2));

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Handle item click here
                // You can directly use position here or get the item from the adapter
                Complaint clickedComplaint = adapter.getComplaint(position);
                // Pass the clicked complaint's ID to the next fragment
                navigateToComplaintDetailsFragment(clickedComplaint.getTicketNo());
            }
        });

        btnInProgress.setOnClickListener(v -> {
            fetchComplaintsFromDatabase("Inprogress", view);
            handleChoiceClick(btnInProgress);
        });

        btnCompleted.setOnClickListener(v -> {
            fetchComplaintsFromDatabase("Completed", view);
            handleChoiceClick(btnCompleted);
        });
        return view;
    }

    private void navigateToComplaintDetailsFragment(String ticketNo) {
        ComplaintDetailsFragment details = new ComplaintDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("ticketNo", ticketNo);
        details.setArguments(bundle);

        // Use navigate instead of replace
        Navigation.findNavController(requireView()).navigate(R.id.action_complaintFragment_to_complaintDetailsFragment, bundle);
    }

    private void handleChoiceClick(Button btnNews) {
        // Reset background color for all choices
        resetChoiceBackgrounds();

        // Change background color of the selected choice to yellow
        btnNews.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.selecedblue));
        btnNews.setTextColor(Color.parseColor("#FFFFFF"));
    }

    private void resetChoiceBackgrounds() {
        // Reset background color for all choices
        BtnCompleted.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.etentercode));
        BtnInProgress.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.etentercode));
        BtnCompleted.setTextColor(Color.parseColor("#000000"));
        BtnInProgress.setTextColor(Color.parseColor("#000000"));
    };

    private void fetchComplaintsFromDatabase(String category, View view) {
        firestore.collection("complaint")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Complaint> complaints = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Complaint complaint = document.toObject(Complaint.class);
                        // Check if the complaint category matches the specified category
                        if (category.equalsIgnoreCase(complaint.getCategory())
                                || ("completed".equalsIgnoreCase(category) && "completed".equalsIgnoreCase(complaint.getCategory()))
                                || ("inprogress".equalsIgnoreCase(category) && "inprogress".equalsIgnoreCase(complaint.getCategory()))) {
                            complaints.add(complaint);
                        }
                    }
                    adapter.updateData(complaints);
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }
}