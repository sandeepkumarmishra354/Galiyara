package com.galiyara.sandy.galiyara.GInterface;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.galiyara.sandy.galiyara.GDatabase.GeneralSettingEntity;

import java.util.List;

@Dao
public interface GeneralSettingDao {
    @Query(value = "SELECT * FROM GeneralSettingEntity")
    List<GeneralSettingEntity> getAll();

    @Insert
    void insert(GeneralSettingEntity generalSettingEntity);

    @Delete
    void delete(GeneralSettingEntity generalSettingEntity);

    @Update
    void update(GeneralSettingEntity generalSettingEntity);
}
