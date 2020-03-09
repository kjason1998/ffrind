package com.kevex.ffriend;

import android.Manifest;
import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private final String TAG = "MapsActivity";

    private final int locationRequestCode = 1000;
    private FirebaseAuth userAuthenticate;
    private FirebaseFirestore db;
    private DocumentReference currentUserRef;
    private FirebaseUser currentUser;

    private FusedLocationProviderClient mFusedLocationClient;
    private GoogleMap mMap;

    private boolean mLocationPermissionGranted;
    private boolean showingBottomSheetCurrentUser = true;
    private double wayLatitude = 0.0, wayLongitude = 0.0;

    private Toolbar mToolbar;
    private ImageView avatar;
    private TextView usernameDisplay;
    private TextView userBioDisplay;

    private BottomSheetBehavior bottomSheetBehavior;
    private User otherUserToBeShown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showingBottomSheetCurrentUser = true;
        otherUserToBeShown = null;

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        LinearLayout llBottomSheet = findViewById(R.id.mapBottomSheet);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLocationPermission();
        mapFragment.getMapAsync(this);

        // Firebase initialize
        userAuthenticate = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = userAuthenticate.getCurrentUser();
        currentUserRef = db.collection(this.getResources().getString(R.string.dbUsers)).document(currentUser.getUid());

        //setup tool bar
        mToolbar = findViewById(R.id.maps_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Maps");

        //initialize bottom sheet
        bottomSheetInitializer(llBottomSheet);
    }

    /**
     *
     * author: kevin jason
     * initiate the card view profile and give animation dragging out and in
     *
     * @param llBottomSheet
     */
    private void bottomSheetInitializer(LinearLayout llBottomSheet) {

        // initialize the information components inside this fragment
        llBottomSheet.post(new Runnable() {

            @Override
            public void run() {
                avatar = findViewById(R.id.avatarImageView);
                usernameDisplay = findViewById(R.id.profileUserNameInfo);
                userBioDisplay = findViewById(R.id.profileBioInfo);
            }
        });

        // init the bottom sheet behavior
        bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);

        // set the peek height
        bottomSheetBehavior.setPeekHeight(120);

        // set hide able or not
        bottomSheetBehavior.setHideable(false);

        // set callback for changes
        bottomSheetBehavior.setBottomSheetCallback(
                new BottomSheetBehavior.BottomSheetCallback() {

                    @Override
                    public void onSlide(@NonNull View view, float v) {

                    }

                    @Override
                    public void onStateChanged(@NonNull View bottomSheet, int newState) {
                        switch (newState) {
                            case BottomSheetBehavior.STATE_HIDDEN:

                                break;
                            case BottomSheetBehavior.STATE_EXPANDED:
                                if(showingBottomSheetCurrentUser){
                                    updateUserAvatar(bottomSheet);
                                    currentUserRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                if (document.exists()) {
                                                    usernameDisplay.setText(document.getString(getResources().getString(R.string.dbUserame)));
                                                    userBioDisplay.setText(document.getString(getResources().getString(R.string.dbUserame)));
                                                } else {
                                                    Log.e(TAG, "No such document");
                                                }
                                            } else {
                                                Log.e(TAG, "get failed with ", task.getException());
                                            }
                                        }
                                    });
                                    usernameDisplay.setText(currentUser.getDisplayName());
                                }else{
                                    updateUserAvatar(bottomSheet);
                                    currentUserRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                if (document.exists()) {
                                                    usernameDisplay.setText(otherUserToBeShown.getUsername());
                                                    userBioDisplay.setText(document.getString(getResources().getString(R.string.dbUserame)));
                                                } else {
                                                    Log.e(TAG, "No such document");
                                                }
                                            } else {
                                                Log.e(TAG, "get failed with ", task.getException());
                                            }
                                        }
                                    });
                                    usernameDisplay.setText(currentUser.getDisplayName());
                                }
                                break;
                            case BottomSheetBehavior.STATE_COLLAPSED:
                                otherUserToBeShown = null;
                                showingBottomSheetCurrentUser = true;
                                break;
                            case BottomSheetBehavior.STATE_DRAGGING:
                                break;
                            case BottomSheetBehavior.STATE_SETTLING:
                                break;
                        }
                        Log.d(TAG, "onStateChanged: " + newState);
                    }

                });
    }

    /**
     * Manipulates the map, once available.
     * This callback is triggered when the map is ready to be used.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // check permission
        mMap = googleMap;
        setGoogleMapStyles(googleMap);
        // Add a marker circle marker around swansea
        Circle circle = mMap.addCircle(new CircleOptions()
                .center(new LatLng(51.62, -3.94))
                .radius(10)
                .strokeWidth(10)
                .strokeColor(Color.WHITE)
                .fillColor(Color.BLUE)
                .clickable(true));

        circle.setTag(new User("email@gmail.com","Strong password", "Other user username", "123423425324"));

        mMap.setOnCircleClickListener(onClickCircleListener());
        mMap.setMyLocationEnabled(true);
    }

    /**
     * handle when after asking permission finish
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            // 1000 -> location request code
            case 1000: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                wayLatitude = location.getLatitude();
                                wayLongitude = location.getLongitude();
                                Toast.makeText(MapsActivity.this.getApplicationContext(), "onRequestPermissionsResult" + String.format(Locale.US, "%s -- %s", wayLatitude, wayLongitude), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(this, "location change", Toast.LENGTH_SHORT).show();
//        mCurrentLocation = location;
//        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
//        moveUser(Location location);
    }


    /**
     * Circle marker on click handler
     */
    private GoogleMap.OnCircleClickListener onClickCircleListener() {
        return new GoogleMap.OnCircleClickListener() {
            @Override
            public void onCircleClick(Circle circle) {
                //Toast.makeText(getApplicationContext(), "id is" + circle.getId(), Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "OBJECT: " + circle.getTag(), Toast.LENGTH_SHORT).show();
                animateCircle(circle);
                openBottomSheetOtherUser(circle.getTag());
            }
        };
    }

    private void openBottomSheetOtherUser(Object user) {
        showingBottomSheetCurrentUser = false;
        otherUserToBeShown = (User) user;
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //ask for permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    locationRequestCode);

        } else {
            mLocationPermissionGranted = true;
            // already permission granted
            // get location here
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        wayLatitude = location.getLatitude();
                        wayLongitude = location.getLongitude();
                        Toast.makeText(MapsActivity.this.getApplicationContext(), "getLocationpermission" + String.format(Locale.US, "%s -- %s", wayLatitude, wayLongitude), Toast.LENGTH_SHORT).show();
                        getDeviceLocation();
                    }
                }
            });
        }
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /**
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "getDevicelocationMLocationGrantedTaskSuccess", Toast.LENGTH_LONG).show();
                            Toast.makeText(getApplicationContext(), "" + task.getResult().getLatitude() + " " + task.getResult().getLongitude(), Toast.LENGTH_LONG).show();
                            // Set the map's camera position to the current location of the device.
                            //mLastKnownLocation = task.getResult(); delete this
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(task.getResult().getLatitude(),
                                            task.getResult().getLongitude()), 15));
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /**
     * Styling the google map
     *
     * @param googleMap
     */
    private void setGoogleMapStyles(GoogleMap googleMap) {
        try {
            // Customise the styling of the base map using a JSON object defined in /res/raw folder
            // Map design is from https://mapstyle.withgoogle.com/
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
    }

    /**
     *
     * author: Kevin Jason
     *
     * animation clicked for circle
     *
     * @param circle
     */
    public void animateCircle(final Circle circle) {
        ValueAnimator vAnimator = ValueAnimator.ofInt(9, 10);
        //vAnimator.setRepeatCount(ValueAnimator.INFINITE);
        vAnimator.setRepeatMode(ValueAnimator.REVERSE);
        vAnimator.setEvaluator(new IntEvaluator());
        vAnimator.setDuration(150);

        vAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        vAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int animatedValue = (Integer) valueAnimator.getAnimatedValue();
                circle.setRadius(animatedValue);
            }
        });
        vAnimator.start();
        //Toast.makeText(getApplicationContext(), "Finish animation", Toast.LENGTH_LONG).show();
    }

    /**
     *
     * set the user profile picture that have been randomly
     * assigned when user registered.
     *
     */
    public void updateUserAvatar(View bottomSheet){
        Glide.with(bottomSheet)
                .load(currentUser.getPhotoUrl())
                .placeholder(R.drawable.avatar_default)
                .into(avatar);
    }

    /**
     * This method is for logging out when called from menu
     */
    private void LogOutUser() {
        Intent startPageIntent =
                new Intent(MapsActivity.this,LoginActivity.class);
        //make sure people can not go back in again
        startPageIntent.addFlags
                (Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(startPageIntent);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.main_logout_button){
            userAuthenticate.signOut();
            LogOutUser();
        }

        return true;
    }
}