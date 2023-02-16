package com.example.cse110_team16_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class SanityActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sanity);

        TextView textViewX = findViewById(R.id.textX);
        TextView textViewY = findViewById(R.id.textY);

        SharedPreferences preferences = getSharedPreferences("HomeLoc", MODE_PRIVATE);
        float yourFamX = preferences.getFloat("yourFamX", 0.0F);
        Log.d("TESTPref", yourFamX + "");
        float yourFamY = preferences.getFloat("yourFamY", 0.0F);
        Log.d("TESTPref", yourFamY + "");

        textViewX.setText(yourFamX + "");
        textViewY.setText(yourFamY + "");

    }
}