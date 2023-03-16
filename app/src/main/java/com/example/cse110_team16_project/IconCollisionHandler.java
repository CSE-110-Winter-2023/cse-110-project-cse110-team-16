package com.example.cse110_team16_project;

import static com.example.cse110_team16_project.classes.Updaters.ScreenDistanceUpdater.LARGEST_RADIUS;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.cse110_team16_project.classes.Misc.Converters;
import com.example.cse110_team16_project.classes.Units.Degrees;

import java.util.ArrayList;
import java.util.List;

public class IconCollisionHandler {

    private final int HEIGHT = 20;
    private final int WIDTH_TOP_DOWN_CHECK = 60;

    private final int WIDTH_LEFT_RIGHT_CHECK = 120;
    private Degrees userDirection;
    private List<Degrees> friendOrientation;
    private List<Double> friendDistances;

    private List<Degrees> adjustedAngles;
    private List<Double> adjustedRadius;
    private List<Boolean> ignore;

    public IconCollisionHandler(Degrees userDirection, List<Degrees> friendOrientation, List<Double> friendDistances){
        this.userDirection = userDirection;
        this.friendDistances = friendDistances;
        this.friendOrientation = friendOrientation;
        ignore = new ArrayList<>(friendDistances.size());
    }

    public void adjustIcons(){
        List<Rect> rects = new ArrayList<>(friendDistances.size());
        for(int i = 0; i < friendDistances.size(); i++){
            ignore.add(friendDistances.get(i) == LARGEST_RADIUS);
            rects.add(calculateTopDownCheckRectangle(friendDistances.get(i),Degrees.subtractDegrees(friendOrientation.get(i),userDirection)));
        }
        adjustTopDown(rects);
        convertToPolar(rects);
    }

    public void adjustTopDown(List<Rect> rects){
        for(int i = 1; i < rects.size(); i++){
            if(ignore.get(i) == true) continue;
            for(int j = 0; j < i; j++){
                if(ignore.get(j) == true) continue;
                Rect rect1 = rects.get(i);
                Rect rect2 = rects.get(j);
                if(Rect.intersects(rect1,rect2) || rect1.equals(rect2)) {
                    if(rect1.top > rect2.top){
                        rect1.offset((HEIGHT-rect1.top-rect2.top)/2,0);
                        rect2.offset(-HEIGHT+(rect1.top-rect2.top)/2,0);
                    }
                    else {
                        rect1.offset(-HEIGHT+(rect1.top-rect2.top)/2,0);
                        rect2.offset(HEIGHT-(rect1.top-rect2.top)/2,0);
                    }
                }
            }
        }
    }

    public Rect calculateTopDownCheckRectangle(Double dist1, Degrees deg1){
        int x1 = (int) (dist1*Math.cos(Converters.DegreesToRadians(deg1).getRadians()));
        int y1 = (int) (dist1*Math.sin(Converters.DegreesToRadians(deg1).getRadians()));
        return new Rect(x1-WIDTH_TOP_DOWN_CHECK/2, y1+HEIGHT/2, x1+WIDTH_TOP_DOWN_CHECK/2, y1-HEIGHT/2);
    }
    public boolean checkTopDownCollision(Rect rect1, Rect rect2){
        return Rect.intersects(rect1,rect2);
    }

    public boolean checkLeftRightCollision(Double dist1, Degrees deg1,
                                           Double dist2, Degrees deg2){
        int x1 = (int) (dist1*Math.cos(Converters.DegreesToRadians(deg1).getRadians()));
        int y1 = (int) (dist1*Math.sin(Converters.DegreesToRadians(deg1).getRadians()));
        int x2 = (int) (dist2*Math.cos(Converters.DegreesToRadians(deg2).getRadians()));
        int y2 = (int) (dist2*Math.sin(Converters.DegreesToRadians(deg2).getRadians()));
        Rect R1= new Rect(x1-WIDTH_LEFT_RIGHT_CHECK/2, y1+HEIGHT/2, x1+WIDTH_LEFT_RIGHT_CHECK/2, y1-HEIGHT/2);
        Rect R2= new Rect(x2-WIDTH_LEFT_RIGHT_CHECK/2, y2+HEIGHT/2, x2+WIDTH_LEFT_RIGHT_CHECK/2, y2-HEIGHT/2);
        return R1.intersect(R2);
    }

    public void convertToPolar(List<Rect> rects){
        adjustedAngles = new ArrayList<>(friendDistances.size());
        adjustedRadius = new ArrayList<>(friendDistances.size());
        for(int i = 0; i < rects.size(); i++){
            if(ignore.get(i) == true){
                adjustedRadius.add(friendDistances.get(i));
                adjustedAngles.add(Degrees.subtractDegrees(friendOrientation.get(i),userDirection));
            }
            else {
                int x = rects.get(i).centerX();
                int y = rects.get(i).centerY();
                adjustedRadius.add(Math.sqrt(x * x + y * y));
                adjustedAngles.add(new Degrees(Math.toDegrees(Math.atan2(y, x))));
            }
        }
    }

    public List<Degrees> getAdjustedAngles() { return this.adjustedAngles; }
    public List<Double> getAdjustedRadius() { return this.adjustedRadius; }
}
