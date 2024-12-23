package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChatListActivity extends AppCompatActivity {
    private RecyclerView recyclerViewChatList;
    private ContactAdapter contactAdapter;
    private List<Contact> contactList;
    private FirebaseFirestore firestore;

    private String currentUserId; // ID of the current user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        // Retrieve the current user's ID from intent
        currentUserId = getIntent().getStringExtra("currentUserId");

        // Initialize Firestore instance
        firestore = FirebaseFirestore.getInstance();

        // Initialize RecyclerView and contact list
        recyclerViewChatList = findViewById(R.id.recyclerViewChatList);
        contactList = new ArrayList<>();

        // Set up RecyclerView with adapter
        contactAdapter = new ContactAdapter(contactList, contact -> {
            // Handle contact click
            Toast.makeText(ChatListActivity.this, "Clicked: " + contact.getName(), Toast.LENGTH_SHORT).show();
            openChat(contact);
        });

        recyclerViewChatList.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewChatList.setAdapter(contactAdapter);

        // Load conversations of the current user
        loadConversations();
    }

    private void loadConversations() {
        Log.d("ChatListActivity", "Current User ID: " + currentUserId);

        // Query "chats" collection to find conversations involving the current user
        firestore.collection("chats")
                .whereArrayContains("userIds", currentUserId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            Log.d("ChatListActivity", "Number of chats found: " + querySnapshot.size());
                            Set<String> loadedContacts = new HashSet<>(); // Avoid duplicates
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                // Decode the chatId to extract the contact's userId
                                String chatId = document.getId();
                                Log.d("ChatListActivity", "Processing chatId: " + chatId);

                                String contactUserId = getContactUserId(chatId, currentUserId);
                                if (contactUserId != null && !loadedContacts.contains(contactUserId)) {
                                    Log.d("ChatListActivity", "Found contactUserId: " + contactUserId);
                                    loadedContacts.add(contactUserId);
                                    loadContactInfo(contactUserId);
                                }
                            }
                        }
                    } else {
                        Log.e("ChatListActivity", "Error loading chats", task.getException());
                        Toast.makeText(ChatListActivity.this, "Error loading conversations", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String getContactUserId(String chatId, String currentUserId) {
        // Extract the contact's userId by splitting the chatId
        String[] ids = chatId.split("_");
        if (ids.length == 2) {
            if (ids[0].equals(currentUserId)) {
                return ids[1]; // Return the other user's ID
            } else if (ids[1].equals(currentUserId)) {
                return ids[0]; // Return the other user's ID
            }
        }
        return null; // Invalid chatId or does not involve the current user
    }

    private void loadContactInfo(String userId) {
        // Retrieve user information from the "users" collection
        firestore.collection("users").document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String name = task.getResult().getString("name");

                        Log.d("ChatListActivity", "Loaded contact info for: " + name);

                        // Add the user to the contact list
                        contactList.add(new Contact(name, userId));

                        // Notify adapter of data changes
                        contactAdapter.notifyDataSetChanged();
                    } else {
                        Log.e("ChatListActivity", "Error loading contact info for user: " + userId);
                        Toast.makeText(ChatListActivity.this, "Error loading contact info", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void openChat(Contact contact) {
        // Open the chat screen with the selected contact
        Intent intent = new Intent(ChatListActivity.this, ChatActivity.class);
        intent.putExtra("contactName", contact.getName());
        intent.putExtra("contactUserId", contact.getUserId());
        intent.putExtra("currentUserId", currentUserId);
        startActivity(intent);
    }
}
