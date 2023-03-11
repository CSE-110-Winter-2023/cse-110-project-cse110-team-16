package com.example.cse110_team16_project.classes.CoordinateClasses;

import com.example.cse110_team16_project.Units.Degrees;
import com.google.gson.annotations.SerializedName;

/**
 * Sort of a quasi adapter for the Coordinates class to make it easy to store and retrieve from databases
 */
public abstract class CoordinateEntity {

    @SerializedName("latitude")
    private double latitude;

    @SerializedName("longitude")
    private double longitude;

    public CoordinateEntity(Coordinates coordinates) {
        setCoordinates(coordinates);
    }
    public CoordinateEntity(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setCoordinates(Coordinates coordinates) {
        if(coordinates != null) {
            this.latitude = coordinates.getLatitude();
            this.longitude = coordinates.getLongitude();
        }
    }

    public Coordinates getCoordinates() {
        return new Coordinates(latitude, longitude);
    }

    public double getLatitude(){
        return this.latitude;
    }

    public double getLongitude(){
        return this.longitude;
    }

    public Degrees bearingTo(Coordinates c){
        return getCoordinates().bearingTo(c);
    }

    public double distanceTo(Coordinates c){
        return getCoordinates().distanceTo(c);
    }
}
