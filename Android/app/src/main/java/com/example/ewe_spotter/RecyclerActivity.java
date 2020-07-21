package com.example.ewe_spotter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import java.util.List;

public class RecyclerActivity extends AppCompatActivity {
    private static final String TAG = "RecyclerActivity";
    public AppDatabase appDb;
    private Handler mainHandler = new Handler();

    RecyclerView recyclerView;
    SheepAdapter sheepAdapter;
    ProgressBar progressBar;
    Button arrowButton;
    CardView cardExt;
    public List<Sheep> sheepList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);

        appDb = AppDatabase.getInstance(this);

        RecyclerActivity.recyclerRunnable runnable = new recyclerRunnable();
        new Thread(runnable).start();

        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void fillRecycler(){
        for (int i=0; i<sheepList.size(); i++){
            Sheep sheepIndv = sheepList.get(i);
            Log.i(TAG, "Sheep Names: " + sheepIndv.getSheepName());
        }

        progressBar.setVisibility(View.GONE);
        sheepAdapter = new SheepAdapter(sheepList, this);

        recyclerView = findViewById(R.id.sheepRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(sheepAdapter);
    }

    class recyclerRunnable implements Runnable{
        recyclerRunnable(){
        }

        @Override
        public void run() {
            sheepList = appDb.sheepDAO().getAllSheep();

            // Posts message to main thread
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    fillRecycler();
                }
            });
        }
    }
}