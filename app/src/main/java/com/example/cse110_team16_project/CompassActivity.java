package com.example.cse110_team16_project;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import static com.example.cse110_team16_project.classes.Constants.APP_REQUEST_CODE;

import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import com.example.cse110_team16_project.classes.CompassUIManager;
import com.example.cse110_team16_project.classes.CoordinateEntity;
import com.example.cse110_team16_project.classes.Coordinates;
import com.example.cse110_team16_project.classes.Degrees;
import com.example.cse110_team16_project.classes.RelativeDirectionUpdater;
import com.example.cse110_team16_project.classes.SCLocation;
import com.example.cse110_team16_project.classes.DeviceTracker;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CompassActivity extends AppCompatActivity {
    private final ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private LiveData<List<CoordinateEntity>> coordinateEntities;
    private SCLocation user;
    private DeviceTracker deviceTracker;
    private RelativeDirectionUpdater relativeDirectionUpdater;
    private CompassUIManager compassUIManager;

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
        loadEntities();

        //TODO
        Coordinates userCoordinates = new Coordinates(0,0);
        String userLabel = "User";
        String publicCode = "";

        this.user = new SCLocation(userCoordinates,userLabel,publicCode);
        runOnUiThread(() -> {
            deviceTracker = new DeviceTracker(this);
            relativeDirectionUpdater = new RelativeDirectionUpdater(this, coordinateEntities, deviceTracker.getCoordinates(), deviceTracker.getOrientation());
            compassUIManager = new CompassUIManager(this, deviceTracker.getOrientation(), findViewById(R.id.compassRing));
        });
    }

    private void loadEntities(){
        //TODO
        coordinateEntities = new MutableLiveData<>(new ArrayList<>());
        //sus
    }

    public LiveData<List<CoordinateEntity>> getCoordinateEntities(){
        return coordinateEntities;
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

    public SCLocation getUser(){return user;}

    @Override
    protected void onPause(){
        super.onPause();
        if(deviceTracker != null) deviceTracker.unregisterListeners();
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(deviceTracker != null) {
            deviceTracker.registerListeners();
            SharedPreferences preferences = getSharedPreferences("HomeLoc", MODE_PRIVATE);
            Degrees mockDir = new Degrees(preferences.getFloat("mockDirection", -1.0F));
            if(mockDir.getDegrees() < 0) deviceTracker.disableMockUserDirection();
            else deviceTracker.mockUserDirection(mockDir);
        }
    }

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
    }

    public CompassUIManager getCompassUIManager() {
        return compassUIManager;
    }

    public RelativeDirectionUpdater getRelativeDirectionUpdater() { return relativeDirectionUpdater; }

    public DeviceTracker getDeviceTracker() { return this.deviceTracker; }

    public void onBackClicked(View view) {
        startActivity(new Intent(this, AddHomeLocations.class));
    }
}