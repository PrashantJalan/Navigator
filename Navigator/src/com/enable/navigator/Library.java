package com.enable.navigator;

import java.util.ArrayList;
import java.util.List;

import com.enable.dsp.filters.ContinuousConvolution;
import com.enable.dsp.filters.FrequencyCounter;
import com.enable.dsp.filters.SinXPiWindow;
import com.enable.service.stepdetector.MovingAverageStepDetector;
import com.enable.service.stepdetector.StrideLengthEstimator;
import com.enable.service.stepdetector.MovingAverageStepDetector.MovingAverageStepDetectorState;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;

public class Library extends Activity implements SensorEventListener {
	/** Tag string for our debug logs */
	private static final String TAG = "Sensors";
	private static final String TAG2 = "Navigate";

	Point root;
	LibraryView view;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private Sensor mGeomagnetic;
	private MovingAverageStepDetector mStepDetector;
	private StrideLengthEstimator mStrideEstimator;
	private ContinuousConvolution mCC;
	private FrequencyCounter freqCounter;
	WifiManager mainWifi;
    WifiReceiver receiverWifi;
    List<ScanResult> wifiList;
	private int mMASize = 20;
	private float mSpeed = 1f;
	float mConvolution;
	final float alpha = (float) 0.8;
	float[] gravity = new float[3];
    float[] orientation = new float[3];
    float[] geomagnetic = new float[3];
    float[] rotation_matrix = new float[9];
    float[] inclination_matrix = new float[9];
    private float direction;
    private int stepCount = 0;
    private float distance = (float) 0.25;
    private float x_max = 14;
    private float y_max = 8;
    private int numColumns;
    private int numRows; 
    public Node[][] nodeGrid;
    private Boolean wifiTriggered = true;
    List<Integer> RSS;

	private Sensor getSensor(int sensorType, String sensorName) {

		Sensor sensor = mSensorManager.getDefaultSensor(sensorType);
		if (sensor != null) {
			Log.d(TAG, "there is a " + sensorName);
		} else {
			Log.d(TAG, "there is no " + sensorName);
		}
		return sensor;
	}


	@Override
	public void onSensorChanged(SensorEvent event) {
		synchronized (this) {
			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				for (int i=0; i<3; i++)	{
					gravity[i] = alpha * gravity[i] + (1 - alpha) * event.values[i];
				}
				processAccelerometerEvent(event);
				freqCounter.push(event.timestamp);
				float rate = freqCounter.getRateF();
				if (rate != 0.0f)
					mSpeed = 100f / rate;
			}
			if (event.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD){
				for (int i=0; i<3; i++)	{
		        	geomagnetic[i] = event.values[i];
		        }
			}
	        
			SensorManager.getRotationMatrix(rotation_matrix, inclination_matrix, gravity, geomagnetic);
			SensorManager.getOrientation(rotation_matrix, orientation);

			/*
			if (orientation[0]<0)
				direction = -orientation[0];
			else
				direction = (float) (-orientation[0]+ 2*3.143);
			*/
			direction = orientation[0];
		}

	}

	
	private void displayStepDetectorState(
			MovingAverageStepDetectorState state) {
		//TODO when receive a step detection
		
		boolean stepDetected = false;
		boolean signalPowerCutoff = true;
		double strideDuration = 0.0;
		double strideLength = 0.7;
		double nodeDistance = 0;
		double newX, newY, min=2.0;
		double level=6;
		Point newNode = new Point();
		
		stepDetected = state.states[0];
		signalPowerCutoff = state.states[1];
		
		if (stepDetected){
			stepCount += 1;
			if (stepCount>=3 && wifiTriggered==true){
				mainWifi.startScan();
				stepCount = 0;
				wifiTriggered = false;
			}
			
			if (signalPowerCutoff == false) {
				strideDuration = state.duration;
				strideLength = mStrideEstimator.getStrideLengthFromDuration(strideDuration);
				
				Log.d(TAG2, "Step Event triggered with strideLength "+strideLength);
				
				newX = root.x*distance + strideLength*Math.sin(direction);
				newY = root.y*distance - strideLength*Math.cos(direction);
				min = 2.0;
				
				int startX = (int) Math.max(root.x - level, 0);
				int startY = (int) Math.max(root.y - level, 0);
				int endX = (int) Math.min(root.x + level, 56);
				int endY = (int) Math.min(root.y + level, 32);
				
				if (strideLength<2)	{
					for (int i=startX; i<= endX; i++)	{
						for (int j=startY; j<= endY; j++)	{
							if (nodeGrid[i][j].exists)	{
								nodeDistance = nodeGrid[i][j].getCoordinateDistance(newX, newY);
								//Log.d(TAG2, "Got distance "+nodeDistance+" from node "+i+","+j);
								if (nodeDistance < min){
									min = nodeDistance;
									newNode.x = i;
									newNode.y = j;
								}
							}
						}
					}
				}
				
				if (min < 2.0)	{
					nodeGrid[root.x][root.y].active = false;
					view.setActiveFalse(root.x, root.y);
					view.setActiveTrue(newNode.x, newNode.y);
					Log.d(TAG2, "Root node changed from "+root.toString()+" to "+newNode.toString());
					root.x = newNode.x;
					root.y = newNode.y;
					view.nodeChanged();
				}
			}
			
		}
	}


	public void processAccelerometerEvent(SensorEvent event) {
			mConvolution = (float) (mCC.process(event.values[2]));
			mStepDetector.onSensorChanged(event);
			displayStepDetectorState(mStepDetector.getState());
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// Be sure to call the super class.
		super.onCreate(savedInstanceState);

	    view = new LibraryView(this);
	    nodeGrid = view.getGrid();
	    root = new Point();
    	root.x = 48;
    	root.y = 32;
    	view.setActiveTrue(root.x, root.y);
		System.out.println("Coming 5");
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mAccelerometer = getSensor(Sensor.TYPE_ACCELEROMETER, "accelerometer");
		mGeomagnetic = getSensor(Sensor.TYPE_MAGNETIC_FIELD, "magnetometer");
        
		double movingAverage1 = MovingAverageStepDetector.MA1_WINDOW;
		double movingAverage2 = MovingAverageStepDetector.MA2_WINDOW;
		double powerCutoff = MovingAverageStepDetector.POWER_CUTOFF_VALUE;
		
		mStepDetector = new MovingAverageStepDetector(movingAverage1, movingAverage2, powerCutoff);
		mStrideEstimator = new StrideLengthEstimator(1.0);

		mCC = new ContinuousConvolution(new SinXPiWindow(mMASize));
		freqCounter = new FrequencyCounter(20);
		
		mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
	    receiverWifi = new WifiReceiver();
	    registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
	       
		setContentView(view);
	}

    
    class WifiReceiver extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent) {
            //TODO when receive a wifi scan Result
        	wifiTriggered = true;
        	Log.d(TAG2, "Wifi Event triggered.");
			RSS = new ArrayList<Integer>();
			wifiList = mainWifi.getScanResults();
			for(int i = 0; i < wifiList.size(); i++){
			    if (wifiList.get(i).SSID=="iitk")	{
			    	RSS.add(wifiList.get(i).level);
			    }
			}
        }
    }
    
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		if (mAccelerometer != null)
			mSensorManager.registerListener(this, mAccelerometer,
					SensorManager.SENSOR_DELAY_FASTEST);
		
		if (mGeomagnetic != null)
			mSensorManager.registerListener(this, mGeomagnetic, 
					SensorManager.SENSOR_DELAY_FASTEST);
		
		registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
	}
	
	@Override
	protected void onPause() {
    	super.onPause();
    	
    	unregisterReceiver(receiverWifi);
  	  	mSensorManager.unregisterListener(this, mAccelerometer);
  	  	mSensorManager.unregisterListener(this, mGeomagnetic);
	}
	
}
