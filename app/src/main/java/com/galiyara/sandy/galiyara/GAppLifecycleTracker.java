package com.galiyara.sandy.galiyara;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import android.content.Context;
import com.galiyara.sandy.galiyara.GConstants.GaliyaraConst;
import com.galiyara.sandy.galiyara.GDatabase.GDBCrud;
import com.galiyara.sandy.galiyara.GDatabase.GeneralSettingEntity;
import com.galiyara.sandy.galiyara.GDatabase.SecuritySettingEntity;
import com.galiyara.sandy.galiyara.GHelper.GSettings;
import com.galiyara.sandy.galiyara.GInterface.DBOperationListener;
import com.galiyara.sandy.galiyara.GSecurity.SecurityScreen;

class GAppLifecycleTracker implements LifecycleObserver {

    private Context context;
    private GDBCrud gdbCrud1,gdbCrud2;
    private boolean firstTime = true;

    GAppLifecycleTracker(Context context) {
        this.context = context;
        initAppSettings();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void onMoveToForeground() {
        if(!GSettings.locked && !firstTime) {
            SecurityScreen.checkAndLock(context);
        }
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void onMoveToBackground() {
        GaliyaraConst.logData("App in background");
    }

    private void initAppSettings() {
        if(gdbCrud1 == null) {
            gdbCrud1 = new GDBCrud(context);
        }
        if(gdbCrud2 == null) {
            gdbCrud2 = new GDBCrud(context);
        }
        gdbCrud1.addListener(new DBOperationListener() {
            @Override
            public void dataReady(SecuritySettingEntity entity) {
                if(entity == null)
                    entity = new SecuritySettingEntity();
                GSettings.updateSecuritySetting(entity);
                SecurityScreen.checkAndLock(context);
                firstTime = false;
                gdbCrud1 = null;
            }
        });
        gdbCrud2.addListener(new DBOperationListener() {
            @Override
            public void dataReady(GeneralSettingEntity entity) {
                if(entity == null)
                    entity = new GeneralSettingEntity();
                GSettings.updateGeneralSetting(entity);
                gdbCrud2 = null;
            }
        });
        try {
            gdbCrud1.getSecuritySetting();
            gdbCrud2.getGeneralSetting();
        } catch (Exception e) {
            e.printStackTrace();
            GaliyaraConst.showToast("setting not initialised");
        }
    }
}
