package com.example.ewe_spotter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;

public class EditImageActivity extends AppCompatActivity {
    private static final String TAG = "EditImageActivity";
    public static final String BMP_MESSAGE = "com.example.ewe_spotter.bmp_method";
    public String photoPath;
    public static FragmentManager fragmentManagerEI;
    public Bitmap userPrepBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);
        fragmentManagerEI = getSupportFragmentManager();

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        photoPath = intent.getStringExtra(MainActivity.PATH_MESSAGE);

        if (findViewById(R.id.fragment_container_edit_image) != null){
            if (savedInstanceState != null){
                return;
            }
            fragmentManagerEI.beginTransaction()
                    .add(R.id.fragment_container_edit_image, new SecondFragment())
                    .commit();
        }
    }

    public void initInfo(){
        EditImage imageView = findViewById(R.id.cropBox);
        userPrepBitmap = imageView.outputImage();
        try (FileOutputStream out = new FileOutputStream(photoPath)) {
            userPrepBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (IOException e) {
            e.printStackTrace();
        }


        Intent intent = new Intent(this, IDActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.putExtra(BMP_MESSAGE, photoPath);
        startActivity(intent);
    }

    public String getCurrentPhotoPath(){
        return photoPath;
    }
}