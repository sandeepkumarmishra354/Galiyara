package com.galiyara.sandy.galiyara.GDialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.GridView;
import android.widget.TextView;

import com.galiyara.sandy.galiyara.GAdapter.ColorChooserAdapter;
import com.galiyara.sandy.galiyara.GInterface.DialogActionListener;
import com.galiyara.sandy.galiyara.GConstants.GaliyaraConst;
import com.galiyara.sandy.galiyara.R;

public class ColorChooserDialog {

    private Dialog dialog;
    private Context context;
    private GridView gridView;
    private ColorChooserAdapter adapter;
    private DialogActionListener listener;
    private int type;

    public ColorChooserDialog(Context ctx, DialogActionListener listener,int t) {
        this.context = ctx;
        this.listener = listener;
        this.type = t;
        this.adapter = new ColorChooserAdapter(ctx, t, new DialogActionListener() {
            @Override
            public void onActionConfirmed() {
                listener.onActionConfirmed();
                dialog.dismiss();
            }
        });
    }

    private void initDialog() {
        if(dialog == null) {
            dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_choose_theme);
            dialog.setOnDismissListener(dialog -> {
                if(listener != null)
                    listener.onActionDismissed();
            });
            dialog.setOnCancelListener(dialog -> {
                if(listener != null)
                    listener.onActionDismissed();
            });
            gridView = dialog.findViewById(R.id.gridView);
            TextView textView = dialog.findViewById(R.id.heading);
            if(type == GaliyaraConst.SET_ACCENT_COLOR)
                textView.setText(context.getString(R.string.accent_color));
            if(type == GaliyaraConst.SET_PRIMARY_COLOR)
                textView.setText(context.getString(R.string.primary_color));
            initButtons();
        }
    }

    private void initButtons() {
        gridView.setAdapter(adapter);
    }

    public void show() {
        initDialog();
        dialog.show();
    }
}
