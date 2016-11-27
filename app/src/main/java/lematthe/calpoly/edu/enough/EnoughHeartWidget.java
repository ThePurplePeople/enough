package lematthe.calpoly.edu.enough;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.app.PendingIntent;
import android.widget.ImageButton;
import android.widget.RemoteViews;
import android.widget.Toast;
import android.os.Message;
import android.os.Handler;

import android.util.Log;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link EnoughHeartWidgetConfigureActivity EnoughHeartWidgetConfigureActivity}
 */
public class EnoughHeartWidget extends AppWidgetProvider {

    private static final int DOUBLE_CLICK_DELAY = 500;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = EnoughHeartWidgetConfigureActivity.loadTitlePref(context, appWidgetId);
        // Construct the RemoteViews object
        //views.setTextViewText(R.id.appwidget_text, widgetText);
        RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.enough_heart_widget);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Intent intent = new Intent(context, getClass());
        intent.setAction("Click");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.enough_heart_widget);
        views.setOnClickPendingIntent(R.id.heartwidget_button, pendingIntent);
        appWidgetManager.updateAppWidget(appWidgetIds, views);
        // There may be multiple widgets active, so update all of them
        context.getSharedPreferences("widget", 0).edit().putInt("clicks", 0).commit();
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            EnoughHeartWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
        }
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
    public void onReceive (final Context context, Intent intent) {
        Log.e("E", "receive");
        if (intent.getAction().equals("Click")) {
            Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show();
            int clickCount = context.getSharedPreferences("widget", Context.MODE_PRIVATE).getInt("clicks", 0);
            context.getSharedPreferences("widget", Context.MODE_PRIVATE).edit().putInt("clicks", ++clickCount).commit();

            final Handler handler = new Handler() {
                public void handleMessage(Message msg) {

                    int clickCount = context.getSharedPreferences("widget", Context.MODE_PRIVATE).getInt("clicks", 0);

                    if (clickCount > 1) Toast.makeText(context, "doubleClick", Toast.LENGTH_SHORT).show();
                    else Toast.makeText(context, "singleClick", Toast.LENGTH_SHORT).show();

                    context.getSharedPreferences("widget", Context.MODE_PRIVATE).edit().putInt("clicks", 0).commit();
                }
            };

            if (clickCount == 1) new Thread() {
                @Override
                public void run(){
                    try {
                        synchronized(this) { wait(DOUBLE_CLICK_DELAY); }
                        handler.sendEmptyMessage(0);
                    } catch(InterruptedException ex) {}
                }
            }.start();
        }

        super.onReceive(context, intent);
    }
}

