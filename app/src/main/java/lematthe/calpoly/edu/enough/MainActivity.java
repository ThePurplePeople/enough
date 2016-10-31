package lavidan.calpoly.edu.locationapp;

import android.annotation.SuppressLint;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.icu.text.DateFormat;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.Date;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private final int REQUEST_CHECK_SETTINGS = 0;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private ImageView imageView;
    private Button button;
    private TextView timeUpdated;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private String mLastUpdateTime;
    private boolean mRequestingLocationUpdates;
    private LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate():");
        button = (Button) findViewById(R.id.button);
        imageView = (ImageView) findViewById(R.id.imageView);
        timeUpdated = (TextView) findViewById(R.id.textView);
        mRequestingLocationUpdates = false;

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("RequestingLocationUpdate", mRequestingLocationUpdates);
        savedInstanceState.putParcelable("LastLocation", mLastLocation);
        savedInstanceState.putString("LastUpdateTime", mLastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and
            // make sure that the Start Updates and Stop Updates buttons are
            // correctly enabled or disabled.
            if (savedInstanceState.keySet().contains("RequestingLocationUpdate")) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        "RequestingLocationUpdate");
            }
            // Update the value of mCurrentLocation from the Bundle and update the
            // UI to show the correct latitude and longitude.
            if (savedInstanceState.keySet().contains("LastLocation")) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that
                // mCurrentLocationis not null.
                mLastLocation = savedInstanceState.getParcelable("LastLocation");
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains("LastUpdateTime")) {
                mLastUpdateTime = savedInstanceState.getString("LastUpdateTime");
            }
            updateUI();
        }
    }

    public void GetLocation(View view) {
        Log.d(TAG, "Pressed");
        if (mLastLocation != null) {
            updateUI();
        }
    }

    public void SendLocation(View view) {
        if (mLastLocation != null) {
            String uri = "http://maps.google.com?q=" + mLastLocation.getLatitude() + "," + mLastLocation.getLongitude();
            Log.d(TAG, "SendLocation: " + uri);
            SmsManager smsManager = SmsManager.getDefault();
            StringBuffer smsBody = new StringBuffer();
            smsBody.append(uri);
            smsManager.sendTextMessage("#", null, smsBody.toString(), null, null);
        } else {
            Log.d(TAG, "SendLocationNull");
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle("Location Not Found");
            adb.setMessage("Couldn't find last current location. \nMake sure you have a data connection");
            adb.setNegativeButton("Ok", null);
            adb.show();
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                Log.d(TAG, "CREATELOCATIONREQUEST");
                final Status status = result.getStatus();
                final LocationSettingsStates states = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location requests here.
                        Log.d(TAG, "Location Settings Satisfied");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        Log.d(TAG, "Location Settings Required");
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                            Log.d(TAG, "Intent Error: " + e.getStackTrace());
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        Log.d(TAG, "Settings Change Unavailable");
                        break;
                }
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //createLocationRequest();
            Log.d(TAG, "Location Permission Denied");
            return;
        }
        Location mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mCurrentLocation != null) {
            mLastLocation = mCurrentLocation;
            Log.d(TAG, "ConnectLocation");
            //mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
            //mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
        } else {
            Log.d(TAG, "ConnectLocationNull");
            // null case
        }
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @SuppressLint("NewApi")
    @Override
    public void onLocationChanged(Location location) {
        Location mCurrentLocation = location;
        if (mCurrentLocation != null) {
            mLastLocation = mCurrentLocation;
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            updateUI();

            Log.d(TAG, "ConnectLocation");
            //mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
            //mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
        } else {
            Log.d(TAG, "ConnectLocationNull");
            // null case
        }

    }

    protected void updateUI() {
        timeUpdated.setText("Last Updated: " + mLastUpdateTime);
        String uri = "http://maps.google.com?q=" + mLastLocation.getLatitude() + "," + mLastLocation.getLongitude();
        Log.d(TAG, "GetLocation: " + uri);
        imageView.setImageURI(Uri.parse(uri));
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }
}
