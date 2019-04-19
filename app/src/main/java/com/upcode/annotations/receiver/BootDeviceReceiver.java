package com.upcode.annotations.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.upcode.annotations.service.CheckAlarmService;

import java.text.DateFormat;
import java.util.Date;

public class BootDeviceReceiver extends BroadcastReceiver {

    private static final String BootComplete = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AppMain", "onReceive: call boot completed");
        if (BootComplete.equals(intent.getAction())) {
            Log.d("AppMain", "BootDeviceReceiver onReceive: " + DateFormat.getDateTimeInstance().format(new Date(System.currentTimeMillis())));
            Intent intentService = new Intent(context, CheckAlarmService.class);
            context.startService(intentService);
        }
    }
}
