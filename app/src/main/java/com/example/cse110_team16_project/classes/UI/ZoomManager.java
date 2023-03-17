package com.example.cse110_team16_project.classes.UI;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cse110_team16_project.R;
import com.example.cse110_team16_project.classes.Misc.Converters;
import com.example.cse110_team16_project.classes.Updaters.ScreenDistanceUpdater;

import org.w3c.dom.Text;

public class ZoomManager {
    private static final int ZOOMERROR = -1;
    private static final int ZOOMDEFAULT = 2;


    private Activity activity;
    private int zoomLevel;
    private SharedPreferences sharedPref;
    private ScreenDistanceUpdater sdu;

    public ZoomManager(Activity activity, ScreenDistanceUpdater sdu){
        this.activity = activity;
        this.sharedPref = activity.getSharedPreferences("user_info", Context.MODE_PRIVATE);
        setUpZoomSharedPref();
        this.zoomLevel = readZoomLevel();
        this.sdu = sdu;
        setZoomButtonVisibility();
        setRing();
        sdu.setZoneSetting(zoomLevel);
    }

    private void setUpZoomSharedPref(){
        this.zoomLevel = sharedPref.getInt("zoomLevel", ZOOMDEFAULT);
        if(zoomLevel > 4 || zoomLevel < 1) zoomLevel = 2; //if stored shared preferences freaks out
    }

    public int getZoomLevel() {
        return this.zoomLevel;
    }

    public int readZoomLevel(){
        return sharedPref.getInt("zoomLevel", ZOOMDEFAULT);
    }

    private void setZoomButtonVisibility(){
        TextView zoomOutBtn = activity.findViewById(R.id.ZoomOut);
        TextView zoomInBtn = activity.findViewById(R.id.ZoomIn);

        if (zoomLevel == 4){
            zoomOutBtn.setClickable(false);
            zoomOutBtn.setAlpha(0.35f);
        }
        else if (zoomLevel == 1){
            zoomInBtn.setClickable(false);
            zoomInBtn.setAlpha(0.35f);
        }
        else{
            zoomOutBtn.setClickable(true);
            zoomInBtn.setClickable(true);
            zoomOutBtn.setAlpha(1f);
            zoomInBtn.setAlpha(1f);
        }
    }

    public void zoomIn(){
        putZoomLevel(--zoomLevel);
        sdu.setZoneSetting(zoomLevel);
        setZoomButtonVisibility();
        setRing();
    }

    public void zoomOut(){
        putZoomLevel(++zoomLevel);
        sdu.setZoneSetting(zoomLevel);
        setZoomButtonVisibility();
        setRing();
    }

    private void putZoomLevel(int level){
        var editor = sharedPref.edit();
        editor.putInt("zoomLevel", level);
        editor.apply();
    }

    private void setRingSize(ImageView iv, int width){
        var params = iv.getLayoutParams();
        params.height = Converters.dpToPixel(activity, width);
        params.width = Converters.dpToPixel(activity, width);
        iv.setLayoutParams(params);
    }

    private void setRing(){
        ImageView first = activity.findViewById(R.id.zeroToOneMilesRing);
        ImageView second = activity.findViewById(R.id.oneTotenMilesRing);
        ImageView third = activity.findViewById(R.id.tenTo500MilesRing);
        if(zoomLevel == 1){
            first.setVisibility(View.INVISIBLE);
            second.setVisibility(View.INVISIBLE);
            third.setVisibility(View.INVISIBLE);
        }
        else if (zoomLevel == 2){
            first.setVisibility(View.INVISIBLE);
            second.setVisibility(View.INVISIBLE);
            third.setVisibility(View.VISIBLE);
            setRingSize(third, 190);
        }
        else if (zoomLevel == 3){
            first.setVisibility(View.INVISIBLE);
            second.setVisibility(View.VISIBLE);
            third.setVisibility(View.VISIBLE);
            setRingSize(second, 127);
            setRingSize(third, 253);

        }
        else if (zoomLevel == 4){
            first.setVisibility(View.VISIBLE);
            second.setVisibility(View.VISIBLE);
            third.setVisibility(View.VISIBLE);
            setRingSize(first, 95);
            setRingSize(second, 190);
            setRingSize(third, 285);
        }
    }
}
