package com.example.ewe_spotter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static final String PATH_MESSAGE = "com.example.ewe_spotter.path_method";
    public static final String CAMERA_OPTION = "com.example.ewe_spotter.";
    public static final int NULL_OPTION = 0;
    public static final int CAMERA = 1;
    public static final int GALLERY = 2;
    public static FragmentManager fragmentManager;
    String currentPhotoPath;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int PICK_IMAGE = 2;
    /**
     * Create the File where the photo should go
     */
    File photoFile = null;
    public Bitmap userPrepBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        int initOption = intent.getIntExtra(CAMERA_OPTION, NULL_OPTION);
        if (initOption == CAMERA){
            requestImageActivity();
        } else if (initOption == GALLERY){
            requestGalleryActivity();
        }

        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, new HomeFragment())
                    .commit();
        }

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
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

    public void requestRecyclerActivity(){
        Intent intentHerd = new Intent(this, RecyclerActivity.class);
        startActivity(intentHerd);
    }

    public void requestImageActivity() {
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

    private void startEditImage(String photoPath) {
        Intent intent = new Intent(this, EditImageActivity.class);
        intent.putExtra(PATH_MESSAGE, photoPath);
        startActivity(intent);
    }

    public String getCurrentPhotoPath() {
        return currentPhotoPath;
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
            case (R.id.camera):
                requestImageActivity();
                break;
            case (R.id.gallery):
                requestGalleryActivity();
                break;
            case (R.id.herd):
                requestRecyclerActivity();
        }

        return super.onOptionsItemSelected(item);
    }
}