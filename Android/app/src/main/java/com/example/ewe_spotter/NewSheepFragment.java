package com.example.ewe_spotter;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.Objects;

public class NewSheepFragment extends Fragment implements View.OnClickListener {
    private Button save_button;
    private Button release_button;
    public TextView breedText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_sheep, container, false);
        save_button = view.findViewById(R.id.save_button);
        save_button.setOnClickListener(this);
        release_button = view.findViewById(R.id.release_button);
        release_button.setOnClickListener(this);

        breedText = view.findViewById(R.id.breedText);
        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getImage(requireView());
        Resources res = getResources();
        int breedId = ((SaveImageActivity) requireActivity()).intId;
        breedText.setText(res.getTextArray(R.array.sheep_names)[breedId]);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.save_button:
                EditText nameEdit = requireView().findViewById(R.id.sheepName);
                String nameText = nameEdit.getText().toString();
                ((SaveImageActivity) requireActivity()).save(nameText);
                // TODO: 18/07/20 update database
                // TODO: 18/07/20 start recycler view
                break;
            case R.id.release_button:
                ((SaveImageActivity) requireActivity()).release();
                // TODO: 18/07/20 remove from database
                break;
        }
    }

    private void getImage(View view){
        Bitmap sheepImg = ((SaveImageActivity) requireActivity()).userBitmapSave;
        ImageView sheepShow = view.findViewById(R.id.sheepView);
        sheepShow.setImageBitmap(sheepImg);
    }
}
