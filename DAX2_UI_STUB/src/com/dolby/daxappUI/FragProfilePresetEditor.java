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
import android.app.Fragment;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.dolby.api.DsAccess;
import com.dolby.api.DsAccessException;
import com.dolby.api.DsConstants;
import com.dolby.api.DsGlobalEx;
import com.dolby.api.DsProfileName;
import com.dolby.api.IDsEvents;
import com.dolby.daxappCoreUI.Constants;
import com.dolby.daxappCoreUI.Tools;

public class FragProfilePresetEditor extends Fragment implements
        OnClickListener, OnLongClickListener, OnEditorActionListener,
        OnKeyListener, IDsEvents {

    // Data members.
    // Profile stuff.
    private ProfileEditInfo mCurrentlyEditedProfile;

    // DsClient instance.
    private DsGlobalEx mDsClient;
    // Required to know whether our local instance has connected to the service
    // or not.
    private boolean mDolbyClientConnected = false;

    // Our observers.
    // Specific Profile Editor Fragment Observer.
    private IDsFragProfileEditorObserver mSpecificObserver;
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
            mSpecificObserver = (IDsFragProfileEditorObserver) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement IDsFragProfileEditorObserver");
        }

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
        View v = inflater.inflate(R.layout.fragprofileeditor, container, false);

        // Updating labels' font, related to this fragment.
        final int textIds[] = { R.id.presetName };
        for (int id : textIds) {
            TextView tv = (TextView) v.findViewById(id);
            if (tv != null) {
                tv.setTypeface(Assets.getFont(Assets.FontType.REGULAR));
            }
        }

        // Adding Click Listeners.
        // Preset name editable only when not in full screen.
        View theV = v.findViewById(R.id.presetName);
        if (theV != null) {
            theV.setOnLongClickListener(this);
        }

        // Revert button in portrait mode, in large and xlarge.
        theV = v.findViewById(R.id.revertButtonMain);
        if (theV != null) {
            theV.setOnClickListener(this);
        }

        // Finding out if using mobile layout.
        mMobileLayout = getResources().getBoolean(R.bool.newLayout);

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
            mSpecificObserver.profileEditorIsAlive();
        }
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

    public void onProfileNameChanged(int profile, String name) {
        setResetProfileVisibility();

        // change the profile name TextView if used in the current layout
        View view = getView();
        if (view != null) {
            TextView tv = (TextView) view.findViewById(R.id.presetName);
            if (tv != null) {
                tv.setText(name);
            }
        }
    }

    // From OnLongClickListener.
    @Override
    public boolean onLongClick(View view) {
        if (view.getId() == R.id.presetName) {
            startEditingProfileName((TextView) view, (EditText) getView().findViewById(R.id.presetNameEdit), mSpecificObserver.getProfileSelected() + 1);
        }

        return true;
    }

    // From OnClickListener.
    @Override
    public void onClick(View view) {
        onDolbyClientUseClick(view);
    }

    // From OnEditorActionListener.
    @Override
    public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
        if ((view.getId() == R.id.presetNameEdit)) {
            if ((actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_PREVIOUS) && (event == null || (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER))) {
                // Accept profile name change.
                endEditingProfileName(true);
                return true;
            }
        }
        return false;
    }

    // From OnKeyListener.
    @Override
    public boolean onKey(View view, int keyCode, KeyEvent event) {
        if ((view.getId() == R.id.presetNameEdit)) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                // Cancel profile name change.
                endEditingProfileName(false);
                return true;
            }
        }
        return false;
    }

    public void setEnabled(boolean on) {
        View theFragV = getView();

        if (!on) {
            // Turning off. So finishing edition.
            endEditingProfileName(true);
        }

        setResetProfileVisibility();

        TextView tv = (TextView) theFragV.findViewById(R.id.presetName);
        if (tv != null) {
            if (!on) {
                tv.setText(R.string.off);
            }
            tv.setEnabled(on);
        }

        View v = theFragV.findViewById(R.id.revertButtonMain);
        if (v != null) {
            v.setVisibility(on ? View.VISIBLE : View.INVISIBLE);
        }
    }

    public void setResetProfileVisibility() {
        if (!mDolbyClientConnected) {
            return;
        }

        String profileName = "";
        final int profile = mDsClient.getProfile();
        switch (profile) {
        case 0:
            profileName = getString(R.string.movie);
            break;
        case 1:
            profileName = getString(R.string.music);
            break;
        case 2:
            profileName = getString(R.string.game);
            break;
        case 3:
            profileName = getString(R.string.voice);
            break;
        case 4:
            profileName = mDsClient.getProfileName(DsConstants.PROFLIE_CUSTOM_1).getCurrentName();
            break;
        case 5:
            profileName = mDsClient.getProfileName(DsConstants.PROFLIE_CUSTOM_2).getCurrentName();
            break;
        default:
            break;
        }

        TextView tv = (TextView) getView().findViewById(R.id.presetName);
        if (tv != null) {
            tv.setText(profileName);
        }

        ImageView v = (ImageView) getView().findViewById(R.id.revertButtonMain);
        if (v != null) {
            //DS1SOC-509 Both MobileUI and TabletUI all should use revert_profile as the revert icon
            /*
            if (!mMobileLayout) {
                v.setImageResource(tmpProfile < Constants.PREDEFINED_PROFILE_COUNT ? R.drawable.revert_profile : R.drawable.presetremove);

            } else {
                v.setImageResource(R.drawable.revert_profile);
            }
            */
            v.setImageResource(R.drawable.revert_profile);

            final boolean modified = mDsClient.isProfileSettingsModified(profile);
            v.setVisibility(modified ? View.VISIBLE : View.INVISIBLE);
        }
    }

    public void cancelPendingEdition() {
        endEditingProfileName(true);
    }

    private void onDolbyClientUseClick(View view) {
        if (!mDolbyClientConnected || !mFObserver.useDsApiOnUiEvent()) {
            return;
        }

        final int id = view.getId();

        if (R.id.revertButtonMain == id) { // Revert icon in the portrait
                                           // layout.
            // Doing local reset (nothing really left to do).

            // Notifying observer that the given profile was reset and that
            // the "event" should be propagated.
            final int selectedProfile = mDsClient.getProfile();
            mSpecificObserver.profileReset(selectedProfile);
        }
    }

    private void startEditingProfileName(TextView text, final EditText edit,
            int position) {
        endEditingProfileName(true);

        if ((position > Constants.PREDEFINED_PROFILE_COUNT) && (text != null) && (edit != null)) {
            edit.setFilters(new InputFilter[] { new InputFilter.LengthFilter(Constants.MAX_PROFILE_NAME_LENGTH) });
            edit.setTypeface(Assets.getFont(Assets.FontType.REGULAR));
            edit.setText(text.getText());
            text.setVisibility(View.INVISIBLE);
            edit.setVisibility(View.VISIBLE);
            edit.setOnEditorActionListener(this);
            edit.setImeOptions(EditorInfo.IME_ACTION_DONE);
            edit.setOnKeyListener(this);
            Tools.showVirtualKeyboard(getActivity());
            if (edit.isInTouchMode()) {
                edit.requestFocusFromTouch();
            } else {
                edit.requestFocus();
            }
            edit.setSelection(0, edit.getText().length());

            mCurrentlyEditedProfile = new ProfileEditInfo(position - 1, text, edit);
            mSpecificObserver.onProfileNameEditStarted();
        }
    }

    private void endEditingProfileName(boolean accept) {
        if (mCurrentlyEditedProfile != null) {
            if (accept) {
                String newName = mCurrentlyEditedProfile.mEditText.getText().toString();
                if (!newName.isEmpty()) {
                    // change the custom modify status, then save it.
                    if (mCurrentlyEditedProfile.mPosition + 1 == Constants.PROFILE_CUSTOM1_INDEX) {
                        DsProfileName dpn = new DsProfileName();
                        dpn.setDefaultName(null);
                        dpn.setCurrentName(newName);

                        if (mDsClient.checkAccessRight() != DsAccess.THIS_APP_GRANTED) {
                            if (DsConstants.DS_NO_ERROR != mDsClient.requestAccessRight()) {
                                return;
                            }
                        }

                        try {
                            mDsClient.setProfileName(DsConstants.PROFLIE_CUSTOM_1, dpn);
                        } catch (DsAccessException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } else if (mCurrentlyEditedProfile.mPosition + 1 == Constants.PROFILE_CUSTOM2_INDEX) {
                        DsProfileName dpn = new DsProfileName();
                        dpn.setDefaultName(null);
                        dpn.setCurrentName(newName);

                        if (mDsClient.checkAccessRight() != DsAccess.THIS_APP_GRANTED) {
                            if (DsConstants.DS_NO_ERROR != mDsClient.requestAccessRight()) {
                                return;
                            }
                        }

                        try {
                            mDsClient.setProfileName(DsConstants.PROFLIE_CUSTOM_2, dpn);
                        } catch (DsAccessException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                    mCurrentlyEditedProfile.mTextView.setText(newName);
                }
            }
            Tools.hideVirtualKeyboard(getActivity());
            mCurrentlyEditedProfile.mEditText.setOnEditorActionListener(null);
            mCurrentlyEditedProfile.mEditText.setOnKeyListener(null);
            mCurrentlyEditedProfile.mEditText.setVisibility(View.GONE);
            mCurrentlyEditedProfile.mTextView.setVisibility(View.VISIBLE);
            mCurrentlyEditedProfile = null;
        }
        // Add the NullPointer check for DSNPA-25.
        if (mSpecificObserver != null) {
            mSpecificObserver.onProfileNameEditEnded();
        }
        setResetProfileVisibility();
    }
}
