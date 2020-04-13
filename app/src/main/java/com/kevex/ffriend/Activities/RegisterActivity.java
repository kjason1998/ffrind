package com.kevex.ffriend.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kevex.ffriend.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RegisterActivity extends AppCompatActivity {
    private final static String TAG = "RegisterActivity";

    private String email;
    private String password;
    private String confirmPassword;
    private String username;
    private String phoneNumber;

    private FirebaseAuth userAuthenticate;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;


    private EditText registerEmail;
    private EditText registerPassword;
    private EditText registerUserName;
    private EditText registerPhoneNumber;
    private EditText registerConfirmPassword;


    private ProgressDialog progressDialog;

    @Override
    public void onStart(){
        super.onStart();

        FirebaseUser currentUser = userAuthenticate.getCurrentUser();
        if(currentUser != null){
            changeToHomeScreen();
        }

        initiateProgressDialog();
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
     * Initiate the progress dialog that will be used in register button.
     */
    private void initiateProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Registering");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
    }

    /**
     *
     * android:onClick for register button
     * This will register a new user
     *
     * @param view
     */
    public void register(View view){
        progressDialog.show();

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
                            progressDialog.dismiss();
                        }
                    });
        } else {
            progressDialog.dismiss();
            Toast.makeText(RegisterActivity.this, getResources().getString(R.string.registerMessagePasswordUnmatched),
                    Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Open the google map activity.
     */
    private void changeToHomeScreen() {
        Intent homeIntent = new Intent(this, MapsActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeIntent);
    }

    /**
     * Open the google map activity.
     */
    private void changeToProfileSetupScreen() {
        Intent profileSetupIntent = new Intent(this, ProfileSetupActivity.class);
        startActivity(profileSetupIntent);
    }

    /**
     * android:onClick cancel button
     *
     * go to previous screen (loggin activity)
     */
    public void back(View view){
        finish();
    }

    /**
     * add the new user to the database (Firestore)
     */
    public void addUserToDB(){

        CollectionReference users = db.collection("users");

        final String url = randomAvatarUrl();

        Map<String, Object> user = new HashMap<>();
        user.put(getResources().getString(R.string.dbEmail), email);
        user.put(getResources().getString(R.string.dbUserame), username);
        user.put(getResources().getString(R.string.dbPhoneNumber), phoneNumber);
        user.put(getResources().getString(R.string.dbLat), 51.6);
        user.put(getResources().getString(R.string.dbLon), -3.9);
        user.put(getResources().getString(R.string.dbAvatarUrl), url);
        user.put(getResources().getString(R.string.dbBio), getResources().getString(R.string.profileDefaultDescription));
        user.put(getResources().getString(R.string.dbAge), getResources().getString(R.string.profileDefaultAge));
        user.put(getResources().getString(R.string.dbGender), getResources().getString(R.string.profileGenderMale));

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Add a new document with a generated ID
        users.document(currentUser.getUid()).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                setUserAvatar(currentUser,url);
                changeToHomeScreen();
                changeToProfileSetupScreen();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this, "Action Failed -> " + e.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * set the user profile picture
     */
    private void setUserAvatar(FirebaseUser currentUser,String url){
        Uri avatarURL = Uri.parse(url);

        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setPhotoUri(avatarURL).build();

        currentUser.updateProfile(request);
    }

    /**
     * Returns a random URL representing the users avatar image
     *
     * @return String - the URL of the assigned avatar image
     */
    public String randomAvatarUrl(){
        final String[] AVATARS = {
                "https://firebasestorage.googleapis.com/v0/b/psyched-garage-265415.appspot.com/o/Avatar%201.jpg?alt=media&token=f30299f7-cfff-48c7-b3e8-d929706cd3b2",
                "https://firebasestorage.googleapis.com/v0/b/psyched-garage-265415.appspot.com/o/Avatar%202.jpg?alt=media&token=dcd1f7ab-bbd4-465e-964a-89fbee403829",
                "https://firebasestorage.googleapis.com/v0/b/psyched-garage-265415.appspot.com/o/Avatar%203.jpg?alt=media&token=c250e79f-e6c0-488b-8263-cf057e63bd98",
                "https://firebasestorage.googleapis.com/v0/b/psyched-garage-265415.appspot.com/o/Avatar%204.jpg?alt=media&token=63ee938d-8e1a-4c00-9661-27d4d1f86366",
                "https://firebasestorage.googleapis.com/v0/b/psyched-garage-265415.appspot.com/o/Avatar%205.jpg?alt=media&token=c28bcdab-27e4-48ef-b4d7-bb123848edf4",
                "https://firebasestorage.googleapis.com/v0/b/psyched-garage-265415.appspot.com/o/Avatar%206.jpg?alt=media&token=7b1e7f7c-7e39-4c0d-979c-081fa0b534df"
        };
        Random random = new Random();
        int rnd = random.nextInt(5);
        return AVATARS[rnd];
    }
}
