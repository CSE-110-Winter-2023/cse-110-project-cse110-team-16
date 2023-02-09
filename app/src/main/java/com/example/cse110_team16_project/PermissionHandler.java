package com.example.cse110_team16_project;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionHandler {
    static final int APP_REQUEST_CODE = 110;
    Activity activity;
    public PermissionHandler(Activity activity){
        this.activity = activity;
    }

    //https://stackoverflow.com/questions/40142331/how-to-request-location-permission-at-runtime
    public boolean handleLocationPermission() {
        if(ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED) {
            return true;
        }
        if(ActivityCompat.shouldShowRequestPermissionRationale(activity, android.Manifest.permission.ACCESS_FINE_LOCATION)){
            new AlertDialog.Builder(activity)
                    .setTitle(R.string.location_request_rationale_title)
                    .setMessage(R.string.location_request_rationale_text)
                    .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                        //Prompt the user once explanation has been shown
                        ActivityCompat.requestPermissions(activity,
                                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                                APP_REQUEST_CODE);
                    })
                    .create()
                    .show();
        }
        else {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    APP_REQUEST_CODE);
        }
        return (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED);
    }

    //
    public void handlePermission(String permission, String rationaleTitle, String rationaleText) {
        if(ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED) {
            return;
        }
        if(ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)){
            new AlertDialog.Builder(activity)
                    .setTitle(rationaleTitle)
                    .setMessage(rationaleText)
                    .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                        //Prompt the user once explanation has been shown
                        ActivityCompat.requestPermissions(activity,
                                new String[]{permission},
                                APP_REQUEST_CODE);
                    })
                    .create()
                    .show();
        }
        else {
            ActivityCompat.requestPermissions(activity,
                    new String[]{permission},
                    APP_REQUEST_CODE);
        }
    }
}
