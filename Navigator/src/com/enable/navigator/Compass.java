package com.enable.navigator;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;

public class Compass extends Activity implements SensorEventListener {

    SensorManager sensorManager;
    Sensor mAccelerometer;
    Sensor mGeomagnetic;
    ImageCompass compass;
    final float alpha = (float) 0.8;
    float[] gravity = new float[3];
    float[] orientation = new float[3];
    float[] geomagnetic = new float[3];
    float[] rotation_matrix = new float[9];
    float[] inclination_matrix = new float[9];
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		compass = new ImageCompass(this);
		setContentView(compass);
		
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        mGeomagnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(this, mGeomagnetic, SensorManager.SENSOR_DELAY_GAME);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	 protected void onResume() {
	  sensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
	  sensorManager.registerListener(this, mGeomagnetic, SensorManager.SENSOR_DELAY_GAME);
	  super.onResume();
	 }
	  
	 @Override
	 protected void onPause() {
	  sensorManager.unregisterListener(this, mAccelerometer);
	  sensorManager.unregisterListener(this, mGeomagnetic);
	  super.onPause();
	 }
	 
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		if (event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
			for (int i=0; i<3; i++)	{
				gravity[i] = alpha * gravity[i] + (1 - alpha) * event.values[i];
			}
		}
		
		if (event.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD){
			for (int i=0; i<3; i++)	{
	        	geomagnetic[i] = event.values[i];
	        }
		}
        
		SensorManager.getRotationMatrix(rotation_matrix, inclination_matrix, gravity, geomagnetic);
		SensorManager.getOrientation(rotation_matrix, orientation);

        compass.setValue(orientation);
	}

}
