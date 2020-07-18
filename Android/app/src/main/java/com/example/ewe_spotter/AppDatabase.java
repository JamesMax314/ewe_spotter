package com.example.ewe_spotter;

import androidx.room.Database;
import androidx.room.Entity;
import androidx.room.RoomDatabase;

@Database(entities = {Breed.class, Sheep.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract SheepDAO sheepDAO();
}
