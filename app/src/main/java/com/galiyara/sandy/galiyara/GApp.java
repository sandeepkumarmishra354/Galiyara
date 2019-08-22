package com.galiyara.sandy.galiyara;

import android.app.Application;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.galiyara.sandy.galiyara.GHelper.GAdManager;
import com.galiyara.sandy.galiyara.GHelper.GSorter;
import com.galiyara.sandy.galiyara.GHelper.GaliyaraHelper;
import com.galiyara.sandy.galiyara.GHelper.ThemeManager;

import io.multimoon.colorful.ColorfulKt;

public class GApp extends Application {

    private static Application mInstance;

    @Override
    public void onCreate() {
        GaliyaraHelper.initLang(this);
        super.onCreate();
        mInstance = this;
        ColorfulKt.initColorful(this,ThemeManager.getDefaultValue(this));
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new GAppLifecycleTracker(this));
        GSorter.initSortOptions(this);
        GAdManager.DisplayAdds();
    }

    public static Application getApplication() {return mInstance;}
}
