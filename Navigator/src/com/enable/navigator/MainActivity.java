package com.enable.navigator;

import android.content.Intent;

/**
 * 
 * @author Prashant Jalan
 * 
 */

public class MainActivity extends DemoListActivity {

	@Override
	public void constructList() {

		intents = new IntentPair[] {

				new IntentPair("Working Prototype", new Intent(this,
						Navigate.class)),
				new IntentPair("Step Detection", new Intent(this,
						StepDetection.class)),
				new IntentPair("Compass", new Intent(this,
						Compass.class)),
				new IntentPair("Step Log", new Intent(this,
						StepLog.class)),
				new IntentPair("Wifi RSS", new Intent(this,
						WifiDemo.class)),

		};

	}
	
}
