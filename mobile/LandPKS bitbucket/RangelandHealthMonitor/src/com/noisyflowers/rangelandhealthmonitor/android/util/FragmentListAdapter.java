package com.noisyflowers.rangelandhealthmonitor.android.util;

import java.util.Date;
import java.util.List;






import com.noisyflowers.rangelandhealthmonitor.android.RHMApplication;
import com.noisyflowers.rangelandhealthmonitor.android.model.Segment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

public class FragmentListAdapter extends ArrayAdapter {
	public static final String ARG_NAME = "displayName";

	Context context; 
    int layoutResourceId;    
 	List<Fragment> list = null;
 	
	public FragmentListAdapter(Context context, int textViewResourceId,
			List<Fragment> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.layoutResourceId = textViewResourceId;
		list = objects;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
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
			name = f.getArguments().getString(ARG_NAME);
		} catch (Exception ex) {}
		//holder.txtTitle.setText(displayName);
		textView.setText(name);
		
		//Plot plot = LandPKSApplication.getInstance().getPlot();
		//Segment range = RHMApplication.getInstance().getDatabaseAdapter().getSegment(position, RHMApplication.getInstance().getTransectID());
		//Segment segment = RHMApplication.getInstance().getDatabaseAdapter().getSegment(position, RHMApplication.getInstance().getTransectID(), new Date());
		Segment segment = RHMApplication.getInstance().getDatabaseAdapter().getSegment(Segment.Range.values()[position], RHMApplication.getInstance().getTransectID(), new Date());
		//if (((PersistenceFragment) f).isComplete(segment)) {
		if (segment != null && segment.isComplete()) {
			((CheckedTextView)rowView).setChecked(true);
		}
		return rowView;
	}

}
