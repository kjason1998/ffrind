package com.kevex.ffriend.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.kevex.ffriend.Adapter.MessageAdapter;
import com.kevex.ffriend.Model.User;
import com.kevex.ffriend.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private final String TAG = "ChatActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore database;
    private DocumentReference chatReference;
    private FirebaseUser currentUser;
    private DocumentReference currentUserRefrence;

    private RecyclerView chatsView;
    private RecyclerView.LayoutManager chatLinearLayout;
    private MessageAdapter chatAdapter;
    private String chatId = "";
    private List<Map> messageList = new ArrayList<>();

    private Toolbar chatToolBar;
    private User otherUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        otherUser = (User)intent.getSerializableExtra(getResources().getString(R.string.intetntOhterUser));

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
        currentUserRefrence = database.collection(getResources().getString(R.string.dbUsers)).document(currentUser.getUid());

        setupToolBar();

        setupChatsRecyclerView();
    }

    private void setupChatsRecyclerView() {
        addListenerOnDabaseRefrence();
        chatsView = findViewById(R.id.chatRecyclerView);
        setRecyclerView();
    }

    private void setupToolBar() {
        chatToolBar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(chatToolBar);
        getSupportActionBar().setTitle(otherUser.getUsername());
    }

    private void addListenerOnDabaseRefrence() {
        // get the chat id
        currentUserRefrence.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        chatId = document.getString(otherUser.getUserID());
                        // add a listener on the documents
                        chatReference = database.collection(getResources().getString(R.string.dbChats)).document(chatId);
                        chatReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                                @Nullable FirebaseFirestoreException e) {
                                if (e != null) {
                                    Log.w(TAG, "Listen failed.", e);
                                    return;
                                }

                                if (snapshot != null && snapshot.exists()) {
                                    messageList.clear();
                                    ArrayList<Map> newArray = (ArrayList<Map>) snapshot.get(getResources().getString(R.string.dbMessages));
                                    messageList.addAll(newArray);
                                    chatAdapter.notifyDataSetChanged();
                                } else {
                                    Log.d(TAG, "Current data: null");
                                }
                            }
                        });
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    /**
     * Starting recycler view and connect it to
     * the message adapter.
     */
    private void setRecyclerView() {
        chatLinearLayout = new LinearLayoutManager(this);
        chatsView.setLayoutManager(chatLinearLayout);

        chatAdapter = new MessageAdapter(messageList);
        chatsView.setAdapter(chatAdapter);
        chatsView.setItemAnimator(new DefaultItemAnimator());
    }
}
