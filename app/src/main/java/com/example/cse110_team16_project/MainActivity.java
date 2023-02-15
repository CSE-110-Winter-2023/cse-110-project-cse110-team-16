package com.example.cse110_team16_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Intent intent = new Intent(this,CompassActivity.class);
        // startActivity(intent);
    }

    public void goToAddHomeLocations(View view) {
        Intent intent = new Intent(this, AddHomeLocations.class);
        startActivity(intent);
    }

    public void goToAddLabels(View view) {
        Intent intent = new Intent(this, AddLabels.class);
        startActivity(intent);
    }
}