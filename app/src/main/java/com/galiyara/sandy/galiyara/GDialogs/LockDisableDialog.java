package com.galiyara.sandy.galiyara.GDialogs;

import android.app.Dialog;
import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import com.galiyara.sandy.galiyara.GHelper.GSettings;
import com.galiyara.sandy.galiyara.GInterface.DialogActionListener;
import com.galiyara.sandy.galiyara.R;
import com.google.android.material.textfield.TextInputLayout;
import androidx.annotation.NonNull;

public class LockDisableDialog {
    private Dialog dialog;
    private Button unlockButton;
    private EditText editText;
    private TextInputLayout inputLayout;
    private Context context;
    private DialogActionListener listener;

    public LockDisableDialog(@NonNull Context ctx,@NonNull DialogActionListener listener) {
        this.context = ctx;
        this.listener = listener;
    }

    private void init() {
        if(dialog == null) {
            dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_lock_disable);
            dialog.setOnCancelListener(dialog1 -> {
                freeMem();
                listener.onActionDenied();
            });
            dialog.setOnDismissListener(dialog1 -> freeMem());
            editText = dialog.findViewById(R.id.passwordEdit);
            inputLayout = dialog.findViewById(R.id.inputLayout);
            unlockButton = dialog.findViewById(R.id.unlockButton);
            unlockButton.setOnClickListener(v -> checkPassword());
        }
    }

    private void freeMem() {
        dialog = null;
        editText = null;
        unlockButton = null;
        inputLayout = null;
    }
    private void checkPassword() {
        String pswrd = GSettings.getSecuritySetting().getPassword();
        if(editText.getText().toString().equals(pswrd)) {
            dialog.dismiss();
            listener.onActionConfirmed();
        }
        else {
            inputLayout.setErrorEnabled(true);
            inputLayout.setError("Wrong password");
        }
    }

    public void show() {
        init();
        dialog.show();
    }
}
