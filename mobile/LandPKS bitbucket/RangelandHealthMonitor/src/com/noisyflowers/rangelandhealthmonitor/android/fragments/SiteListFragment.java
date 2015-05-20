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
 * com.noisyflowers.rangelandhealthmonitor.android.fragments
 * SiteListFragment.java
 */

package com.noisyflowers.rangelandhealthmonitor.android.fragments;

import android.app.Activity;
//import android.app.ListFragment;
//import android.content.CursorLoader;
//import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Html;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.noisyflowers.landpks.android.LandPKSContract;
import com.noisyflowers.rangelandhealthmonitor.android.R;
import com.noisyflowers.rangelandhealthmonitor.android.RHMApplication;
import com.noisyflowers.rangelandhealthmonitor.android.dummy.DummyContent;
import com.noisyflowers.rangelandhealthmonitor.android.util.IHelp;
import com.noisyflowers.rangelandhealthmonitor.android.util.SiteCursorAdapter;

/**
 * A list fragment representing a list of TransectSegments. This fragment also
 * supports tablet devices by allowing list items to be given an 'activated'
 * state upon selection. This helps indicate which item is currently being
 * viewed in a {@link SiteDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class SiteListFragment extends ListFragment implements IHelp, LoaderManager.LoaderCallbacks<Cursor>{

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";

	/**
	 * The fragment's current callback object, which is notified of list item
	 * clicks.
	 */
	private Callbacks mCallbacks = sDummyCallbacks;

	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int mActivatedPosition = ListView.INVALID_POSITION;

	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface Callbacks {
		/**
		 * Callback for when an item has been selected.
		 */
		public void onItemSelected(String id);
	}

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onItemSelected(String id) {
		}
	};

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public SiteListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		fillData();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// Restore the previously serialized activated item position.
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState
					.getInt(STATE_ACTIVATED_POSITION));
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = sDummyCallbacks;
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);
		Cursor c = ((SimpleCursorAdapter)listView.getAdapter()).getCursor();
		c.moveToPosition(position);
		//String siteName = c.getString(1);
		//I can't decide if this is lame or not. Need to get both name and id to SiteDetailActivity, but callback 
		//interface only has one string param. I didn't choose to use the callback interface; it was included by the Google
		//code generator.  Could probably get along just fine without it, as we did in LandPKS, just letting fragment start
		//SiteDetailActivity.  So TODO: possibly get rid of callback interface here.
		String siteStr = c.getString(0) + "/" + c.getString(1);
		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.
		//mCallbacks.onItemSelected(DummyContent.ITEMS.get(position).id);
		mCallbacks.onItemSelected(siteStr);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		getListView().setChoiceMode(
				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}

	private void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(mActivatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}

	
	
	private SimpleCursorAdapter adapter;
	
	private void fillData() {
		// Fields from the database (projection)
	    // Must include the _id column for the adapter to work
	    String[] from = new String[] {LandPKSContract.SITE_COLUMN_NAME};
	    // Fields on the UI to which we map
	    int[] to = new int[] { android.R.id.text1 };

	    getLoaderManager().restartLoader(0, null, (LoaderManager.LoaderCallbacks<Cursor>)this);
	    adapter = new SiteCursorAdapter(getActivity(), android.R.layout.simple_list_item_activated_1, null, from, to, 0);

	    setListAdapter(adapter);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    	SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
    	String accountName = settings.getString(RHMApplication.PREF_ACCOUNT_NAME_KEY, null);
	    String[] projection = {LandPKSContract.SITE_COLUMN_ID, LandPKSContract.SITE_COLUMN_NAME, LandPKSContract.SITE_COLUMN_REMOTE_ID};  
        String URL = LandPKSContract.CONTENT_URI + "/" + LandPKSContract.SITES_PATH;
        Uri sites = Uri.parse(URL);
	    CursorLoader cursorLoader = new CursorLoader(getActivity(),
	    		sites, projection, LandPKSContract.SITE_COLUMN_RECORDER_NAME + "=?", new String[] {accountName}, LandPKSContract.SITE_COLUMN_NAME);
	    return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
	    adapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	    adapter.swapCursor(null);
	}

	@Override
	public View getHelpView() {
		ScrollView sV = new ScrollView(getActivity());
		TextView tV = new TextView(getActivity());
		tV.setPadding(10,10,10,10);
		tV.setText(Html.fromHtml(getString(R.string.fragment_site_list_help)));
		sV.addView(tV);
		return sV;
	}
}
