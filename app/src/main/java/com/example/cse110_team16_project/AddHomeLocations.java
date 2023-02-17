package com.example.cse110_team16_project;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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

    public static float[] storeCoords(String coords) {
        coords = coords.substring(1,coords.length() - 1);
        String[] arrOfStr = coords.split(", ", 2);

        float xCoord = Float.parseFloat(arrOfStr[0]);
        float yCoord = Float.parseFloat(arrOfStr[1]);

        float[] toRet = new float[]{xCoord, yCoord};
        return toRet;

    }

    public void saveProfile() {
        SharedPreferences preferences = getSharedPreferences("HomeLoc", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        TextView homeView = findViewById(R.id.yourHomeField);
        TextView famView = findViewById(R.id.yourFamHomeField);
        TextView friendView = findViewById(R.id.yourBestFriendHomeField);

        if(famView.getText().toString().isEmpty() == false) {
            float[] yourFamFloats = this.storeCoords(famView.getText().toString());
            editor.putFloat("yourFamX", yourFamFloats[0]);
            editor.putFloat("yourFamY", yourFamFloats[1]);
        } else {
            editor.putFloat("yourFamX", 0F);
            editor.putFloat("yourFamY", 0F);
        }

        editor.apply();
    }

    public void onSubmitClicked(View view) {
        saveProfile();
        finish();
        Intent intent = new Intent(this, AddLabels.class);
        startActivity(intent);
    }

    public void onSanityClicked(View view) {
        saveProfile();

        Intent intent = new Intent(this, SanityActivity.class);
        startActivity(intent);
    }

    public void onMockClicked(View view) {
        SharedPreferences preferences = getSharedPreferences("HomeLoc", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        TextView mockView = findViewById(R.id.mockDirectionText);
        if(mockView.getText().toString().isEmpty() == false) {
            float mockFloat = Float.parseFloat(mockView.getText().toString());
            editor.putFloat("mockDirection", mockFloat);
        } else {
            editor.putFloat("mockDirection", -1F);
        }

        editor.apply();

//        saveProfile();
        finish();
//        Intent intent = new Intent(this, CompassActivity.class);
//        startActivity(intent);
    }
}