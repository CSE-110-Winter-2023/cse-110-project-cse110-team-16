package com.example.cse110_team16_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import java.util.UUID;

public class AddNameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_name);
    }

    public void onSubmitClicked(View view) {
        String name = findViewById(R.id.YourNameField).toString();
        String private_code = UUID.randomUUID().toString();
        String public_code = UUID.randomUUID().toString();

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString("label", name);
        editor.putString("private_code", private_code);
        editor.putString("public_code", public_code);

        editor.apply();
    }
}