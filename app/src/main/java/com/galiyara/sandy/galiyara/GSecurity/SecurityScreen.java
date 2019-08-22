package com.galiyara.sandy.galiyara.GSecurity;

import android.content.Context;
import android.content.Intent;

import com.galiyara.sandy.galiyara.GActivity.LockActivity;
import com.galiyara.sandy.galiyara.GHelper.GSettings;

public class SecurityScreen {

    private static Thread thread;

    private static void showLockScreen(Context context) {
        Intent intent = new Intent(context, LockActivity.class);
        launchScreen(context,intent);
    }

    public static void checkAndLock(Context context) {
        if(thread == null)
            thread = new Thread(() -> {
                if(GSettings.getSecuritySetting() != null) {
                    if(GSettings.getSecuritySetting().isAppLockEnabled()) {
                        showLockScreen(context);
                    }
                }
                thread = null;
            });
        thread.start();
    }

    private static void launchScreen(Context context,Intent intent) {
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        GSettings.locked = true;
    }
}
