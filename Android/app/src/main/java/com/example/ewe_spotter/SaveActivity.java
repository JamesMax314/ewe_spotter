package com.example.ewe_spotter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

public class SaveActivity extends AppCompatActivity {

    public static final String BMP_PATH = "com.example.ewe_spotter.bmp_path";
    public static final String INT_BID = "com.example.ewe_spotter.int_bid";
    public static final String INT_SID = "com.example.ewe_spotter.int_sid";
    public static final String STR_NAME = "com.example.ewe_spotter.STR_NAME";

    public static final int REMOVE = 1;
    public static final int SAVE = 2;

    public String photoPath;
    public Bitmap userBitmapSave;
    public int bID;
    public int sID;
    public static FragmentManager fragmentManagerSave;
    private Handler mainHandler = new Handler();
    public AppDatabase appDb;

    public String nameString;

    public Sheep sheepInstance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);

        fragmentManagerSave = getSupportFragmentManager();

        if (findViewById(R.id.fragment_container_save) != null){
            if (savedInstanceState != null){
                return;
            }
            fragmentManagerSave.beginTransaction()
                    .add(R.id.fragment_container_save, new SaveFragment())
                    .commit();
        }

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        photoPath = intent.getStringExtra(BMP_PATH);
        userBitmapSave = BitmapFactory.decodeFile(photoPath);

        // Get the breed ID
        bID = intent.getIntExtra(INT_BID, -1);
        sID = intent.getIntExtra(INT_SID, -1);

        nameString = intent.getStringExtra(STR_NAME);

        // Get instance of database
        appDb = AppDatabase.getInstance(this);

        if (sID != -1){
            sheepInstance = appDb.sheepDAO().findBySID(sID);
        }
    }

    public void save(String name){
        if (sID != -1){
            sheepInstance.setSheepName(name);
        } else {
            sheepInstance = new Sheep(bID, photoPath, name);
        }
        dbRunnable runnable = new dbRunnable(SAVE);
        new Thread(runnable).start();
    }

    public void recycler(){
        Toast.makeText(getApplicationContext(), "saved", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, RecyclerActivity.class);
        startActivity(intent);
    }

    public void release(){
        dbRunnable runnable = new dbRunnable(REMOVE);
        new Thread(runnable).start();
    }

    public void home(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    class dbRunnable implements Runnable{
        private int OPTION;
        dbRunnable(int OPTION){
            this.OPTION = OPTION;
        }

        @Override
        public void run() {
            switch (OPTION){
                case (REMOVE):
                    if (sID != -1) {
                        appDb.sheepDAO().deleteSheep(sheepInstance);
                    }
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            home();
                        }
                    });
                    break;
                case (SAVE):
                    if (sID != -1) {
                        appDb.sheepDAO().updateSheep(sheepInstance);
                    } else {
                        appDb.sheepDAO().inertSheep(sheepInstance);
                    }
                    // Posts message to main thread
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            recycler();
                        }
                    });
                    break;
            }
        }
    }

    public String getNameString() {
        return nameString;
    }
}