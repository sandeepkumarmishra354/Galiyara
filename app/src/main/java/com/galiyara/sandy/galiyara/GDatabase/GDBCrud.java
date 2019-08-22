package com.galiyara.sandy.galiyara.GDatabase;

import android.content.Context;
import androidx.annotation.NonNull;

import com.galiyara.sandy.galiyara.GInterface.DBOperationListener;
import com.galiyara.sandy.galiyara.GEventBroadcaster.Broadcaster;
import com.galiyara.sandy.galiyara.GFileUtil.GFileUtils;
import com.galiyara.sandy.galiyara.GHelper.GSettings;
import com.galiyara.sandy.galiyara.GConstants.GaliyaraConst;
import java.util.ArrayList;
import java.util.List;

public class GDBCrud {
    private DBOperationListener actionListener;
    private Context context;
    private Thread mThread;
    private int setPassAndBiometric = GaliyaraConst.SET_PASSWORD & GaliyaraConst.ENABLE_BIOMETRIC_OPTION;

    public GDBCrud(Context ctx) { this.context = ctx; }

    public synchronized void deleteData(SecuritySettingEntity entity) {
        mThread = new Thread(() -> {
            GDatabaseClient.getInstance(context).getGDatabase()
                    .securitySettingDao()
                    .delete(entity);
            mThread = null;
        });
        mThread.start();
    }
    public synchronized void deleteData(GeneralSettingEntity entity) {
        mThread = new Thread(() -> {
            GDatabaseClient.getInstance(context).getGDatabase()
                .generalSettingDao()
                .delete(entity);
        mThread = null;
        });
        mThread.start();
    }

    public synchronized void updateData(SecuritySettingEntity newSetting,int op) {
        mThread = new Thread(() -> {
            SecuritySettingEntity entity = secSetting();
            if(entity == null) {
                if(op == GaliyaraConst.SET_PASSWORD)
                    newSetting.setAppLockEnabled(true);
                secSettingAdd(newSetting);
                entity = newSetting;
            }
            else {
                if(op == GaliyaraConst.ENABLE_LOCK_OPTION)
                    entity.setAppLockEnabled(newSetting.isAppLockEnabled());
                if(op == GaliyaraConst.ENABLE_IND_LOCK_OPTION)
                    entity.setIndLockEnabled(newSetting.isIndLockEnabled());
                if(op == GaliyaraConst.SET_PASSWORD) {
                    entity.setPassword(newSetting.getPassword());
                    entity.setAppLockEnabled(true);
                }
                if(op == setPassAndBiometric) {
                    entity.setPassword(newSetting.getPassword());
                    entity.setAppLockEnabled(true);
                    entity.setUseBiometric(newSetting.isUseBiometric());
                }
                secSettingUpdate(entity);
            }
            GSettings.updateSecuritySetting(entity);
            mThread = null;
        });
        mThread.start();
    }

    public synchronized void updateData(GeneralSettingEntity newSetting,int op) {
        mThread = new Thread(() -> {
            GeneralSettingEntity entity = genSetting();
            boolean h = false;
            if(entity == null) {
                genSettingAdd(newSetting);
                entity = newSetting;
                if(op == GaliyaraConst.ENABLE_HIDDEN_OPTION)
                    h = true;
            }
            else {
                if(op == GaliyaraConst.ENABLE_HIDDEN_OPTION) {
                    entity.setShowHidden(newSetting.getShowHidden());
                    h = true;
                }
                if(op == GaliyaraConst.ENABLE_HQ_OPTION)
                    entity.setShowHq(newSetting.getShowHq());
                if(op == GaliyaraConst.ENABLE_TRASH_OPTION)
                    entity.setMoveTrash(newSetting.getMoveTrash());
                genSettingUpdate(entity);
            }
            GSettings.updateGeneralSetting(entity);
            if(h)
                Broadcaster.dataModified(context);
            mThread = null;
        });
        mThread.start();
    }

    public synchronized void getSecuritySetting() {
        mThread = new Thread(() -> {
            SecuritySettingEntity securitySettingEntity = secSetting();
            actionListener.dataReady(securitySettingEntity);
            mThread = null;
        });
        mThread.start();
    }
    public synchronized void getGeneralSetting() {
        mThread = new Thread(() -> {
            GeneralSettingEntity generalSettingEntity = genSetting();
            actionListener.dataReady(generalSettingEntity);
            mThread = null;
        });
        mThread.start();
    }
    public synchronized void addListener(@NonNull DBOperationListener listener) {
        this.actionListener = listener;
    }

    public synchronized void moveToTrash(String path) {
        if(!path.isEmpty()) {
            new Thread(() -> addToTrash(path)).start();
        }
    }
    public synchronized void moveToTrash(ArrayList<String> paths) {
        if(!paths.isEmpty()) {
            mThread = new Thread(() -> {
                for(String path : paths)
                    addToTrash(path);
                mThread = null;
            });
            mThread.start();
        }
    }
    public synchronized void deleteFromTrash(ArrayList<String> paths) {
        if(!paths.isEmpty()) {
            mThread = new Thread(() -> {
                ArrayList<String> allPath = new ArrayList<>(paths);
                List<TrashEntity> trashEntities = allTrashData();
                if(trashEntities != null && !trashEntities.isEmpty()) {
                    for(String path : allPath) {
                        for(TrashEntity trashEntity : trashEntities) {
                            if(trashEntity.getImageName().equals(GFileUtils.getNameWithoutExtension(path))) {
                                removeFromTrash(trashEntity);
                                break;
                            }
                        }
                    }
                    allPath.clear();
                    trashEntities.clear();
                }
                mThread = null;
            });
            mThread.start();
        }
    }
    /* CRUD
       OPERATIONS
     */
    //general setting
    private GeneralSettingEntity genSetting() {
        List<GeneralSettingEntity> list;
        GeneralSettingEntity generalSettingEntity = null;
        list = GDatabaseClient.getInstance(context).getGDatabase()
                .generalSettingDao()
                .getAll();
        if(!list.isEmpty())
            generalSettingEntity = list.get(0);
        return generalSettingEntity;
    }
    private void genSettingAdd(GeneralSettingEntity entity) {
        GDatabaseClient.getInstance(context).getGDatabase()
                .generalSettingDao()
                .insert(entity);
    }
    private void genSettingUpdate(GeneralSettingEntity entity) {
        GDatabaseClient.getInstance(context).getGDatabase()
                .generalSettingDao()
                .update(entity);
    }
    //security setting
    private SecuritySettingEntity secSetting() {
        List<SecuritySettingEntity> list;
        SecuritySettingEntity securitySettingEntity = null;
        list = GDatabaseClient.getInstance(context).getGDatabase()
                .securitySettingDao()
                .getAll();
        if(!list.isEmpty())
            securitySettingEntity = list.get(0);
        return securitySettingEntity;
    }
    private void secSettingUpdate(SecuritySettingEntity entity) {
        GDatabaseClient.getInstance(context).getGDatabase()
                .securitySettingDao()
                .update(entity);
    }

    private void secSettingAdd(SecuritySettingEntity entity) {
        GDatabaseClient.getInstance(context).getGDatabase()
                .securitySettingDao()
                .insert(entity);
    }
    //trash data
    public List<TrashEntity> allTrashData() {
        return GDatabaseClient.getInstance(context).getGDatabase()
                .trashDao()
                .getAll();
    }
    private void addToTrash(String path) {
        TrashEntity entity = new TrashEntity();
        entity.setFullPath(path);
        entity.setImageName(GFileUtils.getNameWithoutExtension(path));
        GDatabaseClient.getInstance(context).getGDatabase()
                .trashDao()
                .insert(entity);
    }
    private void removeFromTrash(TrashEntity entity) {
        GDatabaseClient.getInstance(context).getGDatabase()
                .trashDao()
                .delete(entity);
    }
}
