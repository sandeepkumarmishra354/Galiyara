package com.galiyara.sandy.galiyara.GSecurity;

import android.content.Context;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat.CryptoObject;
import androidx.core.os.CancellationSignal;

import com.galiyara.sandy.galiyara.GInterface.BiometricActionListener;

public class FingerprintHelper extends FingerprintManagerCompat.AuthenticationCallback {

    private BiometricActionListener listener;
    private Context context;
    private CancellationSignal cancellationSignalv4;
    private boolean authenticating = false;

    public FingerprintHelper(Context ctx, BiometricActionListener listener) {
        this.context = ctx;
        this.listener = listener;
        this.cancellationSignalv4 = new androidx.core.os.CancellationSignal();
        this.cancellationSignalv4.setOnCancelListener(listener::onAuthenticationCancelled);
    }

    public void startAuth(CryptoObject cryptoObject) {
            if(BiometricUtils.isFingerprintRegistered(context) &&
                    BiometricUtils.isHardwareSupported(context)) {
                try {
                    FingerprintManagerCompat manager = FingerprintManagerCompat.from(context);
                    manager.authenticate(cryptoObject,0,cancellationSignalv4,this,null);
                    authenticating = true;
                } catch (Exception e) {
                    authenticating = false;
                    e.printStackTrace();
                    listener.onAuthenticationFailed();
                }
            }
    }

    public void cancel() {
        if(authenticating) {
            cancellationSignalv4.cancel();
            authenticating = false;
        }
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        super.onAuthenticationError(errorCode, errString);
        listener.onAuthenticationError(errorCode, errString);
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        super.onAuthenticationHelp(helpCode, helpString);
        listener.onAuthenticationHelp(helpCode, helpString);
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
        super.onAuthenticationSucceeded(result);
        listener.onAuthenticationSucceeded(result);
    }

    @Override
    public void onAuthenticationFailed() {
        super.onAuthenticationFailed();
        listener.onAuthenticationFailed();
    }
}