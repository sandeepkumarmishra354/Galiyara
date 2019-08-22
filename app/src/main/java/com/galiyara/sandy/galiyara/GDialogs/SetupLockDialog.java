package com.galiyara.sandy.galiyara.GDialogs;

import android.app.Dialog;
import android.content.Context;
import com.galiyara.sandy.galiyara.GSecurity.BiometricUtils;
import com.google.android.material.textfield.TextInputLayout;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import com.galiyara.sandy.galiyara.GInterface.DialogActionListener;
import com.galiyara.sandy.galiyara.GHelper.GSettings;
import com.galiyara.sandy.galiyara.R;
import androidx.annotation.NonNull;

public class SetupLockDialog {
    private Context context;
    private Dialog dialog;
    private DialogActionListener listener;
    private EditText choosePassword,repeatPassword;
    private TextInputLayout chooseLayout,repeatLayout;
    private CheckBox checkBox,useBiometric;
    private Button enableButton;

    public SetupLockDialog(@NonNull Context ctx,@NonNull DialogActionListener listener) {
        this.context = ctx;
        this.listener = listener;
    }

    private void initDialog() {
        if (dialog == null) {
            this.dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_password);
            dialog.setOnCancelListener(dialog -> {
                freeMem();
                listener.onActionDismissed();
            });
            dialog.setOnDismissListener(dialog1 -> freeMem());
            initVars();
        }
    }
    private void initVars() {
        choosePassword = dialog.findViewById(R.id.choosePassword);
        repeatPassword = dialog.findViewById(R.id.repeatPassword);
        chooseLayout = dialog.findViewById(R.id.chooseLayout);
        repeatLayout = dialog.findViewById(R.id.repeatLayout);
        checkBox = dialog.findViewById(R.id.showPassword);
        useBiometric = dialog.findViewById(R.id.useBiometric);
        enableButton = dialog.findViewById(R.id.enableButton);

        useBiometric.setEnabled(BiometricUtils.isHardwareSupported(context));
        if(GSettings.getSecuritySetting() != null) {
            choosePassword.setText(GSettings.getSecuritySetting().getPassword());
            repeatPassword.setText(GSettings.getSecuritySetting().getPassword());
            if(useBiometric.isEnabled())
                useBiometric.setChecked(GSettings.getSecuritySetting().isUseBiometric());
        }
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                int type = EditorInfo.TYPE_CLASS_NUMBER |
                        EditorInfo.TYPE_NUMBER_VARIATION_NORMAL;
                choosePassword.setInputType(type);
                repeatPassword.setInputType(type);
            }
            else {
                int type = EditorInfo.TYPE_CLASS_NUMBER |
                        EditorInfo.TYPE_NUMBER_VARIATION_PASSWORD;
                choosePassword.setInputType(type);
                repeatPassword.setInputType(type);
            }
        });
        enableButton.setOnClickListener(v -> checkAndSavePassword());
    }

    private void checkAndSavePassword() {
        String p1 = choosePassword.getText().toString();
        String p2 = repeatPassword.getText().toString();
        if(p1.isEmpty() || p1.length() < 6) {
            repeatLayout.setErrorEnabled(false);
            chooseLayout.setErrorEnabled(true);
            chooseLayout.setError(context.getString(R.string.password_length_error));
        }
        else if(!p1.equals(p2)) {
            chooseLayout.setErrorEnabled(false);
            repeatLayout.setErrorEnabled(true);
            repeatLayout.setError(context.getString(R.string.password_not_match));
        }
        else {
            dialog.dismiss();
            listener.onActionConfirmed(p1,useBiometric.isChecked());
        }
    }
    private void freeMem() {
        dialog = null;
        chooseLayout = null;
        repeatLayout = null;
        choosePassword = null;
        repeatPassword = null;
        checkBox = null;
        useBiometric = null;
        enableButton = null;
    }

    public void show() {
        initDialog();
        dialog.show();
    }
}
