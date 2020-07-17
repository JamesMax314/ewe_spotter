package com.example.ewe_spotter;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.Objects;

public class NewSheepFragment extends Fragment implements View.OnClickListener {
    private Button save_button;
    private Button release_button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_sheep, container, false);
        save_button = view.findViewById(R.id.save_button);
        save_button.setOnClickListener(this);
        release_button = view.findViewById(R.id.release_button);
        release_button.setOnClickListener(this);
        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getImage(requireView());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.save_button:
                break;
            case R.id.release_button:
                break;
        }
    }

    private void getImage(View view){
        Bitmap sheepImg = ((MainActivity) requireActivity()).userPrepBitmap;
        ImageView sheepShow = view.findViewById(R.id.sheepView);
        sheepShow.setImageBitmap(sheepImg);
    }
}
