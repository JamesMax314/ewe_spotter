package com.example.ewe_spotter;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SheepDAO {
    // This is meta programming and abstracts the SQLite interface
    @Query("SELECT * FROM Breed")
    abstract List<Breed> getAllBreeds();

    @Query("SELECT * FROM Sheep")
    abstract List<Sheep> getAllSheep();
}
