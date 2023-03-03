package com.example.cse110_team16_project.classes;

import android.location.Location;

import androidx.room.TypeConverter;

import com.example.cse110_team16_project.classes.Coordinates;
import com.example.cse110_team16_project.classes.Degrees;
import com.example.cse110_team16_project.classes.Radians;

import java.time.Instant;

public class Converters {
    @TypeConverter
    public static Coordinates StringToCoordinates(String str){
        if (str == null) return null;
        String[] data = str.split(",",2);
        return new Coordinates(Double.parseDouble(data[0]), Double.parseDouble(data[1]));
    }

    @TypeConverter
    public static String CoordinatesToString(Coordinates c){
        return c == null ? null : c.first + "," + c.second;
    }

    @TypeConverter
    public static String InstantToString(Instant instant){
        return instant.toString();
    }

    @TypeConverter
    public static Instant StringToInstant(String str){
        return Instant.parse(str);
    }

    public static Coordinates LocationToCoordinates(Location location){
        return new Coordinates(location.getLatitude(),location.getLongitude());
    }

    public static Location CoordinatesToLocation(Coordinates c){
        Location loc = new Location("");
        loc.setLatitude(c.getLatitude());
        loc.setLongitude(c.getLongitude());
        return loc;
    }

    public static Degrees RadiansToDegrees(Radians radians){
        return new Degrees(Math.toDegrees(radians.getRadians()));
    }

    public static Radians DegreesToRadians(Degrees degrees){
        return new Radians(Math.toRadians(degrees.getDegrees()));
    }
}
