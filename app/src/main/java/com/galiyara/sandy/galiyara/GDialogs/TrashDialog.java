package com.galiyara.sandy.galiyara.GDialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.widget.Button;
import com.galiyara.sandy.galiyara.GInterface.DialogActionListener;
import com.galiyara.sandy.galiyara.GHelper.ThemeManager;
import com.galiyara.sandy.galiyara.R;

public class TrashDialog {
    private Context context;
    private Dialog dialog;
    private DialogActionListener listener;
    private Button resButton,delButton;

    public TrashDialog(Context ctx,DialogActionListener listener) {
        this.context = ctx;
        this.listener = listener;
    }

    private void initDialog() {
        if (dialog == null) {
            this.dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_trash);
            dialog.setOnDismissListener((v) -> {
                freeMem();
                listener.onActionDismissed();
            });
            dialog.setOnCancelListener(dialog -> {
                freeMem();
                listener.onActionDismissed();
            });
            resButton = dialog.findViewById(R.id.resButton);
            delButton = dialog.findViewById(R.id.delButton);
            if(ThemeManager.isUseDarkTheme()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    resButton.setCompoundDrawableTintList(ColorStateList.valueOf(context.getColor(R.color.whiteColor)));
                    delButton.setCompoundDrawableTintList(ColorStateList.valueOf(context.getColor(R.color.whiteColor)));
                }
            }
            resButton.setOnClickListener(v -> {
                dialog.dismiss();
                listener.onActionRestore();
            });
            delButton.setOnClickListener(v -> {
                dialog.dismiss();
                listener.onActionDelete();
            });
        }
    }

    private void freeMem() {
        dialog = null;
        resButton = null;
        delButton = null;
    }

    public void show() {
        initDialog();
        dialog.show();
    }
}
