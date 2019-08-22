package com.galiyara.sandy.galiyara.GEventBroadcaster;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.galiyara.sandy.galiyara.GFileUtil.GFileUtils;
import com.galiyara.sandy.galiyara.GConstants.GaliyaraConst;
import java.io.File;

public class Broadcaster {

    public static void broadcastDeleteEvent(Context context,String imgPath,String intentFilter) {
        String albumName = GFileUtils.getFileParent(imgPath);
        Intent intent = new Intent(intentFilter);
        intent.putExtra("img_path", imgPath);
        intent.putExtra(GaliyaraConst.ALBUM_NAME, albumName);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
    public static void broadcastNewAddedEvent(Context context,String imgPath,String intentFilter) {
        String albumName = GFileUtils.getFileParent(imgPath);
        Intent intent = new Intent(intentFilter);
        intent.putExtra("img_path", imgPath);
        intent.putExtra(GaliyaraConst.ALBUM_NAME, albumName);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
    public static void broadcastRenameEvent(Context context,String oldImg, String newImg) {
        Intent intent = new Intent(GaliyaraConst.PHOTO_RENAMED);
        intent.putExtra("old_img", oldImg);
        intent.putExtra("new_img", newImg);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
    public static void addNewFileToMediaScanner(String filePath,Context context) {
        MediaScannerConnection.scanFile(context,new String[]{filePath},null,null);
    }
    public static void updateDeletedMedia(String filePath,Context context) {
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.fromFile(new File(filePath))));
    }
    public static void dataModified(Context context) {
        Intent intent = new Intent(GaliyaraConst.DATA_MODIFIED_FILTER);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
    public static boolean isReadExternalPermissionGranted(Context context) {
        if(Build.VERSION.SDK_INT >= 23) {
            if(context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                return true;
            else {
                ActivityCompat.requestPermissions((Activity)context,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE}, GaliyaraConst.PERMISSION_CODE);
                return false;
            }
        }
        else
            return true;//automatically granted on sdk<23 upon installation
    }
}
