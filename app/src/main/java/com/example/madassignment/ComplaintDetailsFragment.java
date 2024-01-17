package com.example.madassignment;

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

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import javax.security.auth.callback.Callback;

public class ComplaintDetailsFragment extends Fragment {
    private TextView titleTextView, dateTextView, timeTextView, byTextView, descriptionTextView, photoTitleTextView;
    private ImageView showPhoto;
    private FirebaseFirestore firestore;
    private String ticketNo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_complaint_details, container, false);

        Bundle bundle = getArguments();
        if(bundle != null){
            ticketNo = bundle.getString("ticketNo");
        }

        firestore = FirebaseFirestore.getInstance();
        titleTextView = view.findViewById(R.id.TVtitle);
        dateTextView = view.findViewById(R.id.TVdate);
        timeTextView = view.findViewById(R.id.TVtime);
        byTextView = view.findViewById(R.id.TVname);
        descriptionTextView = view.findViewById(R.id.TVDescription);
        photoTitleTextView = view.findViewById(R.id.TVphotoTitle);
        showPhoto = view.findViewById(R.id.IVPhotoDetails);

        retrievePhotoUrlFromFirestore(showPhoto);

        fetchDataFromDatabase();
        return view;
    }

    private void fetchDataFromDatabase() {
        firestore.collection("complaint")
                .whereEqualTo("ticketNo", ticketNo)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        if (!task.getResult().isEmpty()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0); // Assuming there's at most one document
                            String title = document.getString("title");
                            String description = document.getString("description");
                            String by = document.getString("name");
                            String photoTitle = document.getString("photoTitle");
                            String date = document.getString("date");
                            String time = document.getString("time");

                            titleTextView.setText(title);
                            dateTextView.setText("Date: " + date);
                            timeTextView.setText("Time: " + time);
                            byTextView.setText("By: " + by);
                            descriptionTextView.setText(description);
                            photoTitleTextView.setText(photoTitle);
                        } else {
                            // Handle the case where no matching document is found
                            Log.e("FirestoreQuery", "No document found for ticketNo: " + ticketNo);
                        }
                    } else {
                        Log.e("FirestoreQuery", "Error fetching document", task.getException());
                    }
                });
    }

    private void retrievePhotoUrlFromFirestore(ImageView imageView) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("complaint")
                .whereEqualTo("ticketNo", ticketNo)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        if (!task.getResult().isEmpty()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            String photoUrl = document.getString("photoUrl");

                            // Now you can use Picasso to load and display the image
                            if (photoUrl != null) {
                                Picasso.get().load(photoUrl).into(imageView);
                            } else {
                                Toast.makeText(getContext(), "Image couldn't load", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Document does not exist
                            // Handle accordingly
                        }
                    } else {
                        // Handle failure
                        Log.e("FirestoreError", "Failed to retrieve photo URL", task.getException());
                    }
                });
    }
}