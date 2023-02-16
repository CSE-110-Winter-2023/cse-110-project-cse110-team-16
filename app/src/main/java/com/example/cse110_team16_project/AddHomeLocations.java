package com.example.cse110_team16_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class AddHomeLocations extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_home_locations);
        // loadProfile();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveProfile();
    }

//    public void loadProfile() {
//        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
//
//        String home = preferences.getString("home", "");
//        TextView homeView = findViewById(R.id.yourHomeField);
//
//        String famHome = preferences.getString("famHome", "");
//        TextView famHomeView = findViewById(R.id.yourFamHomeField);
//
//        String friendHome = preferences.getString("friendHome", "");
//        TextView friendHomeView = findViewById(R.id.yourBestFriendHomeField);
//
//        homeView.setText(home);
//        famHomeView.setText(famHome);
//        friendHomeView.setText(friendHome);
//    }

    public float[] storeCoords(String coords) {
        coords = coords.substring(1,coords.length() - 1);
        String[] arrOfStr = coords.split(", ", 2);

        float xCoord = Float.parseFloat(arrOfStr[0]);
        float yCoord = Float.parseFloat(arrOfStr[1]);

        float[] toRet = new float[]{xCoord, yCoord};
        return toRet;

    }

    public void saveProfile() {
        SharedPreferences preferences = getSharedPreferences("famHomeLoc", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        TextView homeView = findViewById(R.id.yourHomeField);
        TextView famView = findViewById(R.id.yourFamHomeField);
        TextView friendView = findViewById(R.id.yourBestFriendHomeField);
        float[] yourFamFloats = this.storeCoords(famView.getText().toString());
        editor.putFloat("yourFamX", yourFamFloats[0]);
        editor.putFloat("yourFamY", yourFamFloats[1]);

        Log.d("TESTFLOAT", yourFamFloats[0] + "");
        Log.d("TESTFLOAT", yourFamFloats[1] + "");

//        Intent sendingIntent = new Intent(this, CompassActivity.class);
//        sendingIntent.putExtra("mockDirection", 0);
//        startActivity(sendingIntent);

        editor.apply();
    }

    public void onSubmitClicked(View view) {
        finish();
    }

    public void onSanityClicked(View view) {
        saveProfile();

        Intent intent = new Intent(this, SanityActivity.class);
        startActivity(intent);
    }
}