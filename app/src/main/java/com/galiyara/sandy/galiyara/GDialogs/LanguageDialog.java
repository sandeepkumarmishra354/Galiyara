package com.galiyara.sandy.galiyara.GDialogs;

import android.app.Dialog;
import android.content.Context;
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.Button;

import com.galiyara.sandy.galiyara.GInterface.DialogActionListener;
import com.galiyara.sandy.galiyara.GConstants.GaliyaraConst;
import com.galiyara.sandy.galiyara.R;

public class LanguageDialog {

    private Dialog dialog;
    private Context context;
    private DialogActionListener listener;
    private Button engButton,hiButton;
    private View.OnClickListener actionListener;

    public LanguageDialog(@NonNull Context ctx, DialogActionListener listener) {
        this.context = ctx;
        this.listener = listener;
    }

    private void initDialog() {
        if(dialog == null) {
            dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_choose_language);
            engButton = dialog.findViewById(R.id.englishButton);
            hiButton = dialog.findViewById(R.id.hindiButton);
            actionListener = v -> {
                if(v.getId() == R.id.englishButton) {
                    if(listener != null)
                        listener.onLangChanged(GaliyaraConst.LANG_ENGLISH);
                }
                if(v.getId() == R.id.hindiButton) {
                    if(listener != null)
                        listener.onLangChanged(GaliyaraConst.LANG_HINDI);
                }
                dialog.dismiss();
            };
            engButton.setOnClickListener(actionListener);
            hiButton.setOnClickListener(actionListener);
            dialog.setOnDismissListener(dialog -> {
                freeMem();
                if(listener != null)
                    listener.onActionDismissed();
            });
            dialog.setOnCancelListener(dialog -> {
                freeMem();
                if(listener != null)
                    listener.onActionDismissed();
            });
        }
    }

    private void freeMem() {
        hiButton = null;
        engButton = null;
        actionListener = null;
        dialog = null;
    }

    public void show() {
        initDialog();
        dialog.show();
    }
}
