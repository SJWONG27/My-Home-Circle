package com.example.madassignment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class Community2 extends Fragment {

    private LinearLayout buttonGroupChat;
    private LinearLayout btnManagementAnnouncement;
    private LinearLayout btnReportComplaint;
    private LinearLayout btnGroupChat;

    private FirebaseFirestore firestore;

    // TextViews for announcement titles and content
    private TextView shortAnnTitle1;
    private TextView shortAnnContent1;
    private TextView shortAnnTitle2;
    private TextView shortAnnContent2;

    public Community2() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_community2, container, false);

        firestore = FirebaseFirestore.getInstance();
        // Initialize UI components
        buttonGroupChat = view.findViewById(R.id.BtnGroupChat);
        btnManagementAnnouncement = view.findViewById(R.id.BtnManagementAnnoucement);
        btnReportComplaint = view.findViewById(R.id.BtnReportComplaint);
        btnGroupChat = view.findViewById(R.id.BtnGroupChat);

        // Initialize TextViews for announcement titles and content
        shortAnnTitle1 = view.findViewById(R.id.shortAnnTitle1);
        shortAnnContent1 = view.findViewById(R.id.shortAnnContent1);
        shortAnnTitle2 = view.findViewById(R.id.shortAnnTitle2);
        shortAnnContent2 = view.findViewById(R.id.shortAnnContent2);

        fetchAnnouncementsFromDatabase();

        // Set OnClickListener for the Group Chat button
        buttonGroupChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to the Group Chat page
//                Navigation.findNavController(view).navigate(R.id.actionCommunity2ToGroupChat);
            }
        });

        // Set OnClickListener for the Management Announcement button
        btnManagementAnnouncement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(requireView());

                // Navigate to Community2
                navController.navigate(R.id.actionCommunity2ToAnnoucement);
            }
        });

        // Set OnClickListener for the Report Complaint button
        btnReportComplaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(requireView());

                // Navigate to Community2
                navController.navigate(R.id.action_Community2_to_complaintFragment2);
            }
        });

        // Set OnClickListener for the Group Chat button
        btnGroupChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(requireView());

                // Navigate to Community2
                navController.navigate(R.id.actionCommunity2ToChat);
            }
        });

        return view;
    }

    private void fetchAnnouncementsFromDatabase() {
        firestore.collection("annoucements")
                .orderBy("timstamp", Query.Direction.DESCENDING)
                .limit(2)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();

                        if (documents.size() >= 1) {
                            updateTextViews(shortAnnTitle1, shortAnnContent1, documents.get(0));
                        }

                        if (documents.size() >= 2) {
                            updateTextViews(shortAnnTitle2, shortAnnContent2, documents.get(1));
                        }
                    } else {
                        // Handle errors
                        Log.w("Announcement", "Error getting announcements.", task.getException());
                    }
                });
    }

    private void updateTextViews(TextView titleTextView, TextView contentTextView, DocumentSnapshot document) {
        Timestamp timestamp = document.getTimestamp("timstamp");
        String title = document.getString("title");
        String content = document.getString("description");

        // Update the TextViews with the retrieved data
        titleTextView.setText(title);
        contentTextView.setText(content);

        // Process the timestamp or any other data as needed
        Log.d("Announcement", "Title: " + title + ", Content: " + content + ", Timestamp: " + timestamp);
    }

}
