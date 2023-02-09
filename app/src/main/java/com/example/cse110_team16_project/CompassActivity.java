package com.example.cse110_team16_project;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.Manifest;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.os.Bundle;

import com.example.cse110_team16_project.classes.CompassUIManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class CompassActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        //CompassUIManager manager = CompassUIManager(this,)
    }
}