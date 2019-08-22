package com.galiyara.sandy.galiyara.GFileUtil;

import android.annotation.SuppressLint;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class GFileUtils {

    public static String getFileSize(String filePath) {
        File file = new File(filePath);
        String fileSize = "";
        if(file.exists() && file.isFile()) {
            long len = file.length();
            long sz = len/1024;
            if(sz < 1)
                fileSize = sz+" bytes";
            else if(sz >= 1024)
                fileSize = getMediaSizeInMb(filePath);
            else
                fileSize = getMediaSizeInKb(filePath);
        }
        return fileSize;
    }

    @SuppressLint("DefaultLocale")
    private static String getMediaSizeInKb(String filePath) {
        String mediaSize = "";
        File file = new File(filePath);
        if(file.exists() && file.isFile()) {
            float sz = file.length() / (float) 1024;
            mediaSize = String.format("%.2f %s",sz,"KB");
        }
        return mediaSize;
    }
    @SuppressLint("DefaultLocale")
    private static String getMediaSizeInMb(String filePath) {
        String mediaSize = "";
        File file = new File(filePath);
        if(file.exists() && file.isFile()) {
            float sz = file.length() / (float) (1024*1024);
            mediaSize = String.format("%.2f %s",sz,"MB");
        }
        return mediaSize;
    }

    public static String getNameWithoutExtension(String filePath) {
        String fullName = new File(filePath).getName();
        String name;
        if(fullName.indexOf(".") > 0)
            name = fullName.substring(0,fullName.lastIndexOf("."));
        else
            name = fullName;

        return name;
    }
    public static String getFileExtension(String filePath) {
        String fullName = new File(filePath).getName();
        String extension;
        if(fullName.lastIndexOf(".") > 0)
            extension = fullName.substring(fullName.lastIndexOf(".")+1);
        else
            extension = fullName;

        return extension;
    }
    public static String getFileParent(String filePath) {
        String path = new File(filePath).getParent();
        if(path != null && path.contains("/")) {
            String[] pathList = path.split(File.separator);
            return pathList[pathList.length - 1];
        }
        else
            return path;
    }
    public static String getFilePathOnly(String p) {
        return new File(p).getParent();
    }

    public static void copy(File src, File dest) throws IOException {
        try (InputStream is = new FileInputStream(src);
             OutputStream os = new FileOutputStream(dest)) {

            // buffer size 1K
            byte[] buf = new byte[1024];

            int bytesRead;
            while ((bytesRead = is.read(buf)) > 0) {
                os.write(buf, 0, bytesRead);
            }
        }
    }

    public static String getFileName(String path) {
        return new File(path).getName();
    }

}
