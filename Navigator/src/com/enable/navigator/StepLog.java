package com.enable.navigator;

import com.enable.dsp.filters.ContinuousConvolution;
import com.enable.dsp.filters.FrequencyCounter;
import com.enable.dsp.filters.SinXPiWindow;
import com.enable.service.stepdetector.MovingAverageStepDetector;
import com.enable.service.stepdetector.MovingAverageStepDetector.MovingAverageStepDetectorState;
import com.enable.service.stepdetector.StrideLengthEstimator;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class StepLog extends Activity implements SensorEventListener {
	/** Tag string for our debug logs */
	private static final String TAG = "Sensors";
	private static final String TAG2 = "SensorLog";

	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private Sensor mGeomagnetic;
	private MovingAverageStepDetector mStepDetector;
	private StrideLengthEstimator mStrideEstimator;
	private ContinuousConvolution mCC;
	private FrequencyCounter freqCounter;
	private int mMASize = 20;
	private float mSpeed = 1f;
	float mConvolution;
	private TextView textView;
	final float alpha = (float) 0.8;
	float[] gravity = new float[3];
    float[] orientation = new float[3];
    float[] geomagnetic = new float[3];
    float[] rotation_matrix = new float[9];
    float[] inclination_matrix = new float[9];
    private float direction;

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

			if (orientation[0]<0)
				direction = -orientation[0];
			else
				direction = (float) (-orientation[0]+ 2*3.143);
		}

	}

	
	private void displayStepDetectorState(
			MovingAverageStepDetectorState state) {
		
		boolean stepDetected = false;
		Double strideDuration = 0.0;
		Double strideLength = 0.7;
		
		textView = (TextView) findViewById(R.id.textView1);
		
		stepDetected = state.states[0];
		Log.d(TAG2, "Coming "+stepDetected);
		
		if (stepDetected){
			strideDuration = state.duration;
			strideLength = mStrideEstimator.getStrideLengthFromDuration(strideDuration);
			
			textView.setText("Step Detected with duration "+strideDuration+" seconds and length "+strideLength+" meters in "+Double.toString(Math.round(Math.toDegrees(direction)))+" degrees to the North." );
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
		
		setContentView(R.layout.activity_steplog);
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
	}
	
	@Override
	protected void onPause() {
    	super.onPause();
    	
  	  	mSensorManager.unregisterListener(this, mAccelerometer);
  	  	mSensorManager.unregisterListener(this, mGeomagnetic);
	}
	
}
