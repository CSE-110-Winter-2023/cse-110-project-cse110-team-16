package com.example.cse110_team16_project.classes;

import static com.example.cse110_team16_project.classes.Updaters.ScreenDistanceUpdater.LARGEST_RADIUS;

import android.graphics.Rect;
import android.widget.TextView;

import com.example.cse110_team16_project.classes.Misc.Converters;
import com.example.cse110_team16_project.classes.Units.Degrees;

import java.util.ArrayList;
import java.util.List;

public class IconTruncater {
    private final int HEIGHT = 20;
    private final int WIDTH_PER_CHAR = 25;

    private Degrees userDirection;
    private List<Degrees> adjustedOrientation;
    private List<Double> friendDistances;
    private List<Integer> labelLengths;

    private List<Integer> finalWidth;
    private List<Boolean> ignore;

    private List<Degrees> adjustedAngles;
    private List<Double> adjustedRadius;

    public IconTruncater(List<Degrees> adjustedOrientation, List<Double> friendDistances, List<TextView> views){
        this.friendDistances = friendDistances;
        this.adjustedOrientation = adjustedOrientation;
        this.labelLengths = new ArrayList<>(views.size());
        finalWidth = new ArrayList<>();
        for(int i = 0; i < views.size(); i++) {
            labelLengths.add(views.get(i).getText().length());
            finalWidth.add(labelLengths.get(i)*WIDTH_PER_CHAR);
        }
         ignore = new ArrayList<>(friendDistances.size());
    }

    public void truncateIcons(){
        List<Rect> rects = new ArrayList<>(friendDistances.size());
        for(int i = 0; i < friendDistances.size(); i++){
            ignore.add(friendDistances.get(i) == LARGEST_RADIUS);
            rects.add(calculateRectangle(friendDistances.get(i),
                    adjustedOrientation.get(i),labelLengths.get(i)));
        }
        calculateAmountTruncate(rects);
        convertToPolar(rects);
    }


    public void calculateAmountTruncate(List<Rect> rects){
        for (int i = 0; i < rects.size(); i++) {
            if (ignore.get(i)) continue;
            for (int j = 0; j < rects.size(); j++) {
                if (ignore.get(j) || i == j) continue;
                Rect rect1 = rects.get(i);
                Rect rect2 = rects.get(j);
                if (Rect.intersects(rect1, rect2)) { //truncate left view
                    if (rect1.left < rect2.left) {
                        finalWidth.set(i,finalWidth.get(i) - rect1.right+rect2.left);
                        rect1.right = rect2.left;
                    }
                }
            }
        }
    }

    public Rect calculateRectangle(Double dist1, Degrees deg1, int width){
        int x1 = (int) (dist1*Math.cos(Converters.DegreesToRadians(deg1).getRadians()-Math.PI/2));
        int y1 = (int) (dist1*Math.sin(Converters.DegreesToRadians(deg1).getRadians()-Math.PI/2));
        return new Rect(x1-width*WIDTH_PER_CHAR/2, y1-HEIGHT/2, x1+width*WIDTH_PER_CHAR/2, y1+HEIGHT/2);
    }

    public List<Integer> getFinalWidth() {
        return this.finalWidth;
    }
    public void convertToPolar(List<Rect> rects){
        adjustedAngles = new ArrayList<>(friendDistances.size());
        adjustedRadius = new ArrayList<>(friendDistances.size());
        for(int i = 0; i < rects.size(); i++){
            if(ignore.get(i)){
                adjustedRadius.add(friendDistances.get(i));
                adjustedAngles.add(adjustedOrientation.get(i));
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
}
