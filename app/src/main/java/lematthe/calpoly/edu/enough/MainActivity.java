package lavidan.calpoly.edu.locationapp;

<<<<<<< HEAD
import android.annotation.SuppressLint;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.icu.text.DateFormat;
import android.location.Location;
import android.net.Uri;
=======
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
>>>>>>> 0e6a36b935404c2b5591816761829350daa3b7de
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
<<<<<<< HEAD
import android.widget.ImageView;
import android.widget.TextView;
=======
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
>>>>>>> 0e6a36b935404c2b5591816761829350daa3b7de

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

<<<<<<< HEAD
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
=======
    //declare globally, this can be any int
    public final int PICK_CONTACT = 2015;
    public DatabaseHelper dbHelper; // The database helper(manager)
    public final int PERMISSION_ALL = 1;
    public final int LOCATION_PERM = 2;

    String TAG = "here";
    Button sendButton;
    Button locationButton;
>>>>>>> 0e6a36b935404c2b5591816761829350daa3b7de

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //implicit layout inflator
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

<<<<<<< HEAD
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("RequestingLocationUpdate", mRequestingLocationUpdates);
        savedInstanceState.putParcelable("LastLocation", mLastLocation);
        savedInstanceState.putString("LastUpdateTime", mLastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
    }
=======
        // carry on the normal flow, as the case of  permissions  granted.
        Button myContact1 = (Button) findViewById(R.id.contact1);
        sendButton = (Button) findViewById(R.id.send);

        dbHelper = new DatabaseHelper(getApplicationContext());

        locationButton = (Button) findViewById(R.id.location);
>>>>>>> 0e6a36b935404c2b5591816761829350daa3b7de

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

<<<<<<< HEAD
            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains("LastUpdateTime")) {
                mLastUpdateTime = savedInstanceState.getString("LastUpdateTime");
=======
        if(checkAndRequestPermissions()) {
            sendButton.setEnabled(true);
            locationButton.setEnabled(false);
        }

        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                System.out.println("Send clicked");
                ArrayList<String> numbers = dbHelper.sendEmergency();
                for(String n : numbers) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(
                            "+1" + n,
                            null,
                            "This is a test message!",
                            null,
                            null);
                    }
>>>>>>> 0e6a36b935404c2b5591816761829350daa3b7de
            }
            updateUI();
        }
    }

<<<<<<< HEAD
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
=======
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("clicked", "get location");
            }
        });
    }

    private  boolean checkAndRequestPermissions() {
        int permissionSendMessage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS);

        int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.SEND_SMS);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), PERMISSION_ALL);
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_CONTACT && resultCode == RESULT_OK) {
            Uri contactUri = data.getData();
            Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
            cursor.moveToFirst();
            int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            String contact1 = cursor.getString(column);
            Log.d("phone number", cursor.getString(column));
            dbHelper.addNewContact(contact1);
>>>>>>> 0e6a36b935404c2b5591816761829350daa3b7de
        }
    }

    @Override
<<<<<<< HEAD
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
=======
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d(TAG, "Permission callback called-------");
        switch (requestCode) {
            case LOCATION_PERM: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("perm granted", "yaya");
                }
                else {
                    Log.d("boo", "poop");
                }
            }
            case PERMISSION_ALL: {
                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(Manifest.permission.SEND_SMS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "sms & location services permission granted");
                        // process the normal flow
                        //else any one or both the permissions are not granted
                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                            showDialogOK("SMS and Location Services Permission required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    locationButton.setEnabled(false);
                                                    sendButton.setEnabled(false);
                                                    break;
                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                    .show();
                            locationButton.setEnabled(false);
                            sendButton.setEnabled(false);
                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
>>>>>>> 0e6a36b935404c2b5591816761829350daa3b7de
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

<<<<<<< HEAD
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
=======
    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(checkAndRequestPermissions()) {

            sendButton.setEnabled(true);
            locationButton.setEnabled(true);

>>>>>>> 0e6a36b935404c2b5591816761829350daa3b7de
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

