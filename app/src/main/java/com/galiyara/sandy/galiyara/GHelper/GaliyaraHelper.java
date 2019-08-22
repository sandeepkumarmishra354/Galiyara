package com.galiyara.sandy.galiyara.GHelper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import android.util.DisplayMetrics;

import com.galiyara.sandy.galiyara.GActivity.AboutActivity;
import com.galiyara.sandy.galiyara.GActivity.AlbumsActivity;
import com.galiyara.sandy.galiyara.GActivity.SettingActivity;
import com.galiyara.sandy.galiyara.GActivity.TrashActivity;
import com.galiyara.sandy.galiyara.GAlbumModel.Albums;
import com.galiyara.sandy.galiyara.BuildConfig;
import com.galiyara.sandy.galiyara.GEventBroadcaster.Broadcaster;
import com.galiyara.sandy.galiyara.GFileUtil.GFileUtils;
import com.galiyara.sandy.galiyara.GApp;
import com.galiyara.sandy.galiyara.GConstants.GaliyaraConst;
import com.galiyara.sandy.galiyara.GImageCropper.CropImage;
import com.galiyara.sandy.galiyara.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Locale;

public class GaliyaraHelper {

    private Thread mThread;

    public void showAboutActivity(Context context) {
        Intent intent = new Intent(context, AboutActivity.class);
        context.startActivity(intent);
    }
    public void showAllPhotosActivity(Context context) {
        Intent intent = new Intent(context, AlbumsActivity.class);
        intent.putExtra(GaliyaraConst.FOLDER_PATH, GaliyaraConst.ALL_IMAGES);
        intent.putExtra(GaliyaraConst.ALBUM_NAME,GaliyaraConst.APP_NAME);
        context.startActivity(intent);
    }
    public void showSettingActivity(Context context) {
        Intent intent = new Intent(context, SettingActivity.class);
        context.startActivity(intent);
    }
    public void showTrashActivity(Context context) {
        Intent intent = new Intent(context, TrashActivity.class);
        context.startActivity(intent);
    }
    public void showPolicy(@NonNull Context context) {
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(GaliyaraConst.PRIVACY_POLICY_LINK));
        context.startActivity(webIntent);
    }
    public void setImageAs(Context context,String currentImagePath) {
        Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        Uri uri = FileProvider.getUriForFile(context,GaliyaraConst.APP_AUTHORITY,new File(currentImagePath));
        intent.setDataAndType(uri,GaliyaraConst.MIME_TYPE_IMG);
        intent.putExtra(GaliyaraConst.MIME_TYPE,GaliyaraConst.MIME_TYPE_IMG);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(Intent.createChooser(intent, "Set as:"));
    }
    public void shareSelectedImage(Context context, ArrayList<String> selectedImagePath, Handler handler) {
        mThread = new Thread(new Runnable() {
            boolean isSuccess;
            @Override
            public void run() {
                ArrayList<String> imgToShare = new ArrayList<>(selectedImagePath);
                ArrayList<Uri> uriList = null;
                if(imgToShare.size() > 1) {
                    uriList = new ArrayList<>();
                    for(String img : imgToShare) {
                        File file = new File(img);
                        Uri uri = FileProvider.getUriForFile(context,GaliyaraConst.APP_AUTHORITY,file);
                        uriList.add(uri);
                    }
                    try {
                        Intent shareIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                        shareIntent.setType(GaliyaraConst.MIME_TYPE_IMG);
                        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList);
                        context.startActivity(shareIntent);
                        isSuccess = true;
                    } catch (Exception e) {
                        isSuccess = false;
                        e.printStackTrace();
                    }
                }
                else if(imgToShare.size() == 1) {
                    File file = new File(imgToShare.get(0));
                    Uri uri = FileProvider.getUriForFile(context,GaliyaraConst.APP_AUTHORITY,file);
                    try {
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType(GaliyaraConst.MIME_TYPE_IMG);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        context.startActivity(shareIntent);
                        isSuccess = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        isSuccess = false;
                    }
                }
                if(uriList != null)
                    uriList.clear();
                imgToShare.clear();
                mThread = null;
                handler.post(() -> {
                    if(!isSuccess)
                        GaliyaraConst.showToast(context.getString(R.string.share_fail));
                });
            }
        });
        mThread.start();
    }
    public void saveCroppedImage(Context context, CropImage.ActivityResult result, String currentImagePath, Handler handler) {
        mThread = new Thread(() -> {
            File orgFile = new File(result.getUri().getPath());
            File destFile = new File(new File(currentImagePath).getParent() + File.separator + orgFile.getName());
            if(orgFile.exists()) {
                try {
                    OutputStream fout = new FileOutputStream(destFile);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    Bitmap bitmap = BitmapFactory.decodeFile(orgFile.getPath(),options);
                    bitmap.compress(Bitmap.CompressFormat.JPEG,85,fout);
                    fout.flush();
                    fout.close();
                    bitmap.recycle();
                    Broadcaster.addNewFileToMediaScanner(destFile.getPath(),context);
                    if(orgFile.delete())
                        Broadcaster.updateDeletedMedia(orgFile.getPath(),context);
                    handler.post(() -> {
                        GaliyaraConst.showToast(context.getString(R.string.crop_success));
                        Broadcaster.broadcastNewAddedEvent(context,
                                destFile.getPath(),GaliyaraConst.NEW_PHOTO_ADDED);
                        Broadcaster.dataModified(context);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.post(() -> GaliyaraConst.showToast(context.getString(R.string.crop_fail)));
                }
            }
            else
                handler.post(() -> GaliyaraConst.showToast(context.getString(R.string.crop_fail)));
            mThread = null;
        });
        mThread.start();
    }

    public void resizeImage(@NonNull Context ctx,@NonNull String imgPath,int width,int height) {
        if(imgPath.isEmpty())
            return;
        mThread = new Thread(() -> {
            ImageResizer imageResizer = new ImageResizer(ctx);
            imageResizer.resizeImage(imgPath,width,height);
            mThread = null;
        });
        mThread.start();
    }
    public void resizeImageToPassportSize(@NonNull Context ctx,@NonNull String imgPath) {
        if(imgPath.isEmpty())
            return;
        mThread = new Thread(() -> {
            ImageResizer imageResizer = new ImageResizer(ctx);
            imageResizer.resizeImage(imgPath,GaliyaraConst.PASSPORT_WIDTH,
                    GaliyaraConst.PASSPORT_HEIGHT);
            mThread = null;
        });
        mThread.start();
    }

    public static ArrayList<String> getHiddenImage(@NonNull ArrayList<String> album) {
        ArrayList<String> list = new ArrayList<>();
        for(String s : album) {
            String parent = GFileUtils.getFileParent(s);
            String name = GFileUtils.getNameWithoutExtension(s);
            if(parent.startsWith(".") || name.startsWith("."))
                list.add(s);
        }
        return list;
    }
    public static ArrayList<Albums> getHiddenAlbums(@NonNull ArrayList<Albums> albums) {
        ArrayList<Albums> list = new ArrayList<>();
        for(Albums album : albums) {
            if(album.getImageFolder().startsWith("."))
                list.add(album);
        }
        return list;
    }

    public static void changeLanguage(@NonNull Context context) {
        Locale locale = new Locale(GSettings.getLanguage());
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration conf = resources.getConfiguration();
        conf.setLocale(locale);
        resources.updateConfiguration(conf,dm);
    }

    public static void initLang(@NonNull Context ctx) {
        SharedPreferences preferences = ctx.getSharedPreferences(GaliyaraConst.MY_PREFS,Context.MODE_PRIVATE);
        String lang = preferences.getString(GaliyaraConst.LANG_KEY,GaliyaraConst.LANG_DEFAULT);
        //noinspection ConstantConditions
        GSettings.setLanguage(lang);
    }
    public static void updateLang(@NonNull Context ctx,@NonNull String lang) {
        SharedPreferences preferences = ctx.getSharedPreferences(GaliyaraConst.MY_PREFS,Context.MODE_PRIVATE);
        preferences.edit().putString(GaliyaraConst.LANG_KEY,lang).apply();
        //GSettings.setLanguage(lang);
        GaliyaraConst.showToast(ctx.getString(R.string.restart_app));
    }
    public static void setTrashedPath() {
        String dataDir = String.valueOf(GApp.getApplication().getExternalFilesDir(null));
        File dir = new File(dataDir,GaliyaraConst.TRASH_DIR_NAME);
        if(!dir.exists())
            //noinspection ResultOfMethodCallIgnored
            dir.mkdir();
        GaliyaraConst.setTrashPath(dir.getPath());
    }
    public static void setResizedPath() {
        File pictureFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File dir = new File(pictureFile, GaliyaraConst.RESIZE_DIR_NAME);
        if(!dir.exists())
            //noinspection ResultOfMethodCallIgnored
            dir.mkdir();
        GaliyaraConst.setResizedPath(dir.getPath());
    }
}
