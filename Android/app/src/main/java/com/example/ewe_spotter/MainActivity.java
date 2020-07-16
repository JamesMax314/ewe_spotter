package com.example.ewe_spotter;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import org.tensorflow.lite.support.model.Model;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static FragmentManager fragmentManager;
    String currentPhotoPath;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    /** Create the File where the photo should go */
    File photoFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();

        if (findViewById(R.id.fragment_container) != null){
            if (savedInstanceState != null){
                return;
            }
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, new FirstFragment())
                    .commit();
        }
    }

    public void requestImageActivity(){
        // Called when cam button pressed; stages intent for image capture
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        //if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            Log.e(TAG, "Error occurred while creating the File");
        }
        Log.i(TAG, "File generated");
        // Continue only if the File was successfully created
        if (photoFile != null) {
            // Uniform Resource Identifier and allows the app image data file:
            // Android/data/com.example.ewe_spotter/files/Pictures/
            // to be temporarily shared
            Uri photoURI = FileProvider.getUriForFile(this,
                    "com.example.ewe_spotter.fileprovider",
                    photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container,
                    new SecondFragment(), "sf")
                    .addToBackStack(null)
                    .commit();
        }
    }


    public void process_image() throws IOException {
        EditImage imageView = findViewById(R.id.cropBox);
        Bitmap userPrepBitmap = imageView.outputImage();
        Log.i(TAG, "Bitmap expported");
        SheepNet network = new SheepNet(this, SheepNet.Device.CPU, 1);
        int idIndex = network.recognizeImage(userPrepBitmap);
        Log.i(TAG, "process_image: " + idIndex);
    }

    public String getCurrentPhotoPath(){
        return currentPhotoPath;
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}