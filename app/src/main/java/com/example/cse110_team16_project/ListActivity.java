package com.example.cse110_team16_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.cse110_team16_project.classes.CoordinateClasses.SCLocation;
import com.example.cse110_team16_project.classes.Misc.Constants;
import com.example.cse110_team16_project.classes.ListViewModel;

import com.example.cse110_team16_project.classes.SCLocationsAdapter;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {
    // This annotation will cause an IDE error if you try to access recyclerView outside of a test.
    // It can also be set to "otherwise = VisibleForTesting.PRIVATE" to allow access from this.
    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public RecyclerView recyclerView;
    private final List<String> changes = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the content view to be the main layout.
        setContentView(R.layout.activity_list);

        // we are avoiding storing viewModel and adapter in fields we access later. This is
        // because fields are mutable, and mutable state is hard to reason about. They could be
        // uninitialized (null), which would mean someone editing this code would have to know
        // which setup methods need to be called before others. This is a recipe for bugs.
        //
        // Instead, we do direct by-parameter dependency inversion/injection.
        // This way, it is impossible to call the setup methods in the wrong order.

        var viewModel = setupViewModel();
        var adapter = setupAdapter(viewModel);

        setupViews(viewModel, adapter);



        // Set text for UID_display
        SharedPreferences sharedPref = this.getSharedPreferences(Constants.SharedPreferences.user_info, Context.MODE_PRIVATE);
        TextView UID_display = findViewById(R.id.uid_text);
        UID_display.setText(sharedPref.getString(Constants.SharedPreferences.public_code, ""));

        // Set text for UID_Btn
        Button UID_Btn = findViewById(R.id.show_uid_btn);
        if (UID_display.getVisibility() == View.INVISIBLE){
            UID_Btn.setText("Show UID");
        } else {
            UID_Btn.setText("Hide UID");
        }
    }

    private ListViewModel setupViewModel() {
        return new ViewModelProvider(this).get(ListViewModel.class);
    }

    @NonNull
    private SCLocationsAdapter setupAdapter(ListViewModel viewModel) {
        SCLocationsAdapter adapter = new SCLocationsAdapter();
        adapter.setHasStableIds(true);
        adapter.setOnSCLocationDeleteClickListener(location -> onSCLocationDeleteClicked(location, viewModel));
        viewModel.getSCLocations().observe(this, adapter::setSCLocations);
        return adapter;
    }

    private void setupViews(ListViewModel viewModel, SCLocationsAdapter adapter) {
        setupToolbar();
        setupRecycler(adapter);
        setupInput(viewModel);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
    }

    // Override the @VisibleForTesting annotation to allow access from this (and only this) method.
    @SuppressLint("RestrictedApi")
    private void setupRecycler(SCLocationsAdapter adapter) {
        // We store the recycler view in a field _only_ because we will want to access it in tests.
        recyclerView = findViewById(R.id.recycler_main);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);
    }

    private void setupInput(ListViewModel viewModel) {
        var input = (EditText) findViewById(R.id.input_new_location_code);
        input.setOnEditorActionListener((view, actionId, event) -> {
            // If the event isn't "done" or "enter", do nothing.
            if (actionId != EditorInfo.IME_ACTION_DONE &&
                    (event == null || (event.getKeyCode() != KeyEvent.KEYCODE_ENTER || event.getAction() != KeyEvent.ACTION_DOWN))) {
                return false;
            }


            var code = input.getText().toString();
            SCLocation newLocation = viewModel.getOrCreateSCLocation(code,this);
            if(newLocation != null){
                input.setText("");
                storeChange(newLocation);
            }
            return true;
        });
    }

    /* Mediation Logic */

    
    public void onSCLocationDeleteClicked(SCLocation location, ListViewModel viewModel) {
        // Delete the location
        Log.d("SCLocationsAdapter", "Deleted location " + location.getPublicCode());
        viewModel.delete(location);
        storeChange(location);
    }

    public void storeChange(SCLocation location){
        if(!changes.remove(location.getPublicCode())) changes.add(location.getPublicCode());
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(this,CompassActivity.class);
        intent.putExtra("locationsChanged", !changes.isEmpty());
        startActivity(intent);
    }

    public void onUIDBtnClicked(View view) {
        TextView UID_display = findViewById(R.id.uid_text);
        Button UID_Btn = findViewById(R.id.show_uid_btn);
        if (UID_display.getVisibility() == View.INVISIBLE){
            UID_display.setVisibility(View.VISIBLE);
            UID_Btn.setText("Hide UID");
        } else {
            UID_display.setVisibility(View.INVISIBLE);
            UID_Btn.setText("Show UID");
        }
    }

    public void onAdvanceBtnClicked(View view) {
        onBackPressed();
    }
}