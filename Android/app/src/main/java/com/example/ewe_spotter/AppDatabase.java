package com.example.ewe_spotter;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Entity;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Sheep.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {
    public abstract SheepDAO sheepDAO();
    private static final String DB_NAME = "app_db";
    private static AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, DB_NAME)
            .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
            .build();
        }
        return instance;
    }
}
