package com.example.ewe_spotter;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static final String PATH_MESSAGE = "com.example.ewe_spotter.path_method";
    public static FragmentManager fragmentManager;
    String currentPhotoPath;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int PICK_IMAGE = 2;
    /** Create the File where the photo should go */
    File photoFile = null;
    public Bitmap userPrepBitmap;

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

    public void requestGalleryActivity() {
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            Log.e(TAG, "Error occurred while creating the File");
        }
        Log.i(TAG, "File generated");
        // Continue only if the File was successfully created
        if (photoFile != null) {
            Intent getPictureIntent = new Intent();
            getPictureIntent.setType("image/*");
            getPictureIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(
                    Intent.createChooser(getPictureIntent, "Select Picture"), PICK_IMAGE);
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
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case (REQUEST_IMAGE_CAPTURE):
                    startEditImage(currentPhotoPath);
                    break;
                case (PICK_IMAGE):
                    Log.i(TAG, "onActivityResult: pick image");
                    Uri mediaUri = data.getData();
                    assert mediaUri != null;
                    String mediaPath = mediaUri.getPath();
                    try {
                        InputStream inputStream = getBaseContext().getContentResolver().
                                openInputStream(mediaUri);
                        FileOutputStream out = new FileOutputStream(currentPhotoPath);
                        byte[] buffer = new byte[1024];
                        assert inputStream != null;
                        int len = inputStream.read(buffer);
                        while (len != -1) {
                            out.write(buffer, 0, len);
                            len = inputStream.read(buffer);
                        }
                        startEditImage(currentPhotoPath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        }

    }

    private void startEditImage(String photoPath){
        Intent intent = new Intent(this, EditImageActivity.class);
        intent.putExtra(PATH_MESSAGE, photoPath);
        startActivity(intent);
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