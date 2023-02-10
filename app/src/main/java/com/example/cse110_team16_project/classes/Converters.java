package com.example.cse110_team16_project.classes;

import androidx.room.TypeConverter;

public class Converters {
    @TypeConverter
    public static Coordinates StringToCoordinates(String str){
        return str == null ? null : new Coordinates(str);
    }

    @TypeConverter
    public static String CoordinatesToString(Coordinates c){
        return c == null ? null : c.toString();
    }
}
