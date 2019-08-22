package com.galiyara.sandy.galiyara.GHelper;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;

import com.galiyara.sandy.galiyara.GConstants.GaliyaraConst;
import com.galiyara.sandy.galiyara.R;

import io.multimoon.colorful.ColorfulKt;
import io.multimoon.colorful.Defaults;
import io.multimoon.colorful.ThemeColor;
import kotlin.Unit;

public class ThemeManager {

    private static int primaryColor = GaliyaraConst.APP_THEME_NOT_LOADED;
    private static int accentColor = GaliyaraConst.APP_THEME_NOT_LOADED;
    private static boolean useDarkTheme = false;
    private static final int PRIMARY = 0,ACCENT = 1;
    private static ThemeColor themeColorPrimary = null;
    private static ThemeColor themeColorAccent = null;

    public static Defaults getDefaultValue(Context context) {
        if(primaryColor == GaliyaraConst.APP_THEME_NOT_LOADED && accentColor == GaliyaraConst.APP_THEME_NOT_LOADED) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(GaliyaraConst.MY_PREFS,
                    Context.MODE_PRIVATE);
            primaryColor = sharedPreferences.getInt(GaliyaraConst.PRIMARY_COLOR_KEY,
                    GaliyaraConst.COLOR_BLUE);
            accentColor = sharedPreferences.getInt(GaliyaraConst.ACCENT_COLOR_KEY,
                    GaliyaraConst.COLOR_BLUE);
            useDarkTheme = sharedPreferences.getBoolean(GaliyaraConst.DARK_THEME_KEY,
                    false);
            loadThemeColor(PRIMARY);
            loadThemeColor(ACCENT);
        }
        return new Defaults(themeColorPrimary,themeColorAccent,useDarkTheme,false,0);
    }

    private static void loadThemeColor(int type) {
        int st = 0;
        if(type == PRIMARY)
            st = primaryColor;
        if(type == ACCENT)
            st = accentColor;
        switch (st) {
            case GaliyaraConst.COLOR_BLUE:
                if(type == PRIMARY)
                    themeColorPrimary = ThemeColor.BLUE;
                else
                    themeColorAccent = ThemeColor.BLUE;
                break;
            case GaliyaraConst.COLOR_AMBER:
                if(type == PRIMARY)
                    themeColorPrimary = ThemeColor.AMBER;
                else
                    themeColorAccent = ThemeColor.AMBER;
                break;
            case GaliyaraConst.COLOR_BLACK:
                if(type == PRIMARY)
                    themeColorPrimary = ThemeColor.AMBER;
                else
                    themeColorAccent = ThemeColor.AMBER;
                break;
            case GaliyaraConst.COLOR_BLUE_GREY:
                if(type == PRIMARY)
                    themeColorPrimary = ThemeColor.BLUE_GREY;
                else
                    themeColorAccent = ThemeColor.BLUE_GREY;
                break;
            case GaliyaraConst.COLOR_BROWN:
                if(type == PRIMARY)
                    themeColorPrimary = ThemeColor.BROWN;
                else
                    themeColorAccent = ThemeColor.BROWN;
                break;
            case GaliyaraConst.COLOR_CYAN:
                if(type == PRIMARY)
                    themeColorPrimary = ThemeColor.CYAN;
                else
                    themeColorAccent = ThemeColor.CYAN;
                break;
            case GaliyaraConst.COLOR_DEEP_ORANGE:
                if(type == PRIMARY)
                    themeColorPrimary = ThemeColor.DEEP_ORANGE;
                else
                    themeColorAccent = ThemeColor.DEEP_ORANGE;
                break;
            case GaliyaraConst.COLOR_DEEP_PURPLE:
                if(type == PRIMARY)
                    themeColorPrimary = ThemeColor.DEEP_PURPLE;
                else
                    themeColorAccent = ThemeColor.DEEP_PURPLE;
                break;
            case GaliyaraConst.COLOR_GREEN:
                if(type == PRIMARY)
                    themeColorPrimary = ThemeColor.GREEN;
                else
                    themeColorAccent = ThemeColor.GREEN;
                break;
            case GaliyaraConst.COLOR_GREY:
                if(type == PRIMARY)
                    themeColorPrimary = ThemeColor.GREY;
                else
                    themeColorAccent = ThemeColor.GREY;
                break;
            case GaliyaraConst.COLOR_INDIGO:
                if(type == PRIMARY)
                    themeColorPrimary = ThemeColor.INDIGO;
                else
                    themeColorAccent = ThemeColor.INDIGO;
                break;
            case GaliyaraConst.COLOR_LIGHT_BLUE:
                if(type == PRIMARY)
                    themeColorPrimary = ThemeColor.LIGHT_BLUE;
                else
                    themeColorAccent = ThemeColor.LIGHT_BLUE;
                break;
            case GaliyaraConst.COLOR_LIGHT_GREEN:
                if(type == PRIMARY)
                    themeColorPrimary = ThemeColor.LIGHT_GREEN;
                else
                    themeColorAccent = ThemeColor.LIGHT_GREEN;
                break;
            case GaliyaraConst.COLOR_LIME:
                if(type == PRIMARY)
                    themeColorPrimary = ThemeColor.LIME;
                else
                    themeColorAccent = ThemeColor.LIME;
                break;
            case GaliyaraConst.COLOR_ORANGE:
                if(type == PRIMARY)
                    themeColorPrimary = ThemeColor.ORANGE;
                else
                    themeColorAccent = ThemeColor.ORANGE;
                break;
            case GaliyaraConst.COLOR_PINK:
                if(type == PRIMARY)
                    themeColorPrimary = ThemeColor.PINK;
                else
                    themeColorAccent = ThemeColor.PINK;
                break;
            case GaliyaraConst.COLOR_PURPLE:
                if(type == PRIMARY)
                    themeColorPrimary = ThemeColor.PURPLE;
                else
                    themeColorAccent = ThemeColor.PURPLE;
                break;
            case GaliyaraConst.COLOR_RED:
                if(type == PRIMARY)
                    themeColorPrimary = ThemeColor.RED;
                else
                    themeColorAccent = ThemeColor.RED;
                break;
            case GaliyaraConst.COLOR_TEAL:
                if(type == PRIMARY)
                    themeColorPrimary = ThemeColor.TEAL;
                else
                    themeColorAccent = ThemeColor.TEAL;
                break;
            case GaliyaraConst.COLOR_YELLOW:
                if(type == PRIMARY)
                    themeColorPrimary = ThemeColor.YELLOW;
                else
                    themeColorAccent = ThemeColor.YELLOW;
                break;
        }
    }

    public static void setPopupTheme(@NonNull Toolbar toolbar) {
        if(useDarkTheme)
            toolbar.setPopupTheme(R.style.ThemeOverlay_AppCompat_Dark);
        else
            toolbar.setPopupTheme(R.style.ThemeOverlay_AppCompat_Light);
    }
    public static void initSortOptions(@NonNull Toolbar toolbar) {
        MenuItem item;
        switch (GSorter.getSortType()) {
            case GaliyaraConst.SORT_BY_DATE:
                item = toolbar.getMenu().findItem(R.id.sortDate);
                item.setChecked(true);
                break;
            case GaliyaraConst.SORT_BY_NAME:
                item = toolbar.getMenu().findItem(R.id.sortName);
                item.setChecked(true);
                break;
            case GaliyaraConst.SORT_BY_SIZE:
                item = toolbar.getMenu().findItem(R.id.sortSize);
                item.setChecked(true);
                break;
        }
        item = toolbar.getMenu().findItem(R.id.sortAsc);
        item.setChecked(GSorter.isSortAscending());
    }

    public static boolean isUseDarkTheme() { return useDarkTheme; }
    public static ThemeColor getThemeColorPrimary() { return themeColorPrimary; }
    public static ThemeColor getThemeColorAccent() { return themeColorAccent; }
    public static int getPrimaryColor() { return primaryColor; }
    public static int getAccentColor() { return accentColor; }

    public static void setPrimaryColor(int p) {
        primaryColor = p;
        loadThemeColor(PRIMARY);
    }
    public static void setAccentColor(int a) {
        accentColor = a;
        loadThemeColor(ACCENT);
    }
    public static void setUseDarkTheme(boolean d) {
        useDarkTheme = d;
    }

    public static void saveSettings(Context context,int type) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(GaliyaraConst.MY_PREFS,
                Context.MODE_PRIVATE);
        switch (type) {
            case GaliyaraConst.ENABLE_DARK_THEME:
                sharedPreferences.edit().putBoolean(GaliyaraConst.DARK_THEME_KEY,useDarkTheme).apply();
                ColorfulKt.Colorful().edit()
                        .setDarkTheme(useDarkTheme).apply(context,()-> Unit.INSTANCE);
                break;
            case GaliyaraConst.SET_ACCENT_COLOR:
                sharedPreferences.edit().putInt(GaliyaraConst.ACCENT_COLOR_KEY,accentColor).apply();
                ColorfulKt.Colorful().edit()
                        .setAccentColor(themeColorAccent).apply(context,()-> Unit.INSTANCE);
                break;
            case GaliyaraConst.SET_PRIMARY_COLOR:
                sharedPreferences.edit().putInt(GaliyaraConst.PRIMARY_COLOR_KEY,primaryColor).apply();
                ColorfulKt.Colorful().edit()
                        .setPrimaryColor(themeColorPrimary).apply(context,()-> Unit.INSTANCE);
                break;
        }
    }
}
