package com.example.cse110_team16_project;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import static com.example.cse110_team16_project.AddNameActivity.mockURLKey;
import static com.example.cse110_team16_project.AddNameActivity.urlFileName;
import static com.example.cse110_team16_project.classes.Misc.Constants.APP_REQUEST_CODE;

import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.cse110_team16_project.Database.SCLocationDatabase;
import com.example.cse110_team16_project.Database.SCLocationRepository;
import com.example.cse110_team16_project.classes.Updaters.AbsoluteDirectionUpdater;
import com.example.cse110_team16_project.classes.DeviceInfo.DeviceTracker;
import com.example.cse110_team16_project.classes.Updaters.DistanceUpdater;
import com.example.cse110_team16_project.classes.LiveDataListMerger;
import com.example.cse110_team16_project.classes.Updaters.ScreenDistanceUpdater;
import com.example.cse110_team16_project.classes.CoordinateClasses.SCLocation;
import com.example.cse110_team16_project.classes.UI.CompassUIManager;
import com.example.cse110_team16_project.classes.Misc.Constants;
import com.example.cse110_team16_project.classes.GPSStatus;
import com.example.cse110_team16_project.classes.UserLocationSync;


public class CompassActivity extends AppCompatActivity {

    private String public_code;
    private String private_code;
    private String userLabel;
    private DeviceTracker deviceTracker;
    private CompassUIManager compassUIManager;
    private GPSStatus gpsstatus;
    private SCLocationRepository repo;

    private RepositoryMediator mediator;
    private LiveDataListMerger<SCLocation> locations = null;

    private DistanceUpdater distanceUpdater;
    private AbsoluteDirectionUpdater absoluteDirectionUpdater;
    private ScreenDistanceUpdater screenDistanceUpdater;



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
        setupRepository();
        mediator = new RepositoryMediator(repo);
        loadUserInfo();

        deviceTracker = new DeviceTracker(this);
        deviceTracker.registerListeners(); //battery life is a social construct
        compassUIManager = new CompassUIManager(this, deviceTracker.getOrientation(), findViewById(R.id.compassRing));
        gpsstatus = new GPSStatus(deviceTracker.getLocation(), findViewById(R.id.gpsLight), findViewById(R.id.gpsText));
        UserLocationSync locationSync = new UserLocationSync(deviceTracker.getCoordinates(),
                new SCLocation(userLabel,public_code),private_code, this, repo);
    }


    private void setupRepository() {
        var db = SCLocationDatabase.provide(this);
        var dao = db.getDao();
        SharedPreferences sharedPrefUrl = this.getSharedPreferences(urlFileName, Context.MODE_PRIVATE);
        String APIUrl = sharedPrefUrl.getString(mockURLKey, "");
        repo = new SCLocationRepository(dao,APIUrl);
    }
    private void loadUserInfo(){
        SharedPreferences sharedPref = this.getSharedPreferences(Constants.SharedPreferences.user_info, Context.MODE_PRIVATE);
        userLabel = sharedPref.getString(Constants.SharedPreferences.label, "");
        public_code = sharedPref.getString(Constants.SharedPreferences.public_code, "");
        private_code = sharedPref.getString(Constants.SharedPreferences.private_code, "");
    }

    private void setupUpdaters() {
        distanceUpdater = new DistanceUpdater(this,locations.getMergedList(),deviceTracker.getCoordinates());
        absoluteDirectionUpdater = new AbsoluteDirectionUpdater(this,locations.getMergedList(),deviceTracker.getCoordinates());
        screenDistanceUpdater = new ScreenDistanceUpdater(this);
        screenDistanceUpdater.startObserve(distanceUpdater.getLastKnownEntityDistancesFromUser());
    }
    private void setupUI(){

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
        //if(deviceTracker != null) deviceTracker.unregisterListeners();
        if(gpsstatus != null) gpsstatus.stopTracking();
    }

    @Override
    protected void onResume(){
        super.onResume();

        if (this.repo != null){
            Log.d("Number of locations","" + repo.getLocalPublicCodes().size());
            if (locations == null || getIntent().getBooleanExtra("locationsChanged",false)){
                Log.d("CompassActivity","Locations changed!");
                if(locations != null) locations.stopUpdating();
                locations = new LiveDataListMerger<>(mediator.refreshSCLocations(repo.getLocalPublicCodes()));
                setupUpdaters();
                setupUI();
            }
        }
        if(gpsstatus != null) gpsstatus.trackGPSStatus();
    }

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        setIntent(intent);
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

}