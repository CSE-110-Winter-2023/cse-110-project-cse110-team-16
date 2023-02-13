package com.example.cse110_team16_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class AddHomeLocations extends AppCompatActivity {
    public double yourHomeX, yourHomeY;

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

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        String home = preferences.getString("home", "");
        double[] yourHomeArr = storeCoords(home);
        yourHomeX = yourHomeArr[0];
        yourHomeY = yourHomeArr[1];
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

    public double[] storeCoords(String coords) {
        coords = coords.substring(1,coords.length() - 1);
        String[] arrOfStr = coords.split(", ", 2);

//        if(arrOfStr.length < 2 ) {
//            // stop and pass error!
//            //Utilities.showAlert(this, "Must pass coordinates in the form (x-coord, y-coord)");
//            return null;
//        }

        double xCoord = Double.parseDouble(arrOfStr[0]);
        double yCoord = Double.parseDouble(arrOfStr[1]);

        double[] toRet = new double[]{xCoord, yCoord};
        return toRet;

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

        //Utilities.showAlert(this, "Dummy alert");

        //checkDataEntered();
        // storing coordinates of your home in public double var

        finish();


    }

//    private void checkDataEntered() {
//        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
//
//        // at least one field should be filled for AddHomeLoc
//        String home = preferences.getString("home", "");
//        String famHome = preferences.getString("famHome", "");
//        String friendHome = preferences.getString("friendHome", "");
//
//        if(!(home.length() > 0 || famHome.length() > 0 || friendHome.length() > 0)){
//            //pass error!
//            Toast t = Toast.makeText(this, "Please fill in at least one coordinate field!", Toast.LENGTH_LONG);
//            t.show();
//        }
//
//        // must provide in format (x-coord, y-coord)
//
//        // x-coord and y-coord must be valid doubles
//
//    }
}