package com.galiyara.sandy.galiyara.GDatabase;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class GeneralSettingEntity implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "show_hidden")
    private boolean showHidden = false;
    @ColumnInfo(name = "show_hq")
    private boolean showHq = false;
    @ColumnInfo(name = "move_trash")
    private boolean moveTrash = true;

    //getters and setters
    public int getId() {
        return id;
    }
    public boolean getShowHidden() {
        return showHidden;
    }
    public boolean getShowHq() {
        return showHq;
    }
    public boolean getMoveTrash() {
        return moveTrash;
    }

    public void setId(int id) {
        this.id = id;
    }
    public void setShowHidden(boolean status) {
        this.showHidden = status;
    }
    public void setShowHq(boolean status) {
        this.showHq = status;
    }
    public void setMoveTrash(boolean status) {
        this.moveTrash = status;
    }
}
