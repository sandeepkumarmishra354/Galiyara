package com.galiyara.sandy.galiyara.GInterface;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.galiyara.sandy.galiyara.GDatabase.SecuritySettingEntity;

import java.util.List;

@Dao
public interface SecuritySettingDao {
    @Query(value = "SELECT * FROM SecuritySettingEntity")
    List<SecuritySettingEntity> getAll();

    @Insert
    void insert(SecuritySettingEntity securitySettingEntity);

    @Delete
    void delete(SecuritySettingEntity securitySettingEntity);

    @Update
    void update(SecuritySettingEntity securitySettingEntity);
}
