package com.example.ewe_spotter;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;

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
}