package info.nightscout.android.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import info.nightscout.android.R;
import info.nightscout.android.medtronic.service.MedtronicCnlIntentService;

/**
 * Wraps android log to send messages to UI
 */

public class Logger {
    private final Context context;
    private final String tag;
    private int logLevel = Log.ASSERT;

    public Logger(String tag, Context context) {
        this.tag = tag;
        this.context = context;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        String currentLogLevel = prefs.getString("logLevel", "0");
        int pos = Integer.parseInt(currentLogLevel) - 1;
        String[] logLevelNames = context.getResources().getStringArray(R.array.levelList);
        if (pos >= 0 && pos < logLevelNames.length) {
            currentLogLevel = logLevelNames[pos].toLowerCase();
            if ("error".equals(currentLogLevel)) {
                logLevel = Log.ERROR;
            } else if ("warning".equals(currentLogLevel)) {
                logLevel = Log.WARN;
            } else if ("info".equals(currentLogLevel)) {
                logLevel = Log.INFO;
            } else if ("debug".equals(currentLogLevel)) {
                logLevel = Log.DEBUG;
            } else if ("verbose".equals(currentLogLevel)) {
                logLevel = Log.VERBOSE;
            }
        }
    }

    public int v(String msg) {
        if (logLevel <= Log.VERBOSE) {
            sendStatus(msg);
        }
        return Log.v(tag, msg);
    }

    public int v(String msg, Throwable tr) {
        if (logLevel <= Log.VERBOSE) {
            sendStatus(msg);
        }return Log.v(tag, msg, tr);
    }

    public int d(String msg) {
        if (logLevel <= Log.DEBUG) {
            sendStatus("(D) " + tag + ": " + msg);
        }
        return Log.d(tag, msg);
    }

    public int d(String msg, Throwable tr) {
        if (logLevel <= Log.DEBUG) {
            sendStatus("(D) " + tag + ": " + msg);
        }
        return Log.d(tag, msg, tr);
    }

    public int i(String msg) {
        if (logLevel <= Log.INFO) {
            sendStatus(msg);
        }
        return Log.i(tag, msg);
    }

    public int i(String msg, Throwable tr) {
        if (logLevel <= Log.INFO) {
            sendStatus(msg);
        }
        return Log.i(tag, msg, tr);
    }

    public int w(String msg) {
        if (logLevel <= Log.WARN) {
            sendStatus(msg);
        }
        return Log.w(tag, msg);
    }

    public int w(String msg, Throwable tr) {
        if (logLevel <= Log.WARN) {
            sendStatus(msg);
        }
        return Log.w(tag, msg, tr);
    }

    public int w(Throwable tr) {
        if (logLevel <= Log.WARN) {
            sendStatus(tr.getMessage());
        }
        return Log.w(tag, tr);
    }

    public int e(String msg) {
        if (logLevel <= Log.ERROR) {
            sendStatus("(E): " + msg);
        }
        return Log.e(tag, msg);
    }

    public int e(String msg, Throwable tr) {
        if (logLevel <= Log.ERROR) {
            sendStatus("(E): " + msg + "(" + tr.getMessage() + ")");
        }
        return Log.e(tag, msg, tr);
    }

    protected void sendStatus(String message) {
        Intent localIntent =
                new Intent(MedtronicCnlIntentService.Constants.ACTION_STATUS_MESSAGE)
                        .putExtra(MedtronicCnlIntentService.Constants.EXTENDED_DATA, message);
        LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);
    }
}
