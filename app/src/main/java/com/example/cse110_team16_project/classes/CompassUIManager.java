package com.example.cse110_team16_project.classes;

import android.app.Activity;
import android.graphics.Color;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LifecycleOwner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class CompassUIManager {
    private static final float SCREEN_PERCENTAGE = .475f;
    Activity activity;
    private List<TextView> homeLabels;
    private List<Float> homeDirection;
    //private List<ImageView> homeIcons;
    private int[] defaultColors = {0xFF000000,
            0xFF003300,0xFF000033}; //black, kashmir green, midnight blue
    //TODO: DECLARATION ABOVE IS DISGUSTING, MAKE TOLERABLE LATER
    private ImageView compass;
    private TextView sampleHome;
    private LocationEntityTracker tracker;
    ConstraintLayout.LayoutParams layoutParams;

    public CompassUIManager(LocationEntityTracker tracker, ImageView compass, TextView sampleHome){
        this.activity = tracker.getActivity();
        this.tracker = tracker;
        this.compass = compass;
        this.sampleHome = sampleHome;
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int dpRadius = (int)(dpWidth/SCREEN_PERCENTAGE);
        layoutParams = (ConstraintLayout.LayoutParams) sampleHome.getLayoutParams();
        layoutParams.circleRadius = dpRadius;

        populateHomeIcons(tracker.getHomes());

        tracker.getUserDirection().observe((LifecycleOwner) activity, direction ->{
            updateUI(tracker.getUserDirection().getValue(),tracker.getLastKnownDirectionHomesFromUser());
        });
        tracker.getUserCoordinates().observe((LifecycleOwner) activity, location ->{
            updateUI(tracker.getUserDirection().getValue(),tracker.getLastKnownDirectionHomesFromUser());
        });
    }

    public void populateHomeIcons(List<Home> homes){
        sampleHome.setLayoutParams(layoutParams);

        homeLabels = new ArrayList<>(homes.size());
        for(int i = 0; i < homes.size(); i++){
            TextView tv = sampleHome;
            tv.setId(View.generateViewId());
            tv.setText(homes.get(i).getLabel());
            tv.setTextColor(defaultColors[i]);
            homeLabels.add(tv);
        }
    }

    public void updateIcon(TextView tv, Float homeDirection){
        layoutParams.circleAngle = homeDirection;
        tv.setLayoutParams(layoutParams);
    }

    public void updateUI(float userDirection, List<Float> homeDirections){

        updateCompassDirection(userDirection);
        updateHomeIconDirections(homeDirections);
    }
    public void updateHomeIconDirections(List<Float> homeDirections){
        for(int i = 1; i < homeLabels.size(); i++){
            updateIcon(homeLabels.get(i),homeDirections.get(i));
        }
    }

    public void updateCompassDirection(float direction){
        compass.setRotation(direction);
    }
}
