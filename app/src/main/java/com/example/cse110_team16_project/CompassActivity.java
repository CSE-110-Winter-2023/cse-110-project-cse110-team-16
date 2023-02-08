package com.example.cse110_team16_project;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.Manifest;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.os.Bundle;

public class CompassActivity extends AppCompatActivity {
    static final int APP_REQUEST_CODE = 110;

    //https://stackoverflow.com/questions/40142331/how-to-request-location-permission-at-runtime
    public void checkLocationPermission() {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED) {
            return;
        }
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.location_request_rationale_title))
                    .setMessage(getString(R.string.location_request_rationale_text))
                    .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                        //Prompt the user once explanation has been shown
                        ActivityCompat.requestPermissions(CompassActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                APP_REQUEST_CODE);
                    })
                    .create()
                    .show();
        }
        else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    APP_REQUEST_CODE);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        checkLocationPermission();
    }
}