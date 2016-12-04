/**
 * Created by Michael Li on 11/26/16.
 * with code adapted from http://gabesechansoftware.com/location-tracking/
 */
package lematthe.calpoly.edu.enough;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.RemoteViews;
import android.widget.Toast;
import android.telephony.SmsManager;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Implementation of App Widget functionality.
 */
public class EnoughHeartWidget extends AppWidgetProvider implements LocationListener {

    private static final int CLICK_DELAY = 500;

    public static String ACTION_CLICK = "ActionClick";
    public static String ACTION_SMS_SENT = "ActionSMSSent";
    public static String ACTION_SMS_DELIVERED = "ActionSMSDelivered";

    protected LocationManager locationManager;

    // Flag for GPS status
    boolean isGPSEnabled = false;

    // Flag for network status
    boolean isNetworkEnabled = false;

    // Flag for GPS status
    boolean canGetLocation = false;

    Location location; // Location
    double latitude; // Latitude
    double longitude; // Longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.enough_heart_widget);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int i = 0; i < appWidgetIds.length; i++) {
            int appWidgetId = appWidgetIds[i];

            // create new intent with action into pendingintent
            Intent intent = new Intent(context, getClass());
            intent.setAction(ACTION_CLICK);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.enough_heart_widget);
            views.setOnClickPendingIntent(R.id.button_heartwidget, pendingIntent);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
        context.getSharedPreferences("widget", 0).edit().putInt("clicks", 0).commit();

    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        super.onReceive(context, intent);
        // on click action
        if (intent.getAction().equals(ACTION_CLICK)) {
            int clickCount = context.getSharedPreferences("widget", Context.MODE_PRIVATE).getInt("clicks", 0);
            context.getSharedPreferences("widget", Context.MODE_PRIVATE).edit().putInt("clicks", ++clickCount).commit();

            final Handler handler = new Handler() {
                public void handleMessage(Message msg) {

                    int clickCount = context.getSharedPreferences("widget", Context.MODE_PRIVATE).getInt("clicks", 0);

                    if (clickCount > 2) {
                        emergencyAlert(context);
                    }

                    context.getSharedPreferences("widget", Context.MODE_PRIVATE).edit().putInt("clicks", 0).commit();
                }
            };

            if (clickCount == 1) new Thread() {
                @Override
                public void run(){
                    try {
                        synchronized(this) { wait(CLICK_DELAY); }
                        handler.sendEmptyMessage(0);
                    } catch(InterruptedException ex) {}
                }
            }.start();
        }
        if (intent.getAction().equals(ACTION_SMS_SENT)) {
            Log.i(ACTION_SMS_SENT, Integer.toString(getResultCode()));
            switch (getResultCode())
            {
                case Activity.RESULT_OK:
                    Toast.makeText(context, "EMERGENCY ALERT SENT",
                            Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    Toast.makeText(context, "ERROR: SMS failure.",
                            Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    Toast.makeText(context, "ERROR: No service.",
                            Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    Toast.makeText(context, "ERROR: Null PDU.",
                            Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    Toast.makeText(context, "ERROR: Cell phone radio off.",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
        if (intent.getAction().equals(ACTION_SMS_DELIVERED)) {
            switch (getResultCode())
            {
                case Activity.RESULT_OK:
                    Toast.makeText(context, "EMERGENCY ALERT DELIVERED",
                            Toast.LENGTH_SHORT).show();
                    break;
                case Activity.RESULT_CANCELED:
                    Toast.makeText(context, "ERROR: Unable to deliver SMS.",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    protected void emergencyAlert(Context context) {
        try {
            Location location = getLocation(context);
            DatabaseHelper dbHelper = new DatabaseHelper(context);
            ArrayList<String> numbers = dbHelper.getNumbers();
            String message = dbHelper.getMessage();
            String location_message = "Location: http://maps.google.com/?q=" + location.getLatitude() + "," + location.getLongitude();
            Log.d("location: ", location_message);
            for (String number : numbers) {
                sendSMS(context, number, message);
                sendSMS(context, number, location_message);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "ERROR: Unable to deliver SMS.", Toast.LENGTH_SHORT).show();
        }
    }

    protected void sendSMS(Context context, String phoneNumber, String message) {
        Intent sentIntent = new Intent(context, getClass());
        sentIntent.setAction(ACTION_SMS_SENT);
        PendingIntent sentPI = PendingIntent.getBroadcast(context, 0,
                sentIntent, 0);
        Intent deliveredIntent = new Intent(context, getClass());
        deliveredIntent.setAction(ACTION_SMS_DELIVERED);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0,
                deliveredIntent, 0);

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
    }

    public Location getLocation(Context context) {
        try {
            locationManager = (LocationManager) context
                    .getSystemService(LOCATION_SERVICE);

            // Getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // Getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // No network provider is enabled
                return null;
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // If GPS enabled, get latitude/longitude using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }
        }
        catch (SecurityException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    @Override
    public void onLocationChanged(Location location) {

    }


    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }


}

