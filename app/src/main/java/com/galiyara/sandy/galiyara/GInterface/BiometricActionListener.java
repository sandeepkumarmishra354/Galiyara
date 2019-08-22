package com.galiyara.sandy.galiyara.GInterface;


import androidx.core.hardware.fingerprint.FingerprintManagerCompat;

@FunctionalInterface
public interface BiometricActionListener {
    void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result);
    default void onAuthenticationFailed(){}
    default void onAuthenticationHelp(int helpCode, CharSequence helpString){}
    default void onAuthenticationError(int errorCode, CharSequence errString){}
    default void onAuthenticationCancelled(){}
}
