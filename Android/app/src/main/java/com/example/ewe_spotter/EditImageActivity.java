package com.example.ewe_spotter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

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
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        photoPath = intent.getStringExtra(MainActivity.PATH_MESSAGE);

        if (findViewById(R.id.fragment_container_edit_image) != null){
            if (savedInstanceState != null){
                return;
            }
            fragmentManagerEI.beginTransaction()
                    .add(R.id.fragment_container_edit_image, new EditImageFragment())
                    .commit();
        }
    }

    public void initInfo(){
        EditImageView imageView = findViewById(R.id.cropBox);
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
//                intentGal.setFlags(intentGal.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intentGal);
                break;
            case (R.id.camera):
                Intent intentCam = new Intent(this, MainActivity.class);
                intentCam.putExtra(MainActivity.CAMERA_OPTION, MainActivity.CAMERA);
//                intentCam.setFlags(intentCam.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intentCam);
                break;
            case (R.id.herd):
                Intent intentHerd = new Intent(this, RecyclerActivity.class);
                startActivity(intentHerd);
        }

        return super.onOptionsItemSelected(item);
    }
}