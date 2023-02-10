package com.example.cse110_team16_project.classes;

import android.location.Location;
import android.util.Pair;

public class Coordinates {
    private Pair<Double,Double> LongLat;

    public Coordinates(double longitude, double latitude){
        LongLat = new Pair<>(longitude,latitude);
    }

    public Coordinates(Location location){
        LongLat = new Pair<>(location.getLongitude(),location.getLatitude());
    }

    public Coordinates(String string){
        String[] coords = string.split(",",2);
        LongLat = new Pair<>(Double.valueOf(coords[0]),Double.valueOf(coords[1]));
    }

    public double getLongitude(){
        return LongLat.first;
    }

    public double getLatitude(){
        return LongLat.second;
    }

    public String toString(){
        return Double.toString(LongLat.first) + "," + Double.toString(LongLat.second);
    }

    public float bearingTo(Coordinates c){
        double lng1 = this.getLongitude();
        double lng2 = c.getLongitude();
        double lat1 = this.getLatitude();
        double lat2 = c.getLatitude();
        double dLon = (lng2-lng1);
        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1)*Math.sin(lat2) - Math.sin(lat1)*Math.cos(lat2)*Math.cos(dLon);
        double bearing = Math.toDegrees((Math.atan2(y, x)));
        return (float) (360 - ((bearing + 360) % 360));

    }
}
