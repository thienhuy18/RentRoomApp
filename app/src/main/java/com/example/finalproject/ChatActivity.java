package com.example.finalproject;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class ChatActivity extends AppCompatActivity {
    private TextView tvContactName;
    private RecyclerView recyclerViewChat;
    private EditText etMessageInput;
    private ImageButton btnSendMessage;
    private List<Message> messageList;
    private MessageAdapter messageAdapter;

    private String contactName;
    private String contactUserId;
    private String currentUserId;

    private FirebaseFirestore db;
    private CollectionReference chatRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        tvContactName = findViewById(R.id.tvContactName);
        recyclerViewChat = findViewById(R.id.recyclerViewChat);
        etMessageInput = findViewById(R.id.etMessageInput);
        btnSendMessage = findViewById(R.id.btnSendMessage);

        contactName = getIntent().getStringExtra("contactName");
        contactUserId = getIntent().getStringExtra("contactUserId");
        currentUserId = getIntent().getStringExtra("currentUserId");

        tvContactName.setText(contactName);

        db = FirebaseFirestore.getInstance();
        chatRef = db.collection("chats");

        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewChat.setAdapter(messageAdapter);

        loadChatHistory();

        btnSendMessage.setOnClickListener(v -> {
            String messageText = etMessageInput.getText().toString().trim();
            if (!TextUtils.isEmpty(messageText)) {
                sendMessage(messageText);
            } else {
                Toast.makeText(ChatActivity.this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void fetchSenderName(String userId, OnNameFetchedListener listener) {
        db.collection("users").document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String name = task.getResult().getString("name");
                        listener.onNameFetched(name != null ? name : "Unknown User");
                    } else {
                        listener.onNameFetched("Unknown User");
                    }
                });
    }private void loadChatHistory() {
        String chatId = getChatId(currentUserId, contactUserId);

        CollectionReference messagesRef = chatRef.document(chatId).collection("messages");

        messagesRef
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(ChatActivity.this, "Failed to load messages", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        List<Message> tempMessageList = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value) {
                            Message message = doc.toObject(Message.class);
                            tempMessageList.add(message);
                        }

                        fetchSenderNames(tempMessageList, updatedMessageList -> {
                            updatedMessageList.sort((m1, m2) -> m1.getTimestamp().compareTo(m2.getTimestamp()));

                            messageList.clear();
                            messageList.addAll(updatedMessageList);
                            messageAdapter.notifyDataSetChanged();

                            recyclerViewChat.scrollToPosition(messageList.size() - 1);
                        });
                    }
                });
    }

    private void fetchSenderNames(List<Message> messages, OnMessagesProcessedListener listener) {
        List<Message> processedMessages = new ArrayList<>();
        for (Message message : messages) {
            db.collection("users").document(message.getSenderId())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            String name = task.getResult().getString("name");
                            message.setSenderName(name != null ? name : "Unknown User");
                        } else {
                            message.setSenderName("Unknown User");
                        }

                        processedMessages.add(message);

                        if (processedMessages.size() == messages.size()) {
                            listener.onMessagesProcessed(processedMessages);
                        }
                    });
        }
    }

    interface OnMessagesProcessedListener {
        void onMessagesProcessed(List<Message> messages);
    }


    interface OnNameFetchedListener {
        void onNameFetched(String name);
    }

    private void sendMessage(String messageText) {
        Message message = new Message(currentUserId, contactUserId, messageText, Timestamp.now());

        String chatId = getChatId(currentUserId, contactUserId);
        CollectionReference messagesRef = chatRef.document(chatId).collection("messages");

        chatRef.document(chatId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (!task.getResult().exists()) {
                    chatRef.document(chatId).set(new Chat(currentUserId, contactUserId))
                            .addOnSuccessListener(aVoid -> Log.d("ChatActivity", "Chat document created with userIds"))
                            .addOnFailureListener(e -> Log.e("ChatActivity", "Error creating chat document", e));
                }
            } else {
                Log.e("ChatActivity", "Error checking chat document", task.getException());
            }
        });

        messagesRef.add(message)
                .addOnSuccessListener(documentReference -> {
                    Log.d("ChatActivity", "Message sent successfully!");
                    etMessageInput.setText("");
                })
                .addOnFailureListener(e -> {
                    Log.e("ChatActivity", "Error sending message", e);
                    Toast.makeText(ChatActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                });
    }

    public class Chat {
        private List<String> userIds;

        public Chat(String userId1, String userId2) {
            this.userIds = new ArrayList<>();
            this.userIds.add(userId1);
            this.userIds.add(userId2);
        }

        public List<String> getUserIds() {
            return userIds;
        }

        public void setUserIds(List<String> userIds) {
            this.userIds = userIds;
        }
    }

    private String getChatId(String userId1, String userId2) {
        return userId1.compareTo(userId2) < 0 ? userId1 + "_" + userId2 : userId2 + "_" + userId1;
    }
}
