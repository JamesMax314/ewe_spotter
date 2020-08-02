package com.example.ewe_spotter;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Sheep {

    public Sheep(int bID, String photoPath, String sheepName, String date) {
        this.bID = bID;
        this.photoPath = photoPath;
        this.sheepName = sheepName;
        this.date = date;
    }

    @PrimaryKey(autoGenerate = true)
    private int sID;

    @ColumnInfo(name = "b_id")
    private int bID;

    @ColumnInfo(name = "photo_path")
    private String photoPath;

    @ColumnInfo(name = "sheep_name")
    private String sheepName;

    @ColumnInfo(name = "date")
    private String date;

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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
