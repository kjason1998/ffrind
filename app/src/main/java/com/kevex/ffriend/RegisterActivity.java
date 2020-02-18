package com.kevex.ffriend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {
    String email;
    String password;
    private FirebaseAuth userAuthenticate;
    @Override
    public void onStart(){
        super.onStart();

        FirebaseUser currentUser = userAuthenticate.getCurrentUser();
        if(currentUser != null) {
            System.out.println(currentUser.toString());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        userAuthenticate = FirebaseAuth.getInstance();
        email = "Testing1@hotmail.com";
        password = "Testing";

    }


    public void register(View view){

        userAuthenticate.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "Success",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
}
