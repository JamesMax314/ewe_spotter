package com.example.ewe_spotter;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;

/* impliments is like defining a c++ class in a geadder with abstract functions so all objects
/* that use the implementation can be used in the same way*/
public class FirstFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "First Fragment";

    /** private so can only be accessed by instance of class */
    private ImageButton cam_button;
    private ImageButton gallary_button;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first, container, false);
        cam_button = view.findViewById(R.id.cam_button);
        cam_button.setOnClickListener(this);
        gallary_button = view.findViewById(R.id.gallary_button);
        gallary_button.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cam_button:
                Log.i(TAG, "onClick: cam_button");
                ((MainActivity) requireActivity()).requestImageActivity();
                break;
            case R.id.gallary_button:
                Log.i(TAG, "onClick: gallary_button");
                ((MainActivity) requireActivity()).requestGalleryActivity();
        }
    }




}