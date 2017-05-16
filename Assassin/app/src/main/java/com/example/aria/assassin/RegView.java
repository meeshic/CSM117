package com.example.aria.assassin;
import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;

/**
 * Created by Lauren on 4/25/2017.
 */

public class RegView extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {
    private final int LOCATION_REQUEST_INTERVAL = 5000; // Query every 5 seconds
    private final int LOCATION_REQUEST_INTERVAL_MAX = 3000; // Maximum rate of every 3 seconds

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regview);

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

            // Set location request parameters
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(LOCATION_REQUEST_INTERVAL);
            mLocationRequest.setFastestInterval(LOCATION_REQUEST_INTERVAL_MAX);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationSettingsRequest.Builder locationBuilder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(mLocationRequest);

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, locationBuilder.build());

            // TODO: Check if location settings are on. Prompt if not.
        }
    }

    @Override
    protected void onStart() {
        // Connect to Google play services
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        // Disconnect from Google play services
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // Start location updates
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionFailed (ConnectionResult result) {
        Log.i("LOCATION", "Location connection failed.");
    }

    @Override
    public void onConnectionSuspended (int i) {
        Log.i("LOCATION", "Location services suspended. Please reconnect.");
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            // Get last known location
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                Log.d("LOCATION", String.valueOf(mLastLocation.getLatitude()));
                Log.d("LOCATION", String.valueOf(mLastLocation.getLongitude()));
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public void pressDash(View view)
    {
        Intent intent = new Intent(this, Dashboard.class);
        startActivity(intent);
    }

    public void showHint(View view)
    {
        //new view to display number?
    }
}
