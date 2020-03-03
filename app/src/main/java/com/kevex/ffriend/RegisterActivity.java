package com.kevex.ffriend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private final static String TAG = "RegisterActivity";

    private String email;
    private String password;
    private String confirmPassword;
    private String username;
    private String phoneNumber;

    private FirebaseAuth userAuthenticate;
    private FirebaseFirestore db;

    private EditText registerEmail;
    private EditText registerPassword;
    private EditText registerUserName;
    private EditText registerPhoneNumber;
    private EditText registerConfirmPassword;

    @Override
    public void onStart(){
        super.onStart();
        // ????????????????????????
        FirebaseUser currentUser = userAuthenticate.getCurrentUser();
        if(currentUser != null) {
            System.out.println(currentUser.toString());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //setting up Firebase
        userAuthenticate = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //inputs
        registerEmail = findViewById(R.id.registerEmailInput);
        registerPassword = findViewById(R.id.registerPasswordInput);
        registerConfirmPassword = findViewById(R.id.registerPasswordConfirmationInput);
        registerPhoneNumber = findViewById(R.id.registerPhoneNumberInput);
        registerUserName = findViewById(R.id.registerUserNameInput);
    }

    /**
     *
     * android:onClick for register button
     * This will register a new user
     *
     * @param view
     */
    public void register(View view){
        Toast.makeText(RegisterActivity.this, TAG + " register button clicked",
                Toast.LENGTH_SHORT).show();

        email = registerEmail.getText().toString();
        password = registerPassword.getText().toString();
        confirmPassword = registerConfirmPassword.getText().toString();
        phoneNumber = registerPhoneNumber.getText().toString();
        username = registerUserName.getText().toString();

        if(password.matches(confirmPassword)) {
            userAuthenticate.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                addUserToDB();
                            } else {
                                Toast.makeText(RegisterActivity.this, getResources().getString(R.string.registerMessageRegisterFailed),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(RegisterActivity.this, getResources().getString(R.string.registerMessagePasswordUnmatched),
                    Toast.LENGTH_SHORT).show();
        }


    }

    /**
     *
     * go to home screen (map activity)
     *
     */
    public void changeToHomeScreen(){
        Intent homeIntent = new Intent(this, MapsActivity.class);
        homeIntent.putExtra("username", username);
        startActivity(homeIntent);
    }

    /**
     *
     * android:onClick cancel button
     *
     * go to previous screen (loggin activity)
     *
     */
    public void back(View view){
        finish();
    }

    /**
     *
     * add the new user to the database (Firestore)
     *
     */
    public void addUserToDB(){

        Map<String, Object> user = new HashMap<>();
        user.put(getResources().getString(R.string.dbEmail), email);
        user.put(getResources().getString(R.string.dbUserame), username);
        user.put(getResources().getString(R.string.dbPhoneNumber), phoneNumber);

        // Add a new document with a generated ID
        db.collection(getResources().getString(R.string.dbUsers))
                .document(userAuthenticate.getCurrentUser().getUid())
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(RegisterActivity.this, getResources().getString(R.string.registerMessageRegisterSuccess),
                                Toast.LENGTH_LONG).show();
                        changeToHomeScreen();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, getResources().getString(R.string.registerMessageRegisterFailed),
                                Toast.LENGTH_SHORT).show();
                        Log.e(TAG,"Error adding document to Firestore :" + e);
                    }
                });


    }
}
