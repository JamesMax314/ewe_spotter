package com.example.ewe_spotter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.io.File;
import java.util.Calendar;

public class SaveActivity extends AppCompatActivity {
    private static final String TAG = "SaveActivity";

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

//    Add
    private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);

        fragmentManagerSave = getSupportFragmentManager();
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.save);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

//        Advert setup
        MobileAds.initialize(this, "\n" +
                "ca-app-pub-5033666133057413/3834548122");
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        AdRequest request = new AdRequest.Builder().build();
        interstitialAd.loadAd(request);
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLeftApplication() {
                recyclerSave();
            }

            @Override
            public void onAdClosed() {
                recyclerSave();
            }
        });
    }

    public void save(String name){
        if (sID != -1){
            sheepInstance.setSheepName(name);
        } else {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            String date = dateFormat.format(calendar.getTime());
            sheepInstance = new Sheep(bID, photoPath, name, date);
        }
        dbRunnable runnable = new dbRunnable(SAVE);
        new Thread(runnable).start();
    }

    public void loadAdd(){
        if (interstitialAd.isLoaded()) {
            interstitialAd.show();
        } else {
            Log.d(TAG, "The interstitial hasn't loaded yet.");
            recyclerSave();
        }
    }

    public void recyclerSave(){
        Toast.makeText(getApplicationContext(), "saved", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, RecyclerActivity.class);
        startActivity(intent);
    }

    public void release(){
        dbRunnable runnable = new dbRunnable(REMOVE);
        new Thread(runnable).start();
    }

    public void recyclerRelease(){
        Toast.makeText(getApplicationContext(), "deleted", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, RecyclerActivity.class);
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
                    File file = new File(photoPath);
                    file.delete();
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            recyclerRelease();
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
                            recyclerSave();
                        }
                    });
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case (R.id.home):
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case (R.id.gallery):
                Intent intentGal = new Intent(this, MainActivity.class);
                intentGal.putExtra(MainActivity.CAMERA_OPTION, MainActivity.GALLERY);
                intentGal.setFlags(intentGal.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intentGal);
                break;
            case (R.id.camera):
                Intent intentCam = new Intent(this, MainActivity.class);
                intentCam.putExtra(MainActivity.CAMERA_OPTION, MainActivity.CAMERA);
                intentCam.setFlags(intentCam.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intentCam);
                break;
            case (R.id.herd):
                Intent intentHerd = new Intent(this, RecyclerActivity.class);
                startActivity(intentHerd);
        }

        return super.onOptionsItemSelected(item);
    }
    public String getNameString() {
        return nameString;
    }
}