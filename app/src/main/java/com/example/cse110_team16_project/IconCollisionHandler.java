package com.example.cse110_team16_project;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;

public class IconCollisionHandler {
    public static final int NO_COLLISION = 0;
    public static final int FIRST_VIEW_ON_TOP = 1;
    public static final int SECOND_VIEW_ON_TOP = 2;
    public static final int FIRST_VIEW_ON_LEFT = 3;
    public static final int SECOND_VIEW_ON_LEFT = 4;


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
