package com.galiyara.sandy.galiyara.GInterface;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.galiyara.sandy.galiyara.GDatabase.TrashEntity;

import java.util.List;

@Dao
public interface TrashDao {
    @Query(value = "SELECT * FROM TrashEntity")
    List<TrashEntity> getAll();

    @Insert
    void insert(TrashEntity trashEntity);

    @Delete
    void delete(TrashEntity trashEntity);

    @Update
    void update(TrashEntity trashEntity);
}
