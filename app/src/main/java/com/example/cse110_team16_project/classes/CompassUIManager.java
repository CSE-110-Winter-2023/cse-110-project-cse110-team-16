package com.example.cse110_team16_project.classes;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LifecycleOwner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class CompassUIManager {
    private static final String TAG = CompassUIManager.class.getSimpleName();
    //FOR DEBUGGING

    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private Future<Void> future;

    private static final float SCREEN_PERCENTAGE = .475f;
    Activity activity;
    private List<TextView> homeLabels;
    private List<Float> homeDirection;
    //private List<ImageView> homeIcons;
    private final int[] defaultColors = {0xFF000000,
            0xFF003300,0xFF000033}; //black, kashmir green, midnight blue
    //Initial colors of the text/icons for the first three homes
    //TODO: DECLARATION ABOVE IS DISGUSTING, MAKE TOLERABLE LATER
    private final ImageView compass;
    private final TextView sampleHome;
    private final UserTracker userTracker;
    private final HomeDirectionUpdater homeDirectionUpdater;

    public CompassUIManager(@NonNull UserTracker userTracker, @NonNull HomeDirectionUpdater homeDirectionTracker,
                            ImageView compass, TextView sampleHome){
        this.activity = userTracker.getActivity();
        this.userTracker = userTracker;
        this.homeDirectionUpdater = homeDirectionTracker;
        this.compass = compass;
        this.sampleHome = sampleHome;

        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int dpRadius = (int)(dpWidth/SCREEN_PERCENTAGE);
        //potentially use to make app work with different screen sizes

        populateHomeIcons(homeDirectionTracker.getHomes());

        userTracker.getUserDirection().observe((LifecycleOwner) activity, direction ->
                this.future = backgroundThreadExecutor.submit(() ->
                {
                    updateUI(direction, homeDirectionTracker.
                        getLastKnownHomeDirectionsFromUser());
                    return null;
                }));


    }

    //create views on the UI for each home
    public void populateHomeIcons(List<Home> homes){

        homeLabels = new ArrayList<>(homes.size());
        for(int i = 0; i < homes.size(); i++){
            TextView tv = new TextView(activity);

        }
    }

    //update the position of the view representing a home on the compass to the correct direction

    //params View to update, direction the home is from user in degrees from absolute north
    public void updateIconDirection(TextView tv, Float homeDirection){
    }


    public void updateUI(float userDirection, List<Float> homeDirections){
        //if(Math.abs(userDirection - compass.getRotation()) < .15f) return;

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
            //compass.startAnimation(ra);
            compass.setRotation(-userDirection);
            //Log.d(TAG,Float.toString(compass.getRotation()));
        });


    }
}
