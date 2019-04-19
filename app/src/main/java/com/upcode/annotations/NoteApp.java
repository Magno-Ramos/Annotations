package com.upcode.annotations;

import android.app.Application;
import android.content.ComponentName;
import android.content.pm.PackageManager;

import com.upcode.annotations.notifications.AppNotificationsChannel;
import com.upcode.annotations.receiver.BootDeviceReceiver;

public class NoteApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        AppNotificationsChannel.from(this).createChannels();
        enableAlarmWhenDeviceRestart();
    }

    private void enableAlarmWhenDeviceRestart() {
        ComponentName receiver = new ComponentName(this, BootDeviceReceiver.class);
        PackageManager pm = getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }
}
