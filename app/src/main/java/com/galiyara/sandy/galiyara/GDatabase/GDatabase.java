package com.galiyara.sandy.galiyara.GDatabase;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.galiyara.sandy.galiyara.GInterface.GeneralSettingDao;
import com.galiyara.sandy.galiyara.GInterface.SecuritySettingDao;
import com.galiyara.sandy.galiyara.GInterface.TrashDao;

@Database(entities = {SecuritySettingEntity.class, GeneralSettingEntity.class, TrashEntity.class},
        version = 1,exportSchema = false)
public abstract class GDatabase extends RoomDatabase {
    public abstract SecuritySettingDao securitySettingDao();
    public abstract GeneralSettingDao generalSettingDao();
    public abstract TrashDao trashDao();
}
