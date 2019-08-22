package com.galiyara.sandy.galiyara.GHelper;

import com.galiyara.sandy.galiyara.BuildConfig;
import com.galiyara.sandy.galiyara.GApp;
import com.galiyara.sandy.galiyara.R;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

public class GAdManager {

    private static InterstitialAd interstitialAd;
    private static AdListener adListener;
    private static AdRequest adRequest;
    private static boolean addInitialized = false;

    private static void setAddId() {
        if(interstitialAd == null) {
            interstitialAd = new InterstitialAd(GApp.getApplication());
            interstitialAd.setAdUnitId(BuildConfig.DEBUG ?
                    GApp.getApplication().getString(R.string.testId) :
                    GApp.getApplication().getString(R.string.interstitialAddUnitId));
            interstitialAd.setAdListener(adListener);
        }
    }
    private static void initAdListener() {
        if(adListener == null)
            adListener = new AdListener() {
                @Override
                public void onAdLoaded() {
                    if(!GSettings.locked)
                        interstitialAd.show();
                }
                @Override
                public void onAdClosed() {freeMem();}
                @Override
                public void onAdFailedToLoad(int var1) {freeMem();}
            };
    }
    private static void freeMem() {
        interstitialAd = null;
        adListener=null;
        adRequest = null;
    }
    private static void initMobileAdId() {
        if(!addInitialized) {
            MobileAds.initialize(GApp.getApplication(),
                    GApp.getApplication().getString(R.string.add_app_id));
            addInitialized = true;
        }
    }
    private static void initAdBuilder() {
        if(adRequest == null)
            adRequest = new AdRequest.Builder().build();
    }

    public static void DisplayAdds() {
        initMobileAdId();
        initAdListener();
        setAddId();
        initAdBuilder();
        if(interstitialAd != null && adRequest != null)
            interstitialAd.loadAd(adRequest);
    }
}
