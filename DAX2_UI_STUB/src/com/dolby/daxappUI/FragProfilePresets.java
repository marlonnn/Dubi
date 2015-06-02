/******************************************************************************
 *  This program is protected under international and U.S. copyright laws as
 *  an unpublished work. This program is confidential and proprietary to the
 *  copyright owners. Reproduction or disclosure, in whole or in part, or the
 *  production of derivative works therefrom without the express permission of
 *  the copyright owners is prohibited.
 *
 *                 Copyright (C) 2011-2015 by Dolby Laboratories,
 *                             All rights reserved.
 ******************************************************************************/
package com.dolby.daxappUI;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListAdapter;

import com.dolby.api.DsGlobalEx;
import com.dolby.api.IDsEvents;
import com.dolby.daxappCoreUI.Constants;
import com.dolby.daxappCoreUI.DAXApplication;
import com.dolby.daxappCoreUI.Tag;
import com.dolby.daxappCoreUI.Tools;

public class FragProfilePresets extends Fragment implements OnClickListener,
		OnItemClickListener, OnItemLongClickListener, IDsEvents {

    // Data members.
    // Profile stuff.
    private ProfilesAdapter mProfilesAdapter;

    // Tooltip stuff?
    private ViewGroup mNativeRootContainer;

    // DsClient instance.
    private DsGlobalEx mDsClient;
    // Required to know whether our local instance has connected to the service
    // or not.
    private boolean mDolbyClientConnected = false;

    // Our observers.
    // Specific Profile Presets Fragment Observer.
    private IDsFragProfilePresetsObserver mSpecificObserver;
    // Generic Fragment Observer (error handling / generic provider).
    private IDsFragObserver mFObserver;

    private boolean mMobileLayout = false;

    // From Fragment.
    // First method called by the framework.
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // The activity shall implement the interfaces we require.
        // Ideally, we should receive those as parameters, since that
        // way, it's more obvious and detached from an activity object. We'll do
        // just that after finishing bringing this up, unless this way is found
        // to be sufficient.
        // CONSIDER THIS TEMPORARY. This might change.

        try {
            mFObserver = (IDsFragObserver) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement IDsFragObserver");
        }

        try {
            mSpecificObserver = (IDsFragProfilePresetsObserver) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement IDsFragProfilePresetsObserver");
        }

        // Activity supports both Interfaces!
        // Fetching DsClient instance.
        mDsClient = mFObserver.getDsClient();
        // Learning whether we are operating in a "mobile" layout or not.
        mMobileLayout = getResources().getBoolean(R.bool.newLayout);
    }

    // Second method called by the framework.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Third method called by the framework: this is where we
    // actually inflate the layout.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflating appropriate layout.
        View v = inflater.inflate(R.layout.fragprofilepresets, container, false);

        // Setting up profile presets list.
        @SuppressWarnings("unchecked")
        AdapterView<ListAdapter> lv = (AdapterView<ListAdapter>) v.findViewById(R.id.presetsListView);
        // TODO change this to expect IDsFragObserver somehow.
        mProfilesAdapter = new ProfilesAdapter((MainActivity) getActivity(), R.layout.preset_list_item, mDsClient, (OnClickListener) this);
        lv.setAdapter(mProfilesAdapter);
        lv.setOnItemClickListener(this);
        lv.setOnItemLongClickListener(this);

        // Related to tooltip display.
        mNativeRootContainer = ViewTools.determineNativeViewContainer(getActivity());

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        // If in mobile layout, reporting for second phase initialization.
        if (mMobileLayout == true) {
            mSpecificObserver.profilePresetsAreAlive();
        }
    }

    @Override
    public void onPause() {

        if (null != mProfilesAdapter) {
            mProfilesAdapter.endEditingProfileName(true);
        }

        super.onPause();
    }

    public void onClientConnected() {
        mDolbyClientConnected = true;
        if (mMobileLayout == true) {
            mSpecificObserver.profilePresetsAreAlive();
        }
    }

    public void onClientDisconnected() {
        mDolbyClientConnected = false;
    }

	public void onProfileNameChanged(int prefile, String name) {
		if (mProfilesAdapter != null) {
			// Redraw the list of profiles (with new profile name).
			mProfilesAdapter.scheduleNotifyDataSetChanged();
		}
	}

    // IDsEvents
    @Override
    public void onDsOn(boolean on) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProfileSelected(int profile) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProfileSettingsChanged(int profile) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onDsSuspended(boolean isSuspended) {
    	// DS2API-TODO: ......???
    }
    // IDsEvents END

    // From OnClickListener.
    @Override
    public void onClick(View view) {
        onDolbyClientUseClick(view);
    }

    // From OnItemLongClickListener.
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view,
            int position, long id) {
        if (parent.getId() == R.id.presetsListView) {
        	final int profile = mDsClient.getProfile();
            if (profile != (position-1)) {
                onItemClick(parent, view, position, id);
            }
            mProfilesAdapter.startEditingProfileName(position);
            return true;
        }

        return false;
    }

    // From OnItemClickListener.
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        if (position == Constants.DEMO_POSITION){
			if (null != mFObserver) {
				startActivity(new Intent(MainActivity.ACTION_LAUNCH_DAX_INSTOREDEMO_APP));
			}
            return;
        }
        //not mobile layout
        if(mMobileLayout == false) {
	        if (position == Constants.EXPLORE_DOLBY_ATMOS){
	        	mSpecificObserver.chooseExploreAtmosProfile();
	            return;
	        } else {
	        	mSpecificObserver.hideExploreAtomsProfile();
	        }
        } else {
        	if (position == Constants.EXPLORE_DOLBY_ATMOS){
				startActivity(new Intent("com.dolby.EXPLORE_DOLBY_ATMOS"));
	            return;

	        }
        }
        if (mMobileLayout == true) {
        	final int profile = mDsClient.getProfile();
            if (profile == (position-1)) {
                // Click was on an already selected profile. Activating
                // edition mode.
                mSpecificObserver.editProfile();
                return;
            }
//            if (position == Constants.EXPLORE_DOLBY_ATMOS){
//                return;
//            }
        }

        mProfilesAdapter.endEditingProfileName(true);

        if ((getView() == null) || (mFObserver == null)) {
            Log.w(Tag.MAIN, "FragProfilePresets.onItemClick(), getView() == null or mFObserver == null.");
            return;
        }
        if (parent == getView().findViewById(R.id.presetsListView) && mFObserver.useDsApiOnUiEvent()) {
            mSpecificObserver.chooseProfile(position-1);
        }
    }

    public void setSelection(int profile) {
        if (mProfilesAdapter != null) {
            mProfilesAdapter.setSelection(profile+1);
        }
    }

    public int getSelection() {
        return mProfilesAdapter.getSelection()-1;
    }

    public String getDefaultProfileName(int profile) {
        return mProfilesAdapter.getDefaultProfileName(profile+1);
    }

    public String getItemName(int profile) {
        if (mProfilesAdapter != null) {
            return mProfilesAdapter.getItemName(profile+1);
        }
        return "";
    }

    public void setEnabled(boolean on) {

        if (this.isAdded()) {
            View theFragV = getView();

            if (!on) {
                if (mProfilesAdapter != null) {
                    mProfilesAdapter.endEditingProfileName(true);
                }
            }

            View listView = theFragV.findViewById(R.id.presetsListView);
            if (listView != null) {
                listView.setEnabled(on);
            }
        }
    }

    public void scheduleNotifyDataSetChanged() {
        if (mProfilesAdapter != null) {
            mProfilesAdapter.scheduleNotifyDataSetChanged();
        }
    }

    /**
     * Call this when going into profile name edit mode using software keyboard.
     */
    // Making public so it can be accessed from MainActivity.
    public void onProfileNameEditStarted() {
        Log.d(Tag.MAIN, "Main.onProfileNameEditStarted()");

        if (!Tools.isLandscapeScreenOrientation(getActivity()) || mNativeRootContainer == null) {
            return;
        }

        final ViewTreeObserver.OnPreDrawListener preDrawListener = new ViewTreeObserver.OnPreDrawListener() {

            private int counter = 30;

            private final Runnable refreshLayout = new Runnable() {

                @Override
                public void run() {
                    refreshLayout();
                }
            };

            private final Runnable removePreDrawListener = new Runnable() {

                @Override
                public void run() {
                    removePreDrawListener();
                }
            };

            private boolean skipNext = false;

            @Override
            public boolean onPreDraw() {
                if(Tag.DEBUG_DSUI)
                    Log.d(Tag.MAIN, "Main.onProfileNameEditStarted.onPreDraw() " + counter--);
                if (!skipNext) {
                    DAXApplication.HANDLER.removeCallbacks(refreshLayout);
                    DAXApplication.HANDLER.postDelayed(refreshLayout, 100);
                } else {
                    skipNext = false;
                }
                if (counter <= 0) {
                    removePreDrawListener();
                }
                return true;
            }

            private void refreshLayout() {
                if(Tag.DEBUG_DSUI)
                    Log.d(Tag.MAIN, "Main.onProfileNameEditStarted.refreshLayout()");
                if (mNativeRootContainer == null) {
                    return;
                }

                skipNext = true;

                mNativeRootContainer.requestLayout();
                mNativeRootContainer.invalidate();

                DAXApplication.HANDLER.removeCallbacks(refreshLayout);
                DAXApplication.HANDLER.removeCallbacks(removePreDrawListener);
                DAXApplication.HANDLER.postDelayed(removePreDrawListener, 2000);
            }

            private void removePreDrawListener() {
                if(Tag.DEBUG_DSUI)
                    Log.d(Tag.MAIN, "Main.onProfileNameEditStarted.removePreDrawListener()");
                DAXApplication.HANDLER.removeCallbacks(refreshLayout);
                DAXApplication.HANDLER.removeCallbacks(removePreDrawListener);
                mNativeRootContainer.getViewTreeObserver().removeOnPreDrawListener(this);
            }
        };

        mNativeRootContainer.getViewTreeObserver().addOnPreDrawListener(preDrawListener);
    }

    private void onDolbyClientUseClick(View view) {
        if (!mDolbyClientConnected || !mFObserver.useDsApiOnUiEvent()) {
            return;
        }

        final int id = view.getId();

        if (R.id.revertButton == id) { // Revert icon in ProfilesAdapter.
            // Doing local reset (nothing really left to do).

            // Notifying observer that the given profile was reset and that
            // the "event" should be propagated.
            final int selectedProfile = mDsClient.getProfile();
            mSpecificObserver.profileReset(selectedProfile);
        }
    }
}
