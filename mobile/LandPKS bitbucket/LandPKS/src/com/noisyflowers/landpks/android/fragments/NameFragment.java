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
 * com.noisyflowers.landpks.android.fragments
 * NameFragment.java
 */

package com.noisyflowers.landpks.android.fragments;

//import com.nineoldandroids.*;
//import com.nineoldandroids.animation.*;
//import com.nineoldandroids.util.*;
//import com.nineoldandroids.view.*;
//import com.nineoldandroids.view.animation.*;

import com.noisyflowers.landpks.android.LandPKSApplication;
import com.noisyflowers.landpks.android.activities.PlotEditDetailActivity;
import com.noisyflowers.landpks.android.activities.PlotEditListActivity;
import com.noisyflowers.landpks.android.activities.PlotListActivity;
import com.noisyflowers.landpks.android.R;
import com.noisyflowers.landpks.android.model.Plot;
import com.noisyflowers.landpks.android.util.PlotEditFragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;

public class NameFragment extends PlotEditFragment  implements OnClickListener {
	private static final String TAG = NameFragment.class.getName(); 

	public static final String DISPLAY_NAME = "Plot ID";
	
	private static final double GPS_ACCURACY_THRESHOLD = 50.0;   //TODO: specs say 10, but causing problems with S3

	private LocationManager locationManager;
	private String provider;
	private LocationListener locationListener = new MyLocationListener();
	private EditText latitudeTextView, longitudeTextView;
	private boolean newPlot;
	
	private EditText nameField;
	private EditText recorderNameField;
	private EditText organizationField;
	private EditText latField, lonField;
	
	private Button gpsStartButton, gpsCancelButton;
	
	private RadioGroup testRG;
	
	//private AnimatorSet fadeOutIn;
	
	private int defaultTextColor = Color.BLACK;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		//getFragmentManager().beginTransaction().add(this, DISPLAY_NAME).commit();
	
        //defaultTextColor = TextView.getTextColor(getActivity(), null, 0);

		//only do location if new site, otherwise load from db
		newPlot = ((PlotEditDetailActivity)getActivity()).isNewPlot();
		//if (newPlot) {
		/***
		if (LandPKSApplication.getInstance().getPlot().latitude == null) {
	        locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
			
	        Criteria criteria = new Criteria();
	        criteria.setAccuracy(Criteria.ACCURACY_FINE);
	        criteria.setAltitudeRequired(false);
	        criteria.setBearingRequired(false);
	        criteria.setCostAllowed(true);
	        criteria.setPowerRequirement(Criteria.POWER_LOW);
	        provider = locationManager.getBestProvider(criteria, true);
	        //Location location = locationManager.getLastKnownLocation(provider);
	        //updateWithNewLocation(location);     
			//if (locationManager != null && provider != null) {
			//	locationManager.requestLocationUpdates(provider, 20000, 1, locationListener);
			//}
		} 
		***/
		if (savedInstanceState != null) {
			Boolean gpsActive = savedInstanceState.getBoolean("locationManagerActive");
			if (gpsActive != null && gpsActive) {
				startGPS();
			}
		}
	}

	private Handler handler = new Handler();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_name, container, false);

        latitudeTextView = (EditText)view.findViewById(R.id.latitudeEditText);
        longitudeTextView = (EditText)view.findViewById(R.id.longitudeEditText);
        nameField = (EditText)view.findViewById(R.id.nameEditText);
        recorderNameField = (EditText)view.findViewById(R.id.recorderNameEditText);
        organizationField = (EditText)view.findViewById(R.id.organizationEditText);
         
        testRG = (RadioGroup)view.findViewById(R.id.name_fragment_testPlotRadioGroup);
        
        defaultTextColor = latitudeTextView.getCurrentTextColor();
        
        latitudeTextView.addTextChangedListener(new MinMaxTextWatcher(-90.0, 90.0));
        longitudeTextView.addTextChangedListener(new MinMaxTextWatcher(-180.0, 180.0));
        
        if (!newPlot) nameField.setEnabled(false);
        
        //if (((PlotEditDetailActivity)getActivity()).isNewPlot()) {
        /***
        if (newPlot) {
	        nameField.setOnFocusChangeListener(new OnFocusChangeListener() {
	        	@Override
	        	public void onFocusChange(View v, boolean hasFocus) {
	        		
	        		if (!hasFocus) {
		        		if (LandPKSApplication.getInstance().getDatabaseAdapter().plotExists(nameField.getText().toString())) {
		        			Toast.makeText(getActivity(), "plot with that name exists", Toast.LENGTH_SHORT).show();
		        			InputMethodManager iMM = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		        			iMM.hideSoftInputFromWindow(nameField.getWindowToken(), 0);
		        			//recorderNameField.clearFocus();
		        			//organizationField.clearFocus();
		        			//latitudeTextView.clearFocus();
		        			//longitudeTextView.clearFocus();
		        			//nameField.requestFocus();
		        			//iMM.showSoftInput(nameField, InputMethodManager.SHOW_IMPLICIT);
		        		}
	        		}
	        	}
	
	        });
        }
        ***/
        /***
         * 
         if (newPlot) {
         	add text changed listener  
         		afterTtextChanged {
         			if (plotNameExists)
         				pop warning dialog
         		}
         */
        /***
        latitudeTextView.addTextChangedListener(new TextWatcher() {
        	@Override
        	public void afterTextChanged(Editable s) {
        		try {
        			double d = Double.parseDouble(s.toString());
        			if (d > 90.0) {
        				s.replace(0, s.length(), "90.0", 0, 4);
        			} else if (d < -90.0) {
        				s.replace(0, s.length(), "-90.0", 0, 5);        				
        			}
        		} catch (NumberFormatException e) {}
        	}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				
			}
        });
        ***/
        
        gpsStartButton = (Button)view.findViewById(R.id.fragment_name_GPS_start_button);
        gpsStartButton.setOnClickListener(this);
        gpsCancelButton = (Button)view.findViewById(R.id.fragment_name_GPS_cancel_button);
        gpsCancelButton.setOnClickListener(this);

        /***
        if (newPlot) {
        	nameField.addTextChangedListener(new TextWatcher() {

				@Override
				public void afterTextChanged(Editable arg0) {
	        	    if(arg0.length() > 0){
		        		if (LandPKSApplication.getInstance().getDatabaseAdapter().plotExists(nameField.getText().toString())) {
		        			Toast.makeText(getActivity(), "plot with that name exists", Toast.LENGTH_SHORT).show();
		        		} else {
		        			runUpAnimation();
		        		}
	        	    } else {
	        			//if (fadeOutIn != null) fadeOutIn.removeAllListeners();
	        	        //ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
	        	        //actionBar.setIcon(R.drawable.ic_launcher);
	        			stopUpAnimation();
	        	    }
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
				}

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
				}
        		
        	});
        }
        ***/

        /***
        //if (newPlot) {
		if (LandPKSApplication.getInstance().getPlot().latitude == null) {
        	latitudeTextView.setTextColor(Color.RED);
        	latitudeTextView.setText("obtaining...");
        	//latitudeTextView.setText("0");	//TODO: set to 0 just for testing in emu
        	longitudeTextView.setTextColor(Color.RED);
        	longitudeTextView.setText("obtaining...");
        	//longitudeTextView.setText("0");  //TODO: set to 0 just for testing in emu
        
        	nameField.addTextChangedListener(new TextWatcher() {

				@Override
				public void afterTextChanged(Editable arg0) {
	        	    if(arg0.length() > 0){
	        			boolean latitudeGood = false;
	        			boolean longitudeGood = false;
	        			try { Double.parseDouble(latitudeTextView.getText().toString()); latitudeGood = true; 
	        			 	  Double.parseDouble(longitudeTextView.getText().toString()); longitudeGood = true; } catch (Exception e){}
	        			if (latitudeGood && longitudeGood)
	        	    		runUpAnimation();
	        	    } else {
	        			//if (fadeOutIn != null) fadeOutIn.removeAllListeners();
	        	        //ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
	        	        //actionBar.setIcon(R.drawable.ic_launcher);
	        			stopUpAnimation();
	        	    }
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
				}

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
				}
        		
        	});
        } 
        ***/
        
	    return view;
	}
	
	/***
	private void runUpAnimation() {
		if (fadeOutIn == null) {
			ActionBar aB = ((ActionBarActivity)getActivity()).getSupportActionBar();
			Drawable animIcon = getResources().getDrawable( R.drawable.ic_launcher );
			aB.setIcon(animIcon);
			ObjectAnimator fadeOut = ObjectAnimator.ofInt(animIcon, "alpha", 255, 0);
			fadeOut.setDuration(1000);
			ObjectAnimator fadeIn = ObjectAnimator.ofInt(animIcon, "alpha", 0, 255);
			fadeIn.setDuration(500);
			fadeOutIn = new AnimatorSet();
			fadeOutIn.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animator) {
					super.onAnimationEnd(animator);
					fadeOutIn.start();
				}
			});
			fadeOutIn.setStartDelay(1000);
			fadeOutIn.play(fadeOut).before(fadeIn);
			fadeOutIn.start();
		}
	}

	private void stopUpAnimation() {
		if (fadeOutIn != null) {
			fadeOutIn.removeAllListeners();
			ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
			actionBar.setIcon(R.drawable.ic_launcher);
			fadeOutIn = null;
		}
	}
	***/
	
	@Override
	public void onPause() {
		super.onPause();
		//if (locationManager != null && provider != null) {
		//	locationManager.removeUpdates(locationListener);
		//}
		stopGPS(null,null);
		
		//if (fadeOutIn != null) fadeOutIn.removeAllListeners();
        //ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
        //actionBar.setIcon(R.drawable.ic_launcher);
		//stopUpAnimation();
	}

	@Override
	public void onResume() {
		super.onResume();
    	if (newPlot) {
    		recorderNameField.setText(LandPKSApplication.getInstance().getCredential().getSelectedAccountName());
    	}
		/***
		boolean latitudeGood = false;
		boolean longitudeGood = false;
		try { Double.parseDouble(latitudeTextView.getText().toString()); latitudeGood = true; 
		 	  Double.parseDouble(longitudeTextView.getText().toString()); longitudeGood = true; } catch (Exception e){}
		if (!latitudeGood || !longitudeGood) {
			if (locationManager != null && provider != null) {
				locationManager.requestLocationUpdates(provider, 20000, 0, locationListener);   
			}
		}
		***/
		/**
		if (locationManager != null && provider != null) {
			locationManager.requestLocationUpdates(provider, 20000, 0, locationListener);   
		}
		**/
	}
	
	@Override
	public void onSaveInstanceState(Bundle state) {
		if (locationManager != null && provider != null) {
			state.putBoolean("locationManagerActive", true);
		} else {
			state.putBoolean("locationManagerActive", false);
			
		}
	}
	
	private void startGPS() {
		latitudeTextView.setFocusable(false);
		//latitudeTextView.setClickable(false);
    	latitudeTextView.setTextColor(Color.RED);
    	//latitudeTextView.setText("obtaining...");
    	latitudeTextView.setText(getString(R.string.name_fragment_gps_wait));
    	//latitudeTextView.setText("0");	//TODO: set to 0 just for testing in emu
		longitudeTextView.setFocusable(false);
		//longitudeTextView.setClickable(false);
    	longitudeTextView.setTextColor(Color.RED);
    	//longitudeTextView.setText("obtaining...");
    	longitudeTextView.setText(getString(R.string.name_fragment_gps_wait));
    	//longitudeTextView.setText("0");  //TODO: set to 0 just for testing in emu
    	gpsStartButton.setVisibility(View.GONE);
    	gpsCancelButton.setVisibility(View.VISIBLE);

    	locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
		
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        provider = locationManager.getBestProvider(criteria, true);		
		locationManager.requestLocationUpdates(provider, 20000, 0, locationListener);   	
	}
	
	private void stopGPS(Double lat, Double lon) {
		latitudeTextView.setFocusable(true);
		latitudeTextView.setFocusableInTouchMode(true);
		//latitudeTextView.setClickable(true);
		//latitudeTextView.setTextColor(Color.BLACK);
		latitudeTextView.setTextColor(defaultTextColor);
		latitudeTextView.setText(lat == null ? null : Double.toString(lat));
		longitudeTextView.setFocusable(true);
		longitudeTextView.setFocusableInTouchMode(true);
		//longitudeTextView.setClickable(true);
		//longitudeTextView.setTextColor(Color.BLACK);
		longitudeTextView.setTextColor(defaultTextColor);
		longitudeTextView.setText(lon == null ? null : Double.toString(lon));
    	gpsCancelButton.setVisibility(View.GONE);
    	gpsStartButton.setVisibility(View.VISIBLE);
		if (locationManager != null && provider != null) {
			locationManager.removeUpdates(locationListener);
		}		
		locationManager = null;  //TODO: setting to null might not be a good idea here
		provider = null;
	}
		
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.fragment_name_GPS_start_button:
				try {
					InputMethodManager iMM = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
					iMM.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
				} catch (Exception eX) {}
				startGPS();
		        break;
			case R.id.fragment_name_GPS_cancel_button:
				Plot plot = LandPKSApplication.getInstance().getPlot();
				/**
				latitudeTextView.setFocusable(true);
				latitudeTextView.setClickable(true);
				latitudeTextView.setTextColor(Color.BLACK);
				latitudeTextView.setText(Double.toString(plot.latitude));
				longitudeTextView.setFocusable(true);
				longitudeTextView.setClickable(true);
				longitudeTextView.setTextColor(Color.BLACK);
				longitudeTextView.setText(Double.toString(plot.longitude));
		    	gpsCancelButton.setVisibility(View.GONE);
		    	gpsStartButton.setVisibility(View.VISIBLE);
				if (locationManager != null && provider != null) {
					locationManager.removeUpdates(locationListener);
				}		
				locationManager = null;  //TODO: setting to null might not be a good idea here
				provider = null;
				**/
				stopGPS(plot.latitude, plot.longitude);
				break;
		}
	}
	
	
    private void updateWithNewLocation(Location location) {
    	if (location != null){
    		/**
			latitudeTextView.setTextColor(Color.BLACK);
   			latitudeTextView.setText(Double.toString(location.getLatitude()));
   			latitudeTextView.setFocusable(true);
   			latitudeTextView.setClickable(true);
   			longitudeTextView.setTextColor(Color.BLACK);
   			longitudeTextView.setText(Double.toString(location.getLongitude()));
   			longitudeTextView.setFocusable(true);
   			longitudeTextView.setClickable(true);
   			if (locationManager != null) locationManager.removeUpdates(locationListener); //could be null if cancelled 
   			gpsCancelButton.setVisibility(View.GONE);
	  		gpsStartButton.setVisibility(View.VISIBLE);
    		**/
    		stopGPS(location.getLatitude(), location.getLongitude());
	       //if (nameField.getText() != null && !"".equals(nameField.getText().toString())) 
	       //runUpAnimation();
    	}
    }
    
    private class MyLocationListener implements LocationListener {
    	
    	public void onLocationChanged(Location location) {
    		Log.i(TAG, "location accuracy = " + location.getAccuracy());
    		if (locationManager != null) {  //if locationManager == null, fix was cancelled
	    		if (location.hasAccuracy() && location.getAccuracy() < GPS_ACCURACY_THRESHOLD) { 
	    			updateWithNewLocation(location);
	    		}
    		}
    	}
    	public void onProviderDisabled(String provider) {
    	}
    	public void onProviderEnabled(String provider) {
    	}
    	public void onStatusChanged(String provider, int status, Bundle extras) {
    	}
    } 
	
    /***
     * TODO: see if this will work instead of doing it in save
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }   
    ***/
    
	@Override
    public void load(Plot plot) {
		if (plot.testPlot != null) {
			if (plot.testPlot) {
				testRG.check(R.id.name_fragment_testPlot_yes_RadioButton);
			} else  {
				testRG.check(R.id.name_fragment_testPlot_no_RadioButton);
			}
		}
		
    	nameField.setText(plot.name);
    	recorderNameField.setText(plot.recorderName);
    	organizationField.setText(plot.organization);
    	try { latitudeTextView.setText(Double.toString(plot.latitude)); } catch (Exception e){latitudeTextView.setText(null);}
    	try { longitudeTextView.setText(Double.toString(plot.longitude));  } catch (Exception e){longitudeTextView.setText(null);} 	
    }
    	
	@Override
    public void save(Plot plot) {
		if (newPlot &&
			(LandPKSApplication.getInstance().getDatabaseAdapter().plotExists(nameField.getText().toString()) || 
			((PlotEditDetailActivity)getActivity()).isAborted())) {
			//Toast.makeText(getActivity(), getString(R.string.name_fragment_plot_already_exists), Toast.LENGTH_LONG).show();
			InputMethodManager iMM = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			iMM.hideSoftInputFromWindow(nameField.getWindowToken(), 0);
			return;
		}
		
		switch (testRG.getCheckedRadioButtonId()) {
			case R.id.name_fragment_testPlot_yes_RadioButton: 
				plot.testPlot= true;
				break;
			case R.id.name_fragment_testPlot_no_RadioButton: 
				plot.testPlot= false;
				break;
			default:
				return;
		}
		
		boolean latitudeGood = false;
		boolean longitudeGood = false;
		try { Double.parseDouble(latitudeTextView.getText().toString()); latitudeGood = true; 
		 	  Double.parseDouble(longitudeTextView.getText().toString()); longitudeGood = true; } catch (Exception e){}
		try { 
			plot.name = nameField.getText().toString(); 
			plot.name = "".equals(plot.name) ? null : plot.name;
		} catch (Exception e){}
		try { plot.recorderName = recorderNameField.getText().toString(); } catch (Exception e){}
		try { plot.organization = organizationField.getText().toString(); } catch (Exception e){}	
		if (nameField == null || nameField.getText() == null || "".equals(nameField.getText()) /* || 
				!latitudeGood || !longitudeGood*/)
				return;
		//try { plot.name = nameField.getText().toString(); } catch (Exception e){}
		//try { plot.recorderName = recorderNameField.getText().toString(); } catch (Exception e){}
		//try { plot.organization = organizationField.getText().toString(); } catch (Exception e){}	
		try { plot.latitude = Double.parseDouble(latitudeTextView.getText().toString()); } catch (Exception e){}
		try { plot.longitude = Double.parseDouble(longitudeTextView.getText().toString()); } catch (Exception e){}
		
		if (plot.name != null) LandPKSApplication.getInstance().getDatabaseAdapter().addPlot(plot);
		newPlot = false;
    }
    
	@Override
	public boolean isComplete(Plot plot) {
    	return plot.name != null && plot.recorderName != null && plot.organization != null && plot.latitude != null && plot.longitude != null &&
    			!"".equals(plot.name) && !"".equals(plot.recorderName) && !"".equals(plot.organization);
    }
	
	//TODO: Experimental way of getting dialog instead of Toast for duplicate plot names. Required rewiring of this in PlotEditDetailActivity and PlotEditFragment.
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean retVal = false;
		switch (item.getItemId()) {
			case android.R.id.home:
				if (newPlot &&
					LandPKSApplication.getInstance().getDatabaseAdapter().plotExists(nameField.getText().toString())) {
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			    	builder.setMessage(getString(R.string.name_fragment_plot_already_exists))
			    			   .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
			    				   @Override
			    				   public void onClick(DialogInterface dialog, int which) {
			    					   nameField.requestFocus();
			     				   }
			    			   });
			    	AlertDialog alert = builder.create();
			    	alert.show();
			    	retVal = true;
				} else if (testRG.getCheckedRadioButtonId() == -1) {
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			    	builder.setMessage(getString(R.string.name_fragment_test_plot_answer_rqd))
			    			   .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
			    				   @Override
			    				   public void onClick(DialogInterface dialog, int which) {
			     				   }
			    			   });
			    	AlertDialog alert = builder.create();
			    	alert.show();
			    	retVal = true;
				} else {
					retVal = super.onOptionsItemSelected(item);
				}
				break;
			default:
				retVal = super.onOptionsItemSelected(item);
				break;
		}
		return retVal;
	}
	
	private class MinMaxTextWatcher implements TextWatcher {
		
		//boolean ignore = false;
		double min, max;
		
		MinMaxTextWatcher(double min, double max) {
			this.min = min;
			this.max = max;
		}
    	@Override
    	public void afterTextChanged(Editable s) {
    		//if (!ignore) {
	    		try {
	    			double d = Double.parseDouble(s.toString());
	    			if (d > max) {
	    				s.replace(0, s.length(), Double.toString(max));
	    	    		//ignore = true;
	    			} else if (d < min) {
	    				s.replace(0, s.length(), Double.toString(min));        				
	    	    		//ignore = true;
	    			}
	    		} catch (NumberFormatException e) {}
    		//} else {
    		//	ignore = false;
    		//}
    	}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1,
				int arg2, int arg3) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
			
		}
        	
	}
}
