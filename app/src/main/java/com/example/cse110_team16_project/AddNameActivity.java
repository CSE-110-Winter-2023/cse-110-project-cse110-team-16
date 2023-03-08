package com.example.cse110_team16_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.cse110_team16_project.classes.Constants;

import java.util.UUID;

public class AddNameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_name);
    }

    public void onSubmitClicked(View view) {
        EditText editName = findViewById(R.id.YourNameField);
        String name = editName.getText().toString();
        String private_code = UUID.randomUUID().toString();
        String public_code = UUID.randomUUID().toString();

        SharedPreferences sharedPref = this.getSharedPreferences(Constants.SharedPreferences.user_info, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(Constants.SharedPreferences.label, name);
        editor.putString(Constants.SharedPreferences.private_code, private_code);
        editor.putString(Constants.SharedPreferences.public_code, public_code);

        editor.apply();

        Log.d("ADDNAME", "name is " + name + ".");
        Log.d("ADDNAME", "private code is " + private_code + ".");

        startActivity(new Intent(this, UIDActivity.class));
    }
}