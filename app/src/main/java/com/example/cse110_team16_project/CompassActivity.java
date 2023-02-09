package com.example.cse110_team16_project;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.Manifest;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import android.app.AlertDialog;
import android.os.Bundle;

import com.example.cse110_team16_project.classes.AppDatabase;
import com.example.cse110_team16_project.classes.CompassUIManager;
import com.example.cse110_team16_project.classes.Home;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CompassActivity extends AppCompatActivity {

    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private Future<Void> future;
    private List<Home> homes;
    AppDatabase appDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        appDatabase = Room.databaseBuilder(this,AppDatabase.class,AppDatabase.NAME).fallbackToDestructiveMigration().build();

        this.future = backgroundThreadExecutor.submit(() -> {
            homes = appDatabase.homeDao().loadAllHomes();
            return null;
        });
        //CompassUIManager manager = CompassUIManager(this,)
    }

    public AppDatabase getAppDatabase(){
        return appDatabase;
    }
}