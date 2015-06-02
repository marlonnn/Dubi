/******************************************************************************
 *  This program is protected under international and U.S. copyright laws as
 *  an unpublished work. This program is confidential and proprietary to the
 *  copyright owners. Reproduction or disclosure, in whole or in part, or the
 *  production of derivative works therefrom without the express permission of
 *  the copyright owners is prohibited.
 *
 *                 Copyright (C) 2011-2014 by Dolby Laboratories,
 *                             All rights reserved.
 ******************************************************************************/

package com.dolby.daxappUI;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.dolby.api.DsAccess;
import com.dolby.api.DsAccessException;
import com.dolby.api.DsConstants;
import com.dolby.api.DsGlobalEx;
import com.dolby.api.DsProfileName;
import com.dolby.api.IDsAccessEvents;
import com.dolby.api.IDsEvents;
import com.dolby.api.IDsProfileNameEvents;
import com.dolby.daxappCoreUI.Constants;
import com.dolby.daxappCoreUI.DAXApplication;
import com.dolby.daxappCoreUI.DsClientSettings;
import com.dolby.daxappCoreUI.Tag;
import com.dolby.daxappCoreUI.Tools;

public class MainActivity extends Activity implements OnClickListener,
        IDsEvents, IDsAccessEvents, IDsProfileNameEvents,
        IDsFragSwitchesObserver, IDsFragPowerObserver,
        IDsFragGraphicVisualizerObserver, IDsFragObserver,
        IDsFragProfilePresetsObserver, IDsFragProfileEditorObserver,
        IDsFragEqualizerPresetsObserver, IDsActivityCommon {

    // Dolby Digital logo reference.
    private ImageView mDSLogo;

    // Dolby Surround Service Client stuff.
    private final DsGlobalEx mDsClient = new DsGlobalEx();
    private boolean mDolbyClientConnected = false;

    // Tooltip stuff?
    private ViewGroup mNativeRootContainer;

    // Splash screen stuff.
    private Dialog mSplashScreenDialog;
    private final int mSplashScreenDelayTime = 3000;
    private boolean mSplashTimerElapsed = false;
    private boolean mSplashClientBound = false;
    private Runnable mSplashScreenDelay = null;

    // ????
    private static long mOnDestroyTimer;

    // Flag indicating whether modifier part of DS API is to be used on UI
    // event. Setting it to false prevents from looping DS API calls.
    private boolean mUseDsApiOnUiEvent = true;

    // Application configuration.
    private com.dolby.daxappCoreUI.Configuration configuration;

    // In Store demo stuff.
    // Menu ID for in store demo launch
    private static final int INSTORE_MENU_ID = 1001;
    public static final String ACTION_LAUNCH_DAX_INSTOREDEMO_APP = "com.dolby.LAUNCH_DAX_INSTOREDEMO_APP";

    // Controls whether the visualizer has been registered with the DsGlobalEx or
    // not.
    private boolean mVisualizerRegistered = false;

    // Fragments references, since we add them dynamically.
    private FragProfilePresetEditor mFPPE = null;
    private FragProfilePresets mFPP = null;
    private FragSwitches mFS = null;
    private FragEqualizerPresets mFEP = null;
    // For mobile-landscape mode only.
    private LinearLayout mLinearLayout;
    private ScrollView mScrollview;
    private final int DYNAMIC_LINEAR_LAYOUT_ID = 8; // TOTALLY RANDOM NUMBER.

    private boolean mMobileLayout = false;
    static private boolean mEditProfile = false;

    private int mOriginX;
    private int mOriginY;

    private boolean mIsScreenOn = false;
    private boolean mIsActivityRunning = false;

    private boolean mIsMonoSpeaker = false;

    private boolean mExploreDolbyAtmosClicked = false;

    private Runnable mShowMainUi = null;
    private boolean mOnPauseFlag = false;

    public boolean isMonoSpeaker() {
        return mIsMonoSpeaker;
    }

    private final BroadcastReceiver mScreenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            FragGraphicVisualizer gv = (FragGraphicVisualizer) getFragmentManager().findFragmentById(R.id.fraggraphicvisualizer);
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                Log.d(Tag.MAIN, "ACTION_SCREEN_OFF");
                mIsScreenOn = false;
                registerVisualizer(mIsScreenOn);
                if (gv != null) {
                    gv.setEnabled(mIsScreenOn);
                }
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                Log.d(Tag.MAIN, "ACTION_SCREEN_ON");
                mIsScreenOn = true;
                if ((null != mDsClient) && mDolbyClientConnected) {
                    final boolean dsOn = (DsConstants.DS_STATE_ON == mDsClient.getState());
                    if (gv != null) {
                        gv.setEnabled(dsOn);
                    }
                    registerVisualizer(dsOn);
                }
            }
        }
    };

    // //////////////////////////////////////////////////////////////////////////////////

    // Called when the activity is first created.
    // Step 1.
    @Override
    public void onCreate(Bundle savedInstanceState) {

        // Debug statement.
        ((DAXApplication) getApplication()).printScreenSpecs();
        // Loading appropriate Status Bar Height.
        Constants.STATUS_BAR_HEIGHT = getResources().getInteger(R.integer.statusbar_height);
        // Calling base class onCreate.
        super.onCreate(savedInstanceState);
        changeScale();
        // Loading type font.
        Assets.init(this);
        // Requesting window feature: no title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Registering for screen events.
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mScreenReceiver, filter);

        // Checking current screen state.
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mIsScreenOn = pm.isScreenOn();

        mShowMainUi = new Runnable() {
            @Override
            public void run() {
                doInitMainUI();
            }
        };

        // If splash screen is to be displayed now, then postpone
        // initialization of main UI to next main thread loop
        // iteration. Otherwise main UI is visible before splash screen
        // for a while.
        if (displaySplashScreen()) {
            DAXApplication.HANDLER.postDelayed(mShowMainUi, mSplashScreenDelayTime);
        } else {
            mShowMainUi.run();
        }
    }

    public void changeScale() {
        Configuration sys = getBaseContext().getResources().getConfiguration();
        if (sys.smallestScreenWidthDp >= 600) {
            Configuration conf = new Configuration();
            conf.fontScale = sys.smallestScreenWidthDp / 800.0f;

            getBaseContext().getResources().updateConfiguration(conf, null);
            return;
        }
        if (sys.smallestScreenWidthDp >= 360) {
            Configuration conf = new Configuration();
            conf.fontScale = sys.smallestScreenWidthDp / 360.0f;

            getBaseContext().getResources().updateConfiguration(conf, null);
        }
    }

    // Step 2, typically.
    private boolean displaySplashScreen() {
        boolean isOrientationChange = mOnDestroyTimer > 0 && (mOnDestroyTimer + 500) > SystemClock.elapsedRealtime();

        if (!isOrientationChange) {
            mSplashScreenDialog = new Dialog(this, R.layout.splash_screen);
            mSplashScreenDialog.setContentView(R.layout.splash_screen);
            mSplashScreenDialog.setCancelable(false);
            mSplashScreenDialog.show();
            mSplashScreenDelay = new Runnable() {
                @Override
                public void run() {
                    mSplashTimerElapsed = true;
                    DAXApplication.HANDLER.removeCallbacks(this);
                    hideSplashScreen();
                }
            };
            DAXApplication.HANDLER.postDelayed(mSplashScreenDelay, mSplashScreenDelayTime);
            return true;
        }
        return false;
    }

    // Step 3, typically.
    private void hideSplashScreen() {
        if (mSplashTimerElapsed && mSplashClientBound) {
            // Second possible way of fixing below:
            // http://bend-ing.blogspot.mx/2008/11/properly-handle-progress-dialog-in.html
            // Might not be required due to our application.
            try {
                mSplashScreenDialog.dismiss();
            } catch (Exception e) {
                // NOTHING.
            }
            mSplashScreenDialog = null;
            mSplashScreenDelay = null;
        }
    }

    /**
     * This method contains what usually is called inside onCreate(...).
     * Invocation of this is postponed on purpose to measure actual screen
     * resolution.
     */
    // Step 4.
    private void doInitMainUI() {

        Configuration sys = getBaseContext().getResources().getConfiguration();
//        if (sys.smallestScreenWidthDp >= 600) {
//            //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
//        }

        try {
            setContentView(R.layout.main);
        } catch (Exception e) {
            // NOTHING.
            return;
        }

        // Setting onClick listener for Dolby Digital logo
        // (drawable-large/dslogo.png).
        mDSLogo = (ImageView) findViewById(R.id.dsLogo);
        mDSLogo.setOnClickListener(this);
        mDSLogo.setSoundEffectsEnabled(false);

        // Related to tooltip display.
        mNativeRootContainer = ViewTools.determineNativeViewContainer(this);

        // Binding to remote service.
        Log.d(Tag.MAIN, "doInitMainUI - mDsClient.bindDsService");
        if (null != mDsClient) {
            mDsClient.registerClient(this, this);
        }

        if (configuration == null) {
            configuration = com.dolby.daxappCoreUI.Configuration.getInstance(getApplicationContext());
            Log.i(Tag.MAIN, "doInitMainUI - NEW CONFIG:" + configuration.getMaxEditGain_DS2() + " : " + configuration.getMinEditGain_DS2());
        }

        // Finding out if using mobile layout.
        mMobileLayout = getResources().getBoolean(R.bool.newLayout);

        if (mMobileLayout == true) {
            // Allocating for the moment only this fragment.
            // The rest will be allocated when required.
            mFPP = new FragProfilePresets();
            // Plugging in the FragProfilePresets fragment.
            // We have to. Otherwise, won't work.
            getFragmentManager().beginTransaction().add(R.id.fragmentcontainer, mFPP).commit();
        }
        if (mEditProfile) {
            this.editProfile();
        }
    }

    private void displayTooltip(View pointToView, CharSequence title,
            CharSequence text) {
        if (mNativeRootContainer == null || pointToView == null) {
            return;
        }
        ViewTools.showTooltip(MainActivity.this, mNativeRootContainer, pointToView, title, text);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //if the splash don't disappear, and pause the app
        if(mOnPauseFlag == true && mSplashScreenDelay != null) {
            mOnPauseFlag = false;
            mSplashClientBound = true;
            //close the last splash dialog
            try {
                mSplashScreenDialog.dismiss();
            } catch (Exception e) {
                // NOTHING.
            }
            mSplashScreenDialog = null;
            mSplashScreenDelay = null;
            //reopen the splash dialog
            if (displaySplashScreen()) {
                DAXApplication.HANDLER.postDelayed(mShowMainUi, mSplashScreenDelayTime);
            } else {
                mShowMainUi.run();
            }
        }
        updateProfileNames();

        mIsActivityRunning = true;
        onDsClientUseChanged(true);
    }

    public com.dolby.daxappCoreUI.Configuration getConfiguration() {
        if (configuration == null) {
            configuration = com.dolby.daxappCoreUI.Configuration.getInstance(this.getApplicationContext());
            Log.i(Tag.MAIN, "getConfiguration - NEW CONFIG:" + configuration.getMaxEditGain_DS2() + " : " + configuration.getMinEditGain_DS2());
        }
        return configuration;
    }

    @Override
    protected void onPause() {
        if(mSplashScreenDelay != null) {
            DAXApplication.HANDLER.removeCallbacks(mSplashScreenDelay);
            DAXApplication.HANDLER.removeCallbacks(mShowMainUi);
        }
        mOnPauseFlag = true;
        onDsClientUseChanged(false);
        mIsActivityRunning = false;
        if (null != mDsClient) {
            mDsClient.abandonAccessRight();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mOnDestroyTimer = SystemClock.elapsedRealtime();
        if (mSplashScreenDelay != null) {
            DAXApplication.HANDLER.removeCallbacks(mSplashScreenDelay);
            hideSplashScreen();
        }
        unbindFromDsApi();
        configuration = null;
        unregisterReceiver(mScreenReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        AssetFileDescriptor demoAfd = getResources().openRawResourceFd(R.raw.instore_demo_media);
        AssetFileDescriptor loopAfd = getResources().openRawResourceFd(R.raw.instore_demo_loop);
        if ((demoAfd != null) && (demoAfd.getLength() > 0) && (loopAfd != null) && (loopAfd.getLength() > 0)) {
            menu.add(0, INSTORE_MENU_ID, 0, R.string.instore_menu_text);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == INSTORE_MENU_ID) {
            startActivity(new Intent(ACTION_LAUNCH_DAX_INSTOREDEMO_APP));
        }
        return super.onOptionsItemSelected(item);
    }

    private void unbindFromDsApi() {
//    	Log.d(Tag.MAIN, "unbindFromDsApi");
        if (mDolbyClientConnected) {
            Log.d(Tag.MAIN, "MainActivity.unBindDsService");
            mDolbyClientConnected = false;
            if (null != mDsClient) {
                mDsClient.unregisterDsEvents();
                mDsClient.abandonAccessRight();
                mDsClient.unregisterClient();
            }
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        // turn off the dithering and use full 24-bit+alpha graphics
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    }

    @Override
    public void onDsClientUseChanged(final boolean on) {
        FragExploreDolbyAtmos exploreFrag = (FragExploreDolbyAtmos)getFragmentManager().findFragmentById(R.id.fragexploredolbyatmos);
        if(exploreFrag != null) {
            exploreFrag.setInitStatus();
        }
        if (on) {
            if (mDolbyClientConnected) {
                FragGraphicVisualizer fgv = (FragGraphicVisualizer) getFragmentManager().findFragmentById(R.id.fraggraphicvisualizer);
                if (fgv != null) {
                    fgv.updateGraphicEqInUI();
                }

                // Shadowing behind FragGraphicVisualizer calls. Only in mobile
                // layout. This fragment shouldn't be used in the tablet for the
                // moment.
                // The tablet should only use FragGraphicVisualizer.
                // The reason for this is: FragGraphicVisualizer contains
                // FragEqualizerPresets in it. That functionality shall be
                // removed and layouts supplied so that FragEqualizerPresets can
                // be used too in the tablet.
                if (mMobileLayout == true && mFEP != null) {
                    mFEP.updateGraphicEqInUI();
                }

                // ELSE!
                if (null != mDsClient) {
                    final boolean dsOn = (DsConstants.DS_STATE_ON == mDsClient.getState());
                    internalOnDsOn(dsOn);
                }
            }
        } else {
            if (mDolbyClientConnected) {
                registerVisualizer(false);
            }
        }
    }

    @Override
    public void chooseProfile(int profile) {
        if ((null != mDsClient) && mDolbyClientConnected) {
            final int profileId = mDsClient.getProfile();
            if (profileId != profile) {
                if (mDsClient.checkAccessRight() != DsAccess.THIS_APP_GRANTED) {
                    if (DsConstants.DS_NO_ERROR != mDsClient.requestAccessRight()) {
                        return;
                    }
                }

                try {
                    mDsClient.setProfile(profile);
                } catch (DsAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return;
                }
            }
        }

        FragProfilePresets pp;
        if (mMobileLayout == true) {
            pp = mFPP;
        } else {
            pp = (FragProfilePresets) getFragmentManager().findFragmentById(R.id.fragprofilepresets);
        }

        if(mExploreDolbyAtmosClicked == true) {
            profile = Constants.EXPLORE_DOLBY_ATMOS - 1;
        }
        if (pp != null) {
            String profileName = pp.getItemName(profile);
            pp.setSelection(profile);

            FragProfilePresetEditor pe;
            if (mMobileLayout == true) {
                pe = mFPPE;
            } else {
                pe = (FragProfilePresetEditor) getFragmentManager().findFragmentById(R.id.fragprofileeditor);
            }
            if (pe != null) {
                // Telling to cancel any text edition.
                // Has to be done here to avoid a stack overflow :S if called
                // inside pe.onProfileNameChangedor just before. STRANGE.
                pe.cancelPendingEdition();

            }

            onProfileNameChanged(profile, profileName);
        }

        mUseDsApiOnUiEvent = false;
        onProfileSettingsChanged();
        mUseDsApiOnUiEvent = true;
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

    @Override
    public void onClick(View view) {
        final int id = view.getId();

        if (R.id.dsLogo == id) {
            FragGraphicVisualizer fgv = (FragGraphicVisualizer) getFragmentManager().findFragmentById(R.id.fraggraphicvisualizer);
            if (fgv != null) {
                fgv.hideEqualizer();
            }
        }
    }

    public void powerOnOff(boolean on) {

        FragPower pwv = (FragPower) getFragmentManager().findFragmentById(R.id.fragpower);
        if (pwv != null) {
            pwv.setEnabled(on);
        }

        mDSLogo.setImageResource(on ? R.drawable.dslogo : R.drawable.dslogodis);

        FragProfilePresets pp;
        if (mMobileLayout == true) {
            if (this.mFEP == null && this.mFPPE == null && this.mFS == null) {
                pp = mFPP;
            } else {
                pp = null;
            }
        } else {
            pp = (FragProfilePresets) getFragmentManager().findFragmentById(R.id.fragprofilepresets);
        }
        if (pp != null) {
            pp.setEnabled(on);
        }

        FragProfilePresetEditor pe;
        if (mMobileLayout == true) {
            pe = mFPPE;
        } else {
            pe = (FragProfilePresetEditor) getFragmentManager().findFragmentById(R.id.fragprofileeditor);
        }
        if (pe != null) {
            pe.setEnabled(on);
        }

        FragSwitches swv;
        if (mMobileLayout == true) {
            swv = mFS;
        } else {
            swv = (FragSwitches) getFragmentManager().findFragmentById(R.id.fragswitches);
        }
        if (swv != null) {
            swv.setEnabled(on);
        }

        if (mIsScreenOn) {
            FragGraphicVisualizer gv = (FragGraphicVisualizer) getFragmentManager().findFragmentById(R.id.fraggraphicvisualizer);
            if (gv != null) {
                gv.setEnabled(on);
            }
        }

        FragExploreDolbyAtmos exploreFrag = (FragExploreDolbyAtmos)getFragmentManager().findFragmentById(R.id.fragexploredolbyatmos);
        if(exploreFrag != null) {
            exploreFrag.setInitStatus();
        }

        // Shadowing behind FragGraphicVisualizer calls. Only in mobile
        // layout. This fragment shouldn't be used in the tablet for the
        // moment.
        // The tablet should only use FragGraphicVisualizer.
        // The reason for this is: FragGraphicVisualizer contains
        // FragEqualizerPresets in it. That functionality shall be
        // removed and layouts supplied so that FragEqualizerPresets can
        // be used too in the tablet.
        if (mMobileLayout == true && mFEP != null) {
            mFEP.setEnabled(on);
        }
    }

    private void internalOnDsOn(boolean on) {
        powerOnOff(on);
        if (on && (null != mDsClient) && mDolbyClientConnected) {
            final int profile = mDsClient.getProfile();
            chooseProfile(profile);
            registerVisualizer(true);
        } else {
            registerVisualizer(false);
        }

        FragProfilePresets pp;
        if (mMobileLayout == true) {
            pp = mFPP;
        } else {
            pp = (FragProfilePresets) getFragmentManager().findFragmentById(R.id.fragprofilepresets);
        }
        if (pp != null) {
            pp.scheduleNotifyDataSetChanged();
        }
    }

    private void registerVisualizer(boolean on) {
        if (!mDolbyClientConnected || mVisualizerRegistered == on) {
            return;
        }

        if (mIsScreenOn == true && mIsActivityRunning == true) {

            mVisualizerRegistered = on;

            FragGraphicVisualizer gv = (FragGraphicVisualizer) getFragmentManager().findFragmentById(R.id.fraggraphicvisualizer);
            if (gv != null) {
                gv.registerVisualizer(on);
            }
        }
    }

    @Override
    public DsGlobalEx getDsClient() {
        return mDsClient;
    }

    @Override
    public void setUserProfilePopulated() {
    }

    @Override
    public void displayTooltip(View pointToView, int idTitle, int idText) {
        displayTooltip(pointToView, getString(idTitle), getString(idText));
    }

    @Override
    public void onDsApiError() {
        unbindFromDsApi();
        finish();
    }

    // IDsEvents
    @Override
    public void onDsOn(boolean on) {
        mUseDsApiOnUiEvent = false;
        internalOnDsOn(on);
        mUseDsApiOnUiEvent = true;
    }

    @Override
    public void onProfileSelected(int profile) {
        if ((null != mDsClient) && mDolbyClientConnected) {
            final boolean dsOn = (DsConstants.DS_STATE_ON == mDsClient.getState());
            internalOnDsOn(dsOn);
        }
    }

    @Override
    public void onProfileSettingsChanged(int profile) {
        Log.e(Tag.MAIN, "onProfileSettingsChanged(), profile = " + profile);
        mUseDsApiOnUiEvent = false;
        onProfileSettingsChanged();
        mUseDsApiOnUiEvent = true;
    }

    @Override
    public void onDsSuspended(boolean isSuspended) {
        onDsOn(!isSuspended);
    }
    // IDsEvents END

    // IDsProfileNameEvents
    @Override
    public void onProfileNameChanged(int profile, String name) {
        FragProfilePresets pp;
        if (mMobileLayout) {
            pp = mFPP;
        } else {
            pp = (FragProfilePresets) getFragmentManager().findFragmentById(R.id.fragprofilepresets);
        }
        if (pp != null) {
            pp.onProfileNameChanged(profile, name);
        }

        FragProfilePresetEditor pe;
        if (mMobileLayout) {
            pe = mFPPE;
        } else {
            pe = (FragProfilePresetEditor) getFragmentManager().findFragmentById(R.id.fragprofileeditor);
        }
        if (pe != null) {
            pe.onProfileNameChanged(profile, name);
        }
    }
    // IDsProfileNameEvents END

    // IDsFragEqualizerPresetsObserver / IDsFragGraphicVisualizerObserver
    @Override
    public void onProfileSettingsChanged() {
        Log.d(Tag.MAIN, "onProfileSettingsChanged()");

        FragProfilePresets pp;
        if (mMobileLayout == true) {
            pp = mFPP;
        } else {
            pp = (FragProfilePresets) getFragmentManager().findFragmentById(
                    R.id.fragprofilepresets);
        }
        if (pp != null) {
            pp.scheduleNotifyDataSetChanged();
        }

        FragProfilePresetEditor pe;
        if (mMobileLayout == true) {
            pe = mFPPE;
        } else {
            pe = (FragProfilePresetEditor) getFragmentManager()
                    .findFragmentById(R.id.fragprofileeditor);
        }
        if (pe != null) {
            pe.setResetProfileVisibility();
        }

        // Set up the toggle buttons.
        FragSwitches swv;
        if (mMobileLayout == true) {
            swv = mFS;
        } else {
            swv = (FragSwitches) getFragmentManager().findFragmentById(
                    R.id.fragswitches);
        }
        if (swv != null) {
            swv.onProfileSettingsChanged();
        }

        FragGraphicVisualizer gv = (FragGraphicVisualizer) getFragmentManager()
                .findFragmentById(R.id.fraggraphicvisualizer);
        if (gv != null) {
            // Show or hide the equalizer Reset Button.
            gv.setResetEqButtonVisibility();
        }

        final int iEqPreset = DsClientSettings.INSTANCE.getIeqPreset(this, mDsClient);
        if (-1 == iEqPreset) {
            return;
        }

        if (gv != null) {
            gv.selectIEqPresetInUI(iEqPreset - 1);
        }

        // Shadowing behind FragGraphicVisualizer calls. Only in mobile
        // layout. This fragment shouldn't be used in the tablet for the
        // moment.
        // The tablet should only use FragGraphicVisualizer.
        // The reason for this is: FragGraphicVisualizer contains
        // FragEqualizerPresets in it. That functionality shall be
        // removed and layouts supplied so that FragEqualizerPresets can
        // be used too in the tablet.
        if (mMobileLayout == true && mFEP != null) {
            mFEP.setResetEqButtonVisibility();
            mFEP.selectIEqPresetInUI(iEqPreset - 1);
        }
    }
    // IDsFragEqualizerPresetsObserver / IDsFragGraphicVisualizerObserver END

    // From IDsFragGraphicVisualizerObserver.
    @Override
    public void exitActivity() {
        Log.d(Tag.MAIN, "exitActivity()");
        if (mDolbyClientConnected) {
            mDolbyClientConnected = false;
            onDsClientUseChanged(false);
            if (null != mDsClient) {
                mDsClient.unregisterDsEvents();
                mDsClient.abandonAccessRight();
                mDsClient.unregisterClient();
            }
        }
        finish();
    }

    // From IDsFragObserver.
    @Override
    public boolean isDolbyClientConnected() {
        return mDolbyClientConnected;
    }

    @Override
    public boolean useDsApiOnUiEvent() {
        return mUseDsApiOnUiEvent;
    }

    @Override
    public void profileReset(int profile) {
        FragGraphicVisualizer fgv = (FragGraphicVisualizer) getFragmentManager().findFragmentById(R.id.fraggraphicvisualizer);
        if (fgv != null) {
            fgv.resetUserGains(profile);
        }

        chooseProfile(profile);
    }

    @Override
    public void chooseExploreAtmosProfile() {
        mExploreDolbyAtmosClicked = true;
        //setting the backgroudImage
        FragProfilePresets pp = (FragProfilePresets) getFragmentManager().findFragmentById(R.id.fragprofilepresets);
        String profileName = pp.getItemName(Constants.EXPLORE_DOLBY_ATMOS - 1);
        pp.setSelection(Constants.EXPLORE_DOLBY_ATMOS - 1);
        onProfileNameChanged(Constants.EXPLORE_DOLBY_ATMOS, profileName);

        //hide the profileeditor
        RelativeLayout rightviewchild =(RelativeLayout)findViewById(R.id.rightviewchild);
        rightviewchild.setVisibility(View.GONE);
        RelativeLayout newRightViewChild =(RelativeLayout)findViewById(R.id.newrightviewchild);
        if(newRightViewChild == null) {
            newRightViewChild = (RelativeLayout)this.getLayoutInflater().inflate(R.layout.explore_dolby_atmos,null);
            RelativeLayout rightview =(RelativeLayout)findViewById(R.id.rightview);
            rightview.addView(newRightViewChild);
//	        //set the btn_scroll postion
//	        int width = rightview.getRight() - rightview.getLeft();
//	        int height = rightview.getBottom() - rightview.getTop();
//
//	        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//	        lp.setMargins(width / 2, -height / 15 * 11, width / 2, height / 15 * 4);
//
//	        ImageView btncontrolImg  = (ImageView) findViewById(R.id.btncontrolImg);
//	        btncontrolImg.setLayoutParams(lp);
        } else {
            FragmentManager fm = getFragmentManager();
            FragExploreDolbyAtmos exploreFrag = (FragExploreDolbyAtmos)fm.findFragmentById(R.id.fragexploredolbyatmos);
            exploreFrag.setInitStatus();
            newRightViewChild.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public void hideExploreAtomsProfile() {
        mExploreDolbyAtmosClicked = false;
        RelativeLayout rightview =(RelativeLayout)findViewById(R.id.rightview);

        RelativeLayout newRightViewChild =(RelativeLayout)findViewById(R.id.newrightviewchild);
        if(newRightViewChild != null) {
            //newRightViewChild.setVisibility(View.GONE);
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Fragment exploreFrag = fm.findFragmentById(R.id.fragexploredolbyatmos);
            ft.remove(exploreFrag);
            ft.commit();
            rightview.removeView(newRightViewChild);
        }
//        RelativeLayout rightviewchild =(RelativeLayout)findViewById(R.id.rightviewchild);
//        rightviewchild.setVisibility(View.VISIBLE);
    }

    // From IDsFragGraphicVisualizerObserver.
    @Override
    public void onEqualizerEditStart() {

        // Have to call this here so that the shadow fragment
        // can reset properly the EQ reset button.
        if (mMobileLayout == true && mFEP != null) {
            mFEP.setResetEqButtonVisibility();
        }

        setUserProfilePopulated();

        FragProfilePresets pp;
        if (mMobileLayout == true) {
            pp = mFPP;
        } else {
            pp = (FragProfilePresets) getFragmentManager().findFragmentById(R.id.fragprofilepresets);
        }
        if (pp != null) {
            pp.scheduleNotifyDataSetChanged();
        }

        FragProfilePresetEditor pe;
        if (mMobileLayout == true) {
            pe = mFPPE;
        } else {
            pe = (FragProfilePresetEditor) getFragmentManager().findFragmentById(R.id.fragprofileeditor);
        }
        if (pe != null) {
            pe.setResetProfileVisibility();
        }
    }

    // From IDsFragPresetEditorObserver.
    // Method to catch "event" from profile adapter.
    @Override
    public void onProfileNameEditStarted() {

        FragProfilePresets pp = (FragProfilePresets) getFragmentManager().findFragmentById(R.id.fragprofilepresets);
        if (pp != null) {
            pp.onProfileNameEditStarted();
        }
    }

    // From IDsFragPresetEditorObserver.
    // Method to catch "event" from profile adapter.
    @Override
    public void onProfileNameEditEnded() {
    }

    // From IDsFragPresetEditorObserver.
    @Override
    public int getProfileSelected() {
        FragProfilePresets pp;
        if (mMobileLayout == true) {
            pp = mFPP;
        } else {
            pp = (FragProfilePresets) getFragmentManager().findFragmentById(R.id.fragprofilepresets);
        }
        if (pp != null) {
            return pp.getSelection();
        }
        return -1;
    }

    @Override
    public void editProfile() {

        if (mMobileLayout && (null != mDsClient) && mDolbyClientConnected) {
            // Only available when on. When off, this shouldn't be possible.
            if (DsConstants.DS_STATE_ON != mDsClient.getState()) {
                return;
            }

            // Profile edition is requested.
            // Switching fragments only if in mobile layout and fragments are
            // null.
            if (mFS == null && mFPPE == null && mFEP == null) {
                mEditProfile = true;
                FragGraphicVisualizer gv = (FragGraphicVisualizer) getFragmentManager().findFragmentById(R.id.fraggraphicvisualizer);
                if (gv != null) {
                    gv.setEnableEditGraphic(true);
                }
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                fragmentTransaction.remove(mFPP);
                fragmentTransaction.commit();
                //Add the line for DSNPA-25.
                fragmentManager.executePendingTransactions();

                mFPPE = new FragProfilePresetEditor();
                mFS = new FragSwitches();
                mFEP = new FragEqualizerPresets();

                int fragmentContainerId = R.id.fragmentcontainer;

                if (Tools.isLandscapeScreenOrientation(this) == true) {

                    mLinearLayout = new LinearLayout(this);
                    mLinearLayout.setId(DYNAMIC_LINEAR_LAYOUT_ID);
                    mLinearLayout.setOrientation(LinearLayout.VERTICAL);
                    mScrollview = new ScrollView(this);
                    mScrollview.setId(R.id.thescrollview);

                    mScrollview.addView(mLinearLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                    ((LinearLayout) findViewById(R.id.fragmentcontainer)).addView(mScrollview, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                    fragmentContainerId = DYNAMIC_LINEAR_LAYOUT_ID;
                }

                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTransaction.add(R.id.preseteditorcontainer, mFPPE);
                fragmentTransaction.add(fragmentContainerId, mFS);
                fragmentTransaction.add(fragmentContainerId, mFEP);
                fragmentTransaction.commit();
                //Add the line for DSNPA-25.
                fragmentManager.executePendingTransactions();
                ScrollView theView = (ScrollView) findViewById(R.id.thescrollview);
                if (theView != null) {

                    // Only doing this in portrait mode, where the
                    // scrollview remains alive always.
                    if (Tools.isLandscapeScreenOrientation(this) == false) {
                        mOriginX = theView.getScrollX();
                        mOriginY = theView.getScrollY();
                    }

                    theView.smoothScrollTo(0, 0);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {

        if (mMobileLayout == true && mFS != null && mFPPE != null && mFEP != null) {
            mEditProfile = false;
            FragGraphicVisualizer gv = (FragGraphicVisualizer) getFragmentManager().findFragmentById(R.id.fraggraphicvisualizer);
            if (gv != null) {
                gv.hideEqualizer();
                gv.setEnableEditGraphic(false);
            }
            // Doing this only if the right fragments are loaded while
            // using mobile layout.

            // Unloading "edition fragments" and restoring profile
            // selector fragment.
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            fragmentTransaction.remove(mFEP);
            fragmentTransaction.remove(mFPPE);
            fragmentTransaction.remove(mFS);
            fragmentTransaction.commit();
            //Add the line for DSNPA-25.
            fragmentManager.executePendingTransactions();

            // Letting the garbage collector get to them.
            mFPPE = null;
            mFS = null;
            mFEP = null;

            if (Tools.isLandscapeScreenOrientation(this) == true) {

                ((LinearLayout) findViewById(R.id.fragmentcontainer)).removeView(mScrollview);
                mLinearLayout = null;
                mScrollview = null;
            } else {
                ScrollView theView = (ScrollView) findViewById(R.id.thescrollview);
                // Restoring previous state.
                if (theView != null) {

                    theView.post(new Runnable() {
                        public void run() {
                            ScrollView theView = (ScrollView) findViewById(R.id.thescrollview);
                            if (theView != null) {
                                theView.scrollTo(mOriginX, mOriginY);
                            }
                        }
                    });
                }
            }

            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.add(R.id.fragmentcontainer, mFPP);
            fragmentTransaction.commit();
            // DS1IS-69: Consumer UI: the app become to black after choose profile several times.
            fragmentManager.executePendingTransactions();

        } else {
            // Calling base class functionality.
            super.onBackPressed();
        }
        return;
    }

    @Override
    public void profileEditorIsAlive() {

        if (mMobileLayout && (null != mDsClient) && mDolbyClientConnected) {
            // Only available when on. When off, this shouldn't be possible.
            final boolean dsOn = (DsConstants.DS_STATE_ON == mDsClient.getState());

            if (dsOn) {
                // Propagating connected status.
                mFPPE.onClientConnected();
                // Enabling fragment.
                mFPPE.setEnabled(true);

                // Extracting selected profile index.
                final int profile = mDsClient.getProfile();
                // Setting selected profile name in Profile Editor.
                mFPPE.onProfileNameChanged(profile, mFPP.getItemName(profile));
            }
        }
    }

    @Override
    public void switchesAreAlive() {
        if (mMobileLayout && (null != mDsClient) && mDolbyClientConnected) {
            // Only available when on. When off, this shouldn't be possible.
            final boolean dsOn = (DsConstants.DS_STATE_ON == mDsClient.getState());

            if (dsOn) {
                mFS.setEnabled(true);

                // Set up the toggle buttons.
                FragSwitches swv;
                if (mMobileLayout == true) {
                    swv = mFS;
                } else {
                    swv = (FragSwitches) getFragmentManager().findFragmentById(R.id.fragswitches);
                }
                if (swv != null) {
                    swv.onProfileSettingsChanged();
                }
            }
        }
    }

    @Override
    public void equalizerPresetsAreAlive() {

        if (mMobileLayout && (null != mDsClient) && mDolbyClientConnected) {
            // Only available when on. When off, this shouldn't be possible.
            final boolean dsOn = (DsConstants.DS_STATE_ON == mDsClient.getState());

            if (dsOn) {
                // Propagating connected status.
                mFEP.onClientConnected();
                // Telling it to update visually speaking.
                mFEP.updateGraphicEqInUI();
                // Enabling fragment.
                mFEP.setEnabled(true);

                // Calling chooseProfile to handle everything else as part of
                // the
                // initialization. Should we initialize individually?
                // Might be more efficient.
                final int profile = mDsClient.getProfile();
                chooseProfile(profile);
            }
        }
    }

    @Override
    public void profilePresetsAreAlive() {

        if (mMobileLayout && (null != mDsClient) && mDolbyClientConnected) {
            // Only available when on. When off, this shouldn't be possible.
            final boolean dsOn = (DsConstants.DS_STATE_ON == mDsClient.getState());
            this.mFPP.setEnabled(dsOn);

            if (dsOn) {
                // Required to highlight the profile currently active. :S
                // This even though the object itself is never destroyed.
                // Interestingly, the state is not saved.
                final int profile = mDsClient.getProfile();
                ListView lv = (ListView) this.findViewById(R.id.presetsListView);
                if (lv != null) {
                    lv.setSelection(profile);
                }
                chooseProfile(profile);

                ScrollView theView = (ScrollView) findViewById(R.id.thescrollview);
                if (theView != null) {
                    theView.post(new Runnable() {
                        public void run() {
                            ScrollView theView = (ScrollView) findViewById(R.id.thescrollview);
                            if (theView != null) {
                                theView.scrollTo(0, profile * theView.getHeight() / 6);
                            }
                        }
                    });

                }
            }
        }
    }

    @Override
    public void resetEqUserGains() {
        FragGraphicVisualizer fgv = (FragGraphicVisualizer) getFragmentManager().findFragmentById(R.id.fraggraphicvisualizer);
        if (fgv != null) {
            fgv.resetUserGains();
        }
    }

    // IDsAccessEvents
    @Override
    public void onAccessAvailable() {
        // TODO Auto-generated method stub
        Log.d(Tag.MAIN, "onAccessAvailable() ------------------");

        if ((null != mDsClient) && (DsAccess.THIS_APP_GRANTED != mDsClient.checkAccessRight())) {
            if (DsConstants.DS_NO_ERROR != mDsClient.requestAccessRight()) {
                return;
            }
        }

        // refresh application UI.
        onDsClientUseChanged(true);
    }

    @Override
    public void onAccessForceReleased(String arg0, int arg1) {
        // TODO Auto-generated method stub
        Log.d(Tag.MAIN, "onAccessForceReleased() ------------------");
    }

    @Override
    public boolean onAccessRequested(String arg0, int arg1) {
        // TODO Auto-generated method stub
        Log.d(Tag.MAIN, "onAccessRequested() ------------------");
        return false;
    }

    // @see IDsAccessEvents#onClientConnected()
    // Step 7.
    @Override
    public void onClientConnected() {
        Log.d(Tag.MAIN, "onClientConnected() ------------------");

        mDsClient.registerDsEvents(this);

        int result = mDsClient.requestAccessRight();
        if (result != DsConstants.DS_NO_ERROR)
        {
            Log.d(Tag.MAIN, "onClientConnected() request audio focuse failed, result = " + result);

            // DS2API-TODO: ......??? When and Where should application request audio focus again???

            return;
        } else {
            Log.d(Tag.MAIN, "onClientConnected() request audio focuse successfully!");
        }


        // Handling connection event locally, first.

        // Setting local variables.
        // Client has connected.
        mDolbyClientConnected = true;
        mSplashClientBound = true;

        updateProfileNames();

        try {
            mIsMonoSpeaker = mDsClient.isMonoSpeaker();
            Log.d(Tag.MAIN, "mIsMonoSpeaker = " + mIsMonoSpeaker);
        } catch (Exception e) {
            e.printStackTrace();
            onDsApiError();
            return;
        }

        // Hiding splash screen.
        hideSplashScreen();

        // Local Variables Set.
        // Letting know the fragments about the connection, so they can set up
        // internal state too.
        FragProfilePresets pp;
        if (mMobileLayout == true) {
            pp = mFPP;
        } else {
            pp = (FragProfilePresets) getFragmentManager().findFragmentById(R.id.fragprofilepresets);
        }
        if (pp != null) {
            pp.onClientConnected();
        }

        FragProfilePresetEditor pe;
        if (mMobileLayout == true) {
            pe = mFPPE;
        } else {
            pe = (FragProfilePresetEditor) getFragmentManager().findFragmentById(R.id.fragprofileeditor);
        }
        if (pe != null) {
            pe.onClientConnected();
        }

        FragGraphicVisualizer gv = (FragGraphicVisualizer) getFragmentManager().findFragmentById(R.id.fraggraphicvisualizer);
        if (gv != null) {
            gv.onClientConnected();
        }

        // Shadowing behind FragGraphicVisualizer calls. Only in mobile
        // layout. This fragment shouldn't be used in the tablet for the
        // moment.
        // The tablet should only use FragGraphicVisualizer.
        // The reason for this is: FragGraphicVisualizer contains
        // FragEqualizerPresets in it. That functionality shall be
        // removed and layouts supplied so that FragEqualizerPresets can
        // be used too in the tablet.
        if (mMobileLayout == true && mFEP != null) {
            mFEP.onClientConnected();
        }

        // Fragments informed.
        // Continuing with initialization.
        mUseDsApiOnUiEvent = false;
        onDsClientUseChanged(true);
        mUseDsApiOnUiEvent = true;
    }

    @Override
    public void onClientDisconnected() {

        // Letting know the fragments about the disconnection, so they can set
        // up internal state too.
        FragGraphicVisualizer gv = (FragGraphicVisualizer) getFragmentManager().findFragmentById(R.id.fraggraphicvisualizer);
        if (gv != null) {
            gv.onClientDisconnected();
        }

        // Shadowing behind FragGraphicVisualizer calls. Only in mobile
        // layout. This fragment shouldn't be used in the tablet for the
        // moment.
        // The tablet should only use FragGraphicVisualizer.
        // The reason for this is: FragGraphicVisualizer contains
        // FragEqualizerPresets in it. That functionality shall be
        // removed and layouts supplied so that FragEqualizerPresets can
        // be used too in the tablet.
        if (mMobileLayout == true && mFEP != null) {
            mFEP.onClientDisconnected();
        }

        FragProfilePresetEditor pe;
        if (mMobileLayout == true) {
            pe = mFPPE;
        } else {
            pe = (FragProfilePresetEditor) getFragmentManager().findFragmentById(R.id.fragprofileeditor);
        }
        if (pe != null) {
            pe.onClientDisconnected();
        }

        FragProfilePresets pp;
        if (mMobileLayout == true) {
            pp = mFPP;
        } else {
            pp = (FragProfilePresets) getFragmentManager().findFragmentById(R.id.fragprofilepresets);
        }
        if (pp != null) {
            pp.onClientDisconnected();
        }

        // Handling disconnection event locally now.
        mDolbyClientConnected = false;
        // Continuing with deinitialization.
        onDsClientUseChanged(false);
    }
    // IDsAccessEvents END

    private void updateProfileNames() {
        if ((null != mDsClient) && mDolbyClientConnected) {
            if (mDsClient.checkAccessRight() != DsAccess.THIS_APP_GRANTED) {
                if (DsConstants.DS_NO_ERROR != mDsClient.requestAccessRight()) {
                    return;
                }
            }

            try {
                DsProfileName dpn_1 = new DsProfileName();
                dpn_1.setDefaultName(getResources().getString(R.string.preset_1));
                dpn_1.setCurrentName(null);
                mDsClient.setProfileName(DsConstants.PROFLIE_CUSTOM_1, dpn_1);

                DsProfileName dpn_2 = new DsProfileName();
                dpn_2.setDefaultName(getResources().getString(R.string.preset_2));
                dpn_2.setCurrentName(null);
                mDsClient.setProfileName(DsConstants.PROFLIE_CUSTOM_2, dpn_2);
            } catch (DsAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
