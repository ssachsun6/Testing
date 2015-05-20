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
 * ReviewFragment.java
 */

package com.noisyflowers.landpks.android.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.noisyflowers.landpks.android.LandPKSApplication;
import com.noisyflowers.landpks.android.R;
import com.noisyflowers.landpks.android.activities.PlotEditListActivity;
import com.noisyflowers.landpks.android.activities.SoilTextureActivity.SoilTexture;
import com.noisyflowers.landpks.android.dal.RestClient;
import com.noisyflowers.landpks.android.fragments.LandUseCoverFragment.LandCover;
import com.noisyflowers.landpks.android.fragments.SlopeFragment.Slope;
import com.noisyflowers.landpks.android.fragments.SlopeShapeFragment.Curve;
import com.noisyflowers.landpks.android.fragments.SoilHorizonFragment.SoilRockFragmentVolume;
import com.noisyflowers.landpks.android.fragments.SoilHorizonsFragment.HorizonName;
import com.noisyflowers.landpks.android.model.Plot;
import com.noisyflowers.landpks.android.model.SoilHorizon;
import com.noisyflowers.landpks.android.util.PlotEditFragment;

public class ReviewFragment extends PlotEditFragment  implements OnClickListener{

	private static final String COLUMN_COUNT = "columnCount";
	private static final int MIN_COLUMNS = 1;
	private static final int TICS_PER_PLOT = 4;
		
	private View root;
	
	private Button submitButton;
	private TextView nameField, recorderNameField, organizationField, latitudeField, longitudeField,
					 landCoverField, floodsField, grazedField, slopeField, downSlopeShapeField,  crossSlopeShapeField,
					 crackedSurfaceField, saltSurfaceField;
	private ImageView landCoverImageView, floodedImageView, grazedImageView, slopeImageView, 
					  downSlopeShapeImageView, crossSlopeShapeImageView, surfaceCrackedImageView, surfaceSaltImageView;
	private LandPKSApplication application;

	private int columnCount = MIN_COLUMNS;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		application = LandPKSApplication.getInstance();
		
		if (savedInstanceState != null && savedInstanceState.containsKey(COLUMN_COUNT)) {
			columnCount = savedInstanceState.getInt(COLUMN_COUNT);
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		root = inflater.inflate(R.layout.fragment_review, container, false);
        
		submitButton = (Button) root.findViewById(R.id.review_Button_submitPlotData);
		submitButton.setOnClickListener(this);
		
		Plot plot = application.getPlot();
		
		if (plot.remoteID != null) {
			submitButton.setVisibility(View.GONE);
			root.findViewById(R.id.fragment_review_separator).setVisibility(View.GONE);
		} else if (plot.needsUpload == 1) {
			submitButton.setVisibility(View.GONE);
			root.findViewById(R.id.review_TextView_uploadPending).setVisibility(View.VISIBLE);			
		}
		
		nameField = (TextView) root.findViewById(R.id.review_EditText_plotName);
		recorderNameField = (TextView) root.findViewById(R.id.review_EditText_recorderName);
		organizationField = (TextView) root.findViewById(R.id.review_EditText_organizationName);
		latitudeField = (TextView) root.findViewById(R.id.review_EditText_latitude);
		longitudeField = (TextView) root.findViewById(R.id.review_EditText_longitude);

		landCoverField = (TextView) root.findViewById(R.id.review_EditText_landcover);
		floodsField = (TextView) root.findViewById(R.id.review_EditText_floods);
		grazedField = (TextView) root.findViewById(R.id.review_EditText_grazed);

		slopeField = (TextView) root.findViewById(R.id.review_EditText_slope);
		
		downSlopeShapeField = (TextView) root.findViewById(R.id.review_EditText_downSlopeShape);
		crossSlopeShapeField = (TextView) root.findViewById(R.id.review_EditText_crossSlopeShape);

		landCoverImageView = (ImageView)root.findViewById(R.id.review_ImageView_landcover);
		floodedImageView = (ImageView)root.findViewById(R.id.review_ImageView_flooded);
		grazedImageView = (ImageView)root.findViewById(R.id.review_ImageView_grazed);
		slopeImageView = (ImageView)root.findViewById(R.id.review_ImageView_slope);
		downSlopeShapeImageView = (ImageView)root.findViewById(R.id.review_ImageView_downSlopeShape);
		crossSlopeShapeImageView = (ImageView)root.findViewById(R.id.review_ImageView_crossSlopeShape);
		surfaceCrackedImageView = (ImageView)root.findViewById(R.id.review_ImageView_surfaceCracked);
		surfaceSaltImageView = (ImageView)root.findViewById(R.id.review_ImageView_surfaceSalt);
			
		crackedSurfaceField = (TextView) root.findViewById(R.id.review_EditText_surfaceCracked);
		saltSurfaceField = (TextView) root.findViewById(R.id.review_EditText_surfaceSalt);

		return root;
	}

	private int columnsFromProgress(int progress) {
		return ((progress/TICS_PER_PLOT) + MIN_COLUMNS);
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
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(COLUMN_COUNT, columnCount);
	}
	
	@Override
	public void load(Plot plot) {
		nameField.setText(plot.name);
		recorderNameField.setText(plot.recorderName);
		organizationField.setText(plot.organization);
		try { latitudeField.setText(plot.latitude.toString()); } catch (Exception e) {}
		try { longitudeField.setText(plot.longitude.toString()); } catch (Exception e) {}
		
		if (plot.landcover != null) {
			landCoverField.setText(LandCover.valueOf(plot.landcover).getDisplayName());
			//I'm not thrilled with how I'm doing this, but can't come up with anything better right now
			switch (LandCover.valueOf(plot.landcover)) {
				case LAND_COVER_1:
					landCoverImageView.setImageResource(R.drawable.ic_tree);
					landCoverImageView.setVisibility(View.VISIBLE);
					break;
				case LAND_COVER_2:
					landCoverImageView.setImageResource(R.drawable.ic_shrub);
					landCoverImageView.setVisibility(View.VISIBLE);
					break;
				case LAND_COVER_3:
					landCoverImageView.setImageResource(R.drawable.ic_herbaceous);
					landCoverImageView.setVisibility(View.VISIBLE);
					break;
				case LAND_COVER_4:
					landCoverImageView.setImageResource(R.drawable.ic_savanna);
					landCoverImageView.setVisibility(View.VISIBLE);
					break;
				case LAND_COVER_5:
					landCoverImageView.setImageResource(R.drawable.ic_mosiac);
					landCoverImageView.setVisibility(View.VISIBLE);
					break;
				case LAND_COVER_6:
					landCoverImageView.setImageResource(R.drawable.ic_cultivated);
					landCoverImageView.setVisibility(View.VISIBLE);
					break;
				case LAND_COVER_7:
					landCoverImageView.setImageResource(R.drawable.ic_urban);
					landCoverImageView.setVisibility(View.VISIBLE);
					break;
				case LAND_COVER_8:
					landCoverImageView.setImageResource(R.drawable.ic_barren);
					landCoverImageView.setVisibility(View.VISIBLE);
					break;
				case LAND_COVER_9:
					landCoverImageView.setImageResource(R.drawable.ic_water);
					landCoverImageView.setVisibility(View.VISIBLE);
					break;
				default:
					landCoverImageView.setVisibility(View.GONE);
					break;
			}
		}
		
		if (plot.flooding != null) {
			floodsField.setText(plot.flooding ? getString(R.string.yes) : getString(R.string.no));
			if (plot.flooding) {
				floodedImageView.setImageResource(R.drawable.ic_flooded); 
			} else {
				floodedImageView.setImageResource(R.drawable.ic_not_flooded); 				
			}
			floodedImageView.setVisibility(View.VISIBLE);
		}
		if (plot.grazed != null) {
			grazedField.setText(plot.grazed ? getString(R.string.yes) : getString(R.string.no));
			if (plot.grazed) {
				grazedImageView.setImageResource(R.drawable.ic_grazed); 
			} else {
				grazedImageView.setImageResource(R.drawable.ic_not_grazed); 				
			}
			grazedImageView.setVisibility(View.VISIBLE);
		}
		
		if (plot.slope != null) {
			try {
				slopeField.setText(Slope.valueOf(plot.slope).getDisplayName());
				switch (Slope.valueOf(plot.slope)) {
					case SLOPE_1:
						slopeImageView.setImageResource(R.drawable.ic_slope_flat);
						slopeImageView.setVisibility(View.VISIBLE);
						break;
					case SLOPE_2:
						slopeImageView.setImageResource(R.drawable.ic_slope_gentle);
						slopeImageView.setVisibility(View.VISIBLE);
						break;
					case SLOPE_3:
						slopeImageView.setImageResource(R.drawable.ic_slope_moderate);
						slopeImageView.setVisibility(View.VISIBLE);
						break;
					case SLOPE_4:
						slopeImageView.setImageResource(R.drawable.ic_slope_rolling);
						slopeImageView.setVisibility(View.VISIBLE);
						break;
					case SLOPE_5:
						slopeImageView.setImageResource(R.drawable.ic_slope_hilly);
						slopeImageView.setVisibility(View.VISIBLE);
						break;
					case SLOPE_6:
						slopeImageView.setImageResource(R.drawable.ic_slope_steep);
						slopeImageView.setVisibility(View.VISIBLE);
						break;
					case SLOPE_7:
						slopeImageView.setImageResource(R.drawable.ic_slope_very_steep);
						slopeImageView.setVisibility(View.VISIBLE);
						break;
					default:
						break;
				}
			} catch (IllegalArgumentException iAE) {
				slopeField.setText(plot.slope);
			}
		}

		if (plot.downSlopeShape != null) {
			downSlopeShapeField.setText(Curve.valueOf(plot.downSlopeShape).getDisplayName());
			switch (Curve.valueOf(plot.downSlopeShape)) {
				case CONCAVE:
					downSlopeShapeImageView.setImageResource(R.drawable.ic_downslopeconcave);
					downSlopeShapeImageView.setVisibility(View.VISIBLE);
					break;
				case CONVEX:
					downSlopeShapeImageView.setImageResource(R.drawable.ic_downslopeconvex);
					downSlopeShapeImageView.setVisibility(View.VISIBLE);
					break;
				case LINEAR:
					downSlopeShapeImageView.setImageResource(R.drawable.ic_downslopeflat);
					downSlopeShapeImageView.setVisibility(View.VISIBLE);
					break;
				default:
					break;
			}
		}

		if (plot.crossSlopeShape != null) {
			crossSlopeShapeField.setText(Curve.valueOf(plot.crossSlopeShape).getDisplayName());
			switch (Curve.valueOf(plot.crossSlopeShape)) {
				case CONCAVE:
					crossSlopeShapeImageView.setImageResource(R.drawable.ic_crossslopeconcave);
					crossSlopeShapeImageView.setVisibility(View.VISIBLE);
					break;
				case CONVEX:
					crossSlopeShapeImageView.setImageResource(R.drawable.ic_crossslopeconvex);
					crossSlopeShapeImageView.setVisibility(View.VISIBLE);
					break;
				case LINEAR:
					crossSlopeShapeImageView.setImageResource(R.drawable.ic_crossslopeflat);
					crossSlopeShapeImageView.setVisibility(View.VISIBLE);
					break;
				default:
					break;
			}
		}
		
		if (plot.surfaceCracking != null) {
			crackedSurfaceField.setText(plot.surfaceCracking ? getString(R.string.yes) : getString(R.string.no));
			if (plot.surfaceCracking) {
				surfaceCrackedImageView.setImageResource(R.drawable.ic_soilcracks); 
			} else {
				surfaceCrackedImageView.setImageResource(R.drawable.ic_no_soilcracks); 				
			}
			surfaceCrackedImageView.setVisibility(View.VISIBLE);
		}
		if (plot.surfaceSalt != null) {
			saltSurfaceField.setText(plot.surfaceSalt ? getString(R.string.yes) : getString(R.string.no));
			if (plot.surfaceSalt) {
				surfaceSaltImageView.setImageResource(R.drawable.ic_salt_soil); 
			} else {
				surfaceSaltImageView.setImageResource(R.drawable.ic_no_salt_soil); 				
			}
			surfaceSaltImageView.setVisibility(View.VISIBLE);
		}

		if (plot.soilHorizons != null) {
			Integer color;
			String cStr;
	        for(HorizonName h : HorizonName.values()) {
	        	SoilHorizon sH = plot.soilHorizons.get(h.name);
	        	if (sH != null) {
	        		color = sH.color;
	        		cStr = color != null ? Color.red(color) + "/" + Color.green(color) + "/" + Color.blue(color) : "";
	        		String horizonNumber = "" + (h.ordinal() + 1);
	        		TextView v = (TextView)root.findViewWithTag("review_EditText_HorizonColor_" + horizonNumber);
	        		if (v != null) v.setText(cStr);
	        		v = (TextView)root.findViewWithTag("review_EditText_HorizonTexture_" + horizonNumber);
	        		//if (v != null) v.setText(sH.texture);
	        		if (v != null) v.setText(sH.texture == null ? null : SoilTexture.valueOf(sH.texture).getDisplayName());
	        		v = (TextView)root.findViewWithTag("review_EditText_HorizonFragment_" + horizonNumber);
	        		//if (v != null) v.setText(sH.rockFragment);
	        		if (v != null) v.setText(sH.rockFragment == null ? null : SoilRockFragmentVolume.valueOf(sH.rockFragment).getDisplayName());
	        	}
	        }
		}
		
		if (plot.northImageFilename != null) {
			TextView linkView = ((TextView)root.findViewById(R.id.fragment_review_photos_landscape_north_link));
			if (plot.northImageFilename.contains("http")) {
				String text = "<a href='" + plot.northImageFilename + "'>" + getString(R.string.photos_fragment_view) + "</a>";
				linkView.setText(Html.fromHtml(text));
				linkView.setClickable(true);
				linkView.setMovementMethod(LinkMovementMethod.getInstance());
			} else {
				linkView.setText(R.string.fragment_review_photo_not_yet_uploaded);				
			}
		}
		
		if (plot.eastImageFilename != null) {
			TextView linkView = ((TextView)root.findViewById(R.id.fragment_review_photos_landscape_east_link));
			if (plot.eastImageFilename.contains("http")) {
				String text = "<a href='" + plot.eastImageFilename + "'>" + getString(R.string.photos_fragment_view) + "</a>";
				linkView.setText(Html.fromHtml(text));
				linkView.setClickable(true);
				linkView.setMovementMethod(LinkMovementMethod.getInstance());
			} else {
				linkView.setText(R.string.fragment_review_photo_not_yet_uploaded);				
			}
		}

		if (plot.southImageFilename != null) {
			TextView linkView = ((TextView)root.findViewById(R.id.fragment_review_photos_landscape_south_link));
			if (plot.southImageFilename.contains("http")) {
				String text = "<a href='" + plot.southImageFilename + "'>" + getString(R.string.photos_fragment_view) + "</a>";
				linkView.setText(Html.fromHtml(text));
				linkView.setClickable(true);
				linkView.setMovementMethod(LinkMovementMethod.getInstance());
			} else {
				linkView.setText(R.string.fragment_review_photo_not_yet_uploaded);				
			}
		}

		if (plot.westImageFilename != null) {
			TextView linkView = ((TextView)root.findViewById(R.id.fragment_review_photos_landscape_west_link));
			if (plot.westImageFilename.contains("http")) {
				String text = "<a href='" + plot.westImageFilename + "'>" + getString(R.string.photos_fragment_view) + "</a>";
				linkView.setText(Html.fromHtml(text));
				linkView.setClickable(true);
				linkView.setMovementMethod(LinkMovementMethod.getInstance());
			} else {
				linkView.setText(R.string.fragment_review_photo_not_yet_uploaded);				
			}
		}

		if (plot.soilPitImageFilename != null) {
			TextView linkView = ((TextView)root.findViewById(R.id.fragment_review_photos_pit_link));
			if (plot.soilPitImageFilename.contains("http")) {
				String text = "<a href='" + plot.soilPitImageFilename + "'>" + getString(R.string.photos_fragment_view) + "</a>";
				linkView.setText(Html.fromHtml(text));
				linkView.setClickable(true);
				linkView.setMovementMethod(LinkMovementMethod.getInstance());
			} else {
				linkView.setText(R.string.fragment_review_photo_not_yet_uploaded);				
			}
		}

		if (plot.soilSamplesImageFilename != null) {
			TextView linkView = ((TextView)root.findViewById(R.id.fragment_review_photos_samples_link));
			if (plot.soilSamplesImageFilename.contains("http")) {
				String text = "<a href='" + plot.soilSamplesImageFilename + "'>" + getString(R.string.photos_fragment_view) + "</a>";
				linkView.setText(Html.fromHtml(text));
				linkView.setClickable(true);
				linkView.setMovementMethod(LinkMovementMethod.getInstance());
			} else {
				linkView.setText(R.string.fragment_review_photo_not_yet_uploaded);				
			}
		}
	}

	@Override
	public void save(Plot plot) {
		//nothing to save from this fragment
	}

	@Override
	public boolean isComplete(Plot plot) {
		//return (plot.recommendation != null && !"".equals(plot.recommendation));  //TODO: change to remoteID?
		return (plot.remoteID != null && !"".equals(plot.remoteID));  
	}
	
	private String verifyInput(Plot plot) {
		StringBuilder sB = new StringBuilder();
		if (plot.remoteID != null && !"".equals(plot.remoteID)) { 
			sB.append(getString(R.string.review_fragment_plot_already_submitted));
		} else {
			StringBuilder sB2 = new StringBuilder();
			if (plot.name == null) {
				sB2.append(getString(R.string.review_fragment_plot_name_required) + "\n");
			}
			if (plot.recorderName == null) {
				sB2.append(getString(R.string.review_fragment_recorder_name_required) + "\n");
			}
			if (plot.latitude == null || plot.longitude == null) {
				sB2.append(getString(R.string.review_fragment_lat_lon_required) + "\n");
			}
			if (plot.slope == null) {
				sB2.append(getString(R.string.review_fragment_slope_required) + "\n");
			}
			if (plot.soilHorizons.isEmpty()) {
				sB2.append(getString(R.string.review_fragment_soil_profile_required) + "\n");
			}
			if (sB2.length() > 0) {
				sB.append(getString(R.string.review_fragment_data_required) + "\n");
				sB.append(sB2.toString());
			}
		}
		return sB.length() == 0 ? null : sB.toString();
	}
	
	@Override
	public void onClick(View v) {
		Plot plot = application.getPlot();
		String dialogString = null;
		boolean goHome = false;
		//if (plot.recommendation != null && !"".equals(plot.recommendation)) { //TODO: change to remoteID?
		if (plot.remoteID != null && !"".equals(plot.remoteID)) { 
			dialogString = getString(R.string.review_fragment_plot_already_submitted);
		} else {
			dialogString = verifyInput(plot);
		}
		
		if (dialogString != null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    	builder.setMessage(dialogString);
			builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});		    		
	    	AlertDialog alert = builder.create();
	    	alert.show(); 		
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    	builder.setMessage(getString(R.string.review_fragment_confirm_submit));
			builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});		    		
			builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Plot plot = application.getPlot();
					String dialogString = null;
					boolean goHome = false;
					plot.needsUpload = 1;
					plot.needsPhotoUpload = 1;
					LandPKSApplication.getInstance().getDatabaseAdapter().addPlot(plot);
					dialogString = getString(R.string.review_fragment_submitted_message);
					goHome = true;
					
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			    	builder.setMessage(dialogString);
			    	if (goHome) {
			    		builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
			    			@Override
			    			public void onClick(DialogInterface dialog, int which) {
			    				Intent upIntent = new Intent(getActivity(), PlotEditListActivity.class);
			    				upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //TODO: experimental
			    				NavUtils.navigateUpTo(getActivity(), upIntent);
			    				//NavUtils.navigateUpTo(getActivity(), new Intent(getActivity(), PlotListActivity.class));
			     			}
			    		});
			    	} else {
			    		builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
			    			@Override
			    			public void onClick(DialogInterface dialog, int which) {
			    			}
		    			});		    		
			    	}
			    	AlertDialog alert = builder.create();
			    	alert.show(); 		
				}
			});		    		
	    	AlertDialog alert = builder.create();
	    	alert.show(); 		
		}
	}
	
    private class SubmitPlotTask extends AsyncTask<Plot, Void, Plot> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
        	progressDialog = ProgressDialog.show(getActivity(), "", "Submitting plot data to server...", true);
        }
        
        protected Plot doInBackground(Plot... plots) {
        	Plot plot = plots[0];
        	

        	        	
        	plot = RestClient.getInstance().putPlot(plot);
        	//TODO: update application plot from plot
        	
        	//long id = plot.ID;
        	//LandPKSApplication.getInstance().getDatabaseAdapter().syncPlots();
        	//plot = LandPKSApplication.getInstance().getDatabaseAdapter().getPlot(id);
        	
        	return plot;
        }

        protected void onPostExecute(Plot plot) {
	       	progressDialog.dismiss();
	        	
	    	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    	if (plot != null) {
	        	application.setPlot(plot);
	        	//recommendationField.setText(plot.recommendation);
		      	builder.setMessage("Plot submitted").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				   @Override
				   public void onClick(DialogInterface dialog, int which) {
				   }
		      	});
	    	} else {
		      	builder.setMessage("Plot submit failed").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				   @Override
				   public void onClick(DialogInterface dialog, int which) {
				   }
		      	});	
	    	}
        	AlertDialog alert = builder.create();
        	alert.show(); 		

        }
    }
        
}
