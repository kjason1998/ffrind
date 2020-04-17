package com.kevex.ffriend.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.kevex.ffriend.Adapter.MessageAdapter;
import com.kevex.ffriend.Model.User;
import com.kevex.ffriend.R;
import com.kevex.ffriend.view.QrCodeView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private final String TAG = "ChatActivity";
    @ServerTimestamp
    Date time;

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

    private EditText messageInput;

    private TextView titleNameToolBar;
    private TextView titleAgeToolBar;
    private TextView titleGenderToolBar;

    private Toolbar chatToolBar;
    private User otherUser;
    private ImageView ivQr;
    private View layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ivQr = (ImageView) findViewById(R.id.iv_qr);

        Intent intent = getIntent();
        otherUser = (User) intent.getSerializableExtra(getResources().getString(R.string.intetntOhterUser));

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
        currentUserRefrence = database.collection(getResources().getString(R.string.dbUsers)).document(currentUser.getUid());

        setupToolBar();

        setupChatsRecyclerView();


    }

    private void setupToolBar() {
        chatToolBar = findViewById(R.id.chat_toolbar);

        titleNameToolBar = findViewById(R.id.chatTooldBarTitleName);
        titleAgeToolBar = findViewById(R.id.chatTooldBarTitleAge);
        titleGenderToolBar = findViewById(R.id.chatTooldBarTitleGender);

        setSupportActionBar(chatToolBar);
        //getSupportActionBar().setTitle(otherUser.getUsername());
        titleNameToolBar.setText(otherUser.getUsername());
        titleAgeToolBar.setText(otherUser.getAge());
        titleGenderToolBar.setText(otherUser.getGender());

    }


    /**
     * will setup the recycle view, by connecting the to the firestore
     * after connecting to firestore, enable to send new message
     */
    private void setupChatsRecyclerView() {
        addListenerOnDatabaseReference();
        chatsView = findViewById(R.id.chatRecyclerView);
        setRecyclerView();
    }

    private void enableSendMessage() {
        messageInput = findViewById(R.id.chatInputMessage);
        messageInput.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    String newStringMessage = messageInput.getText().toString();
                    putNewMessageInFirestore(newStringMessage);
                    messageInput.setText("");
                    return true;
                }
                return false;
            }
        });
    }

    private void putNewMessageInFirestore(String message) {
        if (!chatId.isEmpty() && chatReference != null) {

            // Atomically add a new region to the "regions" array field.
            Map<String, Object> newMessage = new HashMap();
            newMessage.put(getResources().getString(R.string.dbMessage), message);
            newMessage.put(getResources().getString(R.string.dbSenderUid), mAuth.getCurrentUser().getUid());
            newMessage.put(getResources().getString(R.string.dbSentMessage), new Date());
            chatReference.update(getResources().getString(R.string.dbMessages), FieldValue.arrayUnion(newMessage));
        }
    }

    private void addListenerOnDatabaseReference() {
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
                                    chatsView.smoothScrollToPosition(messageList.size()-1);
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


        enableSendMessage();
    }

    public void startScan(View view) {
        startActivity(new Intent(this, ScanActivity.class));
    }

    public void generateQR(View view) {
        String userID = otherUser.getUserID();
        QRCodeWriter writer = new QRCodeWriter();
        ivQr = (ImageView) findViewById(R.id.iv_qr);
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.view_dialog_qr, null);
        ImageView qrCodeImage = layout.findViewById(R.id.iv_qr);
        Bitmap bitmap = null;
        try {
            BitMatrix matrix = writer.encode(userID.trim(), BarcodeFormat.QR_CODE, 300, 300);
            bitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.RGB_565);
            for (int i = 0; i < 300; i++) {
                for (int j = 0; j < 300; j++) {
                    bitmap.setPixel(i, j, matrix.get(i, j) ? Color.BLACK : Color.WHITE);
                }
            }
            qrCodeImage.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }

        new QrCodeView(this, bitmap).show();

    }
}
