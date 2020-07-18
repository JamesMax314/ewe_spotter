package com.example.ewe_spotter;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Sheep {

    public Sheep(int sID, String photoPath, String sheepName) {
        this.sID = sID;
        this.photoPath = photoPath;
        this.sheepName = sheepName;
    }

    @PrimaryKey(autoGenerate = true)
    private int sID;

    @ColumnInfo(name = "b_id")
    private int bID;

    @ColumnInfo(name = "photo_path")
    private String photoPath;

    @ColumnInfo(name = "sheep_name")
    private String sheepName;

    public int getSID() {
        return sID;
    }

    public void setSID(int sID) {
        this.sID = sID;
    }

    public int getBID() {
        return bID;
    }

    public void setBID(int bID) {
        this.bID = bID;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getSheepName() {
        return sheepName;
    }

    public void setSheepName(String sheepName) {
        this.sheepName = sheepName;
    }
}
