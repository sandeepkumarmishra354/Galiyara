package com.galiyara.sandy.galiyara.GDatabase;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class SecuritySettingEntity implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "lock_app")
    private boolean appLockEnabled = false;
    @ColumnInfo(name = "lock_ind")
    private boolean indLockEnabled = false;
    @ColumnInfo(name = "lock_password")
    private String password = "";
    @ColumnInfo(name = "use_biometric")
    private boolean useBiometric = false;

    //getters and setters
    public int getId() {
        return id;
    }
    public boolean isAppLockEnabled() {
        return appLockEnabled;
    }
    public boolean isIndLockEnabled() {
        return indLockEnabled;
    }
    public String getPassword() { return password; }
    public boolean isUseBiometric() {return useBiometric;}

    public void setId(int id) {
        this.id = id;
    }
    public void setUseBiometric(boolean b) { this.useBiometric = b; }
    public void setPassword(String pass) { this.password = pass; }
    public void setAppLockEnabled(boolean status) {
        this.appLockEnabled = status;
    }
    public void setIndLockEnabled(boolean status) {
        this.indLockEnabled = status;
    }
}
