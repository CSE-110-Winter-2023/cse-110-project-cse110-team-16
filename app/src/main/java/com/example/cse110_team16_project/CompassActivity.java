package com.example.cse110_team16_project;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import static com.example.cse110_team16_project.classes.Misc.Constants.APP_REQUEST_CODE;

import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.cse110_team16_project.Database.SCLocationDatabase;
import com.example.cse110_team16_project.Database.SCLocationRepository;
import com.example.cse110_team16_project.classes.UI.CompassUIManager;
import com.example.cse110_team16_project.Database.SCLocationDatabase;
import com.example.cse110_team16_project.Database.SCLocationRepository;
import com.example.cse110_team16_project.classes.CompassUIManager;
import com.example.cse110_team16_project.classes.CompassViewModel;
import com.example.cse110_team16_project.classes.Misc.Constants;
import com.example.cse110_team16_project.Units.Degrees;
import com.example.cse110_team16_project.classes.GPSstatus;
import com.example.cse110_team16_project.classes.SCLocation;
import com.example.cse110_team16_project.classes.DeviceTracker;
import com.example.cse110_team16_project.classes.UserLocationSynch;


import java.security.spec.ECField;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CompassActivity extends AppCompatActivity {
    private final ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();

    private String public_code;
    private String private_code;
    private String userLabel;
    private DeviceTracker deviceTracker;
    private CompassUIManager compassUIManager;
    private CompassViewModel viewModel;


    private GPSstatus gpsstatus;
    private UserLocationSynch locationSyncer;
    private SCLocationRepository repo;


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
        var db = SCLocationDatabase.provide(this);
        var dao = db.getDao();
        this.repo = new SCLocationRepository(dao);

        repo = new SCLocationRepository(SCLocationDatabase.
                provide(this).getDao());
        loadUserInfo();

        viewModel = setupViewModel();
        deviceTracker = new DeviceTracker(this);
        compassUIManager = new CompassUIManager(this, deviceTracker.getOrientation(), findViewById(R.id.compassRing));
        gpsstatus = new GPSstatus(deviceTracker.getLocation(), findViewById(R.id.gpsLight), findViewById(R.id.gpsText));
        try{
            gpsstatus.trackGPSStatus();
        } catch (Exception e){
            e.printStackTrace();
        }

        locationSyncer = new UserLocationSynch(deviceTracker.getCoordinates(),
                new SCLocation(userLabel,public_code),private_code, this, repo);
        compassUIManager = new CompassUIManager(this, deviceTracker.getOrientation(),
                findViewById(R.id.compassRing));
    }


    private void loadUserInfo(){

        SharedPreferences sharedPref = this.getSharedPreferences(Constants.SharedPreferences.user_info, Context.MODE_PRIVATE);
        userLabel = sharedPref.getString(Constants.SharedPreferences.label, "");
        public_code = sharedPref.getString(Constants.SharedPreferences.public_code, "");
        private_code = sharedPref.getString(Constants.SharedPreferences.private_code, "");
    }

    private CompassViewModel setupViewModel() {
        return new ViewModelProvider(this).get(CompassViewModel.class);
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
                finishOnCreate();

            }
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }

    @Override
    protected void onPause(){
        super.onPause();
        if(deviceTracker != null) deviceTracker.unregisterListeners();
    }

    @Override
    protected void onResume(){
        super.onResume();
        if (this.repo != null) Log.d("Number of locations","" + repo.getLocalPublicCodes().size());
        if(deviceTracker != null) {
            deviceTracker.registerListeners();
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

    public DeviceTracker getDeviceTracker() { return this.deviceTracker; }

    public void onUIDClicked(View view) {
        startActivity(new Intent(this, UIDActivity.class));
    }

    public void ontoListClicked(View view) {
        startActivity(new Intent(this, ListActivity.class));
    }

    public void setGpsstatus(GPSstatus gpsstatus) {
        this.gpsstatus = gpsstatus;
    }
}