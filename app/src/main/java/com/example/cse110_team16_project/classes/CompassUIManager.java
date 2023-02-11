package com.example.cse110_team16_project.classes;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LifecycleOwner;

import java.util.ArrayList;
import java.util.List;


public class CompassUIManager {
    private static final String TAG = CompassUIManager.class.getSimpleName();
    //FOR DEBUGGING

    private static final float SCREEN_PERCENTAGE = .475f;
    Activity activity;
    private List<TextView> homeLabels;
    private List<Float> homeDirection;
    //private List<ImageView> homeIcons;
    private final int[] defaultColors = {0xFF000000,
            0xFF003300,0xFF000033}; //black, kashmir green, midnight blue
    //TODO: DECLARATION ABOVE IS DISGUSTING, MAKE TOLERABLE LATER
    private final ImageView compass;
    private final TextView sampleHome;
    private final UserTracker userTracker;
    private final HomeDirectionTracker homeDirectionTracker;
    ConstraintLayout.LayoutParams layoutParams;

    public CompassUIManager(UserTracker userTracker, HomeDirectionTracker homeDirectionTracker,
                            ImageView compass, TextView sampleHome){
        this.activity = userTracker.getActivity();
        this.userTracker = userTracker;
        this.homeDirectionTracker = homeDirectionTracker;
        this.compass = compass;
        this.sampleHome = sampleHome;
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int dpRadius = (int)(dpWidth/SCREEN_PERCENTAGE);
        layoutParams = (ConstraintLayout.LayoutParams) sampleHome.getLayoutParams();
        layoutParams.circleRadius = dpRadius;

        populateHomeIcons(homeDirectionTracker.getHomes());

        userTracker.getUserDirection().observe((LifecycleOwner) activity, direction ->
                updateUI(userTracker.getUserDirection().getValue(), homeDirectionTracker.
                        getLastKnownHomeDirectionsFromUser()));
        userTracker.getUserCoordinates().observe((LifecycleOwner) activity, location ->
            updateUI(userTracker.getUserDirection().getValue(), homeDirectionTracker.
                    getLastKnownHomeDirectionsFromUser()));

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

    public void updateIconDirection(TextView tv, Float homeDirection){
        layoutParams.circleAngle = homeDirection;
        activity.runOnUiThread(() -> {
            tv.setLayoutParams(layoutParams);
        });
    }

    public void updateUI(float userDirection, List<Float> homeDirections){
        updateCompassDirection(userDirection);
        updateHomeIconDirections(homeDirections);
    }
    public void updateHomeIconDirections(List<Float> homeDirections){
        for(int i = 1; i < homeLabels.size(); i++){
                updateIconDirection(homeLabels.get(i), homeDirections.get(i));
        }
    }

    //given userDirection in degrees, changes compass to face correct direction
    public void updateCompassDirection(float userDirection){
        activity.runOnUiThread(() -> {
            compass.setRotation(-userDirection);
        });
        //Log.d(TAG,Float.toString(compass.getRotation()));
    }
}
