package com.kevex.ffriend.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.kevex.ffriend.R;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kevin
 * Profile settup is an activity for changging some default information
 * TODO: Sajal ask to change showing profile picture instead of showing logo
 */
public class ProfileSetupActivity extends AppCompatActivity {
    private final static int RADIO_ID_MALE = 1000;
    private final static int RADIO_ID_FEMALE = 2000;

    private Button submitButton;
    private Button skipButton;

    private EditText ageEditText;
    private EditText bioEditText;

    private RadioGroup radioGroupGender;
    private RadioButton radioMaleButton;
    private RadioButton radioFemaleButton;

    private FirebaseAuth userAuthenticate;
    private FirebaseFirestore db;
    private DocumentReference currentUserRef;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        initializeFirebase();
        initializeComponents();
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    private void initializeFirebase() {
        userAuthenticate = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = userAuthenticate.getCurrentUser();
        currentUserRef = db.collection(this.getResources().getString(R.string.dbUsers)).document(currentUser.getUid());
    }

    private void initializeComponents() {
        ageEditText= findViewById(R.id.profileSetupAge);
        bioEditText= findViewById(R.id.profileSetupDescription);

        radioGroupGender = findViewById(R.id.profileSetupRadioGenderGroup);
        radioMaleButton = findViewById(R.id.profileSetupRadioMaleRadioButton);
        radioFemaleButton= findViewById(R.id.profileSetupRadioFemaleRadioButton);

        radioMaleButton.setId(RADIO_ID_MALE);
        radioFemaleButton.setId(RADIO_ID_FEMALE);

        submitButton = findViewById(R.id.profileSetupSubmitButton);
        skipButton = findViewById(R.id.profileSetupSkipButton);
    }

    /**
     * skip button method for this activity
     */
    public void skipProfileSetup(View view){
        changeToHomeScreen();
    }

    /**
     * submit button method for this activity
     *
     * check all input are not empty
     * check age is between 16 and 150
     * check if radio button is chosen
     */
    public void submitProfileSetup(View view){

        String newDescription = bioEditText.getText().toString();
        String newAgeString = ageEditText.getText().toString();
        int selected = radioGroupGender.getCheckedRadioButtonId();

        if (TextUtils.isEmpty(newDescription)) {
            bioEditText.setError(getResources().getString(R.string.noBioStringWarning));
        }else if (TextUtils.isEmpty(newAgeString)) {
            ageEditText.setError(getResources().getString(R.string.noAgeStringWarning));
        }else{
            if(checkAgeFormat(newAgeString)) {
                if (selected == RADIO_ID_MALE) {
                    updateDetail(newDescription, newAgeString, getResources().getString(R.string.profileGenderMale));
                } else if (selected == RADIO_ID_FEMALE) {
                    updateDetail(newDescription, newAgeString, getResources().getString(R.string.profileGenderFemale));
                } else {
                    Toast.makeText(this, getResources().getString(R.string.profileSetupMessageGenderWrong), Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this, getResources().getString(R.string.profileSetupMessageAgeWrong), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean checkAgeFormat(String age){
        if(Integer.valueOf(age)>16&&Integer.valueOf(age)<150){
            return true;
        }else{
            return false;
        }
    }

    /**
     * Open the google map activity.
     */
    private void changeToHomeScreen() {
        Intent homeIntent = new Intent(this, MapsActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(homeIntent);
    }

    /**
     * udpate all the param inside the method
     *
     * @param bio - new biography for the user
     * @param age - new Age (16-150) for the user
     * @param gender - new Gender (male/female) for the user
     */
    private void updateDetail(String bio, String age, String gender) {
        Map<String, Object> data = new HashMap<>();
        data.put(getResources().getString(R.string.dbAge), age);
        data.put(getResources().getString(R.string.dbBio), bio);
        data.put(getResources().getString(R.string.dbGender), gender);

        currentUserRef.set(data, SetOptions.merge());
        changeToHomeScreen();

    }
}
