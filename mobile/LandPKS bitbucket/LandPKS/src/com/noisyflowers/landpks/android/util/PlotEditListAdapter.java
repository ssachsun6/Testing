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
 * PlotEditListAdapter.java
 */

package com.noisyflowers.landpks.android.util;

import java.util.List;

import com.noisyflowers.landpks.android.LandPKSApplication;
import com.noisyflowers.landpks.android.R;
import com.noisyflowers.landpks.android.fragments.NameFragment;
import com.noisyflowers.landpks.android.model.Plot;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

public class PlotEditListAdapter extends ArrayAdapter {

	   	Context context; 
	    int layoutResourceId;    
	 	List<Fragment> list = null;
	 	
	public PlotEditListAdapter(Context context, int textViewResourceId,
			List<Fragment> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.layoutResourceId = textViewResourceId;
		list = objects;
	}

	
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	/***
    	View row = convertView;
        ViewHolder holder = null;
        
        if(row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new ViewHolder();
            //holder.imgIcon = (ImageView)row.findViewById(R.id.imgIcon);
            holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);
            
            row.setTag(holder);
        } else {
            holder = (ViewHolder)row.getTag();
        }
        ***/
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	 
		//View rowView = inflater.inflate(R.layout.plot_edit_list_element, parent, false);
		//TextView textView = (TextView) rowView.findViewById(R.id.taskName);
		View rowView = inflater.inflate(/*layoutResourceId*/android.R.layout.simple_list_item_checked, parent, false);
		TextView textView = (TextView) rowView.findViewById(android.R.id.text1);  //TODO: can't assume simple_list_item_1.  Should probably use our own layout
		//ImageView imageView = (ImageView) rowView.findViewById(R.id.logo);    	

        Fragment f = list.get(position);

		String name = "";
		try {
			name = f.getArguments().getString("Name");
		} catch (Exception ex) {}
		//holder.txtTitle.setText(name);
		textView.setText(name);
		
    	Plot plot = LandPKSApplication.getInstance().getPlot();
		if (((PlotEditFragment) f).isComplete(plot)) {
			//rowView.setBackgroundColor(Color.LTGRAY);
			((CheckedTextView)rowView).setChecked(true);
		}
		return rowView;
    }

    /*** use this if we decide not to provide only Name when new plot
    @Override
    public boolean areAllItemsEnabled () {
    	return false;
    }
    
    //This doesn't seem to be getting called as expected
    @Override
    public boolean isEnabled(int position) {
    	PlotEditFragment f = (PlotEditFragment) getItem(position);
    	//String name = f.getArguments().getString("Name");
    	//if (!"Name".equals(name)) { 	//TODO:  Name should not be hard-coded here
    	if (!(f instanceof NameFragment)) {
	    	Plot plot = LandPKSApplication.getInstance().getPlot();
	        if(plot.name == null || "".equals(plot.name)) {
	            return false;
	        }
    	}
        return true;
    }
    ***/
    
    static class ViewHolder
    {
        //ImageView imgIcon;
        TextView txtTitle;
    }
}
	
