package com.galiyara.sandy.galiyara.GAlbumModel;

import android.os.Parcel;
import android.os.Parcelable;

public class Albums implements Parcelable {
    private String imagePath,imageFolderPath;
    private int count;

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Albums createFromParcel(Parcel in) {return new Albums(in);}
        public Albums[] newArray(int size) {return new Albums[size];}
    };

    public Albums(String ip, String fp, int c) {
        this.imagePath = ip;
        this.imageFolderPath = fp;
        this.count = c;
    }
    public Albums(Parcel in) {
        this.count = in.readInt();
        this.imageFolderPath = in.readString();
        this.imagePath = in.readString();
    }
    @Override
    public int describeContents() {return 0;}
    @Override
    public void writeToParcel(Parcel dest,int flags) {
        dest.writeInt(this.count);
        dest.writeString(this.imageFolderPath);
        dest.writeString(this.imagePath);
    }

    public String getImagePath() {return this.imagePath;}
    public String getImageFolder() {return this.imageFolderPath;}
    public int getCount() {return this.count;}
    public void setCount(int c) {this.count = c;}
    public void setImagePath(String ip) {this.imagePath = ip;}
    public void setImageFolder(String fp) {this.imageFolderPath = fp;}
}
