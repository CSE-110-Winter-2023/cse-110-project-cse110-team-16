package com.example.cse110_team16_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class AddLabels extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_labels);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveProfile();
    }

    public void saveProfile() {
        SharedPreferences preferences = getSharedPreferences("FamHomeLabel", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        TextView famView = findViewById(R.id.famHomeLabelField);
        editor.putString("famLabel", famView.getText().toString());

        editor.apply();
    }

    public void onSubmitClicked(View view) {
        finish();
    }
}