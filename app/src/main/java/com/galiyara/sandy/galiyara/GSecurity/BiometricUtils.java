package com.galiyara.sandy.galiyara.GSecurity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;

public class BiometricUtils {
    public static boolean isBiometricPromptEnabled() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P);
    }
    public static boolean isHardwareSupported(Context context) {
        FingerprintManagerCompat fingerprintManager = FingerprintManagerCompat.from(context);
        return fingerprintManager.isHardwareDetected();
    }
    public static boolean isFingerprintRegistered(Context context) {
        FingerprintManagerCompat fingerprintManager = FingerprintManagerCompat.from(context);
        return fingerprintManager.hasEnrolledFingerprints();
    }
    public static boolean isPermissionGranted(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) ==
                    PackageManager.PERMISSION_GRANTED);
        }
        return false;
    }
}
