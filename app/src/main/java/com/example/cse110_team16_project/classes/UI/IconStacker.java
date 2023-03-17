package com.example.cse110_team16_project.classes.UI;

import static com.example.cse110_team16_project.classes.Updaters.ScreenDistanceUpdater.LARGEST_RADIUS;

import android.graphics.Rect;

import com.example.cse110_team16_project.classes.Misc.Converters;
import com.example.cse110_team16_project.classes.Units.Degrees;

import java.util.ArrayList;
import java.util.List;

public class IconStacker {

    private final int HEIGHT = 60;
    private final int WIDTH_TOP_DOWN_CHECK = 40; //maybe 60

    private Degrees userDirection;
    private List<Degrees> friendOrientation;
    private List<Double> friendDistances;

    private List<Degrees> adjustedAngles;
    private List<Double> adjustedRadius;
    private List<Boolean> ignore;

    public IconStacker(Degrees userDirection, List<Degrees> friendOrientation, List<Double> friendDistances){
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
        boolean adjusted = false;
        int tries = 0;
        while (!adjusted && tries < 3) {
            tries++;
            adjusted = true;
            for (int i = 1; i < rects.size(); i++) {
                if (ignore.get(i)) continue;
                for (int j = 0; j < i; j++) {
                    if (ignore.get(j)) continue;
                    Rect rect1 = rects.get(i);
                    Rect rect2 = rects.get(j);
                    if (Rect.intersects(rect1, rect2)) {
                        if (rect1.bottom > rect2.bottom) {
                            rect1.offset(0, HEIGHT / 2 - (rect1.bottom - rect2.bottom));
                            rect2.offset(0, -HEIGHT / 2 + (rect1.bottom - rect2.bottom));
                        } else {
                            rect1.offset(0, -HEIGHT / 2 + (-rect1.bottom + rect2.bottom));
                            rect2.offset(0, HEIGHT / 2 - (-rect1.bottom + rect2.bottom));
                        }
                        adjusted = false;
                    }
                }
            }
        }
    }

    public Rect calculateTopDownCheckRectangle(Double dist1, Degrees deg1){
        int x1 = (int) (dist1*Math.cos(Converters.DegreesToRadians(deg1).getRadians()-Math.PI/2));
        int y1 = (int) (dist1*Math.sin(Converters.DegreesToRadians(deg1).getRadians()-Math.PI/2));
        return new Rect(x1-WIDTH_TOP_DOWN_CHECK/2, y1-HEIGHT/2, x1+WIDTH_TOP_DOWN_CHECK/2, y1+HEIGHT/2);
    }

    public void convertToPolar(List<Rect> rects){
        adjustedAngles = new ArrayList<>(friendDistances.size());
        adjustedRadius = new ArrayList<>(friendDistances.size());
        for(int i = 0; i < rects.size(); i++){
            if(ignore.get(i)){
                adjustedRadius.add(friendDistances.get(i));
                adjustedAngles.add(Degrees.subtractDegrees(friendOrientation.get(i),userDirection));
            }
            else {
                int x = rects.get(i).centerX();
                int y = rects.get(i).centerY();
                adjustedRadius.add(Math.sqrt(x * x + y * y));
                adjustedAngles.add(new Degrees(Math.toDegrees(Math.atan2(y, x))+90));
            }
        }
    }

    public List<Degrees> getAdjustedAngles() { return this.adjustedAngles; }
    public List<Double> getAdjustedRadius() { return this.adjustedRadius; }

    public List<Degrees> getRegularAngles() {
        List<Degrees> regAngles = new ArrayList<>();
        for(Degrees fO: friendOrientation) {
            regAngles.add(Degrees.subtractDegrees(fO,userDirection));
        }
        return regAngles;
    }
    public List<Double> getRegularRadius() { return this.friendDistances; }
}
