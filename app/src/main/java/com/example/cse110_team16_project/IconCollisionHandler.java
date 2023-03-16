package com.example.cse110_team16_project;

import static com.example.cse110_team16_project.classes.Updaters.ScreenDistanceUpdater.LARGEST_RADIUS;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;

public class IconCollisionHandler {
    public static final int NO_COLLISION = 0;
    public static final int FIRST_VIEW_ON_TOP = 1;
    public static final int SECOND_VIEW_ON_TOP = 2;
    public static final int FIRST_VIEW_ON_LEFT = 3;
    public static final int SECOND_VIEW_ON_LEFT = 4;
    private static final int VERTICAL_ADJUST = 30;

    public static final int ADJUST_UP = 5;
    public static final int ADJUST_DOWN = 6;
    public static final int ADJUST_BOTH = 7;
    public static final int ADJUST_NONE = 7;


    /* Prioritize bumping each view up and down. If doing either would cause a view to leave circle or have negative radius,
     attempt to prevent.
     If return is positive, move Top up by that amount.
     If return negative, move Bottom down by the abs amount.
     */
    public static int adjustTopDownCollision(@NonNull View topView, int d1, @NonNull View bottomView, int d2){
        if(d2-VERTICAL_ADJUST >= 0) return ADJUST_DOWN;
        if(d1+VERTICAL_ADJUST < LARGEST_RADIUS) return ADJUST_UP;
        if(d2-VERTICAL_ADJUST/2 >= 0 && d1+VERTICAL_ADJUST/2 < LARGEST_RADIUS) return ADJUST_BOTH;
        return ADJUST_NONE; //give up

    }

    public static int checkTopDownCollision(@NonNull View v1, @NonNull View v2){
        Rect R1= new Rect(v1.getLeft(), v1.getTop(), v1.getRight(), v1.getBottom());
        Rect R2= new Rect(v2.getLeft(), v2.getTop(), v2.getRight(), v2.getBottom());
        if (!R1.intersect(R2)) return NO_COLLISION;
        if(v1.getTop() < v2.getTop()) return SECOND_VIEW_ON_TOP;
        return FIRST_VIEW_ON_TOP;
    }

    public static int checkLeftRightCollision(@NonNull View v1, @NonNull View v2){
        Rect R1= new Rect(v1.getLeft(), v1.getTop(), v1.getRight(), v1.getBottom());
        Rect R2= new Rect(v2.getLeft(), v2.getTop(), v2.getRight(), v2.getBottom());
        if (!R1.intersect(R2)) return NO_COLLISION;
        if(v1.getLeft() < v2.getLeft()) return FIRST_VIEW_ON_LEFT;
        return SECOND_VIEW_ON_LEFT;
    }
}
