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

public class SaveFragment extends Fragment implements View.OnClickListener {
    public TextView breedText;
    public TextView infoText;
    public String nameText;
    private EditText nameEdit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_sheep, container, false);
        Button save_button = view.findViewById(R.id.save_button);
        save_button.setOnClickListener(this);
        Button release_button = view.findViewById(R.id.release_button);
        release_button.setOnClickListener(this);

        breedText = view.findViewById(R.id.breedText);
        infoText = view.findViewById(R.id.infoView);
        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getImage(requireView());
        Resources res = getResources();
        int breedId = ((SaveActivity) requireActivity()).bID;
        breedText.setText(res.getTextArray(R.array.sheep_names)[breedId]);
        infoText.setText(res.getTextArray(R.array.sheep_descriptions)[breedId]);
        nameEdit = requireView().findViewById(R.id.sheepName);

        String name = ((SaveActivity) requireActivity()).getNameString();
        if (name != null){
            setNameText(name);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.save_button:
                nameText = nameEdit.getText().toString();
                ((SaveActivity) requireActivity()).save(nameText);
                break;
            case R.id.release_button:
                ((SaveActivity) requireActivity()).release();
                break;
        }
    }

    private void getImage(View view){
        Bitmap sheepImg = ((SaveActivity) requireActivity()).userBitmapSave;
        ImageView sheepShow = view.findViewById(R.id.sheepView);
        sheepShow.setImageBitmap(sheepImg);
    }

    public void setNameText(String nameText) {
        this.nameText = nameText;
        nameEdit.setText(nameText);
    }
}
