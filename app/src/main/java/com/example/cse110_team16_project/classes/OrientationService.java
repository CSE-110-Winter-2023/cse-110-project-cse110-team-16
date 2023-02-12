package com.example.cse110_team16_project.classes;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class OrientationService implements SensorEventListener{
    private static OrientationService instance;

    private final SensorManager sensorManager;

    private float[] accelerometerReading;
    private float[] magnetometerReading;
    private MutableLiveData<Float> azimuth;

    public static OrientationService singleton(Activity activity){
        if(instance == null) {
            instance = new OrientationService(activity);
        }
            return instance;
    }
    protected OrientationService(Activity activity){
        this.azimuth = new MutableLiveData<>();
        this.sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        this.registerSensorListeners();
    }

    protected void registerSensorListeners(){
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(accelerometer != null)
            sensorManager.registerListener((SensorEventListener) this,accelerometer,
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

            this.azimuth.postValue(orientation[0]);
        }
    }

    public void unregisterSensorListeners(){
        sensorManager.unregisterListener((SensorListener) this);
    }

    public LiveData<Float> getOrientation() {return this.azimuth;}

    public void setMockOrientationSource(MutableLiveData<Float> mockDataSource){
        unregisterSensorListeners();
        this.azimuth = mockDataSource;
    }
}
