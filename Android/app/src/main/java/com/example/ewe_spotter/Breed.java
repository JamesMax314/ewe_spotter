package com.example.ewe_spotter;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Breed {

    public Breed(int bID, String breedName, String description) {
        this.bID = bID;
        this.breedName = breedName;
        this.description = description;
    }

    @PrimaryKey
    private int bID;

    @ColumnInfo(name = "breed_name")
    private String breedName;

    @ColumnInfo(name = "description")
    private String description;

    public int getBID() {
        return bID;
    }

    public String getBreedName() {
        return breedName;
    }

    public String getDescription() {
        return description;
    }
}
