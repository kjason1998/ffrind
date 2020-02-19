package com.kevex.ffriend;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
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
import java.util.Locale;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {
    private final String TAG = "MapsActivity";

    private final int locationRequestCode = 1000;


    private FusedLocationProviderClient mFusedLocationClient;
    private GoogleMap mMap;

    private boolean mLocationPermissionGranted;

    private double wayLatitude = 0.0, wayLongitude = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);// Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLocationPermission();
        mapFragment.getMapAsync(this);
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
                Toast.makeText(getApplicationContext(), "id is" + circle.getId(), Toast.LENGTH_SHORT).show();
                animateCircle(circle);
            }
        };
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
        Toast.makeText(getApplicationContext(), "Finish animation", Toast.LENGTH_LONG).show();
    }
}
