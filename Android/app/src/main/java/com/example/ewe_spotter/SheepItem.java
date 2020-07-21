package com.example.ewe_spotter;

public class SheepItem {
    private String sheepName;
    private String photoPath;
    private String sheepBreed;

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

    public String getSheepBreed() {
        return sheepBreed;
    }

    public void setSheepBreed(int breedID) {
        this.sheepBreed = sheepBreed;
    }
}
