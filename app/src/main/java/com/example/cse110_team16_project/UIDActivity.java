package com.example.cse110_team16_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.cse110_team16_project.classes.Misc.Constants;

public class UIDActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uidactivity);

        // Set text for UID_display
        SharedPreferences sharedPref = this.getSharedPreferences(Constants.SharedPreferences.user_info, Context.MODE_PRIVATE);
        TextView UID_display = findViewById(R.id.UID_display);
        UID_display.setText(sharedPref.getString(Constants.SharedPreferences.public_code, ""));

        // Set text for UID_Btn
        Button UID_Btn = findViewById(R.id.UID_Btn);
        if (UID_display.getVisibility() == View.INVISIBLE){
            UID_Btn.setText("Show UID");
        } else {
            UID_Btn.setText("Hide UID");
        }
    }

    public void onUIDBtnClicked(View view) {
        TextView UID_display = findViewById(R.id.UID_display);
        Button UID_Btn = findViewById(R.id.UID_Btn);
        if (UID_display.getVisibility() == View.INVISIBLE){
            UID_display.setVisibility(View.VISIBLE);
            UID_Btn.setText("Hide UID");
        } else {
            UID_display.setVisibility(View.INVISIBLE);
            UID_Btn.setText("Show UID");
        }
    }

    public void onAdvanceBtnClicked(View view) {
        startActivity(new Intent(this, CompassActivity.class));
    }
}