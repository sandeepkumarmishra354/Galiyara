package com.galiyara.sandy.galiyara.GActivity;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.core.content.ContextCompat;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.galiyara.sandy.galiyara.GInterface.BiometricActionListener;
import com.galiyara.sandy.galiyara.GHelper.GSettings;
import com.galiyara.sandy.galiyara.GHelper.GaliyaraHelper;
import com.galiyara.sandy.galiyara.GConstants.GaliyaraConst;
import com.galiyara.sandy.galiyara.R;
import com.galiyara.sandy.galiyara.GSecurity.BiometricUtils;
import com.galiyara.sandy.galiyara.GSecurity.FingerprintHelper;
import io.multimoon.colorful.CAppCompatActivity;
import jp.wasabeef.glide.transformations.BlurTransformation;

public class LockActivity extends CAppCompatActivity {

    private EditText editText;
    private ImageButton clearButton;
    private Button doneButton;
    private LinearLayout linearLayout;
    private FingerprintHelper fingerprintHelper;
    private TextView lockMsg;
    private ImageView imageView;
    private Handler handler = new Handler();
    private Vibrator vibrator;
    private RequestManager glide;
    private boolean isHardwareSupported = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GaliyaraHelper.changeLanguage(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_lock);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        linearLayout = findViewById(R.id.lockLinearLayout);
        lockMsg = findViewById(R.id.lockMsg);
        editText = findViewById(R.id.lockEditText);
        doneButton = findViewById(R.id.doneButton);
        clearButton = findViewById(R.id.clearButton);
        imageView = findViewById(R.id.bgImage);
        doneButton.setTag(R.drawable.ic_arrow_forward_grey);
        glide = Glide.with(this);
        glide.load(R.drawable.lady_joker)
             .apply(RequestOptions
                    .bitmapTransform(new BlurTransformation(10, 2)))
             .into(imageView);
        setupActions();
        initButtons();
        checkForBiometric();
    }

    @Override
    public void onBackPressed() {
        if (GSettings.locked) {
            if (fingerprintHelper != null) {
                fingerprintHelper.cancel();
                fingerprintHelper = null;
            }
            moveTaskToBack(true);
        } else
            finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (GSettings.getSecuritySetting().isUseBiometric() && isHardwareSupported)
            startBiometricAuthentication();
    }

    @Override
    public void onDestroy() {
        glide.clear(imageView);
        glide = null;
        imageView = null;
        fingerprintHelper = null;
        handler = null;
        doneButton = null;
        clearButton = null;
        linearLayout = null;
        lockMsg = null;
        vibrator = null;
        editText = null;
        super.onDestroy();
    }

    private void setupActions() {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 6)
                    enableDoneButton();
                else
                    disableDoneButton();
                if (s.length() == 0)
                    disableClrButton();
                else
                    enableClrButton();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        clearButton.setOnClickListener(v -> {
            String txt = editText.getText().toString();
            if (!txt.isEmpty()) {
                editText.setText(txt.subSequence(0, txt.length() - 1));
            }
            vibrate(100);
        });
        doneButton.setOnClickListener(v -> {
            String pass = editText.getText().toString();
            checkPassword(pass);
        });
    }

    private void initButtons() {
        View rowView1 = findViewById(R.id.row1);
        View rowView2 = findViewById(R.id.row2);
        View rowView3 = findViewById(R.id.row3);

        initKeypad(rowView1, R.id.row1);
        initKeypad(rowView2, R.id.row2);
        initKeypad(rowView3, R.id.row3);

        Button b1 = findViewById(R.id.button0);
        b1.setText(String.valueOf(0));
        b1.setOnClickListener(v -> {
            editText.append(String.valueOf(0));
            vibrate(100);
        });
    }

    private void initKeypad(View rowView, int resId) {
        if (resId == R.id.row1) {
            assignNums(rowView, R.id.button1, 1);
            assignNums(rowView, R.id.button2, 2);
            assignNums(rowView, R.id.button3, 3);
        }
        if (resId == R.id.row2) {
            assignNums(rowView, R.id.button1, 4);
            assignNums(rowView, R.id.button2, 5);
            assignNums(rowView, R.id.button3, 6);
        }
        if (resId == R.id.row3) {
            assignNums(rowView, R.id.button1, 7);
            assignNums(rowView, R.id.button2, 8);
            assignNums(rowView, R.id.button3, 9);
        }
    }

    private void assignNums(View view, int bId, int value) {
        Button b1 = view.findViewById(bId);
        b1.setText(String.valueOf(value));
        b1.setOnClickListener(v -> {
            editText.append(String.valueOf(value));
            vibrate(100);
        });
    }

    private void checkForBiometric() {
        ImageView imageView = findViewById(R.id.fingerPrintImg);
        if (BiometricUtils.isHardwareSupported(this)) {
            isHardwareSupported = true;
            if(GSettings.getSecuritySetting().isUseBiometric()) {
                if(BiometricUtils.isFingerprintRegistered(this)) {
                    startBiometricAuthentication();
                    imageView.animate().setDuration(1500).alpha(1f);
                }
            } else
                linearLayout.removeView(imageView);
        } else {
            linearLayout.removeView(imageView);
        }
    }

    private void startBiometricAuthentication() {
        if (fingerprintHelper == null)
            fingerprintHelper = new FingerprintHelper(this, getActionListener());
        fingerprintHelper.startAuth(null);
    }

    private BiometricActionListener getActionListener() {
        return new BiometricActionListener() {
            @Override
            public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                GSettings.locked = false;
                onBackPressed();
            }

            @Override
            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                setErrorMsg(helpString.toString());
            }

            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                //setErrorMsg(errString.toString());
            }

            @Override
            public void onAuthenticationFailed() {
                setErrorMsg(getString(R.string.finger_not_match));
            }

            @Override
            public void onAuthenticationCancelled() {
                //
            }
        };
    }

    private void enableDoneButton() {
        Drawable drawable = getDrawable(R.drawable.round_button_unlock);
        doneButton.setBackground(drawable);
        doneButton.setEnabled(true);
    }

    private void disableDoneButton() {
        doneButton.setEnabled(false);
        Drawable drawable = getDrawable(R.drawable.round_edit_box);
        doneButton.setBackground(drawable);
    }

    private void enableClrButton() {
        Drawable drawable = getDrawable(R.drawable.round_button_unlock);
        clearButton.setBackground(drawable);
        clearButton.setEnabled(true);
    }

    private void disableClrButton() {
        clearButton.setEnabled(false);
        Drawable drawable = getDrawable(R.drawable.round_edit_box);
        clearButton.setBackground(drawable);
    }

    private void checkPassword(String pass) {
        if (GSettings.getSecuritySetting() != null) {
            String orgPass = GSettings.getSecuritySetting().getPassword();
            if (!orgPass.isEmpty()) {
                try {
                    if (orgPass.equals(pass)) {
                        GSettings.locked = false;
                        onBackPressed();
                    } else {
                        setErrorMsg(getString(R.string.wrong_pass));
                        editText.setText("");
                        vibrate(300);
                    }
                } catch (Exception e) {
                    GaliyaraConst.showToast("Decryption failed");
                }
            }
        }
    }

    private void setErrorMsg(String msg) {
        lockMsg.setText(msg);
        if (Build.VERSION.SDK_INT < 23)
            lockMsg.setTextColor(ContextCompat.getColor(this, R.color.pinkColor));
        else
            lockMsg.setTextColor(getColor(R.color.pinkColor));
        handler.postDelayed(() -> {
            lockMsg.setText(R.string.enter_pin);
            if (Build.VERSION.SDK_INT < 23)
                lockMsg.setTextColor(ContextCompat.getColor(this, R.color.whiteColor));
            else
                lockMsg.setTextColor(getColor(R.color.whiteColor));
        }, 2500);
    }

    private void vibrate(int ms) {
        if (vibrator != null) {
            vibrator.vibrate(ms);
        }
    }
}
