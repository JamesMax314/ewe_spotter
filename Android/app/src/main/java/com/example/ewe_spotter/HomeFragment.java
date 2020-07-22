package com.example.ewe_spotter;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

/* impliments is like defining a c++ class in a geadder with abstract functions so all objects
/* that use the implementation can be used in the same way*/
public class HomeFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "First Fragment";

    /** private so can only be accessed by instance of class */
    private Button cam_button;
    private Button gallery_button;
    private Button herd_button;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        cam_button = view.findViewById(R.id.cam_button);
        cam_button.setOnClickListener(this);
        gallery_button = view.findViewById(R.id.gallary_button);
        gallery_button.setOnClickListener(this);
        herd_button = view.findViewById(R.id.herd_button);
        herd_button.setOnClickListener(this);
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
                break;
            case R.id.herd_button:
                Log.i(TAG, "onClick: Herd button");
                ((MainActivity) requireActivity()).requestRecyclerActivity();
        }
    }




}