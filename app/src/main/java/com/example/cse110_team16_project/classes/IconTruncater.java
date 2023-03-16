package com.example.cse110_team16_project.classes;

import static com.example.cse110_team16_project.classes.Updaters.ScreenDistanceUpdater.LARGEST_RADIUS;

import android.graphics.Rect;

import com.example.cse110_team16_project.classes.Misc.Converters;
import com.example.cse110_team16_project.classes.Units.Degrees;

import java.util.ArrayList;
import java.util.List;

public class IconTruncater {
    private final int HEIGHT = 50;

    private Degrees userDirection;
    private List<Degrees> friendOrientation;
    private List<Double> friendDistances;
    private List<Integer> labelLengths;

    private List<Integer> truncateAmounts;
    private List<Boolean> ignore;

    public IconTruncater(Degrees userDirection, List<Degrees> friendOrientation, List<Double> friendDistances, List<Integer> labelLengths){
        this.userDirection = userDirection;
        this.friendDistances = friendDistances;
        this.friendOrientation = friendOrientation;
        this.labelLengths = labelLengths;

        ignore = new ArrayList<>(friendDistances.size());
    }

    public void truncateIcons(){
        List<Rect> rects = new ArrayList<>(friendDistances.size());
        for(int i = 0; i < friendDistances.size(); i++){
            ignore.add(friendDistances.get(i) == LARGEST_RADIUS);
            rects.add(calculateRectangle(friendDistances.get(i),
                    Degrees.subtractDegrees(friendOrientation.get(i),userDirection),labelLengths.get(i)));
        }
        calculateAmountTruncate(rects);
    }


    public void calculateAmountTruncate(List<Rect> rects){
        for (int i = 1; i < rects.size(); i++) {
            if (ignore.get(i)) continue;
            for (int j = 0; j < i; j++) {
                if (ignore.get(j)) continue;
                Rect rect1 = rects.get(i);
                Rect rect2 = rects.get(j);
                if (Rect.intersects(rect1, rect2)) {
                    if (rect1.left < rect2.left) {

                    } else {

                    }
                }
            }
        }
    }

    public Rect calculateRectangle(Double dist1, Degrees deg1, int width){
        int x1 = (int) (dist1*Math.cos(Converters.DegreesToRadians(deg1).getRadians()-Math.PI/2));
        int y1 = (int) (dist1*Math.sin(Converters.DegreesToRadians(deg1).getRadians()-Math.PI/2));
        return new Rect(x1-width/2, y1-HEIGHT/2, x1+width/2, y1+HEIGHT/2);
    }

    public List<Integer> getTruncateAmounts() {
        return this.truncateAmounts;
    }
}
