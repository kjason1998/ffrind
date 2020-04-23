package com.kevex.ffriend.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kevex.ffriend.R;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private EditText emailInput;
    private EditText passwordInput;
    private FirebaseAuth userAuthenticate;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userAuthenticate = FirebaseAuth.getInstance();

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("login");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = userAuthenticate.getCurrentUser();

        if(currentUser != null){
            changeToHomeScreen();
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
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();


        if (TextUtils.isEmpty(email)) {
            emailInput.setError(getResources().getString(R.string.noEmailStringWarning));
        } else if (TextUtils.isEmpty(password)) {
            passwordInput.setError(getResources().getString(R.string.noPasswordStringWarning));
        } else {
            progressDialog.show();
            userAuthenticate.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            try {
                                task.getResult();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(LoginActivity.this, e.getMessage().split(": ")[1], Toast.LENGTH_SHORT).show();
                            }
                            if (task.isSuccessful()) {
                                changeToHomeScreen();
                                Toast.makeText(LoginActivity.this, getResources().getString(R.string.loginMessageLoginSuccess),
                                        Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(LoginActivity.this, getResources().getString(R.string.loginMessageLoginFailed),
                                        Toast.LENGTH_LONG).show();
                            }
                            progressDialog.dismiss();
                        }
                    });
        }
    }
}
