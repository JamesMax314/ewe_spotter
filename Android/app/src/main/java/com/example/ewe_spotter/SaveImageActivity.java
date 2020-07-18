package com.example.ewe_spotter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

public class SaveImageActivity extends AppCompatActivity {
    public String photoPath;
    public Bitmap userBitmapSave;
    public int intId;
    public static FragmentManager fragmentManagerSave;
    private Handler mainHandler = new Handler();
    public AppDatabase appDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_image);

        fragmentManagerSave = getSupportFragmentManager();

        if (findViewById(R.id.fragment_container_save) != null){
            if (savedInstanceState != null){
                return;
            }
            fragmentManagerSave.beginTransaction()
                    .add(R.id.fragment_container_save, new NewSheepFragment())
                    .commit();
        }

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        photoPath = intent.getStringExtra(IDActivity.BMP_PATH);
        userBitmapSave = BitmapFactory.decodeFile(photoPath);
        intId = intent.getIntExtra(IDActivity.INT_ID, 0);

        // Get instance of database
        appDb = AppDatabase.getInstance(this);
    }

    public void save(String name){
        Sheep newSheep = new Sheep(intId, photoPath, name);
        dbRunnable runnable = new dbRunnable(newSheep);
        new Thread(runnable).start();
    }

    public void recyclerActivity(){
        Toast.makeText(getApplicationContext(), "saved", Toast.LENGTH_SHORT).show();
    }

    public void release(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    class dbRunnable implements Runnable{
        private Sheep newSheep;
        dbRunnable(Sheep newSheep){
            this.newSheep = newSheep;
        }

        @Override
        public void run() {
            appDb.sheepDAO().inertSheep(newSheep);;
            // Posts message to main thread
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    recyclerActivity();
                }
            });
        }
    }
}