package com.example.cse110_team16_project.classes;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class OrientationService implements SensorEventListener{
    private static OrientationService instance;
    private boolean mockMode = false;
    private final SensorManager sensorManager;

    private float[] accelerometerReading;
    private float[] magnetometerReading;
    private float minChange;
    private final MutableLiveData<Float> azimuth;

    public static OrientationService singleton(Activity activity, float minChange){
        if(instance == null) {
            instance = new OrientationService(activity, minChange);
        }
            return instance;
    }
    public static OrientationService singleton(Activity activity){
        return singleton(activity,0f);
    }
    protected OrientationService(Activity activity, float minChange){
        this.azimuth = new MutableLiveData<>();
        this.minChange = minChange;
        this.sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        this.registerSensorListeners();
    }

    protected void registerSensorListeners(){
        if(mockMode) return;
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(accelerometer != null)
            sensorManager.registerListener(this,accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL);
        Sensor magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (magneticField != null) {
            sensorManager.registerListener(this, magneticField,
                    SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        }
    }

    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            accelerometerReading = event.values;
        }
        if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magnetometerReading = event.values;
        }
        if(accelerometerReading != null && magnetometerReading != null){
            onBothSensorDataAvailable();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void onBothSensorDataAvailable() {
        if(accelerometerReading == null || magnetometerReading == null){
            throw new IllegalStateException("Both sensors must be available to compute orientation.");
        }

        float[] r = new float[9];
        float[] i = new float[9];

        boolean success = SensorManager.getRotationMatrix(r,i,accelerometerReading,magnetometerReading);

        if(success){
            float[] orientation = new float[3];
            SensorManager.getOrientation(r,orientation);
            if(this.azimuth.getValue() == null || Math.abs(orientation[0] -
                    this.azimuth.getValue()) > minChange) {
                this.azimuth.postValue(orientation[0]);
            }
        }
    }

    public void unregisterSensorListeners(){
        sensorManager.unregisterListener(this);
    }

    public LiveData<Float> getOrientation() {return this.azimuth;}

    public void setMockOrientationSource(float mockOrientation){
        unregisterSensorListeners();
        mockMode = true;
        azimuth.postValue(mockOrientation);
    }

    public void disableMockMode(){
        mockMode = false;
        registerSensorListeners();
    }

    public void setMinChange(float newMinChange){
        this.minChange = newMinChange;
    }
}
