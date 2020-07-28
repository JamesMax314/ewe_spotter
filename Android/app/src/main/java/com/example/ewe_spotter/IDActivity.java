package com.example.ewe_spotter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.io.IOException;
import java.util.Objects;

public class IDActivity extends AppCompatActivity {
    private static final String TAG = "IDActivity";
    public Bitmap userBitmap;
    public String photoPath;


    private Handler mainHandler = new Handler();
    public int outId;
    public Activity thisActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_i_d);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        photoPath = intent.getStringExtra(EditImageActivity.BMP_MESSAGE);
        userBitmap = BitmapFactory.decodeFile(photoPath);
        thisActivity = this;
        netRunnable runnable = new netRunnable(userBitmap);
        new Thread(runnable).start();
    }

    public void initSave(int id){
        Intent intent = new Intent(this, SaveActivity.class);
        intent.putExtra(SaveActivity.BMP_PATH, photoPath);
        intent.putExtra(SaveActivity.INT_BID, id);
        startActivity(intent);
    }

    class netRunnable implements Runnable{
        private Bitmap inputBmp;
        netRunnable(Bitmap inputBmp){
            this.inputBmp = inputBmp;
        }

        @Override
        public void run() {
            try {
                final int idIndex = process_image(inputBmp);
                // Posts message to main thread
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        initSave(idIndex);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private int process_image(Bitmap bmp) throws IOException {
            Log.i(TAG, "Bitmap expported");
            SheepNet network = new SheepNet(thisActivity, SheepNet.Device.CPU, 4);
            int idIndex = network.recognizeImage(bmp);
            Log.i(TAG, "process_image: " + idIndex);
            return idIndex;
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
}