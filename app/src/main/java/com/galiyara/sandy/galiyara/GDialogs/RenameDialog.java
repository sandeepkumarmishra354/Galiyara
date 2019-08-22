package com.galiyara.sandy.galiyara.GDialogs;

import android.app.Dialog;
import android.content.Context;
import android.widget.Button;
import android.widget.EditText;

import com.galiyara.sandy.galiyara.GInterface.DialogActionListener;
import com.galiyara.sandy.galiyara.GFileUtil.GFileUtils;
import com.galiyara.sandy.galiyara.R;

public class RenameDialog {

    private Button renameOkButton;
    private Button renameCancelButton;
    private EditText renameEditText;
    private Context context;
    private String currentImagePath;
    private DialogActionListener dialogActionListener;
    private Dialog mainDialog;

    public RenameDialog(Context context,String currentImagePath) {
        this.context = context;
        this.currentImagePath = currentImagePath;
    }

    private void initRenamePopup() {
        if (mainDialog == null) {
            mainDialog = new Dialog(context);
            mainDialog.setContentView(R.layout.dialog_rename);
            renameOkButton = mainDialog.findViewById(R.id.renameOkBtn);
            renameCancelButton = mainDialog.findViewById(R.id.renameCancelBtn);
            renameEditText = mainDialog.findViewById(R.id.renameTextEdit);
            renameOkButton.setOnClickListener(v -> {
                mainDialog.dismiss();
                if(dialogActionListener != null)
                    dialogActionListener.onActionConfirmed(renameEditText.getText().toString());
            });
            renameCancelButton.setOnClickListener(v -> {
                mainDialog.dismiss();
                if(dialogActionListener != null)
                    dialogActionListener.onActionDenied();
            });
        }
    }
    public void show() {
        initRenamePopup();
        String imgName = GFileUtils.getNameWithoutExtension(currentImagePath);
        renameEditText.setText(imgName);
        mainDialog.show();
    }
    @SuppressWarnings("WeakerAccess")
    public void hidePopup() {
        mainDialog.dismiss();
        renameEditText = null;
        renameCancelButton = null;
        renameOkButton = null;
        mainDialog = null;
        if(dialogActionListener != null)
            dialogActionListener = null;
    }
    public void setActionListener(DialogActionListener dialogActionListener) {
        this.dialogActionListener = dialogActionListener;
    }
}
