/**
 * Created by Michael Li on 11/26/16.
 */
package lematthe.calpoly.edu.enough;

import android.Manifest;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.PendingIntent;
import android.provider.Telephony;
import android.support.v4.content.ContextCompat;
import android.widget.ImageButton;
import android.widget.RemoteViews;
import android.widget.Toast;
import android.os.Message;
import android.os.Handler;
import android.telephony.SmsManager;
import android.content.pm.PackageManager;


import android.util.Log;

import java.util.ArrayList;

/**
 * Implementation of App Widget functionality.
 */
public class EnoughHeartWidget extends AppWidgetProvider {

    public static String ACTION_CLICK = "ActionClick";
    public static String ACTION_SMS_SENT = "ActionSMSSent";
    public static String ACTION_SMS_DELIVERED = "ActionSMSDelivered";

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
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        // on click action
        if (intent.getAction().equals(ACTION_CLICK)) {
            Log.i("M", "click");
            emergencyAlert(context);
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
            DatabaseHelper dbHelper = new DatabaseHelper(context);
            ArrayList<String> numbers = dbHelper.getNumbers();
            String message = dbHelper.getMessage();

            for (String number : numbers) {
                sendSMS(context, number, message);
            }
        } catch (Exception e) {
            Toast.makeText(context, R.string.alert_error, Toast.LENGTH_SHORT).show();
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
}

