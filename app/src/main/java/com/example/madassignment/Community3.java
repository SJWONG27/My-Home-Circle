package com.example.madassignment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.madassignment.AnnouncementAdapter;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

    public class Community3 extends Fragment {
        private RecyclerView recyclerView;
        private AnnouncementAdapter adapter;
        private FirebaseFirestore firestore;
        private Button BtnAll;
        private Button BtnActivities;
        private Button BtnNews;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_community3, container, false);
            firestore = FirebaseFirestore.getInstance();

            recyclerView = view.findViewById(R.id.recyclerView);
            adapter = new AnnouncementAdapter(new ArrayList<>());
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            BtnAll = view.findViewById(R.id.BtnAll);
            BtnActivities = view.findViewById(R.id.BtnActivites);
            BtnNews = view.findViewById(R.id.BtnNews);

            handleChoiceClick(BtnAll);
            fetchAllAnnouncementsFromDatabase();
            adapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    // Handle item click here
                    Announcement clickedAnnouncement = adapter.getAnnouncement(position);
                    // Pass the clicked announcement's ID to the next fragment
                    navigateToCommunity4(clickedAnnouncement.getId());
                }
            });

            BtnAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    handleChoiceClick(BtnAll);
                    fetchAllAnnouncementsFromDatabase();
                }
            });

            BtnActivities.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    handleChoiceClick(BtnActivities);
                    fetchAnnouncementsFromDatabase("Activities");
                }
            });

            BtnNews.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    handleChoiceClick(BtnNews);
                    fetchAnnouncementsFromDatabase("News");
                }
            });

            return view;
        }

        private void navigateToCommunity4(String announcementId) {
            Community4 community4Fragment = new Community4();
            Bundle bundle = new Bundle();
            bundle.putString("announcementId", announcementId);
            community4Fragment.setArguments(bundle);

            // Use navigate instead of replace
            Navigation.findNavController(requireView()).navigate(R.id.actionCommunity3ToCommunity4, bundle);
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
        BtnAll.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.etentercode));
        BtnActivities.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.etentercode));
        BtnNews.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.etentercode));
        BtnAll.setTextColor(Color.parseColor("#000000"));
        BtnActivities.setTextColor(Color.parseColor("#000000"));
        BtnNews.setTextColor(Color.parseColor("#000000"));
    };

    private void fetchAnnouncementsFromDatabase(String category) {
        firestore.collection("annoucements")
                .whereEqualTo("category", category)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Announcement> announcements = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            Timestamp timestamp = document.getTimestamp("timstamp");

                            // Check if timestamp is not null before proceeding
                            if (timestamp != null) {
                                // Convert Firestore Timestamp directly to a Date object
                                Date timestampDate = timestamp.toDate();

                                // Extracting day, date, and time from timestamp
                                String day = calculateDay(timestampDate.getTime());
                                String date = calculateDate(timestampDate.getTime());
                                String time = calculateTime(timestampDate.getTime());

                                // Retrieving other fields from Firestore document
                                String title = document.getString("title");
                                String description = document.getString("description");
                                String id = document.getString("id");

                                Announcement announcement = new Announcement(day, date, time, title, description, id);
                                announcements.add(announcement);
                            }

                        }
                        try {
                            announcements = sortAnnouncementsByTimestamp(announcements);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                        adapter.setAnnouncements(announcements);
                        // Rest of your code...
                    } else {
                        Log.e("FirestoreQuery", "Error fetching announcements", task.getException());
                    }

                });
    }

    private void fetchAllAnnouncementsFromDatabase() {
        firestore.collection("annoucements")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Announcement> announcements = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            Timestamp timestamp = document.getTimestamp("timstamp");

                            // Check if timestamp is not null before proceeding
                            if (timestamp != null) {
                                // Convert Firestore Timestamp directly to a Date object
                                Date timestampDate = timestamp.toDate();

                                // Extracting day, date, and time from timestamp
                                String day = calculateDay(timestampDate.getTime());
                                String date = calculateDate(timestampDate.getTime());
                                String time = calculateTime(timestampDate.getTime());
                                String id = document.getString("id");

                                // Retrieving other fields from Firestore document
                                String title = document.getString("title");
                                String description = document.getString("description");

                                Announcement announcement = new Announcement(day, date, time, title, description,id);
                                announcements.add(announcement);
                            }

                        }
                        try {
                            announcements = sortAnnouncementsByTimestamp(announcements);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                        adapter.setAnnouncements(announcements);
                        // Rest of your code...
                    } else {
                        Log.e("FirestoreQuery", "Error fetching announcements", task.getException());
                    }

                });
    }

    private List<Announcement> sortAnnouncementsByTimestamp(List<Announcement> announcements) throws ParseException {
        // Sort announcements by timestamp in descending order
        Collections.sort(announcements, (announcement1, announcement2) -> {
            long timestamp1 = 0;
            try {
                timestamp1 = getTimestamp(announcement1);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            long timestamp2 = 0;
            try {
                timestamp2 = getTimestamp(announcement2);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            // Compare in reverse order to have the latest announcement first
            return Long.compare(timestamp2, timestamp1);
        });
        return announcements;
    }

    private long getTimestamp(Announcement announcement) throws ParseException {
        String dateString = announcement.getDate() + " " + announcement.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());

        Date date = sdf.parse(dateString);

        if (date != null) {
            // Format the date to ensure the correct format
            String specifiedDateString = sdf.format(date);
            // Parse the formatted date to get the actual timestamp
            return sdf.parse(specifiedDateString).getTime();
        } else {
            // Handle the case where parsing fails
            throw new ParseException("Failed to parse date", 0);
        }
    }





    private String calculateDay(long timestamp) {
        // Example: Calculate day from timestamp
        // You can use a library like SimpleDateFormat or implement your own logic
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.getDefault());
        return sdf.format(new Date(timestamp));
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
}
