package com.example.ewe_spotter;

public class SheepItem {
    private String date;
    private String sheepName;
    private String photoPath;
    private int sheepBreed;

    public String getSheepName() {
        return sheepName;
    }

    public void setSheepName(String sheepName) {
        this.sheepName = sheepName;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public int getSheepBreed() {
        return sheepBreed;
    }

    public void setSheepBreed(int breedID) {
        this.sheepBreed = breedID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
