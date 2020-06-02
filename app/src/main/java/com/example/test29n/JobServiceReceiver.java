package com.example.test29n;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class JobServiceReceiver extends BroadcastReceiver {

    private static final String TAG = "Receiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) || Intent.ACTION_MAIN.equals(intent.getAction()))
        {
            Log.i(TAG, "Receiver called, calling UTIL.java");
            JobInfoBuilder.scheduleJob(context);
        }

    }
}
