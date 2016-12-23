package info.nightscout.android.medtronic.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.WakefulBroadcastReceiver;

import java.util.Date;

import info.nightscout.android.utils.Logger;

/**
 * Created by lgoedhart on 14/07/2016.
 */
public class MedtronicCnlAlarmReceiver extends WakefulBroadcastReceiver {
    private static final String TAG = MedtronicCnlAlarmReceiver.class.getSimpleName();
    private static final int ALARM_ID = 102; // Alarm id

    private static PendingIntent pendingIntent = null;
    private static AlarmManager alarmManager = null;
    private Logger logger;
    private Context context;

    @Override
    public void onReceive(final Context context, Intent intent) {
        // Start the IntentService

        this.context = context;
        logger = new Logger(TAG, context.getApplicationContext());
        logger.d("Received broadcast message at " + new Date(System.currentTimeMillis()));
        Intent service = new Intent(context, MedtronicCnlIntentService.class);
        startWakefulService(context, service);
        restartAlarm();
    }

    public void setContext(Context context) {
        this.context = context;

        logger = new Logger(TAG, context.getApplicationContext());

        cancelAlarm();

        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, MedtronicCnlAlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, ALARM_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    // Setting the alarm in 15 seconds from now
    public void setAlarm() {
        setAlarm(System.currentTimeMillis());
    }

    // Setting the alarm to call onRecieve
    public void setAlarm(long millis) {
        if (alarmManager == null || pendingIntent == null)
            return;
        logger = new Logger(TAG, this.context.getApplicationContext());

        cancelAlarm();

        // don't trigger the past and at least 30 sec away
        if (millis < System.currentTimeMillis())
            millis = System.currentTimeMillis();

        logger.d("AlarmManager set to fire at " + new Date(millis));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(millis, null), pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, millis, pendingIntent);
        } else
            alarmManager.set(AlarmManager.RTC_WAKEUP, millis, pendingIntent);
    }

    // restarting the alarm after MedtronicCnlIntentService.POLL_PERIOD_MS from now
    public void restartAlarm() {
        setAlarm(System.currentTimeMillis() + MedtronicCnlIntentService.POLL_PERIOD_MS + MedtronicCnlIntentService.POLL_GRACE_PERIOD_MS);
    }

    // Cancel the alarm.
    public void cancelAlarm() {
        if (alarmManager == null || pendingIntent == null)
            return;

        alarmManager.cancel(pendingIntent);
    }

}
