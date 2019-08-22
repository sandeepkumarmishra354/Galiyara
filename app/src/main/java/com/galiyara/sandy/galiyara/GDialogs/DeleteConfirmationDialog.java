package com.galiyara.sandy.galiyara.GDialogs;

import android.app.Dialog;
import android.content.Context;
import android.widget.Button;

import com.galiyara.sandy.galiyara.GInterface.DialogActionListener;
import com.galiyara.sandy.galiyara.R;

public class DeleteConfirmationDialog {

    private Dialog alertDialog;
    private Context context;
    private DialogActionListener deleteDialogListener;
    private Button okButton,cancelButton;

    public DeleteConfirmationDialog(Context context) {
        this.context = context;
    }

    private void initDialog() {
        if(alertDialog == null) {
            alertDialog = new Dialog(context);
            alertDialog.setContentView(R.layout.dialog_delete_confirmation);
            okButton = alertDialog.findViewById(R.id.okButton);
            cancelButton = alertDialog.findViewById(R.id.cancelButton);
            okButton.setOnClickListener(v -> {
                if(deleteDialogListener != null) {
                    deleteDialogListener.onActionConfirmed();
                }
                alertDialog.dismiss();
            });
            cancelButton.setOnClickListener(v -> {
                if(deleteDialogListener != null) {
                    deleteDialogListener.onActionDenied();
                }
                alertDialog.dismiss();
            });
            alertDialog.setOnCancelListener(dialog -> freeMem());
        }
    }
    public void show() {
        initDialog();
        alertDialog.show();
    }
    private void freeMem() {
        alertDialog = null;
        okButton = null;
        cancelButton = null;
        if(deleteDialogListener != null)
            deleteDialogListener = null;
    }
    public void setActionListener(DialogActionListener dl) {
        this.deleteDialogListener = dl;
    }
}
