package com.galiyara.sandy.galiyara.GHelper;

import androidx.annotation.NonNull;

import com.galiyara.sandy.galiyara.GDatabase.GeneralSettingEntity;
import com.galiyara.sandy.galiyara.GDatabase.SecuritySettingEntity;
import com.galiyara.sandy.galiyara.GConstants.GaliyaraConst;

public class GSettings {
    private static SecuritySettingEntity securitySetting;
    private static GeneralSettingEntity generalSetting;
    private static String language = GaliyaraConst.LANG_DEFAULT;
    private static boolean firstTime = true;
    public static boolean locked = false;

    public static void updateSecuritySetting(SecuritySettingEntity s) {
        if(securitySetting != null)
            securitySetting = null;
        securitySetting = s;
    }
    public static void updateGeneralSetting(GeneralSettingEntity s) {
        if(generalSetting != null)
            generalSetting = null;
        generalSetting = s;
    }
    public static SecuritySettingEntity getSecuritySetting() {return securitySetting;}
    public static GeneralSettingEntity getGeneralSetting() {return generalSetting;}
    public static boolean isFirstTime() { return firstTime; }
    public static void setFirstTime(boolean status) { firstTime = status; }
    static void setLanguage(@NonNull String lang) {language = lang;}
    static String getLanguage() {return language;}
}
