package com.example.cse110_team16_project.classes;

import android.app.Activity;
import android.graphics.Color;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Arrays;


public class CompassUIManager {
    Activity activity;
    private User user;
    private ArrayList<Home> homes;
    private ArrayList<ImageView> homeIcons;
    private ArrayList<String> colors = new ArrayList<String>(Arrays.asList("#FF000000",
            "#FF003300","#FF000033")); //black, kashmir green, midnight blue
    private ImageView compass;

    CompassUIManager(Activity activity, ArrayList<Home> homes, ImageView compass){
        this.activity = activity;
        this.user = new User(activity);
        this.homes = homes;
        this.compass = compass;
    }


}
