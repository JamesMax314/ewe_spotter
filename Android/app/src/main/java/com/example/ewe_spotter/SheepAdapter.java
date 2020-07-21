package com.example.ewe_spotter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;




public class SheepAdapter extends RecyclerView.Adapter<SheepAdapter.ViewHolder> {

    List<Sheep> sheepItemList;
    Context context;
    Activity activity;

    public SheepAdapter(List<Sheep>sheepItemList, Activity activity)
    {
        this.sheepItemList = sheepItemList;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view,
                parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        context = parent.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Sheep sheepI = sheepItemList.get(position);
        Resources res = activity.getResources();

        holder.nameView.setText(sheepI.getSheepName());

        holder.setPhotoPath(sheepI.getPhotoPath());

        int breedID = sheepI.getBID();
        holder.setbID(breedID);
        holder.breedView.setText(res.getTextArray(R.array.sheep_names)[breedID]);

        holder.setsID(sheepI.getSID());

        Bitmap bmp = BitmapFactory.decodeFile(sheepI.getPhotoPath());
        holder.sheepImageView.setImageBitmap(bmp);
    }

    @Override
    public int getItemCount() {
        return sheepItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        ImageView sheepImageView;
        TextView nameView;
        TextView breedView;
        CardView cv;
        CardView cardExt;
        Button arrowButton;
        Button penButton;

        String photoPath;
        int bID;
        int sID;

        public ViewHolder(View itemView)
        {
            super(itemView);
            sheepImageView = (ImageView)itemView.findViewById(R.id.sheepImageView);
            nameView = (TextView)itemView.findViewById(R.id.nameView);
            breedView = (TextView)itemView.findViewById(R.id.breedView);
            cv = (CardView)itemView.findViewById(R.id.cardView);

            cardExt = (CardView)itemView.findViewById(R.id.cardExt);
            arrowButton = (Button)itemView.findViewById(R.id.arrowButton);
            arrowButton.setOnClickListener(this);

            penButton = (Button)itemView.findViewById(R.id.penButton);
            penButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case (R.id.arrowButton):
                    if (cardExt.getVisibility() == View.GONE){
                        TransitionManager.beginDelayedTransition(cardExt, new AutoTransition());
                        cardExt.setVisibility(View.VISIBLE);
                        arrowButton.setBackgroundResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
                    } else {
                        TransitionManager.beginDelayedTransition(cardExt, new AutoTransition());
                        cardExt.setVisibility(View.GONE);
                        arrowButton.setBackgroundResource(
                                R.drawable.ic_baseline_keyboard_arrow_down_24);
                    }
                    break;
                case (R.id.penButton):
                    Intent intent = new Intent(activity, SaveActivity.class);
                    intent.putExtra(SaveActivity.BMP_PATH, photoPath);
                    intent.putExtra(SaveActivity.INT_BID, bID);
                    intent.putExtra(SaveActivity.INT_SID, sID);
                    activity.startActivity(intent);
            }
        }

        public void setPhotoPath(String photoPath){
            this.photoPath = photoPath;
        }

        public void setbID(int bID) {
            this.bID = bID;
        }

        public void setsID(int sID) {
            this.sID = sID;
        }
    }
}
