package com.example.cse110_team16_project.classes;

import android.location.Location;

public class Home{
    Location location;
    String label;
    public Home(Location location, String label){
        this.location = location;
        this.label = label;
    }
    public void setLocation(Location location){
        this.location = location;
    }
    public Location getLocation(){
        return this.location;
    }
    public String getLabel(){
        return this.label;
    }
}
