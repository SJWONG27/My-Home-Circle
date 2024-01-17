package com.example.madassignment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
public class Community4 extends Fragment {
    private TextView titleTextView, dateTextView, timeTextView, byTextView, descriptionTextView, pdfContentTextView;
    private View pdfView;

    private FirebaseFirestore firestore;

    private String id;
    private String pdfLink;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community4, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            id = bundle.getString("announcementId");
        }

        firestore = FirebaseFirestore.getInstance();
        // Initialize UI elements
        titleTextView = view.findViewById(R.id.TVtitle);
        dateTextView = view.findViewById(R.id.TVDate);
        timeTextView = view.findViewById(R.id.TVTime);
        byTextView = view.findViewById(R.id.TVByWho);
        descriptionTextView = view.findViewById(R.id.TVDescription);
        pdfContentTextView = view.findViewById(R.id.TVpdfTitle);
        pdfView = view.findViewById(R.id.ViewPdf);


        // Fetch data from the database
        fetchDataFromDatabase();

        // Set click listeners
        pdfView.setOnClickListener(v -> openPdfViewer(pdfLink));
        titleTextView.setOnClickListener(v -> openPdfViewer(pdfLink));

        return view;
    }

    private void fetchDataFromDatabase() {
        firestore.collection("annoucements")
                .whereEqualTo("id", id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            Timestamp timestamp = document.getTimestamp("timstamp");

                            // Check if timestamp is not null before proceeding
                            if (timestamp != null) {
                                // Convert Firestore Timestamp directly to a Date object
                                Date timestampDate = timestamp.toDate();

                                // Extracting day, date, and time from timestamp
                                String date = calculateDate(timestampDate.getTime());
                                String time = calculateTime(timestampDate.getTime());

                                // Retrieving other fields from Firestore document
                                String title = document.getString("title");
                                String description = document.getString("description");
                                String by = document.getString("byWho");
                                String pdfTitle = document.getString("pdfTitle");
                                pdfLink = document.getString("pdfLink");

                                // Set the fetched data to UI elements
                                titleTextView.setText(title);
                                dateTextView.setText("Date: " + date);
                                timeTextView.setText("Time: " + time);
                                byTextView.setText("By: " + by);
                                descriptionTextView.setText(description);
                                pdfContentTextView.setText(pdfTitle);
                            }

                        }

                        // Rest of your code...
                    } else {
                        Log.e("FirestoreQuery", "Error fetching announcements", task.getException());
                    }

                });
    }
    private String calculateDate(long timestamp) {
        // Example: Calculate date from timestamp
        // You can use a library like SimpleDateFormat or implement your own logic
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    private String calculateTime(long timestamp) {
        // Example: Calculate time from timestamp
        // You can use a library like SimpleDateFormat or implement your own logic
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
    private void openPdfViewer(String pdfLink) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(pdfLink), "application/pdf");

        // Create a chooser to allow the user to pick an app to handle the PDF
        Intent chooser = Intent.createChooser(intent, "Open PDF with:");

        // Check if there are apps available to handle the PDF
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(chooser);
        } else {
            // Handle the case where there are no apps to handle the PDF
            // You can display a message to the user or take another action
            // For example, you can open a WebView to display the PDF within your app
            // or provide a download link for a PDF viewer app.

            // In this example, let's show a toast message
            Toast.makeText(getActivity(), "No app available to open PDF", Toast.LENGTH_SHORT).show();
        }
    }
}
