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
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class EditImageFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "SecondFragment";

    EditImageView editImageView;

    Button identify_button;
    ImageButton rotate_left;
    ImageButton rotate_right;
    ImageButton reset;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_image, container, false);

        editImageView = view.findViewById(R.id.cropBox);

        identify_button = view.findViewById(R.id.identify_button);
        rotate_left = view.findViewById(R.id.rotate_left);
        rotate_right = view.findViewById(R.id.rotate_right);
        reset = view.findViewById(R.id.reset);

        identify_button.setOnClickListener(this);
        rotate_left.setOnClickListener(this);
        rotate_right.setOnClickListener(this);
        reset.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.identify_button:
                ((EditImageActivity) requireActivity()).initInfo();
                break;
            case R.id.rotate_left:
                editImageView.rotate(EditImageView.LEFT);
                break;
            case R.id.rotate_right:
                editImageView.rotate(EditImageView.RIGHT);
                break;
            case R.id.reset:
                editImageView.reset();
                break;
        }
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setPic();
    }

    public void setPic() {
        // Get the dimensions of the View
        String currentPhotoPath = ((EditImageActivity) requireActivity()).getCurrentPhotoPath();
        EditImageView imageView = requireActivity().findViewById(R.id.cropBox);

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