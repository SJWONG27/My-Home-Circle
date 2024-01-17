package com.example.madassignment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class HomePageFragment extends Fragment {

    private FirebaseFirestore firestore;
    private FirebaseUser currentUser;

    private TextView announcementsTitle;
    private TextView announcementsContent;
    private TextView reminderTitle;
    private TextView reminderContent;
    private TextView newsTitle1;
    private ImageView newsImage1;
    private TextView newsTitle2;
    private ImageView newsImage2;
    private TextView newsTitle3;
    private ImageView newsImage3;

    private View an;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);

        // Initialize Firestore components
        firestore = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Initialize UI components
        announcementsTitle = view.findViewById(R.id.announcementsTitle);
        announcementsContent = view.findViewById(R.id.announcementsContent);
        reminderTitle = view.findViewById(R.id.reminderTitle);
        reminderContent = view.findViewById(R.id.reminderContent);
        newsTitle1 = view.findViewById(R.id.newsTitle1);
        newsImage1= view.findViewById(R.id.newsImage1);
        newsTitle2 = view.findViewById(R.id.newsTitle2);
        newsImage2= view.findViewById(R.id.newsImage2);
        newsTitle3 = view.findViewById(R.id.newsTitle3);
        newsImage3= view.findViewById(R.id.newsImage3);
        an = view.findViewById(R.id.notificationsSection);

        an.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(requireView());
                navController.navigate(R.id.hometoAnn);

            }
        });

        // Fetch data from Firestore
        fetchAnnouncements();
        fetchNews();
        fetchReminder();

        return view;
    }

    private void fetchNews() {
        firestore.collection("news")

                .document("uwuD0ny1hMGMyE154TAi") // Replace with your actual news document ID
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            String title = document.getString("Title1");
                            if (title != null) {
                                newsTitle1.setText(title);
                            }

                            String imageUrl = document.getString("photoLink1");
                            if (imageUrl != null) {
                                loadAndDisplayPhoto(imageUrl,newsImage1);
                            }

                            title = document.getString("Title2");
                            if (title != null) {
                                newsTitle2.setText(title);
                            }

                            imageUrl = document.getString("photoLink2");
                            if (imageUrl != null) {
                                loadAndDisplayPhoto(imageUrl,newsImage2);
                            }

                            title = document.getString("Title3");
                            if (title != null) {
                                newsTitle3.setText(title);
                            }

                            imageUrl = document.getString("photoLink3");
                            if (imageUrl != null) {
                                loadAndDisplayPhoto(imageUrl,newsImage3);
                            }
                        }
                    } else {
                        // Handle errors
                    }
                });
    }

    private void fetchReminder() {
        firestore.collection("appointments")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            // Get a random document from the query result
                            int randomIndex = new Random().nextInt(querySnapshot.size());
                            DocumentSnapshot document = querySnapshot.getDocuments().get(randomIndex);

                            // Extract appointment details
                            String appointmentId = document.getString("appointmentId");
                            String companyName = document.getString("companyName");
                            String date = document.getString("date");
                            String time = document.getString("time");
                            int serviceId = document.getLong("serviceId").intValue(); // Convert to int

                            // Format the date and time
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

                            try {
                                Date dateObject = dateFormat.parse(date);
                                Date timeObject = timeFormat.parse(time);

                                // Format the date and time to the desired format
                                String formattedDate = dateFormat.format(dateObject);
                                String formattedTime = timeFormat.format(timeObject);

                                // Display the reminder
                                reminderTitle.setText("Upcoming Appointment: ");
                                reminderContent.setText("Appointment ID: " + appointmentId + "\n" +
                                        "Company Name: " + companyName + "\n" +
                                        "Date: " + formattedDate + "\n" +
                                        "Time: " + formattedTime);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        } else {
                            // Handle case where there are no appointments
                            reminderTitle.setText("No upcoming appointments");
                        }
                    } else {
                        // Handle errors
                        reminderTitle.setText("Error fetching appointments");
                    }
                });
    }

    private void fetchAnnouncements() {
        firestore.collection("annoucements")
                .orderBy("timstamp", Query.Direction.DESCENDING) // Assuming "timestamp" is a field indicating the creation time
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            DocumentSnapshot document = querySnapshot.getDocuments().get(0);

                            String title = document.getString("title");
                            String description = document.getString("description");

                            if (title != null && description != null) {
                                announcementsTitle.setText(title);
                                announcementsContent.setText(description);
                            }
                        }
                    } else {
                        // Handle errors
                    }
                });
    }

    private void loadAndDisplayPhoto(String storagePath, ImageView imageView) {
        // Get Firebase Storage reference
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(storagePath);

        // Get the download URL
        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            // Load the image using Picasso with the HTTP/HTTPS URL
            Picasso.get().load(uri.toString()).into(imageView, new Callback() {

                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {

                }
            });
        }).addOnFailureListener(exception -> {
            // Handle failure
        });
    }
}