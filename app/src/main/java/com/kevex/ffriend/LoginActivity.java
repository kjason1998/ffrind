package com.kevex.ffriend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.view.View;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private String email;
    private String password;
    private EditText emailInput;
    private EditText passwordInput;
    private FirebaseAuth userAuthenticate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userAuthenticate = FirebaseAuth.getInstance();

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = userAuthenticate.getCurrentUser();

        if(currentUser != null){
            //changeToHomeScreen();
        }
    }

    /**
     *
     * android:onClick for sign up button
     * go to register screen
     *
     * @param view
     */
    public void changeToRegister(View view) {
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        startActivity(registerIntent);
    }

    /**
     *
     *  go to home screen (map activity)
     *
     */
    public void changeToHomeScreen(){
        Intent homeIntent = new Intent(this, MapsActivity.class);
        startActivity(homeIntent);
    }

    /**
     *
     * android:onClick for login button
     * try to log user in, if success this will call
     * changeToHomeScreen - to direct user to home screen
     *
     * @param view
     */
    public void login(View view) {
        userAuthenticate.signOut();
        email = emailInput.getText().toString();
        password = passwordInput.getText().toString();

        //replacement for loading coz we dont know if we clicked it or not
        Toast.makeText(this,"LOGIN BUTTON CLICKED", Toast.LENGTH_SHORT).show();

        userAuthenticate.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            changeToHomeScreen();
                            Toast.makeText(LoginActivity.this, getResources().getString(R.string.loginMessageLoginSuccess),
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(LoginActivity.this, getResources().getString(R.string.loginMessageLoginFailed),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
