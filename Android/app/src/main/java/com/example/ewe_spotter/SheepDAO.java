package com.example.ewe_spotter;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SheepDAO {
    // This is meta programming and abstracts the SQLite interface
    @Query("SELECT * FROM Sheep")
    abstract List<Sheep> getAllSheep();

    @Query("SELECT * FROM Sheep WHERE sID=:sheep_id")
    abstract Sheep findBySID(int sheep_id);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    int updateSheep(Sheep sheep);

    @Insert
    void inertSheep(Sheep sheep);

    @Delete
    void deleteSheep(Sheep sheep);
}
