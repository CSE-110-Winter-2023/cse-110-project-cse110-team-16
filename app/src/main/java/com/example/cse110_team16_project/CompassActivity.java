package com.example.cse110_team16_project;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import static com.example.cse110_team16_project.classes.Constants.APP_REQUEST_CODE;

import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.example.cse110_team16_project.Room.AppDatabase;
import com.example.cse110_team16_project.classes.CompassUIManager;
import com.example.cse110_team16_project.classes.Coordinates;
import com.example.cse110_team16_project.classes.Home;
import com.example.cse110_team16_project.classes.HomeDirectionUpdater;
import com.example.cse110_team16_project.classes.User;
import com.example.cse110_team16_project.classes.UserTracker;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CompassActivity extends AppCompatActivity {
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
        handleLocationPermission();
    }

    private void finishOnCreate(){
        loadHomes();
        this.user = new User();
        runOnUiThread(() -> {
            userTracker = new UserTracker(this, user);
            homeDirectionUpdater = new HomeDirectionUpdater(this, homes, user);
            manager = new CompassUIManager(this, user, homeDirectionUpdater, findViewById(R.id.compassRing),
                    findViewById(R.id.sampleHome));
        });
    }

    private void loadHomes(){
        /*
        AppDatabase appDatabase = Room.databaseBuilder(this,AppDatabase.class,AppDatabase.NAME)
                .fallbackToDestructiveMigration().build();
        homes = appDatabase.homeDao().loadAllHomes();
         */

        //TODO: Verify correct name parameter
        SharedPreferences labelPreferences = getSharedPreferences("FamHomeLabel",Context.MODE_PRIVATE);
        SharedPreferences locationPreferences = getSharedPreferences("famHomeLoc", Context.MODE_PRIVATE);

        homes = new ArrayList<>();

        //TODO: Verify correct prefs
        //all arrays should be same length
        String[] prefLabelStrings = new String[]{"famLabel"};
        String[] prefLatStrings = new String[]{"yourFamX"};
        String[] prefLongStrings = new String[]{"yourFamY"};

        for (int i = 0; i < prefLabelStrings.length; i++) {
            String label = labelPreferences.getString(prefLabelStrings[i], null);
            if(label == null) continue;
            double lat = locationPreferences.getFloat(prefLatStrings[i], 0.0f);
            double longitude = locationPreferences.getFloat(prefLongStrings[i], 0.0f);
            Coordinates parentCoordinates = new Coordinates(lat,longitude);

            Home home = new Home(parentCoordinates, label);
            homes.add(home);
        }
    }

    public List<Home> getHomes(){
        return homes;
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

    public User getUser(){return user;}
    @Override
    protected void onPause(){
        super.onPause();
        if(userTracker != null) userTracker.unregisterListeners();
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(userTracker != null) {
            userTracker.registerListeners();
            SharedPreferences preferences = getSharedPreferences("HomeLoc", MODE_PRIVATE);
            float mockDir = preferences.getFloat("mockDirection", -1.0F);
            userTracker.mockUserDirection(mockDir);
        }
    }

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
    }
}