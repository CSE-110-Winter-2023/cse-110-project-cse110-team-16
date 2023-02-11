package com.example.cse110_team16_project;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.Manifest;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cse110_team16_project.classes.AppDatabase;
import com.example.cse110_team16_project.classes.CompassUIManager;
import com.example.cse110_team16_project.classes.Home;
import com.example.cse110_team16_project.classes.LocationEntityTracker;


import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CompassActivity extends AppCompatActivity {
    private static final int APP_REQUEST_CODE = 110;
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private Future<Void> future;
    private List<Home> homes;
    private LocationEntityTracker tracker;
    private CompassUIManager manager;
    private AppDatabase appDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        this.future = backgroundThreadExecutor.submit(() ->{
            appDatabase = Room.databaseBuilder(this,AppDatabase.class,AppDatabase.NAME)
                    .fallbackToDestructiveMigration().allowMainThreadQueries().build();
                //allowMainThreadQueries workaround for my incompetence at Async
            homes = appDatabase.homeDao().loadAllHomes();
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {
                // You can use the API that requires the permission.
                finishCreate();
            } else {
                // You can directly ask for the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                        APP_REQUEST_CODE);
            }
            return null;
        });
    }

    private void finishCreate(){
        tracker = new LocationEntityTracker(this,homes);
        manager = new CompassUIManager(tracker, findViewById(R.id.compassRing),
                 findViewById(R.id.sampleHome));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == APP_REQUEST_CODE) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 &&
                    grantResults[0] == PERMISSION_GRANTED) {
                finishCreate();
            }
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }

    @Override
    protected void onPause(){
        super.onPause();
        //tracker.unregisterListeners();
    }

    @Override
    protected void onResume(){
        super.onResume();
        //tracker.registerListeners();
    }
    public AppDatabase getAppDatabase(){
        return appDatabase;
    }
}