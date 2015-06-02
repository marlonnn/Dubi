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
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.dolby.api.DsAccess;
import com.dolby.api.DsConstants;
import com.dolby.api.DsGlobalEx;
import com.dolby.api.IDsEvents;
import com.dolby.api.IDsVisualizerEvents;
import com.dolby.daxappCoreUI.DAXApplication;
import com.dolby.daxappCoreUI.DsClientSettings;
import com.dolby.daxappCoreUI.Tag;
import com.dolby.daxappUI.EqualizerAdapter.IPresetListener;

// Fragment only supporting regular (not full screen) graphic visualizer.
public class FragGraphicVisualizer extends Fragment implements OnClickListener,
        IEqualizerChangeListener, IDsEvents, IDsVisualizerEvents {
    private static final String TAG = "FragGraphicVisualizer";

    // Constants.
    private static final int EQUALIZER_SETTING_CUSTOM = -1;
    private static final int CUSTOM_EQ = 4;
    private static final int CUSTOM_EQ_MOBILE = 3;

    // Data members.
    private GraphicVisualiser mVisualiser;
    private GridView mIEqPresets;
    private EqualizerAdapter mEqualizerAdapter;
    // private boolean mVisualizerRegistered = false;
    // DsClient instance.
    private DsGlobalEx mDsClient;
    // Required to know whether our local instance has connected to the service
    // or not.
    private boolean mDolbyClientConnected = false;

    // Our observers.
    // Specific Graphic Visualizer Fragment Observer.
    private IDsFragGraphicVisualizerObserver mSpecificObserver;
    // Generic Fragment Observer (error handling / generic provider).
    private IDsFragObserver mFObserver;

    // What's this for?
    // Tooltip?
    private View mQmIntEq;

    // Variable used to store selected preset when no EQAdapter is available.
    private int mPreset;

    private ImageView mFrame;

    private boolean mMobileLayout = false;
    
    LayoutInflater mInflater = null;
    ViewGroup  mContainer = null;
    private Activity mActivity;

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
            mSpecificObserver = (IDsFragGraphicVisualizerObserver) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement IDsFragGraphicVisualizerObserver");
        }
        
        mActivity = activity;

        // Activity supports both Interfaces!
        // Fetching DsClient instance.
        mDsClient = mFObserver.getDsClient();
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
        View v = inflater.inflate(R.layout.fraggraphicvisualizer, container, false);
        mInflater = inflater;
        mContainer = container;

        // Updating labels' font, related to this fragment.
        final int textIds[] = { R.id.equalizerLabel, R.id.equalizerName };
        for (int id : textIds) {
            TextView tv = (TextView) v.findViewById(id);
            if (tv != null) {
                tv.setTypeface(Assets.getFont(Assets.FontType.REGULAR));
            }
        }
        mFrame = (ImageView) v.findViewById(R.id.eqListFrame);
        // Setting up equalizer settings list, if available.
        mIEqPresets = (GridView) v.findViewById(R.id.equalizerListView);
        if (mIEqPresets != null) {
            mEqualizerAdapter = new EqualizerAdapter(getActivity(), R.layout.equalizer_list_item, new IPresetListener() {

                @Override
                public void onPresetChanged(int position) {
                    chooseEqualizerSettinginUI(position);
                }
            });

            mIEqPresets.setAdapter(mEqualizerAdapter);

            mIEqPresets.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if ((event.getAction() == MotionEvent.ACTION_MOVE) && mIEqPresets.isEnabled()) {
                        mEqualizerAdapter.scheduleNotifyDataSetChanged();
                        return true;
                    }
                    return false;
                }
            });
        }
        // Adding Click Listeners.
        // We won't always always have these 2.
        View theV = v.findViewById(R.id.equalizerCustom);
        if (theV != null) {
            theV.setOnClickListener(this);
            theV.setSoundEffectsEnabled(false);
        }

        theV = v.findViewById(R.id.eqResetButton);
        if (theV != null) {
            theV.setOnClickListener(this);
            theV.setSoundEffectsEnabled(false);
        }

        // Tooltip?
        mQmIntEq = v.findViewById(R.id.qm_inteq);
        if (mQmIntEq != null) {
            mQmIntEq.setOnClickListener(this);
            mQmIntEq.setSoundEffectsEnabled(false);
        }

        // This is the mandatory component.
        mVisualiser = (GraphicVisualiser) v.findViewById(R.id.graphic_vis);
        mVisualiser.getEqualizer().setActivity((IDsActivityCommon) getActivity());
        mVisualiser.getEqualizer().setDsClient(mDsClient);
        mVisualiser.getEqualizer().setEqualizerListener(this);

        // Finding out if using mobile layout.
        mMobileLayout = getResources().getBoolean(R.bool.newLayout);
        if (mMobileLayout) {
            mVisualiser.mEnableEditTouch = false;
        }

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    // From OnClickListener.
    @Override
    public void onClick(View view) {
        final int id = view.getId();

        if (R.id.qm_inteq == id) {
            mSpecificObserver.displayTooltip(view, R.string.tooltip_eq_title, R.string.tooltip_eq_text);
        } else {
            onDolbyClientUseClick(view);
        }
    }

    // From IEqualizerChangeListener.
    @Override
    public void onEqualizerEditStart() {
        setResetEqButtonVisibility();

        // Propagating event, so the observer can take action.
        mSpecificObserver.onEqualizerEditStart();
    }

    // From IDsVisualizerEvents.
    @Override
    public void onVisualizerUpdate(float[] excitations, float[] gains) {
        if (mVisualiser == null) {
            return;
        }

        if (excitations != null) {
            mVisualiser.setExcitations(excitations);
        }

        if (gains != null) {
            mVisualiser.onVisualizerUpdate(gains);
        }

        if (excitations != null || gains != null) {
            mVisualiser.repaint();
        }
    }

    @Override
    public void onVisualizerSuspended(boolean suspended) {
        Log.d(TAG, "MainActivity.onVisualizerSuspended ? " + suspended);
        mVisualiser.setSuspended(suspended);
        mVisualiser.repaint(true);
    }

    public void onClientConnected() {
        mDolbyClientConnected = true;
    }

    public void onClientDisconnected() {
        mDolbyClientConnected = false;
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

    // Public Fragment API???
    public void setEnabled(boolean on) {
        if (mIEqPresets != null) {
            mIEqPresets.setEnabled(on);
            mEqualizerAdapter.setDolbyOnOff(on);
            mEqualizerAdapter.scheduleNotifyDataSetChanged();
        }

        View theFragV = getView();

        View v = theFragV.findViewById(R.id.equalizerLabel);
        if (v != null) {
            v.setEnabled(on);
        }

        v = theFragV.findViewById(R.id.equalizerName);
        if (v != null) {
            v.setEnabled(on);
        }

        v = theFragV.findViewById(R.id.equalizerCustom);
        if (v != null) {
            v.setEnabled(on);
            v.setSoundEffectsEnabled(false);
        }

        v = theFragV.findViewById(R.id.eqListFrame);
        if (v != null) {
            v.setEnabled(on);
            v.setSoundEffectsEnabled(false);
        }

        v = theFragV.findViewById(R.id.spectralVis);
        if (v != null) {
            v.setEnabled(on);
            v.setSoundEffectsEnabled(false);
        }

        v = theFragV.findViewById(R.id.verticalAxis);
        if (v != null) {
            v.setEnabled(on);
            v.setSoundEffectsEnabled(false);
        }

        if (mQmIntEq != null) {
            mQmIntEq.setEnabled(on);
        }

        setResetEqButtonVisibility();
    }

    public void registerVisualizer(boolean on) {
        try {
            mVisualiser.setSuspended(false);
            if (on) {
                //DS1SOC-566, don't register the visualizer listener when visualizer data is disabled
                if (DAXApplication.VISUALIZER_ENABLE) {
                    Log.d(TAG, "registerVisualizer");
                    mDsClient.registerVisualizer(this);
                } else {
                    mVisualiser.setSuspended(true);
                }
            } else {
                if (DAXApplication.VISUALIZER_ENABLE) {
                    Log.d(TAG, "unregisterVisualizer");
                    mDsClient.unregisterVisualizer();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            mFObserver.onDsApiError();
            return;
        }
    }

    public void hideEqualizer() {
        mVisualiser.getEqualizer().hide();
    }

    // Deep reset.
    public void resetUserGains(int selectedProfile) {
        if (DsAccess.THIS_APP_GRANTED != mDsClient.checkAccessRight()) {
            final int arValue = mDsClient.requestAccessRight();
            if (DsConstants.DS_NO_ERROR != arValue) {
                return;
            }
        }
        try {
            mVisualiser.getEqualizer().resetUserGains(false);
            mDsClient.resetProfile(selectedProfile);
            mVisualiser.repaint(true);
        } catch (Exception e) {
            e.printStackTrace();
            mFObserver.onDsApiError();
            return;
        }
    }

    // Light reset. Triggered by clicking on eqResetButton, from external
    // fragment.
    public void resetUserGains() {
        mVisualiser.getEqualizer().resetUserGains(true);
    }

    public void selectIEqPresetInUI(int preset) {

        if (mMobileLayout == true) {
            if (preset == EQUALIZER_SETTING_CUSTOM) {
                preset = CUSTOM_EQ_MOBILE;
            }
        }

        View v = getView();

        if (mEqualizerAdapter != null) {
            mEqualizerAdapter.setSelection(preset);
        } else {
            mPreset = preset;
        }

        if (mMobileLayout == false) {
            // Does this really make sense?
            TextView theTV = (TextView) v.findViewById(R.id.equalizerLabel);
            if (theTV != null) {
                final int p = mDsClient.getProfile();
                if (p != CUSTOM_EQ) {
                    theTV.setText(R.string.intelligent_equalizer);
                } else {
                    theTV.setText(R.string.graphical_equalizer);
                }
            }

            View theV = v.findViewById(R.id.equalizerCustom);
            if (theV != null) {
                theV.setSelected(preset == EQUALIZER_SETTING_CUSTOM);
            }

            TextView eqText = (TextView) v.findViewById(R.id.equalizerLabel);
            if (eqText != null) {
                eqText.setText(preset == EQUALIZER_SETTING_CUSTOM ? getString(R.string.graphical_equalizer) : getString(R.string.intelligent_equalizer));
            }

            TextView tv = (TextView) v.findViewById(R.id.equalizerName);
            if (tv != null && mEqualizerAdapter != null) {
                tv.setText(preset == EQUALIZER_SETTING_CUSTOM ? getString(R.string.custom) : mEqualizerAdapter.getItem(preset).getName());
            }
        } else {
            TextView eqText = (TextView) v.findViewById(R.id.equalizerLabel);
            if (eqText != null) {
                eqText.setText(preset == CUSTOM_EQ_MOBILE ? getString(R.string.graphical_equalizer) : getString(R.string.intelligent_equalizer));
            }

            TextView tv = (TextView) v.findViewById(R.id.equalizerName);
            if (tv != null && mEqualizerAdapter != null) {
                tv.setText(mEqualizerAdapter.getItem(preset).getName());
            }
        }

        updateGraphicEqInUI();
    }

    public void setResetEqButtonVisibility() {
        int vis = View.INVISIBLE;
        if (DsConstants.DS_STATE_ON == mDsClient.getState()) {
            final boolean geqOn = DsClientSettings.INSTANCE.getGeqOn((MainActivity) mActivity, mDsClient);
            if (geqOn) {
                vis = View.VISIBLE;
            }
        }
        
        View theV = getView().findViewById(R.id.eqResetButton);
        if (theV != null) {
            theV.setVisibility(vis);
        }

    }

    public void updateGraphicEqInUI() {
        if (mVisualiser == null) {
            return;
        }

        final int selectedProfile = mDsClient.getProfile();
        if (selectedProfile == -1) {
            return;
        }

        final GraphicEqualizerPainter eq = mVisualiser.getEqualizer();

        int temp;

        if (mEqualizerAdapter != null) {
            temp = mEqualizerAdapter.getSelection();
        } else {
            temp = mPreset;
        }

        if (mMobileLayout == true) {
            if (temp == CUSTOM_EQ_MOBILE) {
                temp = EQUALIZER_SETTING_CUSTOM;
            }
        }

        eq.switchPreset(selectedProfile, temp, ((IDsActivityCommon) getActivity()).useDsApiOnUiEvent());
        mVisualiser.repaint(true);

        // show or hide the equalizer Reset Button
        setResetEqButtonVisibility();
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        
        Log.d(TAG, "GraphicVisualiser fragment onResume");
        mInflater.inflate(R.layout.fraggraphicvisualizer, mContainer, false);
        getView().invalidate();
        if (mVisualiser != null) {
            mVisualiser.setActiveStatus(true);
        }

    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        Log.d(TAG, "GraphicVisualiser fragment onPause");
        if (mVisualiser != null) {
            mVisualiser.setActiveStatus(false);  
         }
    }

    private void chooseEqualizerSettinginUI(int preset) {
        if(Tag.DEBUG_DSUI)
            Log.d(TAG, "chooseEqualizerSetting " + preset);

        if (mMobileLayout == true) {
            if (preset == CUSTOM_EQ_MOBILE) {
                preset = EQUALIZER_SETTING_CUSTOM;
            }
        }

        if (!mDolbyClientConnected) {
            return;
        }

        if(Tag.DEBUG_DSUI)
            Log.d(TAG, "chooseEqualizerSettinginUI(): setIeqPreset " + (preset + 1));
        if (!DsClientSettings.INSTANCE.setIeqPreset((MainActivity) mActivity, mDsClient, preset + 1)) {
            return;
        }

        selectIEqPresetInUI(preset);

        final int selectedProfile = mDsClient.getProfile();
        GraphicEqualizerPainter eq = mVisualiser.getEqualizer();
        eq.switchPreset(selectedProfile, preset, ((IDsActivityCommon) getActivity()).useDsApiOnUiEvent());

        setResetEqButtonVisibility();

        chooseEqualizerSetting(preset);
    }

    /**
     * Set selected preset in the service, update EQ
     * 
     * @param preset
     *            number of the preset selected in UI.
     * 
     *            Note: UI assumes EQUALIZER_SETTING_CUSTOM = -1 as the custom
     *            preset (default), predefined presets = 0..5.
     * 
     *            Service assumes custom preset idx = 0, predefided presets =
     *            1..6.
     */
    private void chooseEqualizerSetting(int preset) {
        if (!mDolbyClientConnected) {
            return;
        }

        if (mMobileLayout == true) {
            if (preset == CUSTOM_EQ_MOBILE) {
                preset = EQUALIZER_SETTING_CUSTOM;
            }
        }

        selectIEqPresetInUI(preset);

        mSpecificObserver.setUserProfilePopulated();

        updateGraphicEqInUI();
        mSpecificObserver.onProfileSettingsChanged();
    }

    private void onDolbyClientUseClick(View view) {
        if (!mDolbyClientConnected || !((IDsActivityCommon) getActivity()).useDsApiOnUiEvent()) {
            return;
        }

        final int id = view.getId();

        if (R.id.equalizerCustom == id) {
            chooseEqualizerSettinginUI(EQUALIZER_SETTING_CUSTOM);
            TextView eqText = (TextView) getView().findViewById(R.id.equalizerLabel);
            eqText.setText(R.string.graphical_equalizer);
        } else if (R.id.eqResetButton == id) {
            resetGEqOnUserClick();
        }
    }

    private void resetGEqOnUserClick() {
        mVisualiser.getEqualizer().resetUserGains(true);
        mSpecificObserver.onProfileSettingsChanged();
    }

    void setEnableEditGraphic(boolean evalue) {
        mVisualiser.mEnableEditTouch = evalue;
    }

}
