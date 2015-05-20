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
 * PhotosFragment.java
 */

package com.noisyflowers.landpks.android.fragments;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.noisyflowers.landpks.android.LandPKSApplication;
import com.noisyflowers.landpks.android.activities.PhotosActivity;
import com.noisyflowers.landpks.android.activities.PlotEditListActivity;
import com.noisyflowers.landpks.android.R;
import com.noisyflowers.landpks.android.model.Plot;
import com.noisyflowers.landpks.android.util.PlotEditFragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PhotosFragment extends PlotEditFragment implements OnClickListener {

	public static final String DISPLAY_NAME = "Photos";
	
	public enum PhotoSubject {
		LANDSCAPE_NORTH (R.string.server_resource_photo_subject_landscape_north),
		LANDSCAPE_EAST (R.string.server_resource_photo_subject_landscape_east),
		LANDSCAPE_SOUTH (R.string.server_resource_photo_subject_landscape_south),
		LANDSCAPE_WEST (R.string.server_resource_photo_subject_landscape_west),
		SOIL_PIT (R.string.server_resource_photo_subject_soil_pit),
		SOIL_SAMPLES (R.string.server_resource_photo_subject_soil_samples);
		
		private final int serverName;
		
		public static final Map<String, PhotoSubject> serverNameLookup = new HashMap<String, PhotoSubject>();
		static {
			for (PhotoSubject s : PhotoSubject.values()) {
				serverNameLookup.put(LandPKSApplication.getInstance().getString(s.serverName), s);
			}
		}

		PhotoSubject(int serverName) {
			this.serverName = serverName;
		}
		
		public String getServerName() {
			return LandPKSApplication.getInstance().getString(serverName);
		}
	}
	
	private ImageButton landscapeIB, pitIB, samplesIB;;
	private TextView northTV, eastTV, southTV, westTV, pitTV, samplesTV;
	private TextView northLinkTV, eastLinkTV, southLinkTV, westLinkTV, pitLinkTV, samplesLinkTV;
	private ImageView northIV, eastIV, southIV, westIV, pitIV, samplesIV;;
	private Plot plot;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//getFragmentManager().beginTransaction().add(this, DISPLAY_NAME).commit();
		plot = LandPKSApplication.getInstance().getPlot();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view =  inflater.inflate(R.layout.fragment_photos, container, false);
		landscapeIB = (ImageButton) view.findViewById(R.id.fragment_photos_startLandscapePhotosButton);
		landscapeIB.setOnClickListener(this);
		pitIB = (ImageButton) view.findViewById(R.id.fragment_photos_startPitPhotoButton);
		pitIB.setOnClickListener(this);
		samplesIB = (ImageButton) view.findViewById(R.id.fragment_photos_startSamplesPhotoButton);
		samplesIB.setOnClickListener(this);
        
        northTV = (TextView) view.findViewById(R.id.fragment_photos_landscape_north_name);
        eastTV = (TextView) view.findViewById(R.id.fragment_photos_landscape_east_name);
        southTV = (TextView) view.findViewById(R.id.fragment_photos_landscape_south_name);
        westTV = (TextView) view.findViewById(R.id.fragment_photos_landscape_west_name);
        pitTV = (TextView) view.findViewById(R.id.fragment_photos_pit_name);
        samplesTV = (TextView) view.findViewById(R.id.fragment_photos_samples_name);

        northLinkTV = (TextView) view.findViewById(R.id.fragment_photos_landscape_north_link);
        eastLinkTV = (TextView) view.findViewById(R.id.fragment_photos_landscape_east_link);
        southLinkTV = (TextView) view.findViewById(R.id.fragment_photos_landscape_south_link);
        westLinkTV = (TextView) view.findViewById(R.id.fragment_photos_landscape_west_link);
        pitLinkTV = (TextView) view.findViewById(R.id.fragment_photos_pit_link);
        samplesLinkTV = (TextView) view.findViewById(R.id.fragment_photos_samples_link);
        
        northIV = (ImageView) view.findViewById(R.id.fragment_photos_landscape_north);
        eastIV = (ImageView) view.findViewById(R.id.fragment_photos_landscape_east);
        southIV = (ImageView) view.findViewById(R.id.fragment_photos_landscape_south);
        westIV = (ImageView) view.findViewById(R.id.fragment_photos_landscape_west);
        pitIV = (ImageView) view.findViewById(R.id.fragment_photos_pit);
        samplesIV = (ImageView) view.findViewById(R.id.fragment_photos_samples);

		return view;
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == 1) {
			load(plot);
			if (requestCode == PhotosActivity.TAKE_LANDSCAPE_PHOTOS) {
				northTV.setVisibility(View.VISIBLE);
				eastTV.setVisibility(View.VISIBLE);
				southTV.setVisibility(View.VISIBLE);
				westTV.setVisibility(View.VISIBLE);
				northTV.setText(data.getStringExtra("northImageFilename"));
				eastTV.setText(data.getStringExtra("eastImageFilename"));
				southTV.setText(data.getStringExtra("southImageFilename"));
				westTV.setText(data.getStringExtra("westImageFilename"));
			} else if (requestCode == PhotosActivity.TAKE_PIT_PHOTO) {
				pitTV.setVisibility(View.VISIBLE);
				pitTV.setText(data.getStringExtra("filename"));
			} else if (requestCode == PhotosActivity.TAKE_SAMPLES_PHOTO) {
				samplesTV.setVisibility(View.VISIBLE);
				samplesTV.setText(data.getStringExtra("filename"));
			}
			save(plot);  //TODO:  not sure I like this here
			loadImages();
		} else {
			//Toast.makeText(getActivity(), "There was a problem with photo capture.  Photo(s) not saved.", Toast.LENGTH_LONG).show();
			Toast.makeText(getActivity(), getString(R.string.photos_fragment_problem), Toast.LENGTH_LONG).show();
		}
	}
	
	@Override
	public void load(Plot plot) {
		
		for (PhotoSubject photoSubject : PhotoSubject.values()) {
			ImageButton button = null;
			TextView nameView = null;
			TextView linkView = null;
			String imageFileName = null;
			
			switch(photoSubject) {
				case LANDSCAPE_NORTH:
					button = landscapeIB;
					imageFileName = plot.northImageFilename;
					nameView = northTV;
					linkView = northLinkTV;
					break;
				case LANDSCAPE_EAST:
					button = landscapeIB;
					imageFileName = plot.eastImageFilename;
					nameView = eastTV;
					linkView = eastLinkTV;
					break;
				case LANDSCAPE_SOUTH:
					button = landscapeIB;
					imageFileName = plot.southImageFilename;
					nameView = southTV;
					linkView = southLinkTV;
					break;
				case LANDSCAPE_WEST:
					button = landscapeIB;
					imageFileName = plot.westImageFilename;
					nameView = westTV;
					linkView = westLinkTV;
					break;
				case SOIL_PIT:
					button = pitIB;
					imageFileName = plot.soilPitImageFilename;
					nameView = pitTV;
					linkView = pitLinkTV;
					break;
				case SOIL_SAMPLES:
					button = samplesIB;
					imageFileName = plot.soilSamplesImageFilename;
					nameView = samplesTV;
					linkView = samplesLinkTV;
					break;
			}

			if (imageFileName != null && !"".equals(imageFileName)) {
				nameView.setVisibility(View.VISIBLE);
				nameView.setText(imageFileName);
				if (imageFileName.contains("http://")) {
					linkView.setClickable(true);
					linkView.setMovementMethod(LinkMovementMethod.getInstance());
					String text = getString(R.string.photos_fragment_uploaded) + " <a href='" + imageFileName + "'>" + getString(R.string.photos_fragment_view) + "</a>";
					linkView.setText(Html.fromHtml(text));
					button.setVisibility(View.GONE);
					nameView.setVisibility(View.GONE);
					linkView.setVisibility(View.VISIBLE);
				}
			}

		}
		
		/***
		northTV.setText(plot.northImageFilename);
		if (plot.northImageFilename != null && plot.northImageFilename.contains("http://")) {
			northLinkTV.setClickable(true);
			northLinkTV.setMovementMethod(LinkMovementMethod.getInstance());
			String text = getString(R.string.photos_fragment_uploaded) + " <a href='" + plot.northImageFilename + "'>" + getString(R.string.photos_fragment_view) + "</a>";
			northLinkTV.setText(Html.fromHtml(text));
			northTV.setVisibility(View.GONE);
			northLinkTV.setVisibility(View.VISIBLE);
		}

		eastTV.setText(plot.eastImageFilename);
		if (plot.eastImageFilename != null && plot.eastImageFilename.contains("http://")) {
			eastLinkTV.setClickable(true);
			eastLinkTV.setMovementMethod(LinkMovementMethod.getInstance());
			String text = getString(R.string.photos_fragment_uploaded) + " <a href='" + plot.eastImageFilename + "'>" + getString(R.string.photos_fragment_view) + "</a>";
			eastLinkTV.setText(Html.fromHtml(text));
			eastTV.setVisibility(View.GONE);
			eastLinkTV.setVisibility(View.VISIBLE);
		}

		southTV.setText(plot.southImageFilename);
		if (plot.southImageFilename != null && plot.southImageFilename.contains("http://")) {
			southLinkTV.setClickable(true);
			southLinkTV.setMovementMethod(LinkMovementMethod.getInstance());
			String text = getString(R.string.photos_fragment_uploaded) + " <a href='" + plot.southImageFilename + "'>" + getString(R.string.photos_fragment_view) + "</a>";
			southLinkTV.setText(Html.fromHtml(text));
			southTV.setVisibility(View.GONE);
			southLinkTV.setVisibility(View.VISIBLE);
		}
		
		westTV.setText(plot.westImageFilename);
		if (plot.westImageFilename != null && plot.westImageFilename.contains("http://")) {
			westLinkTV.setClickable(true);
			westLinkTV.setMovementMethod(LinkMovementMethod.getInstance());
			String text = getString(R.string.photos_fragment_uploaded) + " <a href='" + plot.westImageFilename + "'>" + getString(R.string.photos_fragment_view) + "</a>";
			westLinkTV.setText(Html.fromHtml(text));
			westTV.setVisibility(View.GONE);
			westLinkTV.setVisibility(View.VISIBLE);
		}
		
		pitTV.setText(plot.soilPitImageFilename);
		if (plot.soilPitImageFilename != null && plot.soilPitImageFilename.contains("http://")) {
				pitLinkTV.setClickable(true);
				pitLinkTV.setMovementMethod(LinkMovementMethod.getInstance());
				String text = getString(R.string.photos_fragment_uploaded) + " <a href='" + plot.soilPitImageFilename + "'>" + getString(R.string.photos_fragment_view) + "</a>";
				pitLinkTV.setText(Html.fromHtml(text));
				pitTV.setVisibility(View.GONE);
				pitLinkTV.setVisibility(View.VISIBLE);
		}
		
		samplesTV.setText(plot.soilSamplesImageFilename);
		if (plot.soilSamplesImageFilename != null && plot.soilSamplesImageFilename.contains("http://")) {
			samplesLinkTV.setClickable(true);
			samplesLinkTV.setMovementMethod(LinkMovementMethod.getInstance());
			String text = getString(R.string.photos_fragment_uploaded) + " <a href='" + plot.soilSamplesImageFilename + "'>" + getString(R.string.photos_fragment_view) + "</a>";
			samplesLinkTV.setText(Html.fromHtml(text));
			samplesTV.setVisibility(View.GONE);
			samplesLinkTV.setVisibility(View.VISIBLE);
		}
		***/
		
		loadImages();
	}
	
	private void loadImages() {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 2;
		if (northTV.getText() != null && !"".equals(northTV.getText())) {
			northIV.setVisibility(View.VISIBLE);
			Bitmap northBM = BitmapFactory.decodeFile(getActivity().getFilesDir() + "/" + northTV.getText(), options);
			northIV.setImageBitmap(northBM);
		}
		if (eastTV.getText() != null && !"".equals(eastTV.getText())) {
			eastIV.setVisibility(View.VISIBLE);
			Bitmap eastBM = BitmapFactory.decodeFile(getActivity().getFilesDir() + "/" + eastTV.getText(), options);
			eastIV.setImageBitmap(eastBM);
		}
		if (southTV.getText() != null && !"".equals(southTV.getText())) {
			southIV.setVisibility(View.VISIBLE);
			Bitmap southBM = BitmapFactory.decodeFile(getActivity().getFilesDir() + "/" + southTV.getText(), options);
			southIV.setImageBitmap(southBM);
		}
		if (westTV.getText() != null && !"".equals(westTV.getText())) {
			westIV.setVisibility(View.VISIBLE);
			Bitmap westBM = BitmapFactory.decodeFile(getActivity().getFilesDir() + "/" + westTV.getText(), options);
			westIV.setImageBitmap(westBM);		
		}
		if (pitTV.getText() != null && !"".equals(pitTV.getText())) {
			pitIV.setVisibility(View.VISIBLE);
			Bitmap pitBM = BitmapFactory.decodeFile(getActivity().getFilesDir() + "/" + pitTV.getText(), options);
			pitIV.setImageBitmap(pitBM);		
		}
		if (samplesTV.getText() != null && !"".equals(samplesTV.getText())) {
			samplesIV.setVisibility(View.VISIBLE);
			Bitmap samplesBM = BitmapFactory.decodeFile(getActivity().getFilesDir() + "/" + samplesTV.getText(), options);
			samplesIV.setImageBitmap(samplesBM);		
		}
	}

	@Override
	public void save(Plot plot) {
		plot.northImageFilename = northTV.getText().toString();
		plot.eastImageFilename = eastTV.getText().toString();
		plot.southImageFilename = southTV.getText().toString();
		plot.westImageFilename = westTV.getText().toString();
		//if (plot.soilPitImageFilename != null && !plot.soilPitImageFilename.contains("http://")) {
			plot.soilPitImageFilename = pitTV.getText().toString();
		//}
		plot.soilSamplesImageFilename = samplesTV.getText().toString();
		if (plot.name != null) LandPKSApplication.getInstance().getDatabaseAdapter().addPlot(plot);
	}

	@Override
	public boolean isComplete(Plot plot) {
		return plot.northImageFilename != null && plot.eastImageFilename != null && plot.southImageFilename != null && plot.westImageFilename != null && plot.soilPitImageFilename != null && plot.soilSamplesImageFilename != null &&
			   !"".equals(plot.northImageFilename) && !"".equals(plot.eastImageFilename) && !"".equals(plot.southImageFilename) && !"".equals(plot.westImageFilename) && !"".equals(plot.soilPitImageFilename) && !"".equals(plot.soilSamplesImageFilename);
	}
		
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.fragment_photos_startLandscapePhotosButton: {
				if (plot.northImageFilename != null && !"".equals(plot.northImageFilename)) {
			    	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			    	//builder.setMessage("This will permenantly delete the current landscape photos for this site. Are you sure you want to clear this data?  This cannot be undone.")
			    	builder.setMessage(getString(R.string.photos_fragment_landscape_delete_confirm))
			    			   //.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			    			   .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
			    				   @Override
			    				   public void onClick(DialogInterface dialog, int which) {
			    						getActivity().deleteFile(plot.northImageFilename);
			    						getActivity().deleteFile(plot.eastImageFilename);
			    						getActivity().deleteFile(plot.southImageFilename);
			    						getActivity().deleteFile(plot.westImageFilename);
			    						plot.northImageFilename = null;
			    						plot.eastImageFilename = null;
			    						plot.southImageFilename = null;
			    						plot.westImageFilename = null;
			    						save(plot); //TODO:  not sure I like this here		    						
			    						northTV.setText(null);
			    						eastTV.setText(null);
			    						southTV.setText(null);
			    						westTV.setText(null);
			    						northIV.setImageBitmap(null);
			    						eastIV.setImageBitmap(null);
			    						southIV.setImageBitmap(null);
			    						westIV.setImageBitmap(null);
										Intent intent = new Intent(getActivity(), PhotosActivity.class);
										intent.putExtra(PhotosActivity.INTENT_EXTRA_FLAVOR, PhotosActivity.TAKE_LANDSCAPE_PHOTOS);
										intent.putExtra(PhotosActivity.INTENT_EXTRA_PLOTNAME, plot.name);
										startActivityForResult(intent, PhotosActivity.TAKE_LANDSCAPE_PHOTOS);
			     				   }
			    			   })
			    			   //.setNegativeButton("No", new DialogInterface.OnClickListener() {
			    			   .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
			    				   @Override
			    				   public void onClick(DialogInterface dialog, int which) {
			     				   }
			    			   });
			    	AlertDialog alert = builder.create();
			    	alert.show();
				} else {
					Intent intent = new Intent(getActivity(), PhotosActivity.class);
					intent.putExtra(PhotosActivity.INTENT_EXTRA_FLAVOR, PhotosActivity.TAKE_LANDSCAPE_PHOTOS);
					intent.putExtra(PhotosActivity.INTENT_EXTRA_PLOTNAME, plot.name);
					startActivityForResult(intent, PhotosActivity.TAKE_LANDSCAPE_PHOTOS);					
				}
			}
			break;
			
			case R.id.fragment_photos_startPitPhotoButton: {
				if (plot.soilPitImageFilename != null && !"".equals(plot.soilPitImageFilename)) {
			    	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			    	//builder.setMessage("This will permenantly delete the current soil pit photo for this site. Are you sure you want to clear this data?  This cannot be undone.")
			    	builder.setMessage(getString(R.string.photos_fragment_soil_pit_delete_confirm))
			    			   //.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			    			   .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
			    				   @Override
			    				   public void onClick(DialogInterface dialog, int which) {
			    						getActivity().deleteFile(plot.soilPitImageFilename);
			    						plot.soilPitImageFilename = null;
			    						save(plot); //TODO:  not sure I like this here		    						
			    						pitTV.setText(null);
			    						pitIV.setImageBitmap(null);
										Intent intent = new Intent(getActivity(), PhotosActivity.class);
										intent.putExtra(PhotosActivity.INTENT_EXTRA_FLAVOR, PhotosActivity.TAKE_PIT_PHOTO);
										intent.putExtra(PhotosActivity.INTENT_EXTRA_PLOTNAME, plot.name);
										startActivityForResult(intent, PhotosActivity.TAKE_PIT_PHOTO);
			     				   }
			    			   })
			    			   //.setNegativeButton("No", new DialogInterface.OnClickListener() {
			    			   .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
			    				   @Override
			    				   public void onClick(DialogInterface dialog, int which) {
			     				   }
			    			   });
			    	AlertDialog alert = builder.create();
			    	alert.show();
				} else {
					Intent intent = new Intent(getActivity(), PhotosActivity.class);
					intent.putExtra(PhotosActivity.INTENT_EXTRA_FLAVOR, PhotosActivity.TAKE_PIT_PHOTO);
					intent.putExtra(PhotosActivity.INTENT_EXTRA_PLOTNAME, plot.name);
					startActivityForResult(intent, PhotosActivity.TAKE_PIT_PHOTO);					
				}
			
			}
			break;

			case R.id.fragment_photos_startSamplesPhotoButton: {
				if (plot.soilSamplesImageFilename != null && !"".equals(plot.soilSamplesImageFilename)) {
			    	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			    	//builder.setMessage("This will permenantly delete the current soil samples photo for this site. Are you sure you want to clear this data?  This cannot be undone.")
			    	builder.setMessage(getString(R.string.photos_fragment_samples_delete_confirm))
			    			   //.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			    			   .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
			    				   @Override
			    				   public void onClick(DialogInterface dialog, int which) {
			    						getActivity().deleteFile(plot.soilSamplesImageFilename);
			    						plot.soilSamplesImageFilename = null;
			    						save(plot); //TODO:  not sure I like this here		    						
			    						samplesTV.setText(null);
			    						samplesIV.setImageBitmap(null);
										Intent intent = new Intent(getActivity(), PhotosActivity.class);
										intent.putExtra(PhotosActivity.INTENT_EXTRA_FLAVOR, PhotosActivity.TAKE_SAMPLES_PHOTO);
										intent.putExtra(PhotosActivity.INTENT_EXTRA_PLOTNAME, plot.name);
										startActivityForResult(intent, PhotosActivity.TAKE_SAMPLES_PHOTO);
			     				   }
			    			   })
			    			   //.setNegativeButton("No", new DialogInterface.OnClickListener() {
			    			   .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
			    				   @Override
			    				   public void onClick(DialogInterface dialog, int which) {
			     				   }
			    			   });
			    	AlertDialog alert = builder.create();
			    	alert.show();
				} else {
					Intent intent = new Intent(getActivity(), PhotosActivity.class);
					intent.putExtra(PhotosActivity.INTENT_EXTRA_FLAVOR, PhotosActivity.TAKE_SAMPLES_PHOTO);
					intent.putExtra(PhotosActivity.INTENT_EXTRA_PLOTNAME, plot.name);
					startActivityForResult(intent, PhotosActivity.TAKE_SAMPLES_PHOTO);					
				}
			
			}
			break;

		}
	}
	
	
}
