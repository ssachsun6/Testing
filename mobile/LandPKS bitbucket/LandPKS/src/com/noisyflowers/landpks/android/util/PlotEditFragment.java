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
 * com.noisyflowers.landpks.android.util
 * PlotEditFragment.java
 */

package com.noisyflowers.landpks.android.util;

import com.noisyflowers.landpks.android.LandPKSApplication;
import com.noisyflowers.landpks.android.activities.AboutActivity;
import com.noisyflowers.landpks.android.activities.PlotEditDetailActivity;
import com.noisyflowers.landpks.android.activities.PlotEditListActivity;
import com.noisyflowers.landpks.android.activities.SettingsActivity;
import com.noisyflowers.landpks.android.R;
import com.noisyflowers.landpks.android.model.Plot;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;

public abstract class PlotEditFragment extends Fragment {
	
	public static PlotEditFragment instantiate(Context context, String fname, Bundle args){
		return (PlotEditFragment)Fragment.instantiate(context, fname, args);
	}
		
	@Override
	public void onPause() {
		super.onPause();
		Plot plot = LandPKSApplication.getInstance().getPlot();
		save(plot);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Plot plot = LandPKSApplication.getInstance().getPlot();
		load(plot);
	}
	
    public abstract void load(Plot plot);
    public abstract void save(Plot plot);
    public abstract boolean isComplete(Plot plot);

    
    //TODO: Experimentally moved this here from PlotEditDetailActivity. Watch for problems.
    //Why? I needed to override it in NameFragment to get in a dialog.
    @Override
    public void onCreate(Bundle b) {
    	super.onCreate(b);
    	setHasOptionsMenu(true);
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
				//
				Intent upIntent = new Intent(getActivity(), PlotEditListActivity.class);
				upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
				NavUtils.navigateUpTo(getActivity(), upIntent);
				return true;
				
				//***tentative	
	        case R.id.action_settings: {
				Intent settingsIntent = new Intent(getActivity(), SettingsActivity.class);
				settingsIntent.putExtra(SettingsActivity.SETTINGS_MODE, SettingsActivity.ALL);
				startActivityForResult(settingsIntent, 0);
	            return true;
			}
	            
	        case R.id.action_about: {
				Intent settingsIntent = new Intent(getActivity(), AboutActivity.class);
				startActivity(settingsIntent);
	            return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}
	
	    
}
