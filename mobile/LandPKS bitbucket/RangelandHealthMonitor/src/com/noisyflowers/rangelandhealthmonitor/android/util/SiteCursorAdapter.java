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
 * com.noisyflowers.rangelandhealthmonitor.android.util
 * SiteCursorAdapter.java
 */

package com.noisyflowers.rangelandhealthmonitor.android.util;

import java.util.Date;
import java.util.List;

import com.noisyflowers.landpks.android.LandPKSContract;
import com.noisyflowers.rangelandhealthmonitor.android.RHMApplication;
import com.noisyflowers.rangelandhealthmonitor.android.dal.RHMDatabaseAdapter;
import com.noisyflowers.rangelandhealthmonitor.android.model.Segment;
import com.noisyflowers.rangelandhealthmonitor.android.model.Transect;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class SiteCursorAdapter extends SimpleCursorAdapter {
	public SiteCursorAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, int flags) {
		super(context, layout, c, from, to, flags);
	}
	
	
	@Override
	public void bindView(View view, Context context, Cursor c) {
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        
        String name = c.getString(c.getColumnIndex(LandPKSContract.SITE_COLUMN_NAME));
        textView.setText(name);
        
        if (isSiteUploaded(c.getString(c.getColumnIndex(LandPKSContract.SITE_COLUMN_ID)), new Date())) {
			((CheckedTextView)view).setChecked(true);
		} else {
			((CheckedTextView)view).setChecked(false); //views are reused under the hood, so must clear the check to avoid ghost checks
		}
        
	}


	@Override
	public View newView(Context context, Cursor c, ViewGroup parent) {
        //Cursor c = getCursor();
        
        final LayoutInflater inflater = LayoutInflater.from(context);
        View rowView = inflater.inflate(android.R.layout.simple_list_item_checked, parent, false);
        TextView textView = (TextView) rowView.findViewById(android.R.id.text1);
        
        String name = c.getString(c.getColumnIndex(LandPKSContract.SITE_COLUMN_NAME));
        textView.setText(name);
        
        if (isSiteUploaded(c.getString(c.getColumnIndex(LandPKSContract.SITE_COLUMN_ID)), new Date())) {
			((CheckedTextView)rowView).setChecked(true);
		} else {
			((CheckedTextView)rowView).setChecked(false);  //views are reused under the hood, so must clear the check to avoid ghost checks
		}
        
        return rowView;
	}
	
	
	public boolean isSiteUploaded(String siteID, Date date) {
		RHMDatabaseAdapter dbA = RHMApplication.getInstance().getDatabaseAdapter();
		
	    Transect transect = dbA.getTransect(siteID, Transect.Direction.NORTH); 
	    Date newDate = RHMApplication.getInstance().getDatabaseAdapter().getMostRecentSegmentDate(transect.ID); 
	    if (newDate == null) {
	    	return false;
	    } else {
	    	return transect.isUploaded(newDate);   
		}    
	}

}

