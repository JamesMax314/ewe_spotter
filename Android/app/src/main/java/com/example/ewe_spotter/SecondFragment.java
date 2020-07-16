package com.example.ewe_spotter;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.Objects;

public class SecondFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "SecondFragment";
    private Button identify_button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_second, container, false);
        identify_button = view.findViewById(R.id.identify_button);
        identify_button.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.identify_button:
                Log.i(TAG, "onClick: ");
                try {
                    ((MainActivity) requireActivity()).process_image();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setPic();
    }

    public void setPic() {
        // Get the dimensions of the View
        String currentPhotoPath = ((MainActivity) requireActivity()).getCurrentPhotoPath();
        EditImage imageView = requireActivity().findViewById(R.id.cropBox);

        Resources res = getResources();
        float width = res.getDimension(R.dimen.thumbnail);
        float height = res.getDimension(R.dimen.thumbnail);

        int targetW = (int) width; //imageView.getWidth();
        int targetH = (int) height; //imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        imageView.setImageBitmap(bitmap);
    }
}