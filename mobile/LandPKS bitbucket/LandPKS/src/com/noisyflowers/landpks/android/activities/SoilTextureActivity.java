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
 * SoilTextureActivity.java
 */

package com.noisyflowers.landpks.android.activities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.noisyflowers.landpks.android.LandPKSApplication;
import com.noisyflowers.landpks.android.R;
import com.noisyflowers.landpks.android.fragments.SlopeShapeFragment.SlopeShape;
import com.noisyflowers.landpks.android.fragments.SoilHorizonsFragment.HorizonName;
import com.noisyflowers.landpks.android.model.Plot;
import com.noisyflowers.landpks.android.util.PlotEditFragment;
import com.noisyflowers.landpks.android.util.PlotEditPagerAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class SoilTextureActivity extends ActionBarActivity {
	
	private static final int COLOR1 = Color.RED;
	private static final int COLOR2 = Color.rgb(0x2e, 0xb8, 0x2e);
	private static final int COLOR3 = Color.rgb(0xff, 0x99, 0);
	
	private String plotName;
	private String horizonName;
	private boolean newPlot = false;
	private int position = 0;
	
	private Boolean soilFormsBall = null;
	private Boolean soilFormsRibbon = null;
	
	private ScrollView decisionScroll;
	private TextView ballLabel, ribbonLabel, ribbonLengthLabel, feelLabel, soilTextureView;
	private RadioGroup ballGroup, ribbonGroup, ribbonLengthGroup, feelGroup;
	private Spinner spinner;
	private ViewGroup ballLayout, ribbonLayout, ribbonLengthLayout, feelLayout;
	private ImageView ribbonHelpImage;
	private Button okButton;
	
	/***
	private enum RibbonLength {
		RIBBON_LENGTH_1 ("<2.5cm"),
		RIBBON_LENGTH_2 ("2.5-5cm"),
		RIBBON_LENGTH_3 (">5cm");
		
		public final String name;
		
		RibbonLength(String name) {
			this.name = name;
		}
	}
	***/
	/***
	public enum RibbonLength {
		RIBBON_LENGTH_1 (R.string.server_resource_soil_texture_ribbon_length_1, R.string.soil_texture_activity_ribbon_length_1),
		RIBBON_LENGTH_2 (R.string.server_resource_soil_texture_ribbon_length_2, R.string.soil_texture_activity_ribbon_length_2),
		RIBBON_LENGTH_3 (R.string.server_resource_soil_texture_ribbon_length_3, R.string.soil_texture_activity_ribbon_length_3);
		
		private final int serverName, displayName;
		
		public static final Map<String, RibbonLength> displayNameLookup = new HashMap<String, RibbonLength>();
		static {
			for (RibbonLength s : RibbonLength.values()) {
				displayNameLookup.put(LandPKSApplication.getInstance().getString(s.displayName), s);
			}
		}

		RibbonLength(int serverName, int displayName) {
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
	private RibbonLength ribbonLength = null;
	
	//TODO: Would be better to get these from strings.xml.  But would need to rethink server interface.
	private enum SoilFeel {
		GRITTY ("Gritty"),
		SMOOTH ("Smooth"),
		NEITHER ("Neither gritty nor smooth");
		
		public final String name;
		
		SoilFeel(String name) {
			this.name = name;
		}
	}
	private SoilFeel soilFeel = null;
	***/
	//TODO: Would be better to get these from strings.xml.  But would need to rethink server interface.
	/***
	public enum SoilTexture {
		SAND ("Sand"),
		LOAMY_SAND ("Loamy sand"),
		SANDY_LOAM ("Sandy loam"),
		SILT_LOAM ("Silt loam"),
		LOAM ("Loam"),
		SANDY_CLAY_LOAM ("Sandy clay loam"),
		SILTY_CLAY_LOAM ("Silty clay loam"),
		CLAY_LOAM ("Clay loam"),
		SANDY_CLAY ("Sandy clay"),
		SILTY_CLAY ("Silty clay"),
		CLAY ("Clay");
		
		public final String name;
		
		SoilTexture(String name) {
			this.name = name;
		}
	}
	private SoilTexture soilTexture = null;
	***/
	public enum SoilTexture {
		SAND (R.string.server_resource_soil_texture_sand, R.string.soil_texture_activity_soil_texture_sand),
		LOAMY_SAND (R.string.server_resource_soil_texture_loamy_sand, R.string.soil_texture_activity_soil_texture_loamy_sand),
		SANDY_LOAM (R.string.server_resource_soil_texture_sandy_loam, R.string.soil_texture_activity_soil_texture_sandy_loam),
		SILT_LOAM (R.string.server_resource_soil_texture_silt_loam, R.string.soil_texture_activity_soil_texture_silt_loam),
		LOAM (R.string.server_resource_soil_texture_loam, R.string.soil_texture_activity_soil_texture_loam),
		SANDY_CLAY_LOAM (R.string.server_resource_soil_texture_sandy_clay_loam, R.string.soil_texture_activity_soil_texture_sandy_clay_loam),
		SILTY_CLAY_LOAM (R.string.server_resource_soil_texture_silty_clay_loam, R.string.soil_texture_activity_soil_texture_silty_clay_loam),
		CLAY_LOAM (R.string.server_resource_soil_texture_clay_loam, R.string.soil_texture_activity_soil_texture_clay_loam),
		SANDY_CLAY (R.string.server_resource_soil_texture_sandy_clay, R.string.soil_texture_activity_soil_texture_sandy_clay),
		SILTY_CLAY (R.string.server_resource_soil_texture_silty_clay, R.string.soil_texture_activity_soil_texture_silty_clay),
		CLAY (R.string.server_resource_soil_texture_clay, R.string.soil_texture_activity_soil_texture_clay);
		
		private final int serverName, displayName;
		
		public static final Map<String, SoilTexture> displayNameLookup = new HashMap<String, SoilTexture>();
		static {
			for (SoilTexture s : SoilTexture.values()) {
				displayNameLookup.put(LandPKSApplication.getInstance().getString(s.displayName), s);
			}
		}

		public static final Map<String, SoilTexture> serverNameLookup = new HashMap<String, SoilTexture>();
		static {
			for (SoilTexture s : SoilTexture.values()) {
				serverNameLookup.put(LandPKSApplication.getInstance().getString(s.serverName), s);
			}
		}

		SoilTexture(int serverName, int displayName) {
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
	private SoilTexture soilTexture = null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		SettingsActivity.setUITheme(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_soil_texture);
		        
        Intent intent = getIntent();
        horizonName = intent.getStringExtra("horizon");
        //position = intent.getIntExtra("position", 0);
        
        if (savedInstanceState != null) {
        	try {
        		soilTexture = SoilTexture.valueOf(savedInstanceState.getString(SOIL_TEXTURE));
        	} catch (Exception eX) {
        		soilTexture = null;
        	}
        }
        
        Plot plot = LandPKSApplication.getInstance().getPlot();
        plotName = plot.name;
        if (plotName == null) {  
        	newPlot = true;
        	//plotName = "New Plot";
        	plotName = getString(R.string.new_plot);
        }
        
        decisionScroll = (ScrollView) findViewById(R.id.activity_soiltexture_decision_scroll);
        ballLayout = (ViewGroup) findViewById(R.id.activity_soiltexture_ball_layout);
        ribbonLayout = (ViewGroup) findViewById(R.id.activity_soiltexture_ribbon_layout);
        ribbonLengthLayout = (ViewGroup) findViewById(R.id.activity_soiltexture_ribbonlength_layout);
        feelLayout = (ViewGroup) findViewById(R.id.activity_soiltexture_feel_layout);
        ballLabel = (TextView) findViewById(R.id.activity_soiltexture_ball_label);
        ribbonLabel = (TextView) findViewById(R.id.activity_soiltexture_ribbon_label);
        ribbonLengthLabel = (TextView) findViewById(R.id.activity_soiltexture_ribbonlength_label);
        feelLabel = (TextView) findViewById(R.id.activity_soiltexture_feel_label);
        soilTextureView = (TextView) findViewById(R.id.activity_soiltexture_texture);
        ballGroup = (RadioGroup) findViewById(R.id.activity_soiltexture_radiogroup_ball);		
        ribbonGroup = (RadioGroup) findViewById(R.id.activity_soiltexture_radiogroup_ribbon);		
        ribbonLengthGroup = (RadioGroup) findViewById(R.id.activity_soiltexture_radiogroup_ribbonlength);		
        feelGroup = (RadioGroup) findViewById(R.id.activity_soiltexture_radiogroup_feel);		
                
        ribbonHelpImage = (ImageView) findViewById(R.id.activity_soiltexture_ribbon_help);		

        okButton = (Button) findViewById(R.id.activity_soiltexture_okButton);		

        ActionBar actionBar = getSupportActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    //actionBar.setTitle(plotName + " " + horizonName + " Soil Texture"); //TODO: put in strings	    
	    actionBar.setTitle(plotName + " " + horizonName + " " + getString(R.string.soil_texture_activity_title)); //TODO: put in strings	    
	}

	

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (ballGroup.getCheckedRadioButtonId() != R.id.activity_soiltexture_radiobutton_ball_yes) {
			ribbonLayout.setVisibility(View.GONE);
		} 
		if (ribbonGroup.getCheckedRadioButtonId() != R.id.activity_soiltexture_radiobutton_ribbon_yes) {
			ribbonLengthLayout.setVisibility(View.GONE);
		} 
		if (ribbonLengthGroup.getCheckedRadioButtonId() == -1) {
			feelLayout.setVisibility(View.GONE);
		} 
		if (soilTexture != null){
			okButton.setVisibility(View.VISIBLE);
		}
	}

	static final String SOIL_TEXTURE = "soilTexture";
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		if (soilTexture != null) {
			savedInstanceState.putString(SOIL_TEXTURE, soilTexture.name());
		}
		super.onSaveInstanceState(savedInstanceState);
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.soil_horizon, menu);
		return super.onCreateOptionsMenu(menu);
	}

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
				
				//NavUtils.navigateUpTo(this, detailIntent); //won't return result
				finishUp();
				return true;
				//***tentative	
	        case R.id.action_settings: {
				Intent settingsIntent = new Intent(this, SettingsActivity.class);
				settingsIntent.putExtra(SettingsActivity.SETTINGS_MODE, SettingsActivity.ALL);
				startActivityForResult(settingsIntent, 0);
	            return true;
			}
	            
	        case R.id.action_about: {
				Intent settingsIntent = new Intent(this, AboutActivity.class);
				startActivity(settingsIntent);
	            return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void finishUp() {
		Intent detailIntent = new Intent(this, SoilHorizonActivity.class);
		int resultCode = 0;
		if (soilTexture != null) {
			//detailIntent.putExtra("texture", soilTexture.name);
			//detailIntent.putExtra("texture", soilTexture.getDisplayName());
			detailIntent.putExtra("texture", soilTexture.name());
			resultCode = 1;
		}
		setResult(resultCode, detailIntent); 
		finish();
	}

	public void onOKButtonClicked(View view){
		finishUp();
	}
	
	public void onHelpClick(View view) {
		String fileURL = null;
		Uri videoURI = null;
		String title = null;

		switch (view.getId()) {
			case R.id.activity_soiltexture_ball_help:
				((RadioButton)findViewById(R.id.activity_soiltexture_radiobutton_ball_yes)).setTextColor(COLOR2);
				((RadioButton)findViewById(R.id.activity_soiltexture_radiobutton_ball_no)).setTextColor(COLOR1);
				videoURI = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.ball);
				title = getString(R.string.soil_texture_activity_ball_demo);
				break;
			case R.id.activity_soiltexture_ribbon_help:
				((RadioButton)findViewById(R.id.activity_soiltexture_radiobutton_ribbon_yes)).setTextColor(COLOR2);
				((RadioButton)findViewById(R.id.activity_soiltexture_radiobutton_ribbon_no)).setTextColor(COLOR1);
				videoURI = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.ribbon);
				title = getString(R.string.soil_texture_activity_ribbon_demo);
				break;
			case R.id.activity_soiltexture_ribbonlength_help:
				//((RadioButton)findViewById(R.id.activity_soiltexture_radiobutton_ribbonlength_1)).setTextColor(Color.BLUE);
				((RadioButton)findViewById(R.id.activity_soiltexture_radiobutton_ribbonlength_1)).setTextColor(COLOR1);
				((RadioButton)findViewById(R.id.activity_soiltexture_radiobutton_ribbonlength_2)).setTextColor(COLOR2);
				//((RadioButton)findViewById(R.id.activity_soiltexture_radiobutton_ribbonlength_3)).setTextColor(Color.rgb(224, 224, 11));
				((RadioButton)findViewById(R.id.activity_soiltexture_radiobutton_ribbonlength_3)).setTextColor(COLOR3);
				videoURI = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.ribbonlength);
				title = getString(R.string.soil_texture_activity_ribbon_length_demo);
				break;
			case R.id.activity_soiltexture_feel_help:
				((RadioButton)findViewById(R.id.activity_soiltexture_radiobutton_feel_gritty)).setTextColor(COLOR1);
				((RadioButton)findViewById(R.id.activity_soiltexture_radiobutton_feel_neither)).setTextColor(COLOR2);
				((RadioButton)findViewById(R.id.activity_soiltexture_radiobutton_feel_smooth)).setTextColor(COLOR3);
				videoURI = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.feel);
				title = getString(R.string.soil_texture_activity_feel_demo);
				break;
		}
		
		if (videoURI != null) {
			AlertDialog.Builder aB = new AlertDialog.Builder(this);
			//aB.setTitle(title);
			VideoView vView = new VideoView(this);
			vView.setVideoURI(videoURI);
			vView.setOnPreparedListener(new OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mp) {
					mp.setLooping(true);
				}
			});
			vView.setZOrderOnTop(true); //to prevent dimming of video
			aB.setView(vView);
			aB.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
				}
			});
			AlertDialog aD = aB.create();
			//aD.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND); //to prevent dimming of video
			aD.show();
			vView.start();
			
		}
	}

	public void onBallRadioButtonClicked(View view) {
		boolean checked = ((RadioButton)view).isChecked();
		switch(view.getId()) {
			case R.id.activity_soiltexture_radiobutton_ball_yes:
				//add ribbon
				ribbonLayout.setVisibility(View.VISIBLE);
				new Handler().post(new Runnable() {
					@Override
					public void run() {
						decisionScroll.smoothScrollTo(0, ribbonLayout.getTop());
						//decisionScroll.scrollTo(0, ribbonLabel.getBottom());
					}
				});
				soilTexture = null;
				soilTextureView.setText(null);
				okButton.setVisibility(View.GONE);
				break;
			case R.id.activity_soiltexture_radiobutton_ball_no:
				//set texture TextView
				//set all other to gone
				ribbonLayout.setVisibility(View.GONE);
				ribbonGroup.clearCheck();
				ribbonLengthLayout.setVisibility(View.GONE);
				ribbonLengthGroup.clearCheck();
				feelLayout.setVisibility(View.GONE);
				feelGroup.clearCheck();
				new Handler().post(new Runnable() {
					@Override
					public void run() {
						decisionScroll.smoothScrollTo(0, ballLayout.getTop());
					}
				});
				soilTexture = SoilTexture.SAND;
				//soilTextureView.setText(soilTexture.name);
				soilTextureView.setText(soilTexture.getDisplayName());
				okButton.setVisibility(View.VISIBLE);
				break;
		}
	}
	
	public void onRibbonRadioButtonClicked(View view) {
		boolean checked = ((RadioButton)view).isChecked();
		switch(view.getId()) {
			case R.id.activity_soiltexture_radiobutton_ribbon_yes:
				//add ribbon
				ribbonLengthLayout.setVisibility(View.VISIBLE);
				new Handler().post(new Runnable() {
					@Override
					public void run() {
						decisionScroll.smoothScrollTo(0, ribbonLengthLayout.getTop());
						//decisionScroll.scrollTo(0, ribbonLengthLabel.getBottom());
					}
				});
				soilTexture = null;
				soilTextureView.setText(null);
				okButton.setVisibility(View.GONE);
				break;
			case R.id.activity_soiltexture_radiobutton_ribbon_no:
				//set texture TextView
				//set all other to gone
				ribbonLengthLayout.setVisibility(View.GONE);
				ribbonLengthGroup.clearCheck();
				feelLayout.setVisibility(View.GONE);
				feelGroup.clearCheck();
				new Handler().post(new Runnable() {
					@Override
					public void run() {
						decisionScroll.smoothScrollTo(0, ribbonLayout.getTop());
					}
				});
				soilTexture = SoilTexture.LOAMY_SAND;
				//soilTextureView.setText(soilTexture.name);
				soilTextureView.setText(soilTexture.getDisplayName());
				okButton.setVisibility(View.VISIBLE);
				break;
		}
	}
		
	public void onRibbonLengthRadioButtonClicked(View view) {
		boolean checked = ((RadioButton)view).isChecked();
		switch(view.getId()) {
			case R.id.activity_soiltexture_radiobutton_ribbonlength_1:
			case R.id.activity_soiltexture_radiobutton_ribbonlength_2:
			case R.id.activity_soiltexture_radiobutton_ribbonlength_3:
				feelLayout.setVisibility(View.VISIBLE);
				feelGroup.clearCheck();
				new Handler().post(new Runnable() {
					@Override
					public void run() {
						decisionScroll.smoothScrollTo(0, feelLayout.getTop());
						//decisionScroll.scrollTo(0, ribbonLengthLabel.getBottom());
					}
				});
				soilTexture = null;
				soilTextureView.setText(null);
				okButton.setVisibility(View.GONE);
				break;
		}
	}
	
	public void onFeelRadioButtonClicked(View view) {
		boolean checked = ((RadioButton)view).isChecked();
		int lengthCheckedID = ribbonLengthGroup.getCheckedRadioButtonId();
		/***
		switch(view.getId()) {
		//TODO: need to check ribbon length for each of these
			case R.id.activity_soiltexture_radiobutton_feel_1: 
				switch (lengthCheckedID) {
					case R.id.activity_soiltexture_radiobutton_ribbonlength_1:
						soilTexture = SoilTexture.SANDY_LOAM;
						break;
					case R.id.activity_soiltexture_radiobutton_ribbonlength_2:
						soilTexture = SoilTexture.SILT_LOAM;
						break;
					case R.id.activity_soiltexture_radiobutton_ribbonlength_3:
						soilTexture = SoilTexture.LOAM;
						break;
				}
				break;
			case R.id.activity_soiltexture_radiobutton_feel_2:
				switch (lengthCheckedID) {
					case R.id.activity_soiltexture_radiobutton_ribbonlength_1:
						soilTexture = SoilTexture.SANDY_CLAY_LOAM;
						break;
					case R.id.activity_soiltexture_radiobutton_ribbonlength_2:
						soilTexture = SoilTexture.SILTY_CLAY_LOAM;
						break;
					case R.id.activity_soiltexture_radiobutton_ribbonlength_3:
						soilTexture = SoilTexture.CLAY_LOAM;
						break;
				}
				break;
			case R.id.activity_soiltexture_radiobutton_feel_3:
				switch (lengthCheckedID) {
					case R.id.activity_soiltexture_radiobutton_ribbonlength_1:
						soilTexture = SoilTexture.SANDY_CLAY;
						break;
					case R.id.activity_soiltexture_radiobutton_ribbonlength_2:
						soilTexture = SoilTexture.SILTY_CLAY;
						break;
					case R.id.activity_soiltexture_radiobutton_ribbonlength_3:
						soilTexture = SoilTexture.CLAY;
						break;
				}
				break;
		}
		***/
		switch(lengthCheckedID) {
			case R.id.activity_soiltexture_radiobutton_ribbonlength_1: 
				switch (view.getId()) {
					case R.id.activity_soiltexture_radiobutton_feel_gritty:
						soilTexture = SoilTexture.SANDY_LOAM;
						break;
					case R.id.activity_soiltexture_radiobutton_feel_smooth:
						soilTexture = SoilTexture.SILT_LOAM;
						break;
					case R.id.activity_soiltexture_radiobutton_feel_neither:
						soilTexture = SoilTexture.LOAM;
						break;
				}
				break;
			case R.id.activity_soiltexture_radiobutton_ribbonlength_2:
				switch (view.getId()) {
					case R.id.activity_soiltexture_radiobutton_feel_gritty:
						soilTexture = SoilTexture.SANDY_CLAY_LOAM;
						break;
					case R.id.activity_soiltexture_radiobutton_feel_smooth:
						soilTexture = SoilTexture.SILTY_CLAY_LOAM;
						break;
					case R.id.activity_soiltexture_radiobutton_feel_neither:
						soilTexture = SoilTexture.CLAY_LOAM;
						break;
				}
				break;
			case R.id.activity_soiltexture_radiobutton_ribbonlength_3:
				switch (view.getId()) {
					case R.id.activity_soiltexture_radiobutton_feel_gritty:
						soilTexture = SoilTexture.SANDY_CLAY;
						break;
					case R.id.activity_soiltexture_radiobutton_feel_smooth:
						soilTexture = SoilTexture.SILTY_CLAY;
						break;
					case R.id.activity_soiltexture_radiobutton_feel_neither:
						soilTexture = SoilTexture.CLAY;
						break;
				}
				break;
		}
		if (soilTexture != null) {
			//soilTextureView.setText(soilTexture.name);
			soilTextureView.setText(soilTexture.getDisplayName());
			okButton.setVisibility(View.VISIBLE);
		}

	}


	
}
