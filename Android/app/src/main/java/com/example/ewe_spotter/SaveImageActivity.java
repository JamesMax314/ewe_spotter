package com.example.ewe_spotter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

public class SaveImageActivity extends AppCompatActivity {
    public String photoPath;
    public Bitmap userBitmapSave;
    private int intId;
    public static FragmentManager fragmentManagerSave;


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
    }
}