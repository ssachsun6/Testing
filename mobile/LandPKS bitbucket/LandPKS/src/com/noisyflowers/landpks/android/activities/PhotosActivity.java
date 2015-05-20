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
 * PhotosActivity.java
 */

package com.noisyflowers.landpks.android.activities;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.noisyflowers.landpks.android.LandPKSApplication;
import com.noisyflowers.landpks.android.R;
import com.noisyflowers.landpks.android.model.Plot;
import com.noisyflowers.landpks.android.util.CompassView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Display;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


public class PhotosActivity extends Activity/*ActionBarActivity*/ implements SensorEventListener, OnClickListener, PictureCallback {
	
	public static final int TAKE_LANDSCAPE_PHOTOS = 0;
	public static final int TAKE_PIT_PHOTO = 1;
	public static final int TAKE_SAMPLES_PHOTO = 2;
	
	private static final int TOTS = 360;
	private static final int NORTH = 0;
	private static final int EAST = 90;
	private static final int SOUTH = 180;
	private static final int WEST = 270;
	private static final int HALF_GOOD_ENOUGH = 10;

	
	
	/***
	public static final int TAKE_NORTH_PHOTO = 3;
	public static final int TAKE_EAST_PHOTO = 4;
	public static final int TAKE_SOUTH_PHOTO = 5;
	public static final int TAKE_WEST_PHOTO = 6;
	***/
	
	public static final String INTENT_EXTRA_FLAVOR = "flavor";
	public static final String INTENT_EXTRA_PLOTNAME = "plotName";

	private TextView directionTV, compassToggleTV, instructionTV;
	private CompassView compass;
	private ImageButton captureButton;
	private CompoundButton compassToggle;
	
	private Camera camera;
	private CameraPreview cameraPreview;
	private Integer pendingPictureDirection = null;
	
	private SensorManager sensorManager;
	private Sensor accSensor;
	private Sensor magnetSensor;
	
	float gravity[] = new float[3];
	float geoMag[] = new float[3];
	//float rotationMatrix[];
	float azimuth;
	float pitch;
	float roll;
	
	//Plot plot;
	private String plotName;
	private int flavor;
	private String eastImageFilename = null, southImageFilename = null, westImageFilename = null, northImageFilename = null;
	
	private boolean compassOn = true;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		SettingsActivity.setUITheme(this);
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		flavor = intent.getIntExtra(INTENT_EXTRA_FLAVOR, 1);
		
		plotName = intent.getStringExtra(INTENT_EXTRA_PLOTNAME); 
		
		//TODO: only use these if not descending from ActionBarActivity
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_photos);

		instructionTV = (TextView)super.findViewById(R.id.activity_photos_instruction);
		directionTV = (TextView)super.findViewById(R.id.activity_photos_direction);
		//directionTV.setTextColor(Color.BLUE);
		compass = (CompassView)super.findViewById(R.id.activity_photos_compass);
		captureButton = (ImageButton)super.findViewById(R.id.activity_photos_button_capture);
		captureButton.setOnClickListener(this);
		
		compassToggle = (CompoundButton)super.findViewById(R.id.activity_photos_compass_toggle);
		compassToggle.setOnClickListener(this);
		compassToggleTV = (TextView)super.findViewById(R.id.activity_photos_compass_toggle_label);
		
		camera = null;
		
		cameraPreview = new CameraPreview(this, camera);
		FrameLayout previewArea = (FrameLayout)findViewById(R.id.camera_preview);
		previewArea.addView(cameraPreview);

		//TODO: if descending from ActionBarActivity
        //ActionBar actionBar = getSupportActionBar();
	    //actionBar.setDisplayHomeAsUpEnabled(true);
	    
		if (flavor == TAKE_PIT_PHOTO) {
		    //actionBar.setTitle(plotName + " Soil Pit Photo"); //TODO: put in strings
			//directionTV.setVisibility(View.GONE);
			//compass.setVisibility(View.GONE);
			captureButton.setVisibility(View.VISIBLE); 
			captureButton.setClickable(true);
		} else if (flavor == TAKE_SAMPLES_PHOTO) {
		    //actionBar.setTitle(plotName + " Soil Samples Photo"); //TODO: put in strings
			//directionTV.setVisibility(View.GONE);
			//compass.setVisibility(View.GONE);
			captureButton.setVisibility(View.VISIBLE); 
			captureButton.setClickable(true);
		} else {
			compass.setVisibility(View.VISIBLE);
			compassToggle.setVisibility(View.VISIBLE);
			compassToggleTV.setVisibility(View.VISIBLE);
			instructionTV.setVisibility(View.VISIBLE);
		    //actionBar.setTitle(plotName + " Landscape Photos"); //TODO: put in strings
			sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
			accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			magnetSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
			compassOn = accSensor != null && magnetSensor != null;
			if (!compassOn) {
				compassToggle.setChecked(false);
				compassToggle.setVisibility(View.GONE);
				compassToggleTV.setVisibility(View.GONE);
				disableCompass();
			}
		}

	}
	
	private void disableCompass() {
		compass.setFloating(false);
		pendingPictureDirection = getNextPictureDirection();
		compass.setDirection(pendingPictureDirection);
		captureButton.setVisibility(View.VISIBLE);					 
		captureButton.setClickable(true);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if (flavor == TAKE_LANDSCAPE_PHOTOS && compassOn) {
			sensorManager.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_NORMAL);
			sensorManager.registerListener(this, magnetSensor, SensorManager.SENSOR_DELAY_NORMAL);
		}
		
		camera = Camera.open();
		Camera.Parameters cParams = camera.getParameters();
		cParams.setPictureSize(640, 480);
		camera.setParameters(cParams);
		cameraPreview.setCamera(camera);
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		if (flavor == TAKE_LANDSCAPE_PHOTOS && compassOn) {
			sensorManager.unregisterListener(this, accSensor);
			sensorManager.unregisterListener(this, magnetSensor);
		}
		
		if (camera != null) {
			cameraPreview.setCamera(null);
			camera.release();
			camera = null;
		}
	}
	
	/***
	 * TODO: only needed if descending from ActionBarActivity
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			Intent detailIntent = new Intent(this, PlotEditDetailActivity.class);
			detailIntent.putExtra("position", 6);//TODO:  Very hokey!!
			NavUtils.navigateUpTo(this,
					detailIntent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	***/
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.activity_photos_button_capture: {
				if (flavor == TAKE_LANDSCAPE_PHOTOS && compassOn) {
					pendingPictureDirection = compass.getDirection();
				}
				captureButton.setClickable(false);
				camera.takePicture(null, null, this);
			}
			break;
			case R.id.activity_photos_compass_toggle: {
				 compassOn = ((CompoundButton) v).isChecked();
				    
				 if (compassOn) {
					sensorManager.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_NORMAL);
					sensorManager.registerListener(this, magnetSensor, SensorManager.SENSOR_DELAY_NORMAL);
					compass.setFloating(true);
					captureButton.setVisibility(View.INVISIBLE);					 
				 } else {
					sensorManager.unregisterListener(this, accSensor);
					sensorManager.unregisterListener(this, magnetSensor);
					disableCompass();
				 }				
			}
			break;
		}
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
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	boolean compassVisible = true;
	boolean captureButtonVisible = false;
	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			//gravity = event.values.clone();
			gravity = lowPass(event.values.clone(), gravity);
			//gravity[0] = (gravity[0]*2 + event.values[0]) * 0.33334f;
			//gravity[1] = (gravity[1]*2 + event.values[1]) * 0.33334f;
			//gravity[2] = (gravity[2]*2 + event.values[2]) * 0.33334f;
		}
		if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
			//geoMag = event.values.clone();
			geoMag = lowPass(event.values.clone(), geoMag);
			//geoMag[0] = (geoMag[0] * 1 + event.values[0]) * 0.5f;
			//geoMag[1] = (geoMag[1] * 1 + event.values[1]) * 0.5f;
			//geoMag[2] = (geoMag[2] * 1 + event.values[2]) * 0.5f;
		}
		
		if (gravity != null && geoMag != null) {
			//rotationMatrix = new float[16];
			//SensorManager.getRotationMatrix(rotationMatrix, null, gravity, geoMag);
			//SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, rotationMatrix);
			
			double normalG = Math.sqrt(Math.pow(gravity[0],2) + Math.pow(gravity[1], 2) + Math.pow(gravity[2], 2));
			int inclination = (int) Math.round(Math.toDegrees(Math.acos(gravity[2]/normalG)));
			boolean flat = inclination < 25 || inclination > 155;
			
			float R[] = new float[9];
			float I[] = new float[9];
			boolean success = SensorManager.getRotationMatrix(R, I, gravity, geoMag);
			if (success) {
				Display display = getWindowManager().getDefaultDisplay();
				if (display.getRotation() == Surface.ROTATION_90 || display.getRotation() == Surface.ROTATION_270) {
					SensorManager.remapCoordinateSystem(R, SensorManager.AXIS_Z, SensorManager.AXIS_MINUS_X, R);
				}
				float orientation[] = new float[3];
				SensorManager.getOrientation(R, orientation);
				//azimuth = 57.29578F * orientation[0];
				//pitch = 57.29578F * orientation[1];
				//roll = 57.29578F * orientation[2];
				azimuth = (float) Math.toDegrees(orientation[0]);
				pitch = (float) Math.toDegrees(orientation[1]);
				roll = (float) Math.toDegrees(orientation[2]);
				
				int az360 = (Math.round(azimuth) + 360) % 360;				
				
				if (!flat) {
					//directionTV.setText(Integer.toString(az360));
					directionTV.setText("");  //TODO: Well, this is crazy.  For some reason getting rid of directionTV altogether prevents the compass from going visible here.  Try it if you don't believe me.
					if (!compassVisible) {
						compass.setVisibility(View.VISIBLE);
						compassVisible = true;
					}
					compass.setDirection(az360);
					if (isCloseEnoughToCardinal(az360, EAST) ||
						isCloseEnoughToCardinal(az360, SOUTH) ||
						isCloseEnoughToCardinal(az360, WEST) ||
						isCloseEnoughToCardinal(az360, NORTH)) {
						if (!captureButtonVisible) {
							captureButton.setVisibility(View.VISIBLE); //TODO: better to have it visible in disabled state?
							captureButton.setClickable(true);
							captureButtonVisible = true;
						}
					} else {
						if (captureButtonVisible) {
							captureButton.setVisibility(View.INVISIBLE); //TODO: better to have it visible in disabled state?
							captureButton.setClickable(false);
							captureButtonVisible = false;
						}
					}
				} else {
					directionTV.setText("");
					if (compassVisible) {
						compass.setVisibility(View.INVISIBLE);
						compassVisible = false;
					}
					if (captureButtonVisible) {
						captureButton.setVisibility(View.INVISIBLE); //TODO: better to have it visible in disabled state?
						captureButton.setClickable(false);
						captureButtonVisible = false;
					}
				}
			}
		}		
	}
		
	private Integer getNextPictureDirection() {
		Integer retVal = 0;
		if (northImageFilename == null) {
			retVal = NORTH;
		} else if (eastImageFilename == null) {
			retVal = EAST;
		} else if (southImageFilename == null) {
			retVal = SOUTH;
		} else if (westImageFilename == null) {
			retVal = WEST;
		}
		return retVal;
	}
	
	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		String filename = null;
		if (flavor == TAKE_LANDSCAPE_PHOTOS && pendingPictureDirection != null) {
			filename = plotName + "_" + pendingPictureDirection + "_" + System.currentTimeMillis() + ".jpg"; 
			
			if (isCloseEnoughToCardinal(pendingPictureDirection, EAST)) {
				eastImageFilename = filename;
			} else if (isCloseEnoughToCardinal(pendingPictureDirection, SOUTH)) {
				southImageFilename = filename;
			} else if (isCloseEnoughToCardinal(pendingPictureDirection, WEST)) {
				westImageFilename = filename;
			} else if (isCloseEnoughToCardinal(pendingPictureDirection, NORTH)) {
				northImageFilename = filename;
			}
		} else if (flavor == TAKE_PIT_PHOTO) {
			filename = plotName + "_pit_" + System.currentTimeMillis() + ".jpg"; 
		} else if (flavor == TAKE_SAMPLES_PHOTO) {
			filename = plotName + "_samples_" + System.currentTimeMillis() + ".jpg"; 
		}
		
		boolean saved = true;
		try {
			FileOutputStream fOS = openFileOutput(filename, Context.MODE_PRIVATE); 
			fOS.write(data);
			fOS.close();
		} catch (Exception eX) {
			saved = false;
			Toast.makeText(this, "Photo capture failed, please try again", Toast.LENGTH_LONG).show(); //TODO: do this only for landscape, let calling activity handle otherwise
		}
		
		if (saved) {
			if (flavor == TAKE_LANDSCAPE_PHOTOS) {
				if (isCloseEnoughToCardinal(pendingPictureDirection, EAST)) {
					compass.setEastChecked(true);
					if (!compassOn) {
						pendingPictureDirection = getNextPictureDirection();
						compass.setDirection(pendingPictureDirection);
					}
				} else if (isCloseEnoughToCardinal(pendingPictureDirection, SOUTH)) {
					compass.setSouthChecked(true);
					if (!compassOn) {
						pendingPictureDirection = getNextPictureDirection();
						compass.setDirection(pendingPictureDirection);
					}
				} else if (isCloseEnoughToCardinal(pendingPictureDirection, WEST)) {
					compass.setWestChecked(true);
					if (!compassOn) {
						pendingPictureDirection = getNextPictureDirection();
						compass.setDirection(pendingPictureDirection);
					}
				} else if (isCloseEnoughToCardinal(pendingPictureDirection, NORTH)) {
					compass.setNorthChecked(true);
					if (!compassOn) {
						pendingPictureDirection = getNextPictureDirection();
						compass.setDirection(pendingPictureDirection);
					}
				}
			}
		}
		
		if (flavor == TAKE_LANDSCAPE_PHOTOS && pendingPictureDirection != null) {
			if (eastImageFilename != null &&
				southImageFilename != null &&
				westImageFilename != null &&
				northImageFilename != null) {
				//return to previous activity
				Intent intent = new Intent();
				intent.putExtra("eastImageFilename", eastImageFilename);
				intent.putExtra("southImageFilename", southImageFilename);
				intent.putExtra("westImageFilename", westImageFilename);
				intent.putExtra("northImageFilename", northImageFilename);
				setResult(saved ? 1 : 0, intent); 
				finish();
			}
		} else {
			Intent intent = new Intent();
			intent.putExtra("filename", filename);
			setResult(saved ? 1 : 0, intent); 
			finish();			
		}
		
		captureButton.setClickable(true);
		camera.startPreview();
	}
	
	private boolean isCloseEnoughToCardinal(int direction, int cardinalDirection) {
		boolean retVal = false;
		switch (cardinalDirection) {
			case NORTH:
				retVal = direction > NORTH + TOTS - HALF_GOOD_ENOUGH || direction < NORTH + HALF_GOOD_ENOUGH;
				break;
			case EAST:
				retVal = direction > EAST - HALF_GOOD_ENOUGH && direction < EAST + HALF_GOOD_ENOUGH;
				break;
			case SOUTH:
				retVal = (direction > SOUTH - HALF_GOOD_ENOUGH && direction < SOUTH + HALF_GOOD_ENOUGH);
				break;
			case WEST:
				retVal = (direction > WEST - HALF_GOOD_ENOUGH && direction < WEST + HALF_GOOD_ENOUGH);
				break;
		}
		
		return retVal;
	}

	private class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
		private SurfaceHolder holder;
		private Camera camera;
		
		public CameraPreview(Context context, Camera camera) {
			super(context);
			this.camera = camera;
			holder = getHolder();
			holder.addCallback(this);
			holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); //required for pre-Honeycomb
		}

		public void setCamera(Camera camera) {
			this.camera = camera;
		}
		
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			if (camera != null)
				camera.startPreview();
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			try {
				if (camera != null)
					camera.setPreviewDisplay(holder);
			} catch (IOException e) {}
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			if (camera != null)
				camera.stopPreview();
		}
	}
}
