package com.kevex.ffriend.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.Result;
import com.kevex.ffriend.R;

import java.util.ArrayList;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.PACKAGE_USAGE_STATS;

public class ScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private static final String TAG = "ScanActivity";
    public static final int REQUEST_CAMERA = 1;
    private ZXingScannerView scannerView;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private FirebaseAuth userAuth;
    private DocumentReference currentUserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);
        db = FirebaseFirestore.getInstance();
        userAuth = FirebaseAuth.getInstance();
        currentUser = userAuth.getCurrentUser();
        currentUserRef = db.collection(getResources().getString(R.string.dbUsers)).
                document(currentUser.getUid());


        if (scannerView == null)
            Log.d(TAG, "onCreate: scannerView==null");
        setContentView(scannerView);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            if (checkPermission()) {
                Toast.makeText(this, "permission is granted!", Toast.LENGTH_SHORT).show();

            } else {
                requestPermissions();
            }
        }

    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }

    private boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(this, CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults.length > 0) {
                    boolean cameraAccept = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccept) {
                        Toast.makeText(this, "permission is granted", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            if (shouldShowRequestPermissionRationale(CAMERA)) {
                                displayAlertMessage("you need to allow access to the permission", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        requestPermissions(new String[]{CAMERA}, REQUEST_CAMERA);
                                    }
                                });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            if (checkPermission()) {
                if (scannerView == null) {
                    scannerView = new ZXingScannerView(this);
                    setContentView(scannerView);
                }
                scannerView.setResultHandler(this);
                scannerView.startCamera();
            } else {
                requestPermissions();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        scannerView.stopCamera();
    }

    public void displayAlertMessage(String message, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("ok", listener)
                .setNegativeButton("cancel", null)
                .create()
                .show();

    }

    /**
     * @author daniel
     * This method retrieves the current users' current points from the firestore database.
     * @param resultText - the UID of the other user to be used for validation.
     */
    public void getCurrentUsersCurrentPoints(final String resultText){
        currentUserRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    double currentPoints = task.getResult().getDouble(getResources().getString(R.string.dbPoints));
                    ArrayList<String> usersMet = (ArrayList<String>) task.getResult().
                            get(getResources().getString(R.string.dbUsersMet));
                    if(!usersMet.contains(resultText) && !currentUser.getUid().equals(resultText)){
                        addPointsToCurrentUser(currentPoints);
                        addOtherUserToCurrentUserMetList(resultText);
                    } else {
                        Log.d(TAG, "onComplete: ++ users already met and got points");
                    }
                }
            }
        });

    }

    /**
     * @author daniel
     * Method to get the other users' current points from firestore database.
     * @param resultText - the UID of the other user, retrieved from scanning the QR code.
     * @param otherUserRef - a document reference for the other users entry in firestore.
     */
    public void getOtherUsersCurrentPoints(final String resultText, DocumentReference otherUserRef){
       otherUserRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    double otherUserCurrentPoints = task.getResult().getDouble(getResources().getString(R.string.dbPoints));
                    ArrayList<String> usersMet = (ArrayList<String>) task.getResult().
                            get(getResources().getString(R.string.dbUsersMet));

                    if(!usersMet.contains(currentUser.getUid())){
                        addPointsToOtherUser(otherUserCurrentPoints, resultText);
                        addCurrentUserToOtherUserMetList(resultText);
                    } else {
                        Log.d(TAG, "onComplete: ++ users already met and got points");
                    }
                }
            }
        });

    }

    @Override
    public void handleResult(Result result) {

        final String resultText = result.getText();
        final DocumentReference otherUserRef = db.collection(getResources().getString(R.string.dbUsers)).
                document(resultText);
        getCurrentUsersCurrentPoints(resultText);
        getOtherUsersCurrentPoints(resultText, otherUserRef);

        Log.d(TAG, "handleResult: " + resultText);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("scan result");
        builder.setMessage(resultText);
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                scannerView.resumeCameraPreview(ScanActivity.this);
            }
        });
        builder.setNegativeButton("visit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(resultText)));

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * @author Daniel
     * Method to update the points of the current user.
     * @param currentPoints - the points that the current user has before updating - retrieved
     *                      from firebase
     */
    public void addPointsToCurrentUser(double currentPoints) {
        double updatedPoints = currentPoints + 10;
        currentUserRef.update(getResources().getString(R.string.dbPoints), updatedPoints).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "onComplete: current user points updated");
            }
        });
    }

    /**
     * @author Daniel
     * Method to update the points of the other user.
     * @param currentPoints - the points that the other user has before updating - retrieved
     *                      from firebase
     */
    public void addPointsToOtherUser(double currentPoints, String otherUserUID) {
        double updatedPoints = currentPoints + 10;
        db.collection(getResources().getString(R.string.dbUsers)).document(otherUserUID).
                update(getResources().getString(R.string.dbPoints), updatedPoints).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "onComplete: other user points updated");
                    }
                });
    }


    /**
     * @author Daniel
     * This method add the other users' uid to the list of users that the current user has met.
     * @param otherUserUID - the firebase UID of the other user.
     */
    public void addOtherUserToCurrentUserMetList(String otherUserUID){
        currentUserRef.update(getResources().getString(R.string.dbUsersMet), FieldValue.arrayUnion(otherUserUID))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "onComplete: other user added to current user met list");
                    }
                });
    }

    /**
     * @author Daniel
     * This method add the current users' uid to the list of users that the other user has met.
     * @param otherUserUID - the firebase UID of the other user.
     */
    public void addCurrentUserToOtherUserMetList(String otherUserUID){
        db.collection(getResources().getString(R.string.dbUsers)).document(otherUserUID).
                update(getResources().getString(R.string.dbUsersMet), FieldValue.arrayUnion(currentUser.getUid()))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "onComplete: current user added to other user met list");
                    }
                });
    }
}
