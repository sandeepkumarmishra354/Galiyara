package com.galiyara.sandy.galiyara.GFileUtil;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.exifinterface.media.ExifInterface;

import com.galiyara.sandy.galiyara.GAlbumModel.Albums;
import com.galiyara.sandy.galiyara.GHelper.GSettings;
import com.galiyara.sandy.galiyara.GHelper.GSorter;
import com.galiyara.sandy.galiyara.GHelper.GaliyaraHelper;
import com.galiyara.sandy.galiyara.GConstants.GaliyaraConst;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

@SuppressWarnings("FieldCanBeLocal")
public class MediaProvider {

    private final String LIKE_AND = " like ? AND ";
    private final String NOT_LIKE = " NOT LIKE ?";
    private final String DATA = MediaStore.MediaColumns.DATA;
    private final String BUCKET_DIS_NAME = MediaStore.Images.Media.BUCKET_DISPLAY_NAME;
    private final String BUCKET_ID = MediaStore.Images.ImageColumns.BUCKET_ID;
    private final String DATE_TAKEN = MediaStore.Images.ImageColumns.DATE_TAKEN;
    private final Uri EXT_URI = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

    public ArrayList<String> getAllShownImagePath(Context context, String folderName) {
        Cursor cursorBucket;
        int column_index_data;
        String absolutePathOfImage;
        ArrayList<String> allImageList = null;
        String[] selectionArgs = {folderName+"/%",folderName+"/%/%"};

        String mData = MediaStore.Images.Media.DATA;
        String selection = mData+LIKE_AND+mData+NOT_LIKE;
        String[] projectionOnlyBucket = {DATA,BUCKET_DIS_NAME};

        cursorBucket = context.getContentResolver().query(EXT_URI, projectionOnlyBucket, selection, selectionArgs, null);
        if(cursorBucket != null) {
            allImageList = new ArrayList<>(cursorBucket.getCount());
            column_index_data = cursorBucket.getColumnIndexOrThrow(DATA);
            while (cursorBucket.moveToNext()) {
                absolutePathOfImage = cursorBucket.getString(column_index_data);
                if (!absolutePathOfImage.isEmpty())
                    allImageList.add(absolutePathOfImage);
            }
            cursorBucket.close();
        }

        if(allImageList != null) {
            if (!GSettings.getGeneralSetting().getShowHidden())
                allImageList.removeAll(GaliyaraHelper.getHiddenImage(allImageList));
            GSorter.sortImages(allImageList);
        }
        return allImageList;
    }

    public ArrayList<String> getAllImages(Context context) {
        Cursor cursor;
        int column_index_data;
        ArrayList<String> listOfAllImages = null;
        String absolutePathOfImage;

        String[] projection = {DATA,BUCKET_DIS_NAME};

        cursor = context.getContentResolver().query(EXT_URI, projection, null,
                null, null);
        if(cursor != null) {
            listOfAllImages = new ArrayList<>(cursor.getCount());
            column_index_data = cursor.getColumnIndexOrThrow(DATA);
            while (cursor.moveToNext()) {
                absolutePathOfImage = cursor.getString(column_index_data);

                listOfAllImages.add(absolutePathOfImage);
            }
            cursor.close();
        }
        if(listOfAllImages != null) {
            Collections.reverse(listOfAllImages);
            if (!GSettings.getGeneralSetting().getShowHidden())
                listOfAllImages.removeAll(GaliyaraHelper.getHiddenImage(listOfAllImages));
            GSorter.sortImages(listOfAllImages);
        }
        return listOfAllImages;
    }

    public ArrayList<Albums> loadAllAlbums(Context context) {
        Cursor cursor, cursorBucket;
        int column_index_data, column_index_folder_name;
        ArrayList<Albums> listOfAllImages = null;
        String absolutePathOfImage;
        String absolutePathOfFolder;

        String[] projection = {BUCKET_ID,BUCKET_DIS_NAME,DATE_TAKEN,DATA};

        cursor = context.getContentResolver().query(EXT_URI, projection, GaliyaraConst.BUCKET_GROUP_BY,
                null, GaliyaraConst.BUCKET_ORDER_BY);
        String fp;
        if(cursor != null) {
            listOfAllImages = new ArrayList<>(cursor.getCount());
            column_index_data = cursor.getColumnIndexOrThrow(DATA);
            column_index_folder_name = cursor.getColumnIndexOrThrow(BUCKET_DIS_NAME);
            while (cursor.moveToNext()) {
                absolutePathOfImage = cursor.getString(column_index_data);
                absolutePathOfFolder = cursor.getString(column_index_folder_name);
                fp = GFileUtils.getFilePathOnly(absolutePathOfImage);
                String[] selectionArgs = {fp+"/%",fp+"/%/%"};
                String selection = DATA+LIKE_AND+DATA+NOT_LIKE;
                String[] projectionOnlyBucket = {DATA,BUCKET_DIS_NAME};
                cursorBucket = context.getContentResolver().query(EXT_URI, projectionOnlyBucket, selection, selectionArgs, null);
                if(cursorBucket != null) {
                    if (!absolutePathOfImage.isEmpty() && !absolutePathOfFolder.isEmpty())
                        listOfAllImages.add(new Albums(absolutePathOfImage, absolutePathOfFolder, cursorBucket.getCount()));
                    cursorBucket.close();
                }
            }
            cursor.close();
        }
        if(listOfAllImages != null) {
            if (!GSettings.getGeneralSetting().getShowHidden())
                listOfAllImages.removeAll(GaliyaraHelper.getHiddenAlbums(listOfAllImages));
            GSorter.sortAlbums(listOfAllImages);
        }
        return listOfAllImages;
    }

    public ArrayList<String> getTrashedImages() {
        File folder = new File(GaliyaraConst.getTrashPath());
        File[] files = folder.listFiles();
        ArrayList<String> imgList = null;
        if(files.length > 0) {
            imgList = new ArrayList<>(files.length);
            for(int i = files.length-1;i>=0; i--)
                imgList.add(files[i].getPath());
        }
        return imgList;
    }

    public String getMediaInfoTag(String imgPath, int tag) {
        if (tag == GaliyaraConst.IMAGE_FULL_NAME)
            return new File(imgPath).getName();
        if (tag == GaliyaraConst.IMAGE_FULL_PATH)
            return new File(imgPath).getPath();
        if (tag == GaliyaraConst.IMAGE_NAME)
            return GFileUtils.getNameWithoutExtension(imgPath);
        if (tag == GaliyaraConst.IMAGE_TYPE)
            return GFileUtils.getFileExtension(imgPath);
        if (tag == GaliyaraConst.IMAGE_PARENT_PATH)
            return new File(imgPath).getParent();

        String infoStr = "N/A";
        ExifInterface exifInterface;
        try {
            exifInterface = new ExifInterface(imgPath);
            infoStr = getAttribute(exifInterface, tag);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return infoStr;
    }

    private String getAttribute(ExifInterface exifInterface, int tag) {
        String attrStr = "N/A";
        String tStr;

        switch (tag) {
            case GaliyaraConst.IMAGE_ISO:
                tStr = exifInterface.getAttribute(ExifInterface.TAG_ISO_SPEED);
                if (tStr != null)
                    attrStr = tStr;
                break;
            case GaliyaraConst.IMAGE_DATETIME:
                tStr = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
                if (tStr != null)
                    attrStr = tStr;
                break;
            case GaliyaraConst.IMAGE_COPYRIGHT:
                tStr = exifInterface.getAttribute(ExifInterface.TAG_COPYRIGHT);
                if (tStr != null)
                    attrStr = tStr;
                break;
            case GaliyaraConst.IMAGE_PIXEL_X:
                tStr = exifInterface.getAttribute(ExifInterface.TAG_PIXEL_X_DIMENSION);
                if (tStr != null)
                    attrStr = tStr;
                break;
            case GaliyaraConst.IMAGE_PIXEL_Y:
                tStr = exifInterface.getAttribute(ExifInterface.TAG_PIXEL_Y_DIMENSION);
                if (tStr != null)
                    attrStr = tStr;
                break;
            case GaliyaraConst.IMAGE_CAPTURE_SOFTWARE:
                tStr = exifInterface.getAttribute(ExifInterface.TAG_SOFTWARE);
                if (tStr != null)
                    attrStr = tStr;
                break;
            case GaliyaraConst.IMAGE_DEVICE_MODEL:
                tStr = exifInterface.getAttribute(ExifInterface.TAG_MODEL);
                if (tStr != null)
                    attrStr = tStr;
                break;
            case GaliyaraConst.IMAGE_WIDTH:
                tStr = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
                if (tStr != null)
                    attrStr = tStr;
                break;
            case GaliyaraConst.IMAGE_HEIGHT:
                tStr = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
                if (tStr != null)
                    attrStr = tStr;
                break;
            case GaliyaraConst.IMAGE_FULL_RESOLUTION:
                String h = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
                String w = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
                if (h != null && w != null)
                    attrStr = w+"x"+h;
                break;
        }

        return attrStr;
    }
}
