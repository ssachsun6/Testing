/**
 * 
 * Copyright 2014 Noisy Flowers LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * 
 * com.noisyflowers.landpks.android.activities
 * ClinometerActivity.java
 */

package com.noisyflowers.landpks.android.activities;

import com.noisyflowers.landpks.android.R;
import com.noisyflowers.landpks.android.util.ClinometerView;
import com.noisyflowers.landpks.android.util.PlotEditFragment;
import com.noisyflowers.landpks.android.R.id;
import com.noisyflowers.landpks.android.R.layout;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.Display;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.Surface;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageButton;

public class ClinometerActivity extends Activity implements SensorEventListener, OnClickListener {

	private ClinometerActivity context;
	
	private ImageButton lockButton;
	private Button returnButton;
	
	private SensorManager sensorManager;
	//private Sensor orientationSensor;
	private Sensor rotVecSensor;
	private Sensor gravSensor;
	private Sensor accSensor;
	private Sensor magnetSensor;
	
	//private RelativeLayout mainView;
	private ClinometerView cV;
	private TextView percentTV, degreeTV;
	LinearLayout returnLayout;
	
	float gravity[] = new float[3];
	float geoMag[] = new float[3];
	//float rotationMatrix[]; ???
	float azimuth;
	float pitch;
	float roll;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		SettingsActivity.setUITheme(this);
		
		super.onCreate(savedInstanceState);
		
		context = this;
				
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_clinometer);

		lockButton = (ImageButton)findViewById(R.id.activity_clinometer_button_capture);
        percentTV = (TextView) findViewById(R.id.roll);
        //RotateAnimation slopeRotate = (RotateAnimation)AnimationUtils.loadAnimation(this, R.anim.rotate_right_quarter);
        //percentTV.setAnimation(slopeRotate);
        degreeTV = (TextView) findViewById(R.id.degrees);
        RotateAnimation rotate = (RotateAnimation)AnimationUtils.loadAnimation(this, R.anim.rotate_right_quarter);
        //percentTV.setAnimation(rotate);
        returnLayout = (LinearLayout)findViewById(R.id.activity_clinometer_layout_return);
        returnLayout.setAnimation(rotate);
        
		returnButton = (Button)findViewById(R.id.activity_clinometer_button_return);
		//returnButton.setOnClickListener(this);
        
		sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		//orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		rotVecSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
		gravSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
		accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		magnetSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

		//mainView = (RelativeLayout)findViewById(R.id.activity_clinometer_top_level_layout);
        cV = (ClinometerView) findViewById(R.id.activity_clinometer_clinometer);
		
		((ImageButton)findViewById(R.id.activity_clinometer_button_capture)).setOnClickListener(this);
	}
	
	
	@Override
	public void onResume() {
		super.onResume();
		//sensorManager.registerListener(this, orientationSensor, SensorManager.SENSOR_DELAY_UI);
		//sensorManager.registerListener(this, rotVecSensor, SensorManager.SENSOR_DELAY_UI);
		//sensorManager.registerListener(this, gravSensor, SensorManager.SENSOR_DELAY_UI);
		if (active) {
			sensorManager.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_UI);
		}
		//sensorManager.registerListener(this, magnetSensor, SensorManager.SENSOR_DELAY_UI);
	}

	@Override
	protected void onPause() {
		super.onPause();
		//sensorManager.unregisterListener(this, orientationSensor);
		//sensorManager.unregisterListener(this, rotVecSensor);
		//sensorManager.unregisterListener(this, gravSensor);
		if (active) {
			sensorManager.unregisterListener(this, accSensor);
		}
		//sensorManager.unregisterListener(this, magnetSensor);
	}
	
	
	private static final float ALPHA = 0.35f;
	private float[] lowPass(float[] input, float[] output) {
		if (output == null) return input;
		for (int i = 0; i < input.length; i++) {
			output[i] = output[i] + ALPHA * (input[i] - output[i]);
		}
		return output;
	}
	
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
        //TextView percentTV = (TextView) findViewById(R.id.roll);
        //TextView pt = (TextView) findViewById(R.id.pitch);
        //TextView yt = (TextView) findViewById(R.id.yaw);
        //ClinometerView cV = (ClinometerView) findViewById(R.id.activity_clinometer_clinometer);
        
	    if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
			gravity = event.values.clone();
			//gravity = lowPass(event.values.clone(), gravity);
	    } else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			//gravity = event.values.clone();
			//gravity = lowPass(event.values.clone(), gravity);
	    	float[] g = event.values.clone();
	    	double normG = Math.sqrt(g[0]*g[0] + g[1]*g[1] + g[2]*g[2]);
	    	g[0] = (float)(g[0]/normG);
	    	g[1] = (float)(g[1]/normG);
	    	g[2] = (float)(g[2]/normG);
	    	int percent = 0;
	    	int inclination = (int)Math.round(Math.toDegrees(Math.acos(g[2])));
	    	if (inclination > 25 && inclination < 155) {
				Display display = getWindowManager().getDefaultDisplay();
				if (display.getRotation() == Surface.ROTATION_90 || display.getRotation() == Surface.ROTATION_270) {
					roll = (int)Math.round(Math.toDegrees(Math.atan2(g[1], g[0])));
				} else {
					//roll = -(int)Math.round(Math.toDegrees(Math.atan2(g[0], g[1])));					
					roll = -(int)Math.round(Math.toDegrees(Math.atan2(g[1], g[0])));					
				}
				roll = Math.min(Math.max(roll, -90), 90);
				percent = (int)Math.min(Math.abs(Math.round(Math.tan(Math.toRadians(roll)) * 100)), 200);
				
	    	}
	    	
	    	//TODO: This is probably not the best way to handle the two themes.  Should probably do it with font color defs tied to theme.
	    	if (Math.abs(roll) > 64) {
	    		//degreeTV.setTextColor(Color.GRAY);
	    		int color = SettingsActivity.getUITheme(this) == R.style.AppTheme ? Color.GRAY : Color.DKGRAY;
	    		degreeTV.setTextColor(color);
	    	} else {
	    		//degreeTV.setTextColor(Color.BLACK);	    		
	    		int color = SettingsActivity.getUITheme(this) == R.style.AppTheme ? Color.BLACK : Color.GRAY;
	    		degreeTV.setTextColor(color);
	    	}
	    	
            degreeTV.setText("(" + Math.round(Math.abs(roll)) + (char)176 + ")");
            percentTV.setText("" + percent + (char)37);
            cV.setGravityDirection(roll);
            return;
	    } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
	        geoMag = event.values.clone();
			//geoMag = lowPass(event.values.clone(), geoMag);
	    } /*else if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            yt.setText("azi z: " + event.values[0]);
            pt.setText("pitch x: " + event.values[1]);
            percentTV.setText("roll y: " + event.values[2]);
            ClinometerView cV = (ClinometerView) findViewById(R.id.activity_clinometer_clinometer);
            cV.setGravityDirection(-event.values[1]);
            return;
	    }*/
	    else if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
			float R[] = new float[9];
	    	SensorManager.getRotationMatrixFromVector(R, event.values.clone());
			Display display = getWindowManager().getDefaultDisplay();
			if (display.getRotation() == Surface.ROTATION_90 || display.getRotation() == Surface.ROTATION_270) {
				//SensorManager.remapCoordinateSystem(R, SensorManager.AXIS_Z, SensorManager.AXIS_MINUS_X, R);
				SensorManager.remapCoordinateSystem(R.clone(), SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, R);
			}
			float orientation[] = new float[3];
			SensorManager.getOrientation(R, orientation);
			azimuth = (float) Math.toDegrees(orientation[0]);
			pitch = (float) Math.toDegrees(orientation[1]);
			roll = (float) Math.toDegrees(orientation[2]);
            //yt.setText("azi z: " + azimuth);
            //pt.setText("pitch x: " + pitch);
            percentTV.setText("roll y: " + roll);
            cV.setGravityDirection(roll);
            return;
	    }
		
	    //testing
	    geoMag[0] = 0;
	    geoMag[1] = 1;
	    geoMag[2] = 0;
	    
	    if(gravity != null && geoMag != null){
	    	/***
	        float rMatrix[] = new float[16];
	        float[] orientation = new float[4];
	        
	        SensorManager.getRotationMatrix(rMatrix, null, gravity, geoMag);// Retrieve RMatrix, necessary for the getOrientation method
	        SensorManager.getOrientation(rMatrix, orientation);// Get the current orientation of the device
            float yaw = orientation[0] * 57.2957795f;
            float pitch = orientation[1] * 57.2957795f;
            float roll = orientation[2] * 57.2957795f;
            TextView percentTV = (TextView) findViewById(R.id.roll);
            TextView pt = (TextView) findViewById(R.id.pitch);
            TextView yt = (TextView) findViewById(R.id.yaw);
            yt.setText("azi z: " + yaw);
            pt.setText("pitch x: " + pitch);
            percentTV.setText("roll y: " + roll);
           ***/
	    	/***/
			//double normalG = Math.sqrt(Math.pow(gravity[0],2) + Math.pow(gravity[1], 2) + Math.pow(gravity[2], 2));
			//int inclination = (int) Math.round(Math.toDegrees(Math.acos(gravity[2]/normalG)));
			//boolean flat = inclination < 25 || inclination > 155;
			
			float R[] = new float[9];
			//float I[] = new float[9];
			//boolean success = SensorManager.getRotationMatrix(R, I, gravity, geoMag);
			boolean success = SensorManager.getRotationMatrix(R, null, gravity, geoMag);
			if (success) {
				Display display = getWindowManager().getDefaultDisplay();
				if (display.getRotation() == Surface.ROTATION_90 || display.getRotation() == Surface.ROTATION_270) {
					//SensorManager.remapCoordinateSystem(R, SensorManager.AXIS_Z, SensorManager.AXIS_MINUS_X, R);
					SensorManager.remapCoordinateSystem(R.clone(), SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, R);
				}
				/***
				float orientation[] = new float[3];
				SensorManager.getOrientation(R, orientation);
				azimuth = (float) Math.toDegrees(orientation[0]);
				pitch = (float) Math.toDegrees(orientation[1]);
				roll = (float) Math.toDegrees(orientation[2]);
            
	            yt.setText("azi z: " + azimuth);
	            pt.setText("pitch x: " + pitch);
	            percentTV.setText("roll y: " + roll);
				***/
				float flatR[] = new float[9];
				float flatGrav[] = {0,0,9.8f};
				success = SensorManager.getRotationMatrix(flatR, null, flatGrav, geoMag);
				if (success) {
					float or[] = new float[3];
					SensorManager.getAngleChange(or, R, flatR);
					azimuth = (float) Math.toDegrees(or[0]);
					pitch = (float) Math.toDegrees(or[1]);
					roll = (float) Math.toDegrees(or[2]);
	            
		            //yt.setText("azi z: " + azimuth);
		            //pt.setText("pitch x: " + pitch);
		            percentTV.setText("roll y: " + roll);
					
				}
			}
			/***/
	    }
        //ClinometerView cV = (ClinometerView) findViewById(R.id.activity_clinometer_clinometer);
        cV.setGravityDirection(roll);
	}

	private boolean active = true;
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.activity_clinometer_button_capture: {
				if (active) {
					sensorManager.unregisterListener(this, accSensor);
					v.playSoundEffect(SoundEffectConstants.CLICK);
					lockButton.setImageResource(R.drawable.big_green_button);
					returnButton.setVisibility(View.VISIBLE);
			        returnLayout.setOnClickListener(this);
					active = false;
				} else {
					active = true;
					sensorManager.registerListener(context, accSensor, SensorManager.SENSOR_DELAY_UI);
					lockButton.setImageResource(R.drawable.big_red_button);
					returnButton.setVisibility(View.INVISIBLE);
			        returnLayout.setOnClickListener(null);
				}
			}
			break;
			case R.id.activity_clinometer_layout_return: {
				Intent detailIntent = new Intent(context, PlotEditDetailActivity.class);
				//detailIntent.putExtra("slope", "" + Math.round(Math.abs(cV.getGravityDirection())));
				detailIntent.putExtra("slope", percentTV.getText());
				int resultCode = 1;
				setResult(resultCode, detailIntent); 
				finish();				
			}
			break;
		}
	}


	/**
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.clinometer, menu);
		return true;
	}
	**/

	
}
