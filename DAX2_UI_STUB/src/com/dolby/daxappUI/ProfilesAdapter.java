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

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.dolby.api.DsAccess;
import com.dolby.api.DsAccessException;
import com.dolby.api.DsConstants;
import com.dolby.api.DsGlobalEx;
import com.dolby.api.DsProfileName;
import com.dolby.daxappCoreUI.Constants;
import com.dolby.daxappCoreUI.DAXApplication;
import com.dolby.daxappCoreUI.Tag;
import com.dolby.daxappCoreUI.Tools;

public class ProfilesAdapter extends BaseAdapter implements OnKeyListener,
        OnEditorActionListener, TextWatcher {

    private final DsGlobalEx mDsClient;
    private final Profile mProfiles[];
    private final int mLayout;
    private int mSelectedPosition = -1;
    private final String mDefaultProfileNames[];
    private View.OnClickListener mOnClickListener;

    private int mCurrentlyEditedProfile = -1;

    private String mCurrentlyEditName;

    private boolean mNewLayout = false;
    private boolean mEditable = false;

    private final MainActivity mActivity;

    public ProfilesAdapter(MainActivity context, int layout, DsGlobalEx dsClient,
            View.OnClickListener listener) {
        this.mActivity = context;
        mLayout = layout;
        this.mDsClient = dsClient;
        mOnClickListener = listener;
        mNewLayout = context.getResources().getBoolean(R.bool.newLayout);

        mDefaultProfileNames = new String[8];
        mDefaultProfileNames[0] = context.getString(R.string.instore_menu_text);
        mDefaultProfileNames[1] = context.getString(R.string.movie);
        mDefaultProfileNames[2] = context.getString(R.string.music);
        mDefaultProfileNames[3] = context.getString(R.string.game);
        mDefaultProfileNames[4] = context.getString(R.string.voice);
        mDefaultProfileNames[5] = context.getString(R.string.preset_1);
        mDefaultProfileNames[6] = context.getString(R.string.preset_2);
        mDefaultProfileNames[7] = context.getString(R.string.explore_dolby_atmos);
        // create default set of presets
        mProfiles = new Profile[8];
        mProfiles[0] = new Profile(R.drawable.profileblank, R.drawable.profileblank, R.drawable.profileblank);
        mProfiles[1] = new Profile(R.drawable.movieon, R.drawable.movieoff, R.drawable.moviedis);
        mProfiles[2] = new Profile(R.drawable.musicon, R.drawable.musicoff, R.drawable.musicdis);
        mProfiles[3] = new Profile(R.drawable.gameon, R.drawable.gameoff, R.drawable.gamedis);
        mProfiles[4] = new Profile(R.drawable.voiceon, R.drawable.voiceoff, R.drawable.voicedis);
        mProfiles[5] = new Profile(R.drawable.preset1on, R.drawable.preset1off, R.drawable.preset1dis);
        mProfiles[6] = new Profile(R.drawable.preset2on, R.drawable.preset2off, R.drawable.preset2dis);
        mProfiles[7] = new Profile(R.drawable.profileblank, R.drawable.profileblank, R.drawable.profileblank);
    }

    public int getCount() {
        return mProfiles.length;
    }

    public Profile getItem(int position) {
        return mProfiles[position];
    }

    public long getItemId(int position) {
        return position;
    }

    public String getItemName(int position) {
        if (position <= Constants.PREDEFINED_PROFILE_COUNT || position == 7) {
            return mDefaultProfileNames[position];
        } else {
            String name = null;
            if (mActivity.isDolbyClientConnected()) {
                try {
                        name = getProfileName(position-1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return (name != null) ? name : mDefaultProfileNames[position];
        }
    }

    public String getDefaultProfileName(int position) {
        return mDefaultProfileNames[position];
    }

    public View getView(final int position, final View convertView,
            final ViewGroup parent) {
        final boolean dsConnected = mActivity.isDolbyClientConnected();

        View row = convertView;
        if (row == null) {
            if (position == 0) {
                row = LayoutInflater.from(parent.getContext()).inflate(R.layout.preset_list_item0, null);
            } else if (position == 7) {
                row = LayoutInflater.from(parent.getContext()).inflate(R.layout.preset_list_item1, null);
            } else {
                row = LayoutInflater.from(parent.getContext()).inflate(mLayout, null);
            }
        } else {
            int tagIndex = Integer.parseInt(row.getTag().toString());
            if (position != tagIndex) {
                if (position == 0) {
                    row = LayoutInflater.from(parent.getContext()).inflate(R.layout.preset_list_item0, null);
                } else if (position == 7) {
                    row = LayoutInflater.from(parent.getContext()).inflate(R.layout.preset_list_item1, null);
                } else {
                    row = LayoutInflater.from(parent.getContext()).inflate(mLayout, null);
                }
            }
        }

        final Profile item = mProfiles[position];

        boolean profileModified = false;
        if (position > 0 && position != 7) {
            try {
                if (mActivity.isDolbyClientConnected()) {
                    profileModified = mDsClient.isProfileSettingsModified(position - 1);
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        final boolean engineEnabled = parent.isEnabled();
        String itemName = null;
        boolean enabled;

        if (position <= Constants.PREDEFINED_PROFILE_COUNT || position == 7) {
            itemName = mDefaultProfileNames[position];
            enabled = true;
        } else {
            itemName = null;

            if (dsConnected) {
                itemName = getItemName(position);
                if(Tag.DEBUG_DSUI)
                    Log.d(Tag.MAIN, "ProfilesAdapter.getView(), itemName = " + itemName);
            }
            enabled = true;
            if (itemName == null) {
                itemName = mDefaultProfileNames[position];
            }
        }
        enabled = enabled && engineEnabled;

        final boolean selected = (position == mSelectedPosition) && engineEnabled;
        final TextView nameTextView = (TextView) row.findViewById(R.id.name);
        final ImageView icon = (ImageView) row.findViewById(R.id.icon);
        final ImageView revertButton = (ImageView) row.findViewById(R.id.revertButton);

        if (nameTextView != null) {
            nameTextView.setTextColor(parent.getResources().getColor(enabled ? R.color.white : (selected ? R.color.disabledblue_selected : R.color.disabledblue)));

            if (mNewLayout) {
                nameTextView.setTypeface(Assets.getFont(Assets.FontType.LIGHT));
            } else {
                nameTextView.setTypeface(Assets.getFont(Assets.FontType.REGULAR));
            }

            if (!this.mNewLayout && !Tools.isLandscapeScreenOrientation(mActivity)) {
                if (position == Constants.DEMO_POSITION || position == Constants.EXPLORE_DOLBY_ATMOS) {
                    nameTextView.setText(itemName);
                } else {
                    nameTextView.setText("");
                }
            } else {
                nameTextView.setText(itemName);
            }
        }
        icon.setImageResource(item.getIcon(selected, enabled));

        if (revertButton != null) {
            if (selected) {
                int vis = View.INVISIBLE;
                if (dsConnected && profileModified) {
                    vis = View.VISIBLE;
                }
                revertButton.setVisibility(vis);
                //DS1SOC-509 All profile should use revert_profile as the revert icon
                //revertButton.setImageResource(position <= Constants.PREDEFINED_PROFILE_COUNT ? R.drawable.revert_profile : R.drawable.presetremove);
                revertButton.setImageResource(R.drawable.revert_profile);
            } else {
                revertButton.setVisibility(View.INVISIBLE);
            }

            revertButton.setOnClickListener(mOnClickListener);
        }

        /*
        if (Tools.isLandscapeScreenOrientation(mActivity)) {
            row.setBackgroundResource(selected ? R.drawable.highlight : 0);
        } else {
            row.setBackgroundResource(selected ? R.drawable.topselectedbackground : 0);
        }
        */
        //DS1SOC-520 Use highlight.png as the highlight background image
        row.setBackgroundResource(selected ? R.drawable.highlight : 0);

        final EditText nameEdit = (EditText) row.findViewById(R.id.nameEdit);
        if (nameEdit != null) {
            mEditable = true;
            try {
                nameEdit.removeTextChangedListener(this);
            } catch (Exception e) {
            }
            if ((position>0) && (mCurrentlyEditedProfile == (position-1)) && (position!=7)) {
                nameEdit.setFilters(new InputFilter[] { new InputFilter.LengthFilter(Constants.MAX_PROFILE_NAME_LENGTH) });
                nameEdit.setTypeface(Assets.getFont(Assets.FontType.REGULAR));
                nameEdit.setText(mCurrentlyEditName);
                nameEdit.setVisibility(View.VISIBLE);
                nameEdit.setSelection(0, nameEdit.getText().length());
                nameEdit.requestFocus();
                nameEdit.setOnEditorActionListener(this);
                nameEdit.setOnKeyListener(this);
                nameEdit.addTextChangedListener(this);
            } else {
                nameEdit.setOnEditorActionListener(null);
                nameEdit.setOnKeyListener(null);
                nameEdit.setVisibility(View.GONE);
            }
        }

        row.setTag(String.valueOf(position));

        return row;
    }

    public int getSelection() {
        return mSelectedPosition;
    }

    public void setSelection(int position) {
        if (mSelectedPosition != position) {
            mSelectedPosition = position;
            scheduleNotifyDataSetChanged();
        }
    }

    public int getCurrentlyEditedProfile() {
        return mCurrentlyEditedProfile;
    }

    public void startEditingProfileName(int position) {
        if(Tag.DEBUG_DSUI)
            Log.d(Tag.MAIN, "ProfilesAdapter.startEditingProfileName " + position);

        // LANDSCAPE orientation only
        if (!mEditable || (position == Constants.DEMO_POSITION) || (position == Constants.EXPLORE_DOLBY_ATMOS)) {
            return;
        }

        DsGlobalEx dsClient = mDsClient;
        if (dsClient == null || !mActivity.isDolbyClientConnected()) {
            return;
        }

        endEditingProfileName(true);
        if (position > Constants.PREDEFINED_PROFILE_COUNT) {
            String name = getItemName(position);
            mCurrentlyEditedProfile = position-1;
            mCurrentlyEditName = name;
            Tools.showVirtualKeyboard(mActivity);
            scheduleNotifyDataSetChanged();
            // TODO take out MainActivity Dependency.
            mActivity.onProfileNameEditStarted();
        }
    }

    private String getProfileName(int position) {
        if (!mActivity.isDolbyClientConnected()) {
            return null;
        }

        try {
            if (mActivity.isDolbyClientConnected()) {
                DsProfileName dpn = mDsClient.getProfileName(position);
                if (dpn != null) {
                    return dpn.getCurrentName();
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void endEditingProfileName(boolean accept) {
        if(Tag.DEBUG_DSUI)
            Log.d(Tag.MAIN, "endEditingProfileName " + accept);
        if (mCurrentlyEditedProfile == -1) {
            return;
        }

        if (accept) {
            if (mCurrentlyEditName == null) {
                mCurrentlyEditName = "";
            } else {
                mCurrentlyEditName = mCurrentlyEditName.trim();
            }
            if (!mCurrentlyEditName.isEmpty()) {
                if (mCurrentlyEditedProfile + 1 == Constants.PROFILE_CUSTOM1_INDEX) {
                    DsProfileName dpn = new DsProfileName();
                    dpn.setDefaultName(null);
                    dpn.setCurrentName(mCurrentlyEditName);

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
                } else if (mCurrentlyEditedProfile + 1 == Constants.PROFILE_CUSTOM2_INDEX) {
                    DsProfileName dpn = new DsProfileName();
                    dpn.setDefaultName(null);
                    dpn.setCurrentName(mCurrentlyEditName);

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
            }
            mCurrentlyEditName = null;
        }
        mCurrentlyEditedProfile = -1;
        scheduleNotifyDataSetChanged();
        Tools.hideVirtualKeyboard(mActivity);
        mActivity.onProfileNameEditEnded();
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent event) {
        if (view.getId() == R.id.nameEdit) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                // cancel profile name change
                endEditingProfileName(false);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
        if (view.getId() == R.id.nameEdit && (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_PREVIOUS) && (event == null || event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
            // accept profile name change
            endEditingProfileName(true);
            return true;
        }
        return false;
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
            int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mCurrentlyEditName = s.toString();
    }

    @Override
    public void notifyDataSetChanged() {
        if(Tag.DEBUG_DSUI)
            Log.d(Tag.MAIN, "ProfilesAdapter.notifyDataSetChanged");
        super.notifyDataSetChanged();
    }

    public void scheduleNotifyDataSetChanged() {
        if(Tag.DEBUG_DSUI)
            Log.d(Tag.MAIN, "ProfilesAdapter.scheduleNotifyDataSetChanged");
        DAXApplication.HANDLER.removeCallbacks(mNotifyDataSetChanged);
        DAXApplication.HANDLER.post(mNotifyDataSetChanged);
    }

    private final Runnable mNotifyDataSetChanged = new Runnable() {

        @Override
        public void run() {
            notifyDataSetChanged();
        }
    };

}
