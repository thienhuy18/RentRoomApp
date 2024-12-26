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

    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        currentUserId = getIntent().getStringExtra("currentUserId");

        firestore = FirebaseFirestore.getInstance();

        recyclerViewChatList = findViewById(R.id.recyclerViewChatList);
        contactList = new ArrayList<>();

        contactAdapter = new ContactAdapter(contactList, contact -> {
            Toast.makeText(ChatListActivity.this, "Clicked: " + contact.getName(), Toast.LENGTH_SHORT).show();
            openChat(contact);
        });

        recyclerViewChatList.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewChatList.setAdapter(contactAdapter);

        loadConversations();
    }

    private void loadConversations() {
        Log.d("ChatListActivity", "Current User ID: " + currentUserId);


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

        String[] ids = chatId.split("_");
        if (ids.length == 2) {
            if (ids[0].equals(currentUserId)) {
                return ids[1];
            } else if (ids[1].equals(currentUserId)) {
                return ids[0];
            }
        }
        return null;
    }

    private void loadContactInfo(String userId) {

        firestore.collection("users").document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String name = task.getResult().getString("name");

                        Log.d("ChatListActivity", "Loaded contact info for: " + name);


                        contactList.add(new Contact(name, userId));


                        contactAdapter.notifyDataSetChanged();
                    } else {
                        Log.e("ChatListActivity", "Error loading contact info for user: " + userId);
                        Toast.makeText(ChatListActivity.this, "Error loading contact info", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void openChat(Contact contact) {
        Intent intent = new Intent(ChatListActivity.this, ChatActivity.class);
        intent.putExtra("contactName", contact.getName());
        intent.putExtra("contactUserId", contact.getUserId());
        intent.putExtra("currentUserId", currentUserId);
        startActivity(intent);
    }
}
