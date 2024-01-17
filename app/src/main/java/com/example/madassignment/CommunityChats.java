package com.example.madassignment;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommunityChats extends AppCompatActivity {

    private EditText editTextCommunityMessage;
    private Button buttonSendCommunityMessage;
    private RecyclerView recyclerViewCommunityChats;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;
    private FirebaseFirestore firestore;
    private String currentCommunityId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_chats);

        // Initialize views
        editTextCommunityMessage = findViewById(R.id.editTextCommunityMessage);
        buttonSendCommunityMessage = findViewById(R.id.buttonSendCommunityMessage);
        recyclerViewCommunityChats = findViewById(R.id.recyclerViewCommunityChats);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Initialize RecyclerView
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages);
        recyclerViewCommunityChats.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCommunityChats.setAdapter(chatAdapter);

        // Fetch the current community ID
        fetchCommunityIdForCurrentUser();

        // Set up real-time chat listener
        setUpChatListener();

        // Set up button click listener for sending messages
        buttonSendCommunityMessage.setOnClickListener(view -> sendMessage());
    }

    private void fetchCommunityIdForCurrentUser() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firestore.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String communityId = documentSnapshot.getString("residentialArea");
                        if (communityId != null && !communityId.isEmpty()) {
                            currentCommunityId = communityId;
                            // Update chat listener when the community ID is fetched
                            setUpChatListener();
                        } else {
                            // Handle the case where community ID is not available
                        }
                    } else {
                        // Handle the case where user document does not exist
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle fetch failure
                });
    }

    private void setUpChatListener() {
        if (currentCommunityId != null) {
            firestore.collection("chats_" + currentCommunityId)
                    .orderBy("timestamp", Query.Direction.ASCENDING)
                    .addSnapshotListener((value, error) -> {
                        if (error != null) {
                            Log.e("Firestore", "Listen failed.", error);
                            return;
                        }

                        if (value != null) {
                            for (DocumentChange dc : value.getDocumentChanges()) {
                                switch (dc.getType()) {
                                    case ADDED:
                                        ChatMessage chatMessage = dc.getDocument().toObject(ChatMessage.class);
                                        chatMessages.add(chatMessage);
                                        Log.d("Firestore", "Message added: " + chatMessage.getMessage());
                                        break;
                                }
                            }
                            chatAdapter.notifyDataSetChanged();
                        }
                    });
        }
    }


    private void sendMessage() {
        String messageText = editTextCommunityMessage.getText().toString().trim();
        if (!messageText.isEmpty() && currentCommunityId != null) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            ensureChatsCollectionExists(currentCommunityId);

            fetchUserName(userId, displayName -> {
                if (displayName != null) {
                    ChatMessage chatMessage = new ChatMessage(userId, displayName, messageText, System.currentTimeMillis());
                    firestore.collection("chats_" + currentCommunityId).add(chatMessage);
                    editTextCommunityMessage.getText().clear();
                }
            });
        }
    }

    private void ensureChatsCollectionExists(String communityId) {
        firestore.collection("chats_" + communityId)
                .document("dummyDocument")
                .set(new HashMap<>())
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error creating chats collection for community " + communityId, e);
                });
    }

    private void fetchUserName(String userId, OnUserNameFetchedListener listener) {
        if (userId != null) {
            firestore.collection("users")
                    .document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String displayName = documentSnapshot.getString("name");
                            Log.d("Firestore", "Fetched displayName: " + displayName);
                            displayName = (displayName != null) ? displayName : "DefaultDisplayName";
                            listener.onUserNameFetched(displayName);
                        } else {
                            listener.onUserNameFetched(null);
                        }
                    })
                    .addOnFailureListener(e -> {
                        listener.onUserNameFetched(null);
                    });
        } else {
            listener.onUserNameFetched(null);
        }
    }

    interface OnUserNameFetchedListener {
        void onUserNameFetched(String displayName);
    }
}
