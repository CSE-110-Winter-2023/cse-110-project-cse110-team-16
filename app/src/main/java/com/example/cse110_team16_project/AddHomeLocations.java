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
        loadProfile();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveProfile();
    }

    public void loadProfile() {
        // SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences preferences = getSharedPreferences("HomeLoc", Context.MODE_PRIVATE);

//        String home = preferences.getString("home", "");
//        TextView homeView = findViewById(R.id.yourHomeField);

        // String famHome = preferences.getString("famHome", "");
        if(preferences.contains("yourFamX") && preferences.contains("yourFamY")) {
            float famHomeX = preferences.getFloat("yourFamX", 0.0F);
            float famHomeY = preferences.getFloat("yourFamY", 0.0F);
            TextView famHomeView = findViewById(R.id.yourFamHomeField);
            famHomeView.setText("(" + famHomeX + ", " + famHomeY + ")");
            //Disallows edits
            famHomeView.setEnabled(false);
        }

//        String friendHome = preferences.getString("friendHome", "");
//        TextView friendHomeView = findViewById(R.id.yourBestFriendHomeField);

        // homeView.setText(home);
        // friendHomeView.setText(friendHome);
    }

    public static float[] storeCoords(String coords){
        if(coords.charAt(0) == '('){
            coords = coords.substring(1,coords.length() - 1);
        }
        String[] arrOfStr = coords.split(",", 2);

        float xCoord = Float.parseFloat(arrOfStr[0]);
        float yCoord = Float.parseFloat(arrOfStr[1]);

        return new float[]{xCoord, yCoord};

    }

    public boolean saveProfile() {
        SharedPreferences preferences = getSharedPreferences("HomeLoc", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        TextView homeView = findViewById(R.id.yourHomeField);
        TextView famView = findViewById(R.id.yourFamHomeField);
        TextView friendView = findViewById(R.id.yourBestFriendHomeField);

        if(!famView.getText().toString().isEmpty()) {
            try {
                float[] yourFamFloats = this.storeCoords(famView.getText().toString());
                editor.putFloat("yourFamX", yourFamFloats[0]);
                editor.putFloat("yourFamY", yourFamFloats[1]);
            } catch(Exception e) {
                Utilities.showAlert(this, "Invalid location input.");
                return false;
            }
        } else {
            Utilities.showAlert(this, "Please enter at least one location");
            return false;
        }

        editor.apply();
        return true;
    }

    public void onSubmitClicked(View view) {
        if(saveProfile()) {
            finish();
            Intent intent = new Intent(this, AddLabels.class);
            startActivity(intent);
        }
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
        if(!mockView.getText().toString().isEmpty()) {
            float mockFloat = Float.parseFloat(mockView.getText().toString());
            editor.putFloat("mockDirection", mockFloat);
        } else {
            editor.putFloat("mockDirection", -1F);
        }

        editor.apply();

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras != null && extras.getBoolean("isNewUser")) return;
        finish();
        intent = new Intent(this, CompassActivity.class);
        startActivity(intent);
    }
}