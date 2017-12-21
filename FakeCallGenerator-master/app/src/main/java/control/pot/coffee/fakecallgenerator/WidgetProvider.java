package control.pot.coffee.fakecallgenerator;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Created by loker on 7/8/2017.
 */

public class WidgetProvider extends AppWidgetProvider {

    public static final String WIDGET_ACTION = "control.pot.coffee.fakecallgenerator.widgetaction";
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int count = appWidgetIds.length;
        for (int i=0; i<count; i++) {
            int appWidgetId = appWidgetIds[i];

            // Create an Intent to launch ExampleActivity
            Intent intent = new Intent(WIDGET_ACTION);
            intent.setAction(WIDGET_ACTION);
            intent.putExtra(Constants.PREFS_WIDGET_ID, Integer.toString(appWidgetId));
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            // Get the layout for the App Widget and attach an on-click listener to the button
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            views.setOnClickPendingIntent(R.id.button, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current App Widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }


    //Handle Widget Click
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.v("WidgetProvider", "received widget broadcast");
        if (intent.getAction().equals(WIDGET_ACTION)) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(Constants.PREFS_WIDGET_NAME, 0);
            String id = intent.getExtras().getString(Constants.PREFS_WIDGET_ID);

            String name     = sharedPrefs.getString(Constants.PREFS_WIDGET_NAME(id), null);
            String number   = sharedPrefs.getString(Constants.PREFS_WIDGET_NUMBER(id), null);
            String photoStr = sharedPrefs.getString(Constants.PREFS_WIDGET_PHOTO(id), null);
            int delay       = sharedPrefs.getInt(Constants.PREFS_WIDGET_DELAY(id), 0);
            int interval    = sharedPrefs.getInt(Constants.PREFS_WIDGET_INTERVAL(id), 0);
            int repeat      = sharedPrefs.getInt(Constants.PREFS_WIDGET_REPEATS(id), 0);

            String spc = " | ";
            Log.v("WidgetProvider", name + spc + number + spc + photoStr + spc + delay + spc + interval + spc + repeat);

            CallScheduler CS = new CallScheduler(context, delay, repeat, interval,
                        name, number, photoStr);
            CS.schedule();
        }
    }
}
