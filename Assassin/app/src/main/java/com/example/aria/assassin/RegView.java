package com.example.aria.assassin;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.aria.assassin.RestClient.AsyncRestClient;
import com.example.aria.assassin.RestClient.SyncRestClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.loopj.android.http.BlackholeHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class RegView extends AppCompatActivity implements
        ConnectionCallbacks,
        OnConnectionFailedListener,
        LocationListener,
        ResultCallback<LocationSettingsResult> {

    protected static final String LOCATION_TAG = "RegView.LOCATION";

    /**
     * Constant used in the location settings dialog.
     */
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    /**
     * Interval for location updates in milliseconds
     */
    public final int LOCATION_REQUEST_INTERVAL = 5000; // Query every 5 seconds

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public final long LOCATION_REQUEST_INTERVAL_MAX = LOCATION_REQUEST_INTERVAL/2;

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;

    /**
     * Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */
    protected LocationSettingsRequest mLocationSettingsRequest;

    /**
     * Represents a geographical location.
     */
    protected Location mCurrentLocation;

    protected String username;

    protected final Launch.ValContainer<String> targetName = new Launch.ValContainer<>("");

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regview);

        username = getIntent().getStringExtra(Launch.EXTRA_USERNAME);

        getTargetInfo();
        buildGoogleApiClient();
        createLocationRequest();
        buildLocationSettingsRequest();
        checkLocationSettings();
    }

    private void getTargetInfo() {
        // Get target
        Thread targetNameThread = new Thread(new Runnable() {
            @Override
            public void run() {
                RequestParams params = new RequestParams();
                params.put("username", username);
                SyncRestClient.get("game/target", params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            targetName.setVal(response.getString("target"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                        Log.d("FAIL", responseString);
                    }
                });
            }
        });
        targetNameThread.start();
        try { targetNameThread.join(); } catch (InterruptedException e) { e.printStackTrace(); }

        // Set display of target name
        TextView textView = (TextView) findViewById(R.id.targetName);
        textView.setText(targetName.getVal());
    }

    protected synchronized void buildGoogleApiClient() {
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    protected void createLocationRequest() {
        // Set location request parameters
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(LOCATION_REQUEST_INTERVAL);
        mLocationRequest.setFastestInterval(LOCATION_REQUEST_INTERVAL_MAX);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder locationBuilder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        mLocationSettingsRequest = locationBuilder.build();
    }

    protected void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, mLocationSettingsRequest);

        result.setResultCallback(this);
    }

    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.i(LOCATION_TAG, "All location settings are satisfied.");
                startLocationUpdates();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.i(LOCATION_TAG, "Location settings are not satisfied. Show the user a dialog to" +
                        "upgrade location settings ");

                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(RegView.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    Log.i(LOCATION_TAG, "PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.i(LOCATION_TAG, "Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(LOCATION_TAG, "User agreed to make required location settings changes.");
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(LOCATION_TAG, "User chose not to make required location settings changes.");
                        break;
                }
                break;
        }
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                mLocationRequest,
                this
        );
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Connect to Google play services
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        // Disconnect from Google play services
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(LOCATION_TAG, "Connected to Google Services");
    }

    @Override
    public void onConnectionFailed (ConnectionResult result) {
        Log.i(LOCATION_TAG, "Location connection failed.");
    }

    @Override
    public void onConnectionSuspended (int i) {
        Log.i(LOCATION_TAG, "Location services suspended. Please reconnect.");
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            // Get last known location
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                Log.d(LOCATION_TAG, "Sending location: " +
                                    String.valueOf(mLastLocation.getLatitude()) + ", " +
                                    String.valueOf(mLastLocation.getLongitude()));
                try {
                    JSONObject jsonParams = new JSONObject();
                    jsonParams.put("username", username);
                    jsonParams.put("latitude", mLastLocation.getLatitude());
                    jsonParams.put("longitude", mLastLocation.getLongitude());
                    StringEntity data = new StringEntity(jsonParams.toString());
                    AsyncRestClient.postJSON(this.getApplicationContext(), "game/location", data, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Log.d(LOCATION_TAG, "Successfully sent location");
                            try {
                                boolean canKill = response.getBoolean("ready_for_kill");
                                boolean inDanger = response.getBoolean("in_danger");
                                boolean alive = response.getBoolean("alive");

                                if (!alive) {
                                    // Go to game over page
                                    Intent nextIntent = new Intent(RegView.this, Dead.class);
                                    startActivity(nextIntent);
                                } else {
                                    Button killButton = (Button) findViewById(R.id.killButton);
                                    TextView dangerText = (TextView)findViewById(R.id.dangerText);

                                    // Enable kill button if within kill radius. Else disable
                                    if (canKill){
                                        killButton.setEnabled(true);
                                    } else {
                                        killButton.setEnabled(false);
                                    }

                                    if (inDanger) {
                                        // Display warning
                                        dangerText.setVisibility(TextView.VISIBLE);
                                    } else {
                                        // Hide warning
                                        dangerText.setVisibility(TextView.INVISIBLE);
                                    }

                                    // Display warning if in danger
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                            Log.d(LOCATION_TAG, "Failed to send location");
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public void pressDash(View view) {
        Intent intent = new Intent(this, Dashboard.class);
        intent.putExtra(Launch.EXTRA_USERNAME, username);
        startActivity(intent);
    }

    public void showHint(View view) {
        //new view to display number?
    }

    @Override
    public void onBackPressed() {
        // Don't let players press back button
    }
}
