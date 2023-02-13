package com.example.cse110_team16_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class AddHomeLocations extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_home_locations);
        loadProfile();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveProfile();
//        SharedPreferences pref = context.getSharedPreferences()
//        SharedPreferences.Editor editor = pref.edit();
//        editor.clear();
    }

    public void loadProfile() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);

        String home = preferences.getString("home", "");
        TextView homeView = findViewById(R.id.yourHomeField);

        String famHome = preferences.getString("famHome", "");
        TextView famHomeView = findViewById(R.id.yourFamHomeField);

        String friendHome = preferences.getString("friendHome", "");
        TextView friendHomeView = findViewById(R.id.yourBestFriendHomeField);

        homeView.setText(home);
        famHomeView.setText(famHome);
        friendHomeView.setText(friendHome);
    }

    public void saveProfile() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        TextView homeView = findViewById(R.id.yourHomeField);
        editor.putString("home", homeView.getText().toString());

        TextView famHomeView = findViewById(R.id.yourFamHomeField);
        editor.putString("famHome", famHomeView.getText().toString());

        TextView friendHomeView = findViewById(R.id.yourBestFriendHomeField);
        editor.putString("friendHome", friendHomeView.getText().toString());

        editor.apply();
    }

    public void onSubmitClicked(View view) {
        finish();
    }
}