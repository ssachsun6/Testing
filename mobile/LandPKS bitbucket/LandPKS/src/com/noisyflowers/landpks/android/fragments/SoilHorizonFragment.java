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
 * SoilHorizonFragment.java
 */

package com.noisyflowers.landpks.android.fragments;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.noisyflowers.landpks.android.LandPKSApplication;
import com.noisyflowers.landpks.android.R;
import com.noisyflowers.landpks.android.activities.SoilTextureActivity;
import com.noisyflowers.landpks.android.activities.SoilTextureActivity.SoilTexture;
import com.noisyflowers.landpks.android.model.Plot;
import com.noisyflowers.landpks.android.model.SoilHorizon;
import com.noisyflowers.landpks.android.util.ColorConstants;
import com.noisyflowers.landpks.android.util.PlotEditFragment;

public class SoilHorizonFragment extends PlotEditFragment  implements OnClickListener/*, OnItemSelectedListener*/ {
	
	private static final int COLOR_APP_REQUEST_CODE = 1;
	private static final int TEXTURE_APP_REQUEST_CODE = 2;

	public enum SoilRockFragmentVolume {
		ROCK_VOLUME_1 (R.string.server_resource_rock_volume_1, R.string.soil_horizon_fragment_rock_volume_1),
		ROCK_VOLUME_2 (R.string.server_resource_rock_volume_2, R.string.soil_horizon_fragment_rock_volume_2),
		ROCK_VOLUME_3 (R.string.server_resource_rock_volume_3, R.string.soil_horizon_fragment_rock_volume_3),
		ROCK_VOLUME_4 (R.string.server_resource_rock_volume_4, R.string.soil_horizon_fragment_rock_volume_4),
		ROCK_VOLUME_5 (R.string.server_resource_rock_volume_5, R.string.soil_horizon_fragment_rock_volume_5);

		private final int serverName, displayName;
		
		public static final Map<String, SoilRockFragmentVolume> displayNameLookup = new HashMap<String, SoilRockFragmentVolume>();
		static {
			for (SoilRockFragmentVolume s : SoilRockFragmentVolume.values()) {
				displayNameLookup.put(LandPKSApplication.getInstance().getString(s.displayName), s);
			}
		}

		public static final Map<String, SoilRockFragmentVolume> serverNameLookup = new HashMap<String, SoilRockFragmentVolume>();
		static {
			for (SoilRockFragmentVolume s : SoilRockFragmentVolume.values()) {
				serverNameLookup.put(LandPKSApplication.getInstance().getString(s.serverName), s);
			}
		}

		SoilRockFragmentVolume(int serverName, int displayName) {
			this.serverName = serverName;
			this.displayName = displayName;
		}
		
		public String getDisplayName() {
			return LandPKSApplication.getInstance().getString(displayName);
		}
		
		public String getServerName() {
			return LandPKSApplication.getInstance().getString(serverName);
		}
	}
	
	private TextView horizonNameView, redTextView, greenTextView, blueTextView, /*textureTextView,*/ munsellTextView;
	private String name;
	private Spinner spinner, fragmentSpinner;
	
	private boolean newColorValues = false;
	private boolean newTextureValue = false;
	private boolean newFragmentValue = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
    	name = "";
		try {
			name = getArguments().getString("Name");
		} catch (Exception ex) {}	
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_soil_horizon, container, false);
        
        Button b = (Button) view.findViewById(R.id.colorButton);
        b.setOnClickListener(this);
        b = (Button) view.findViewById(R.id.textureButton);
        b.setOnClickListener(this);
 		
        horizonNameView = (TextView)view.findViewById(R.id.horizonNameView);
        horizonNameView.setText(name);
        
        redTextView = (TextView)view.findViewById(R.id.redValue);
        greenTextView = (TextView)view.findViewById(R.id.greenValue);
        blueTextView = (TextView)view.findViewById(R.id.blueValue);
        //textureTextView = (TextView)view.findViewById(R.id.textureValue);
        munsellTextView = (TextView)view.findViewById(R.id.munsellValue);

        spinner = (Spinner) view.findViewById(R.id.fragment_soil_horizon_texture_spinner);
        /***
        ArrayAdapter<String> adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.add(getString(R.string.soil_horizon_fragment_choose_texture));
        for (SoilTexture s : SoilTexture.values()) {
        	adapter.add(s.getDisplayName());
        }
        ***/
        SoilTextureAdapter adapter = new SoilTextureAdapter(getActivity());
        spinner.setAdapter(adapter);
        
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
	    	@Override
	    	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
	    		newTextureValue = true;
	    	}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
        });

        fragmentSpinner = (Spinner) view.findViewById(R.id.fragment_soil_horizon_rockfragment_spinner);
        RockFragmentVolumeAdapter rockFragmentAdapter = new RockFragmentVolumeAdapter(getActivity());
        fragmentSpinner.setAdapter(rockFragmentAdapter);
        
        fragmentSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
	    	@Override
	    	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
	    		newFragmentValue = true;
	    	}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
        });
  
        return view;
	}
        
	@Override
	public void load(Plot plot) {
		//redTextView.setText(null);
		//greenTextView.setText(null);
		//blueTextView.setText(null);
		//munsellTextView.setText(null);
		//spinner.setSelection(0);
		//fragmentSpinner.setSelection(0);

		SoilHorizon horizon = plot.soilHorizons.get(name);
		if (horizon != null) {
			if (!newFragmentValue) {
				setSpinnerByName(fragmentSpinner, horizon.rockFragment == null ? null : SoilRockFragmentVolume.valueOf(horizon.rockFragment));
			}
			if (!newColorValues) { 
				if (horizon.color != null) {
					int mask = 0xFF;
					int red = (horizon.color >> 16) & mask;
					int green = (horizon.color >> 8) & mask;
					int blue = (horizon.color) & mask;
					redTextView.setText(Integer.toString(red));
					greenTextView.setText(Integer.toString(green));
					blueTextView.setText(Integer.toString(blue));
					munsellTextView.setText(calculateMunsell(horizon.color));
				}
			}
			if (!newTextureValue) {
				//setSpinnerByName(spinner, horizon.texture == null ? null : SoilTexture.valueOf(horizon.texture).getDisplayName());
				setSpinnerByName(spinner, horizon.texture == null ? null : SoilTexture.valueOf(horizon.texture));
			}
		}
	}

	@Override
	public void save(Plot plot) {
		if (!newFragmentValue && !newColorValues && ! newTextureValue) return;
			SoilHorizon horizon = new SoilHorizon();
			if (fragmentSpinner.getSelectedItemPosition() > 0)  //0 is change prompt
				horizon.rockFragment = ((SoilRockFragmentVolume)fragmentSpinner.getSelectedItem()).name(); 
			try { //we might be here with null or blank number fields; just ignore
				int red = Integer.parseInt(redTextView.getText().toString());
				red = red << 16 & 0xFF0000;
				int green = Integer.parseInt(greenTextView.getText().toString());
				green = green << 8 & 0xFF00;
				int blue = Integer.parseInt(blueTextView.getText().toString());
				blue = blue & 0xFF;
				int color = red + green + blue;
				horizon.color = color;
			} 
			catch (NumberFormatException nFE){}
			catch (NullPointerException nPE){}
			/***
			String textureStr = spinner.getSelectedItem().toString();
			if (!getString(R.string.soil_horizon_fragment_choose_texture).equals(textureStr)) 
				horizon.texture = SoilTexture.displayNameLookup.get(textureStr).name();
			***/ 
			if (spinner.getSelectedItemPosition() > 0)  //0 is change prompt
				horizon.texture = ((SoilTexture)spinner.getSelectedItem()).name(); 
			plot.soilHorizons.put(name, horizon);
			LandPKSApplication.getInstance().getDatabaseAdapter().addPlot(plot);
			newFragmentValue = false;
			newColorValues = false;
			newTextureValue = false;
		//}
	}

	public boolean isRestrictiveLayer(Plot plot) {
		String name = getArguments().getString("Name");
		SoilHorizon horizon = plot.soilHorizons.get(name);
		return horizon != null && SoilRockFragmentVolume.ROCK_VOLUME_5.name().equals(horizon.rockFragment);
	}
	
	@Override
	public boolean isComplete(Plot plot) {
		String name = getArguments().getString("Name");
		SoilHorizon horizon = plot.soilHorizons.get(name);
		//return horizon != null && (SoilRockFragmentVolume.ROCK_VOLUME_5.name().equals(horizon.rockFragment) || (horizon.rockFragment != null && horizon.color != null && horizon.texture != null));
		return horizon != null && (SoilRockFragmentVolume.ROCK_VOLUME_5.name().equals(horizon.rockFragment) || (horizon.rockFragment != null && horizon.texture != null));
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.colorButton: {
				Intent intent = new Intent("com.noisyflowers.android.soilanalysis.ACTION_GET_SOIL_COLOR");
				PackageManager packageManager = getActivity().getPackageManager();
				List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
				boolean isIntentSafe = activities.size() > 0;

				// Start an activity if it's safe
				if (isIntentSafe) {				
					startActivityForResult(intent, COLOR_APP_REQUEST_CODE);
				} else {
					//Toast.makeText(getActivity(), "Cannot find soil color app", Toast.LENGTH_LONG).show();					
					Toast.makeText(getActivity(), getString(R.string.soil_horizon_fragment_cannot_find_color_app), Toast.LENGTH_LONG).show();					
				}
			}
			break;
			case R.id.textureButton: {
				/***
				Intent intent = new Intent("com.noisyflowers.android.soilanalysis.ACTION_GET_SOIL_TEXTURE");
				PackageManager packageManager = getActivity().getPackageManager();
				List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
				boolean isIntentSafe = activities.size() > 0;

				// Start an activity if it's safe
				if (isIntentSafe) {				
					startActivityForResult(intent, TEXTURE_APP_REQUEST_CODE);
				} else {
					Toast.makeText(getActivity(), "Cannot find soil texture app", Toast.LENGTH_LONG).show();		
					textureTextView.setText("smooooov"); //TODO: for testing
					newTextureValue = true; //TODO: for testing
				}
				***/
				Intent intent = new Intent(getActivity(), SoilTextureActivity.class);
				intent.putExtra("horizon", name);
				//intent.putExtra("position", ((SoilHorizonActivity)getActivity()).position);
				//startActivity(intent);
				startActivityForResult(intent, TEXTURE_APP_REQUEST_CODE);
			}
			break;
		}
	}
	
	//@Override
	//public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
	//	newTextureValue = true;
	//}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == COLOR_APP_REQUEST_CODE) {
			if (data != null) {
				//TODO:  should probably pass value back through data intent rather than resultCode
				int mask = 0xFF;
				int red = (resultCode >> 16) & mask;
				int green = (resultCode >> 8) & mask;
				int blue = (resultCode) & mask;
				redTextView.setText(Integer.toString(red));
				greenTextView.setText(Integer.toString(green));
				blueTextView.setText(Integer.toString(blue));
				newColorValues = true;
	
				munsellTextView.setText(calculateMunsell(resultCode));
	
				//TODO: munsell in db?
				String munsell = data.getStringExtra("Munsell");
				Log.i("!!!!!!", munsell);
			}
		} else if (requestCode == TEXTURE_APP_REQUEST_CODE) {
			if (resultCode == 1) {
				if (data != null) {
					//textureTextView.setText(data.getStringExtra("texture"));
					
					/***
					ArrayAdapter aA = (ArrayAdapter)spinner.getAdapter();
					for (int i = 0; i < aA.getCount(); i++) {
						if (data.getStringExtra("texture").equals(aA.getItem(i))) {
							spinner.setSelection(i);
							break;
						}
					}
					***/
					//setTextureSpinnerByName(data.getStringExtra("texture"));
					//setSpinnerByName(spinner, data.getStringExtra("texture"));
					SoilTexture texture = SoilTexture.valueOf(data.getStringExtra("texture"));
					setSpinnerByName(spinner, texture);
					newTextureValue = true;
				}
			}
		}
	}
	
	private void setSpinnerByName(Spinner spinner, Object name) {
		if (name == null) {
			spinner.setSelection(0);
			return;
		}
		
		SpinnerAdapter aA = spinner.getAdapter();
		for (int i = 0; i < aA.getCount(); i++) {
			if (name.equals(aA.getItem(i))) {
				spinner.setSelection(i);
				break;
			}
		}		
	}
	
	private String calculateMunsell(int color) {
		int red = Color.red(color);
		int green = Color.green(color);
		int blue = Color.blue(color);
		
		double[] distances = new double[ColorConstants.NUMBER_OF_SUPPORTED_MUNSELL_VALUES];
		for (int i = 0; i < ColorConstants.NUMBER_OF_SUPPORTED_MUNSELL_VALUES; i++) {
			//((r - r1) * .299)^2 + ((g - g1) * .587)^2 + ((b - b1) * .114)^2
			distances[i] = Math.pow(((red - ColorConstants.MUNSELL_SRGB_RED_VALUES[i]) * 0.299), 2) +
						   Math.pow(((green - ColorConstants.MUNSELL_SRGB_GREEN_VALUES[i]) * 0.587), 2) +
						   Math.pow(((blue - ColorConstants.MUNSELL_SRGB_BLUE_VALUES[i]) * 0.114), 2);
		}
		
		int indexOfMin = 0;
		double min = distances[indexOfMin];
		for (int i = 0; i < ColorConstants.NUMBER_OF_SUPPORTED_MUNSELL_VALUES; i++) {
			if (distances[i] < min) {
				indexOfMin = i;
				min = distances[i];
			}
		}	
		
		return ColorConstants.MUNSELL_SPECS[indexOfMin];
	}

	//@Override
	//public void onNothingSelected(AdapterView<?> arg0) {
	//	// TODO Auto-generated method stub
	//	
	//}
	
	
	private class RockFragmentVolumeAdapter extends BaseAdapter {
		Context context;
		
		public RockFragmentVolumeAdapter(Context ctx) { 
			this.context = ctx;
		} 
		
		
		@Override 
		public View getDropDownView(int position, View cnvtView, ViewGroup parent) { 
			LayoutInflater inflater = LayoutInflater.from(context); 
			View mySpinner = inflater.inflate(R.layout.rock_fragment_volume_spinner, parent, false); 
			
			switch (position) {
					case 0: {
						TextView v = (TextView)mySpinner.findViewById(R.id.rock_fragment_volume_text_view);
						v.setVisibility(View.GONE);
						v.setText(getString(R.string.soil_horizon_fragment_choose_fragment));
						break;
					}
					case 1: {
						ImageView v = (ImageView)mySpinner.findViewById(R.id.rock_fragment_volume_image_view);
						v.setVisibility(View.VISIBLE);
						v.setImageResource(R.drawable.custom_rock_fragment_volume_1);
						break;
					}
					case 2: {
						ImageView v = (ImageView)mySpinner.findViewById(R.id.rock_fragment_volume_image_view);
						v.setVisibility(View.VISIBLE);
						v.setImageResource(R.drawable.custom_rock_fragment_volume_2);
						break;
					}
					case 3: {
						ImageView v = (ImageView)mySpinner.findViewById(R.id.rock_fragment_volume_image_view);
						v.setVisibility(View.VISIBLE);
						v.setImageResource(R.drawable.custom_rock_fragment_volume_3);
						break;
					}
					case 4: {
						ImageView v = (ImageView)mySpinner.findViewById(R.id.rock_fragment_volume_image_view);
						v.setVisibility(View.VISIBLE);
						v.setImageResource(R.drawable.custom_rock_fragment_volume_4);
						break;
					}
					case 5: {
						TextView v = (TextView)mySpinner.findViewById(R.id.rock_fragment_volume_text_view);
						v.setVisibility(View.VISIBLE);
						v.setText(SoilRockFragmentVolume.values()[position-1].getDisplayName());
						break;	
					}
				}
			
			return mySpinner;
		} 
		
		@Override 
		public View getView(int position, View cnvtView, ViewGroup parent) { 
			LayoutInflater inflater = LayoutInflater.from(context);//getActivity().getLayoutInflater(); 
			View myThing = inflater.inflate(R.layout.rock_fragment_volume_spinner_selected, parent, false); 
			TextView tV = (TextView)myThing.findViewById(R.id.rock_fragment_volume_spinner_selected_text_view);
			if (position == 0) {
				tV.setText(getString(R.string.soil_horizon_fragment_choose_fragment));
			} else {
				tV.setText(SoilRockFragmentVolume.values()[position-1].getDisplayName());
			}
			return tV;
		}


		@Override
		public SoilRockFragmentVolume getItem(int position) {
			// TODO Auto-generated method stub
			//return super.getItem(position);
			if (position == 0) {
				return null;
			} else {
				return (SoilRockFragmentVolume.values()[position-1]);
			}
		}


		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return SoilRockFragmentVolume.values().length + 1;
		}


		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			if (position == 0) {
				return 0;
			} else {
				return SoilRockFragmentVolume.values()[position-1].hashCode();//??
			}
		} 

	}

	private class SoilTextureAdapter extends BaseAdapter {
		Context context;
		
		public SoilTextureAdapter(Context ctx) { 
			this.context = ctx;
		} 
		
		
		@Override 
		public View getDropDownView(int position, View cnvtView, ViewGroup parent) { 
			LayoutInflater inflater = LayoutInflater.from(context); 
			View mySpinner = inflater.inflate(R.layout.rock_fragment_volume_spinner, parent, false); 
			
			TextView v = (TextView)mySpinner.findViewById(R.id.rock_fragment_volume_text_view);
			switch (position) {
					case 0: {
						v.setVisibility(View.GONE);
						break;
					}
					default: {
						v.setVisibility(View.VISIBLE);
						v.setText(SoilTexture.values()[position-1].getDisplayName());
						break;	
					}
				}
			
			return mySpinner;
		} 
		
		@Override 
		public View getView(int position, View cnvtView, ViewGroup parent) { 
			LayoutInflater inflater = LayoutInflater.from(context);//getActivity().getLayoutInflater(); 
			View myThing = inflater.inflate(R.layout.rock_fragment_volume_spinner_selected, parent, false); 
			TextView tV = (TextView)myThing.findViewById(R.id.rock_fragment_volume_spinner_selected_text_view);
			if (position == 0) {
				tV.setText(getString(R.string.soil_horizon_fragment_choose_fragment));
			} else {
				tV.setText(SoilTexture.values()[position-1].getDisplayName());
			}
			return tV;
		}


		@Override
		public SoilTexture getItem(int position) {
			// TODO Auto-generated method stub
			//return super.getItem(position);
			if (position == 0) {
				return null;
			} else {
				return (SoilTexture.values()[position-1]);
			}
		}


		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return SoilTexture.values().length + 1;
		}


		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			if (position == 0) {
				return 0;
			} else {
				return SoilTexture.values()[position-1].hashCode();//??
			}
		} 

	}
	
}
