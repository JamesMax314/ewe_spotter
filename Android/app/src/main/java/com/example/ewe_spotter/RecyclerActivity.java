package com.example.ewe_spotter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("My Flock");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
            case (R.id.home):
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case (R.id.gallery):
                Intent intentGal = new Intent(this, MainActivity.class);
                intentGal.putExtra(MainActivity.CAMERA_OPTION, MainActivity.GALLERY);
//                intentGal.setFlags(intentGal.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intentGal);
                break;
            case (R.id.camera):
                Intent intentCam = new Intent(this, MainActivity.class);
                intentCam.putExtra(MainActivity.CAMERA_OPTION, MainActivity.CAMERA);
//                intentCam.setFlags(intentCam.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intentCam);
                break;
            case (R.id.herd):
                Intent intentHerd = new Intent(this, RecyclerActivity.class);
                startActivity(intentHerd);
        }

        return super.onOptionsItemSelected(item);
    }
}