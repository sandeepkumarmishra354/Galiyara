package com.galiyara.sandy.galiyara.GDatabase;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class TrashEntity implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "full_path")
    private String fullPath;
    @ColumnInfo(name = "img_name")
    private String imageName;

    //getters and setters
    public int getId() { return id; }
    public String getFullPath() { return fullPath; }
    public String getImageName() { return imageName; }

    public void setId(int id) { this.id = id; }
    public void setFullPath(String fp) { this.fullPath = fp; }
    public void setImageName(String in) { this.imageName = in; }
}
