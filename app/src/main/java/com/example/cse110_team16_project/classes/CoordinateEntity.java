package com.example.cse110_team16_project.classes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public abstract class CoordinateEntity {
    private final MutableLiveData<Coordinates> coordinates = new MutableLiveData<>(new Coordinates(0.0,0.0));

    public CoordinateEntity(Coordinates coordinates) {
        setCoordinates(coordinates);
    }

    public void setCoordinates(Coordinates coordinates) {
        if(coordinates != null) this.coordinates.postValue(coordinates);
    }

    public LiveData<Coordinates> getCoordinates() {
        return coordinates;
    }

}
