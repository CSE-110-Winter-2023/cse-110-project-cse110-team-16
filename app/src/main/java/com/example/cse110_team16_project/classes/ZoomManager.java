package com.example.cse110_team16_project.classes;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class ZoomManager {
    private static final int ZOOMERROR = -1;
    private static final int ZOOMDEFAULT = 4;


    private Activity activity;
    private int zoomLevel;

    private SharedPreferences sharedPref;

    public ZoomManager(Activity activity){
        this.activity = activity;
        this.sharedPref = activity.getSharedPreferences("user_info", Context.MODE_PRIVATE);
        setUpZoomSharedPref();
        this.zoomLevel = readZoomLevel();
    }

    private void setUpZoomSharedPref(){
        if (sharedPref.contains("zoomLevel")){
            this.zoomLevel = sharedPref.getInt("zoomLevel", ZOOMERROR);
        }
        else{
            var editor = sharedPref.edit();
            editor.putInt("zoomLevel", ZOOMDEFAULT);
            editor.apply();
        }
    }

    private int readZoomLevel(){
        return sharedPref.getInt("zoomLevel", -1);
    }

    private void setZoomButtonVisibility(){

    }
}
