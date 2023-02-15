package com.example.cse110_team16_project;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import android.content.pm.PackageManager;
import android.os.Bundle;

import com.example.cse110_team16_project.Room.AppDatabase;
import com.example.cse110_team16_project.classes.CompassUIManager;
import com.example.cse110_team16_project.classes.Home;
import com.example.cse110_team16_project.classes.HomeDirectionUpdater;
import com.example.cse110_team16_project.classes.User;
import com.example.cse110_team16_project.classes.UserTracker;


import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CompassActivity extends AppCompatActivity {
    private static final int APP_REQUEST_CODE = 110;
    private final ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private List<Home> homes;
    private User user;
    private UserTracker userTracker;
    private HomeDirectionUpdater homeDirectionUpdater;
    private CompassUIManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_compass);
        backgroundThreadExecutor.submit(this::handleLocationPermission);
    }

    private void finishOnCreate(){
        loadHomes();
        this.user = new User();
        runOnUiThread(() -> {
            userTracker = new UserTracker(this, user);
            homeDirectionUpdater = new HomeDirectionUpdater(this, homes, user);
            manager = new CompassUIManager(this, user, homeDirectionUpdater, findViewById(R.id.compassRing),
                    findViewById(R.id.sampleHome));


            Bundle extras = getIntent().getExtras();
            if(extras != null){
                userTracker.mockUserDirection(extras.getFloat("mockDirection"));
            }
        });
    }

    private void loadHomes(){
        AppDatabase appDatabase = Room.databaseBuilder(this,AppDatabase.class,AppDatabase.NAME)
                .fallbackToDestructiveMigration().build();
        homes = appDatabase.homeDao().loadAllHomes();
    }
    private void handleLocationPermission(){
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            // You can use the API that requires the permission.
            finishOnCreate();
        } else {
            // You can directly ask for the permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    APP_REQUEST_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == APP_REQUEST_CODE) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 &&
                    grantResults[0] == PERMISSION_GRANTED) {
                backgroundThreadExecutor.submit(this::finishOnCreate);

            }
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }

    @Override
    protected void onPause(){
        super.onPause();
        if(userTracker != null) userTracker.unregisterListeners();
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(userTracker != null) userTracker.registerListeners();
    }

}