package com.galiyara.sandy.galiyara.GFileUtil;

import android.content.Context;
import android.os.AsyncTask;
import android.os.SystemClock;
import androidx.annotation.NonNull;
import com.galiyara.sandy.galiyara.GInterface.AsyncCallbackListener;
import com.galiyara.sandy.galiyara.GDatabase.GDBCrud;
import com.galiyara.sandy.galiyara.GDatabase.TrashEntity;
import com.galiyara.sandy.galiyara.GEventBroadcaster.Broadcaster;
import com.galiyara.sandy.galiyara.GHelper.GSettings;
import com.galiyara.sandy.galiyara.GConstants.GaliyaraConst;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileCopyMoveDelete extends AsyncTask<ArrayList<String>,Integer,Integer> {

    private int COPY_MOVE_DELETE;
    private Context context;
    private AsyncCallbackListener asyncTaskCallback;
    private String copyMoveDestination;

    public FileCopyMoveDelete(@NonNull Context c, int cm,@NonNull AsyncCallbackListener asc) {
        COPY_MOVE_DELETE = cm;
        asyncTaskCallback = asc;
        context = c;
    }
    public FileCopyMoveDelete(@NonNull Context c, int cm,@NonNull AsyncCallbackListener asc,@NonNull String cmDest) {
        COPY_MOVE_DELETE = cm;
        asyncTaskCallback = asc;
        context = c;
        copyMoveDestination = cmDest;
    }
    @Override
    protected void onPreExecute() {
        asyncTaskCallback.taskInitiated();
    }
    @Override
    protected void onPostExecute(Integer d) {
        asyncTaskCallback.taskCompleted(d);
    }
    @Override
    public void onProgressUpdate(Integer... d) {
        asyncTaskCallback.taskProgress(d[0]);
    }
    @Override
    @SafeVarargs
    protected final Integer doInBackground(ArrayList<String>... a) {
        ArrayList<String> imgList = a[0];
        int size = 0;
        if(COPY_MOVE_DELETE == GaliyaraConst.COPY_FILE)
            size = copyFile(imgList);
        if(COPY_MOVE_DELETE == GaliyaraConst.MOVE_FILE)
            size = moveFile(imgList);
        if(COPY_MOVE_DELETE == GaliyaraConst.DELETE_FILE) {
            boolean moveToTrash = GSettings.getGeneralSetting().getMoveTrash();
            if(!moveToTrash)
                size = deleteFile(imgList);
            else
                size = moveFileToTrash(imgList);
        }
        if(COPY_MOVE_DELETE == GaliyaraConst.DELETE_TRASH)
            size = deleteFromTrash(imgList);
        if(COPY_MOVE_DELETE == GaliyaraConst.RESTORE_TRASH)
            size = restoreFromTrash(imgList);
        return size;
    }

    private int copyFile(ArrayList<String> imgList) {
        if(!copyMoveDestination.isEmpty()) {
            for(String imgPath : imgList) {
                File originalFile = new File(imgPath);
                File destinationFile = new File(copyMoveDestination+originalFile.getName());
                if(!destinationFile.exists()) {
                    try {
                        GFileUtils.copy(originalFile,destinationFile);
                        publishProgress(imgList.indexOf(imgPath)+1);
                        Broadcaster.addNewFileToMediaScanner(destinationFile.getPath(), context);
                        SystemClock.sleep(30);
                    } catch (Exception e) {
                        //
                    }
                }
            }
        }
        return imgList.size();
    }
    private int moveFile(ArrayList<String> imgList) {
        if(!copyMoveDestination.isEmpty()) {
            for(String imgPath : imgList) {
                File originalFile = new File(imgPath);
                File destinationFile = new File(copyMoveDestination+originalFile.getName());
                if(!destinationFile.exists()) {
                    if(originalFile.renameTo(destinationFile)) {
                        publishProgress(imgList.indexOf(imgPath)+1);
                        Broadcaster.addNewFileToMediaScanner(destinationFile.getPath(), context);
                        Broadcaster.updateDeletedMedia(originalFile.getPath(),context);
                        SystemClock.sleep(30);
                    }
                }
            }
        }
        return imgList.size();
    }
    private int deleteFile(ArrayList<String> imgPath) {
        for(String i : imgPath) {
            deleteFilePermanently(i);
            publishProgress(imgPath.indexOf(i)+1);
            SystemClock.sleep(30);
        }
        return imgPath.size();
    }

    private void deleteFilePermanently(String filePath) {
        File file = new File(filePath);
        if(file.delete())
            Broadcaster.updateDeletedMedia(filePath,context);
    }

    private int moveFileToTrash(ArrayList<String> imgPath) {
        ArrayList<String> list = new ArrayList<>();
        String trashPath = GaliyaraConst.getTrashPath()+File.separator;
        File srcFile,destFile;
        for(String img : imgPath) {
            srcFile = new File(img);
            destFile = new File(trashPath+GFileUtils.getNameWithoutExtension(img));
            if(srcFile.renameTo(destFile)) {
                publishProgress(imgPath.indexOf(img)+1);
                Broadcaster.updateDeletedMedia(img,context);
                list.add(img);
                SystemClock.sleep(30);
            }
        }
        new GDBCrud(context).moveToTrash(list);
        return imgPath.size();
    }

    private int deleteFromTrash(ArrayList<String> i) {
        if(!i.isEmpty()) {
            ArrayList<String> imgList = new ArrayList<>(i);
            GDBCrud gdbCrud = new GDBCrud(context);
            for(String path : imgList) {
                deleteFilePermanently(path);
                publishProgress(imgList.indexOf(path)+1);
                SystemClock.sleep(30);
            }
            gdbCrud.deleteFromTrash(imgList);
        }
        return i.size();
    }
    private int restoreFromTrash(ArrayList<String> i) {
        if(!i.isEmpty()) {
            ArrayList<String> imgList = new ArrayList<>(i);
            GDBCrud gdbCrud = new GDBCrud(context);
            List<TrashEntity> entityList = gdbCrud.allTrashData();
            if(entityList != null) {
                File srcFile,destFile;
                for(String img : imgList) {
                    for(TrashEntity entity : entityList) {
                        if(entity.getImageName().equals(GFileUtils.getNameWithoutExtension(img))) {
                            srcFile = new File(img);
                            destFile = new File(entity.getFullPath());
                            if(srcFile.renameTo(destFile)) {
                                Broadcaster.addNewFileToMediaScanner(destFile.getPath(),context);
                                publishProgress(imgList.indexOf(img)+1);
                                SystemClock.sleep(30);
                            }
                            break;
                        }
                    }
                }
                gdbCrud.deleteFromTrash(imgList);
            }
        }
        return i.size();
    }
}
