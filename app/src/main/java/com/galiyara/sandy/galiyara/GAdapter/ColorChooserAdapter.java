package com.galiyara.sandy.galiyara.GAdapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.galiyara.sandy.galiyara.GInterface.DialogActionListener;
import com.galiyara.sandy.galiyara.GHelper.ThemeManager;
import com.galiyara.sandy.galiyara.GConstants.GaliyaraConst;

import io.multimoon.colorful.ThemeColor;

public class ColorChooserAdapter extends BaseAdapter {

    private Context context;
    private DialogActionListener listener;

    private final int[] colorsConst = {GaliyaraConst.COLOR_AMBER,GaliyaraConst.COLOR_BLACK,GaliyaraConst.COLOR_BLUE,
            GaliyaraConst.COLOR_BLUE_GREY,GaliyaraConst.COLOR_BROWN,GaliyaraConst.COLOR_CYAN,
            GaliyaraConst.COLOR_DEEP_ORANGE,GaliyaraConst.COLOR_DEEP_PURPLE,GaliyaraConst.COLOR_GREEN,
            GaliyaraConst.COLOR_GREY,GaliyaraConst.COLOR_INDIGO,GaliyaraConst.COLOR_LIGHT_BLUE,
            GaliyaraConst.COLOR_LIGHT_GREEN,GaliyaraConst.COLOR_LIME,GaliyaraConst.COLOR_ORANGE,
            GaliyaraConst.COLOR_PINK,GaliyaraConst.COLOR_PURPLE,GaliyaraConst.COLOR_RED,
            GaliyaraConst.COLOR_TEAL,GaliyaraConst.COLOR_YELLOW};

    private final int[] colors = {ThemeColor.AMBER.getColorPack().normal().asInt(),ThemeColor.BLACK.getColorPack().normal().asInt(),
            ThemeColor.BLUE.getColorPack().normal().asInt(),ThemeColor.BLUE_GREY.getColorPack().normal().asInt(),
            ThemeColor.BROWN.getColorPack().normal().asInt(),ThemeColor.CYAN.getColorPack().normal().asInt(),
            ThemeColor.DEEP_ORANGE.getColorPack().normal().asInt(),ThemeColor.DEEP_PURPLE.getColorPack().normal().asInt(),
            ThemeColor.GREEN.getColorPack().normal().asInt(),ThemeColor.GREY.getColorPack().normal().asInt(),
            ThemeColor.INDIGO.getColorPack().normal().asInt(),ThemeColor.LIGHT_BLUE.getColorPack().normal().asInt(),
            ThemeColor.LIGHT_GREEN.getColorPack().normal().asInt(),ThemeColor.LIME.getColorPack().normal().asInt(),
            ThemeColor.ORANGE.getColorPack().normal().asInt(),ThemeColor.PINK.getColorPack().normal().asInt(),
            ThemeColor.PURPLE.getColorPack().normal().asInt(),ThemeColor.RED.getColorPack().normal().asInt(),
            ThemeColor.TEAL.getColorPack().normal().asInt(),ThemeColor.YELLOW.getColorPack().normal().asInt()};

    private int type;

    public ColorChooserAdapter(Context ctx,int t,DialogActionListener l) {
        this.context = ctx;
        this.type = t;
        this.listener = l;
    }

    @Override
    public int getCount() {
        return colorsConst.length;
    }
    @Override
    public Object getItem(int pos) {
        return null;
    }
    @Override
    public long getItemId(int pos) {
        return 0;
    }
    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        Button button;

        if(convertView == null) {
            button = new Button(context);
            button.setBackgroundTintList(ColorStateList.valueOf(colors[pos]));
        }
        else
            button = (Button) convertView;

        button.setOnClickListener(v -> {
            if(type == GaliyaraConst.SET_PRIMARY_COLOR) {
                if(colorsConst[pos] != ThemeManager.getPrimaryColor()) {
                    ThemeManager.setPrimaryColor(colorsConst[pos]);
                    ThemeManager.saveSettings(context, GaliyaraConst.SET_PRIMARY_COLOR);
                }
            }
            if(type == GaliyaraConst.SET_ACCENT_COLOR) {
                if(colorsConst[pos] != ThemeManager.getAccentColor()) {
                    ThemeManager.setAccentColor(colorsConst[pos]);
                    ThemeManager.saveSettings(context, GaliyaraConst.SET_ACCENT_COLOR);
                }
            }
            listener.onActionConfirmed();
        });

        return button;
    }
}
