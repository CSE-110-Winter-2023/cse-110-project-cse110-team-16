package com.example.cse110_team16_project.classes;

import android.location.Location;

import androidx.room.TypeConverter;

import com.example.cse110_team16_project.Units.Degrees;
import com.example.cse110_team16_project.Units.Meters;
import com.example.cse110_team16_project.Units.Miles;
import com.example.cse110_team16_project.Units.Radians;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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
        if (location == null) return null;
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

    public static Miles metersToMiles(Meters meters) {
        return new Miles(meters.getMeters()*.0006213712);
    }

    public static List<Miles> listMetersToMiles(List<Meters> meters) {
        if(meters == null) return null;
        List<Miles> inMiles = new ArrayList<>(meters.size());
        for(Meters meter: meters){
            if(meter == null) inMiles.add(null);
            else inMiles.add(metersToMiles(meter));
        }
        return inMiles;
    }
}
