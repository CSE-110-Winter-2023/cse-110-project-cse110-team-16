package com.example.cse110_team16_project;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import static com.example.cse110_team16_project.classes.Misc.Constants.APP_REQUEST_CODE;

import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.cse110_team16_project.Database.SCLocationDatabase;
import com.example.cse110_team16_project.Database.SCLocationRepository;
import com.example.cse110_team16_project.classes.AbsoluteDirectionUpdater;
import com.example.cse110_team16_project.classes.CoordinateClasses.Coordinates;
import com.example.cse110_team16_project.classes.DeviceInfo.DeviceTracker;
import com.example.cse110_team16_project.classes.DeviceInfo.OrientationService;
import com.example.cse110_team16_project.classes.DistanceUpdater;
import com.example.cse110_team16_project.classes.UI.UserUIAdapter;
import com.example.cse110_team16_project.classes.Units.Degrees;
import com.example.cse110_team16_project.classes.Units.Meters;
import com.example.cse110_team16_project.classes.Units.Radians;
import com.example.cse110_team16_project.classes.ViewModels.CompassViewModel;
import com.example.cse110_team16_project.classes.CoordinateClasses.SCLocation;
import com.example.cse110_team16_project.classes.UI.CompassUIManager;
import com.example.cse110_team16_project.classes.Misc.Constants;
import com.example.cse110_team16_project.classes.GPSStatus;
import com.example.cse110_team16_project.classes.UserLocationSynch;


import java.util.ArrayList;
import java.util.List;
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

    private UserUIAdapter userUIAdapter;
    private GPSStatus gpsstatus;
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

        List<String> publicCodes = repo.getLocalPublicCodes();
        List<SCLocation> friendSCLocations = new ArrayList<>();
        List<String> friendLabels = new ArrayList<>(); //Friend Labels
        MutableLiveData<List<SCLocation>> liveFriendSCLoc = new MutableLiveData<>();
        liveFriendSCLoc.setValue(friendSCLocations);
        LiveData<Coordinates> userCoord = deviceTracker.getCoordinates();
        for(String s: publicCodes){
            friendSCLocations.add(repo.getRemote(s));
        }
        for(SCLocation singular: friendSCLocations){
            friendLabels.add(singular.getLabel());
        }

        AbsoluteDirectionUpdater absoluteDirectionUpdater = new AbsoluteDirectionUpdater(this,
                liveFriendSCLoc, userCoord);

        LiveData<List<Degrees>> friendOrientations
                = absoluteDirectionUpdater.getLastKnownEntityDirectionsFromUser(); //Friend Orientations

        DistanceUpdater distanceUpdater = new DistanceUpdater(this, liveFriendSCLoc, userCoord);

        LiveData<List<Meters>> friendDistance = distanceUpdater.getLastKnownEntityDistancesFromUser();
        List<Double> friendDistInDouble = new ArrayList<>();

        for(Meters distance: friendDistance.getValue()){
            friendDistInDouble.add(distance.getMeters());
        }
        MutableLiveData<List<Double>> liveFriendDist = new MutableLiveData<>(); //Friend Distances
        liveFriendDist.setValue(friendDistInDouble);

        LiveData<Radians> userOrientation = deviceTracker.getOrientation(); //User Orientation

        repo = new SCLocationRepository(SCLocationDatabase.
                provide(this).getDao());
        loadUserInfo();

        viewModel = setupViewModel();
        deviceTracker = new DeviceTracker(this);
        compassUIManager = new CompassUIManager(this, deviceTracker.getOrientation(), findViewById(R.id.compassRing));

        gpsstatus = new GPSStatus(deviceTracker.getLocation(), findViewById(R.id.gpsLight), findViewById(R.id.gpsText));
        try{
            gpsstatus.trackGPSStatus();
        } catch (Exception e){
            e.printStackTrace();
        }

        locationSyncer = new UserLocationSynch(deviceTracker.getCoordinates(),
                new SCLocation(userLabel,public_code),private_code, this, repo);
        compassUIManager = new CompassUIManager(this, deviceTracker.getOrientation(),
                findViewById(R.id.compassRing));

        userUIAdapter = new UserUIAdapter(this, liveFriendDist, friendOrientations
        ,friendLabels, userOrientation);


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

    public void setGpsStatus(GPSStatus gpsstatus) {
        this.gpsstatus = gpsstatus;
    }



}