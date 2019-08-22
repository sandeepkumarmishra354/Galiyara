package com.galiyara.sandy.galiyara.GHelper;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import androidx.annotation.NonNull;

import com.galiyara.sandy.galiyara.GFileUtil.GFileUtils;
import com.galiyara.sandy.galiyara.GApp;
import com.galiyara.sandy.galiyara.GConstants.GaliyaraConst;
import com.galiyara.sandy.galiyara.R;

import java.io.File;

import id.zelory.compressor.Compressor;

@SuppressWarnings("SpellCheckingInspection")
class ImageResizer {

    private Handler handler;
    private Compressor compressor;
    private int quality = 80;

    ImageResizer(@NonNull Context ctx) {
        this.handler = new Handler(ctx.getMainLooper());
        this.compressor = new Compressor(ctx);
    }

    void resizeImage(@NonNull String imgPath,int width,int height) {
        try {
            File rFile = compressor.setMaxWidth(width)
                    .setMaxHeight(height)
                    .setQuality(quality)
                    .setCompressFormat(GFileUtils.getFileExtension(imgPath)
                            .equalsIgnoreCase("png") ?
                            Bitmap.CompressFormat.PNG :
                            Bitmap.CompressFormat.JPEG)
                    .setDestinationDirectoryPath(GaliyaraConst.getResizedPath())
                    .compressToFile(new File(imgPath),GFileUtils.getFileName(imgPath));

            String msg = String.format(GApp.getApplication().getString(R.string.msg_format),rFile.getPath());
            handler.post(() -> GaliyaraConst.showToast(msg));
        } catch (Exception e) {
            e.printStackTrace();
            handler.post(() -> GaliyaraConst.showToast("something went wrong"));
        }
    }

    public int getQuality() {return quality;}
    public void setQuality(int q) {quality = q;}
}
