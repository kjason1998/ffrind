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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    String email;
    String password;
    String confirmPassword;
    String username;
    String phoneNumber;
    private FirebaseAuth userAuthenticate;
    FirebaseFirestore db;
    EditText registerEmail;
    EditText registerPassword;
    EditText registerUserName;
    EditText registerPhoneNumber;
    EditText registerConfirmPassword;

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = userAuthenticate.getCurrentUser();
        if (currentUser != null) {
            System.out.println(currentUser.toString());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        userAuthenticate = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        registerEmail = findViewById(R.id.registerEmailInput);
        registerPassword = findViewById(R.id.registerPasswordInput);
        registerConfirmPassword = findViewById(R.id.registerPasswordConfirmationInput);
        registerPhoneNumber = findViewById(R.id.registerPhoneNumberInput);
        registerUserName = findViewById(R.id.registerUserNameInput);
    }


    public void register(View view) {

        email = registerEmail.getText().toString();
        password = registerPassword.getText().toString();
        confirmPassword = registerConfirmPassword.getText().toString();
        phoneNumber = registerPhoneNumber.getText().toString();
        username = registerUserName.getText().toString();

        if (password.matches(confirmPassword)) {
            userAuthenticate.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Success",
                                        Toast.LENGTH_LONG).show();
                                addUserToDB();
                                changeToHomeScreen();
                            } else {
                                Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(RegisterActivity.this, "Passwords do not match",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void changeToHomeScreen() {
        Intent homeIntent = new Intent(this, MapsActivity.class);
        startActivity(homeIntent);
    }


    public void addUserToDB() {

        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        user.put("username", username);
        user.put("phone number", phoneNumber);

        // Add a new document with a generated ID
        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(RegisterActivity.this, "DocumentSnapshot added with ID: " + documentReference.getId(),
                                Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, "Error adding document" + e,
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void cancel(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }
}
