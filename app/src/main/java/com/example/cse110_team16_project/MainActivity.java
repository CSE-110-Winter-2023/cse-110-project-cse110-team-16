package com.example.cse110_team16_project;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences preferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        Intent intent;
        if(preferences.contains("private_code")) {
            intent = new Intent(this, UIDActivity.class);
        }
        else {
            intent = new Intent(this, AddNameActivity.class);
            intent.putExtra("isNewUser",true);
        }
        finish();
        startActivity(intent);

    }

    /*public void goToAddHomeLocations(View view) {
        Intent intent = new Intent(this, AddHomeLocations.class);
        startActivity(intent);
    }

    public void goToAddLabels(View view) {
        Intent intent = new Intent(this, AddLabels.class);
        startActivity(intent);
    }*/
}