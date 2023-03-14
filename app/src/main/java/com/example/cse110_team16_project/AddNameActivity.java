package com.example.cse110_team16_project;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.example.cse110_team16_project.classes.CoordinateClasses.SCLocation;
import com.example.cse110_team16_project.classes.Misc.Constants;
import com.example.cse110_team16_project.Database.SCLocationDatabase;
import com.example.cse110_team16_project.Database.SCLocationRepository;

import java.util.UUID;

public class AddNameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_name);

        var input = (EditText) findViewById(R.id.YourNameField);
        input.setOnEditorActionListener((view, actionId, event) -> {
            // If the event isn't "done" or "enter", do nothing.
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN))) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                input.clearFocus();
            }
            return false;
        });
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

        Log.d("AddName", "name is " + name + ".");
        Log.d("AddName", "private code is " + private_code + ".");
        SCLocationRepository repo = new SCLocationRepository(SCLocationDatabase.
                provide(this).getDao());
        SCLocation newUser = new SCLocation(name,public_code);
        repo.upsertRemote(newUser,private_code);
        finish();
        startActivity(new Intent(this, CompassActivity.class));
    }
}