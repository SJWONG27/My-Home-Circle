package com.example.madassignment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import com.example.madassignment.residentialArea;


// Import statements...

public class Community1 extends Fragment {
    private EditText etEnterNameOrCode;
    private Button btnConfirm;

    private FirebaseFirestore firestore;
    private String userId;

    private String selectedResidentialAreaCode;
    private List<residentialArea> allSuggestions;

    private static final String TAG = "Community1";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community1, container, false);

        etEnterNameOrCode = view.findViewById(R.id.ETenternameorcode);
        btnConfirm = view.findViewById(R.id.BtnConfirm);

        firestore = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        allSuggestions = fetchDataFromDatabase();

        etEnterNameOrCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchResidentialArea(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(requireView());
                navController.navigate(R.id.actionCommunity1ToCommunity2);
                saveSelectedChoiceToDatabase();
            }
        });

        return view;
    }

    private void searchResidentialArea(String query) {
        List<residentialArea> searchResults = new ArrayList<>();

        if (query.isEmpty()) {
            // If the EditText is empty, show all suggestions
            searchResults.addAll(allSuggestions);
        } else {
            // If the EditText is not empty, filter suggestions based on the query
            for (residentialArea area : allSuggestions) {
                if (area.getName().toLowerCase().contains(query.toLowerCase()) ||
                        area.getCode().toLowerCase().contains(query.toLowerCase())) {
                    searchResults.add(area);
                }
            }
        }

        updateSuggestionsUI(searchResults);
    }


    private void updateSuggestionsUI(List<residentialArea> suggestions) {
        RecyclerView recyclerView = getView().findViewById(R.id.recyclerViewSuggestions);
        AreaAdapter communityAdapter = new AreaAdapter(suggestions);

        recyclerView.setAdapter(communityAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        communityAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Handle item click here
                residentialArea clickedarea = communityAdapter.getResidentialArea(position);
                // Pass the clicked announcement's ID to the next fragment
                selectedResidentialAreaCode = clickedarea.getCode();

            }
        });
    }



    private void saveSelectedChoiceToDatabase() {
        String selectedChoice = selectedResidentialAreaCode;
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userId = currentUser != null ? currentUser.getUid() : null;

        if (userId != null) {
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            CollectionReference usersCollection = firestore.collection("users");
            DocumentReference userDocument = usersCollection.document(userId);

            userDocument.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        userDocument.update("residentialArea", selectedChoice)
                                .addOnSuccessListener(aVoid -> showToast("Residential area updated successfully!"))
                                .addOnFailureListener(e -> showToast("Failed to update residential area. Please try again."));
                    } else {
                        userDocument.set(new HashMap<String, Object>() {{
                                    put("residentialArea", selectedChoice);
                                }})
                                .addOnSuccessListener(aVoid -> showToast("Residential area created successfully!"))
                                .addOnFailureListener(e -> showToast("Failed to create residential area. Please try again."));
                    }
                } else {
                    showToast("Failed to fetch user data. Please try again.");
                }
            });
        }
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private List<residentialArea> fetchDataFromDatabase() {
        List<residentialArea> suggestions = new ArrayList<>();

        firestore.collection("communities").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    residentialArea community = new residentialArea(
                            document.getString("residentialName"),
                            document.getString("communityCode"),
                            document.getString("photoLink")
                    );

                    suggestions.add(community);
                }

                updateSuggestionsUI(suggestions);
            } else {
                Log.w(TAG, "Error getting documents.", task.getException());
            }
        });

        return suggestions;
    }
}


