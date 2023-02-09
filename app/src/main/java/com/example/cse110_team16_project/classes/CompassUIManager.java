package com.example.cse110_team16_project.classes;

import android.app.Activity;
import android.graphics.Color;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class CompassUIManager {
    Activity activity;
    private List<TextView> homeLabels;
    private List<Float> homeDirection;
    private List<ImageView> homeIcons;
    private List<String> defaultColors = new ArrayList<>(Arrays.asList("#FF000000",
            "#FF003300","#FF000033")); //black, kashmir green, midnight blue
    //TODO: DECLARATION ABOVE IS DISGUSTING, MAKE TOLERABLE LATER
    private ImageView compass;

    CompassUIManager(Activity activity, List<Home> homes, ImageView compass){
        this.activity = activity;
        this.compass = compass;
        populateHomes(homes);
    }

    public void populateHomes(List<Home> homes){
        for(int i = 0; i < homes.size(); i++){
            //homeLabels.add(homes.get(i).getLabel());

        }
    }

}
