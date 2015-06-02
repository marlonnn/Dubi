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

package com.dolby.instoredemoapp;

import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.dolby.api.DsAccess;
import com.dolby.api.DsAccessException;
import com.dolby.api.DsConstants;
import com.dolby.api.DsFocus;
import com.dolby.api.DsParams;
import com.dolby.api.IDsAccessEvents;
import com.dolby.api.IDsEvents;
import com.dolby.daxappCoreUI.Tag;

public class DlbApController implements IDsEvents,
        IDsAccessEvents {
    private static final String TAG = "DlbApController";

    private class APMessage {

        public long delayTime;
        public Message message;

        public APMessage(long time, Message msg) {
            delayTime = time;
            message = msg;
        }
    }

    private DsFocus mDsFocus;
    private MediaPlayer mMediaPlayer;
    private Handler mHandler;
    private InputStream mApInfoStream = null;
    // This context is used to register a DsFocus Client
    private Context mContext;
    private DlbApInfoExtractor mApInfoExtractor;
    private ArrayList<APMessage> mMsgList;

    private boolean mDsConnected = false;

    public DlbApController(Context ctx) {
        super();
        mContext = ctx;
        mApInfoExtractor = new DlbApInfoExtractor();
        mDsFocus = new DsFocus();

        try {
            Log.d(TAG, "going to bind the DS service...");
            mDsFocus.registerClient((Activity) mContext, this);
        } catch (Exception e) {
            Log.e(TAG, "Consturction of DlbApController, bindDsService failed");
            e.printStackTrace();
            return;
        }
    }

    public void setMediaPlayer(MediaPlayer mp) {
        if (mp != null) {
            mMediaPlayer = mp;
        }
    }

    public void setHandler(Handler handler) {
        if (handler != null) {
            mHandler = handler;
        }
    }

    public void onExit() {
        abandonAFandAR();
        mDsFocus.unregisterDsEvents();
        try {
            Log.d(TAG, "about to unbind DS service...");
            mDsFocus.unregisterClient();
        } catch (Exception e) {
            Log.e(TAG, "DlbApController.onExit(), unBindDsService failed");
        }
        mDsConnected = false;
    }

    public void sendApMessages() {
        if (mHandler != null) {
            if(Tag.DEBUG_DSUI)
                Log.d(TAG, "the un-handled messages will be removed!");
            mHandler.removeCallbacksAndMessages(null);
        }

        //Log.d(TAG, "duaration of the media is " + mMediaPlayer.getDuration());
        for (int i = 0; i < mMsgList.size(); ++i) {
            APMessage apmsg = mMsgList.get(i);
            if(Tag.DEBUG_DSUI)
                Log.d(TAG, "will send ap msg after " + apmsg.delayTime + " millisecond");
            mHandler.sendMessageDelayed(apmsg.message, apmsg.delayTime);
        }
    }

    public void setApInfoFile(InputStream apstream) {
        mApInfoStream = apstream;
        mApInfoExtractor.setApInfoFile(mApInfoStream);
        initMsgList();
    }

    public boolean isDsConnected() {
        return mDsConnected;
    }

    public boolean processApMessage(Message msg) {
        Log.d(TAG, "processApMessage " + msg.what);
        // Log.d(TAG, "obj = " + msg.obj);
        if (msg.obj == null) {
            Log.e(TAG, "the msg.obj is null");
            return true;
        }
        AutoPilotItem apitem = (AutoPilotItem) msg.obj;
        boolean ret = handleMasterControl(apitem.getMasterControlValue());
        if(Tag.DEBUG_DSUI)
            Log.d(TAG, "handleMasterControl, returns " + ret);

        // check for master control failure and signal autopilot failure.
        if (!ret)
            return false;

        ret = handleProfileControl(apitem.getProfileControlValue());
        Log.d(TAG, "handleProfileControl, returns " + ret);
        ret = handleSurroundVirtualizer(apitem.getSurroundVirtualizerValue());
        Log.d(TAG, "handleSurroundVirtualizer, returns " + ret);
        ret = handleDialogEnhancer(apitem.getDialogEnahancerValue());
        Log.d(TAG, "handleDialogEnhancer, returns " + ret);
        ret = handleVolumeLeveler(apitem.getVolumeLevelerValue());
        Log.d(TAG, "handleVolumeLeveler, returns " + ret);
        ret = handleIntelligentEq(apitem.getIntelligenEqValue());
        Log.d(TAG, "handleIntelligentEq, returns " + ret);
        handleTextInfo(apitem.getDisplayText());

        return true;
    }

    private void handleTextInfo(TextInfo ti) {
        if (ConstValue.UPDATE_TEXT) {
            if(Tag.DEBUG_DSUI)
                Log.d(TAG, "handleTextInfo, ti = " + ti);
            Message msg = mHandler.obtainMessage(ConstValue.UPDATE_TXT_MSG_ID, ti);
            mHandler.sendMessage(msg);
        }
    }

    private boolean handleIntelligentEq(String sieq) {
        /*
         * Note:At present, the lib of ds1 implemented Off, Open, Rich and
         * Focused
         */
        Log.d(TAG, "handleIntelligentEq, ieq = " + sieq);
        int ieq = -1;
        if (sieq.equalsIgnoreCase("off")) {
            ieq = ConstValue.IEQ_OFF;
        } else if (sieq.equalsIgnoreCase("Open")) {
            ieq = ConstValue.IEQ_OPEN;
        } else if (sieq.equalsIgnoreCase("Rich")) {
            ieq = ConstValue.IEQ_RICH;
        } else if (sieq.equalsIgnoreCase("Focused")) {
            ieq = ConstValue.IEQ_FOCUSED;
        } else if (sieq.equalsIgnoreCase("Warm")) {
            ieq = ConstValue.IEQ_WARM;
            // Workaround
            Log.d(TAG, "Not supported yet");
            return true;
        } else if (sieq.equalsIgnoreCase("Bright")) {
            ieq = ConstValue.IEQ_BRIGHT;
            // Workaround
            Log.d(TAG, "Not supported yet");
            return true;
        } else if (sieq.equalsIgnoreCase("Balanced")) {
            ieq = ConstValue.IEQ_BALANCED;
            // Workaround
            Log.d(TAG, "Not supported yet");
            return true;
        } else if (sieq.equalsIgnoreCase("unset")) {
            Log.d(TAG, "value does not change");
            return true;
        } else {
            Log.e(TAG, "DlbApController.handleIntelligentEq, invalid value = " + ieq);
            return false;
        }

        if (!mDsConnected || (!requestAFandAR())) {
            return false;
        }
        try {
            mDsFocus.setIeqPreset(ieq);
        } catch (DsAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean handleVolumeLeveler(String vl) {
        Log.d(TAG, "handleVolumeLeveler, vl = " + vl);
        boolean on = false;
        if (vl.equalsIgnoreCase("on")) {
            on = true;
        } else if (vl.equalsIgnoreCase("off")) {
            on = false;
        } else if (vl.equalsIgnoreCase("unset")) {
            Log.d(TAG, "value does not change");
            return true;
        } else {
            Log.e(TAG, "DlbApController.handleVolumeLeveler, invalid value = " + vl);
            return false;
        }

        int[] values = new int[1];
        values[0] = (on? 1: 0);
        if (!mDsConnected || (!requestAFandAR())) {
            return false;
        }
        try {
            mDsFocus.setParameter(DsParams.DolbyVolumeLevelerEnable.toInt(), values);
        } catch (DsAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean handleDialogEnhancer(String deh) {
        Log.d(TAG, "handleDialogEnhancer, deh = " + deh);
        boolean on = false;
        if (deh.equalsIgnoreCase("on")) {
            on = true;
        } else if (deh.equalsIgnoreCase("off")) {
            on = false;
        } else if (deh.equalsIgnoreCase("unset")) {
            Log.d(TAG, "value does not change");
            return true;
        } else {
            Log.e(TAG, "DlbApController.handleDialogEnhancer, invalid value = " + deh);
            return false;
        }

        int[] values = new int[1];
        values[0] = (on? 1: 0);
        if (!mDsConnected || (!requestAFandAR())) {
            return false;
        }
        try {
            mDsFocus.setParameter(DsParams.DialogEnhancementEnable.toInt(), values);
        } catch (DsAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean handleSurroundVirtualizer(String sv) {
        Log.d(TAG, "handleSurroundVirtualizer " + sv);
        boolean on = false;
        if (sv.equalsIgnoreCase("on")) {
            on = true;
        } else if (sv.equalsIgnoreCase("off")) {
            on = false;
        } else if (sv.equalsIgnoreCase("unset")) {
            Log.d(TAG, "value does not change");
            return true;
        } else {
            Log.e(TAG, "DlbApController.handleSurroundVirtualizer, invalid value = " + sv);
            return false;
        }

        int[] values = new int[1];
        values[0] = (on? 1: 0);
        if (!mDsConnected || (!requestAFandAR())) {
            return false;
        }
        try {
            mDsFocus.setParameter(DsParams.DolbyHeadphoneVirtualizerControl.toInt(), values);
        } catch (DsAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean handleProfileControl(String proctl) {
        Log.d(TAG, "handleProfileControl, profilecontrol = " + proctl);
        int profile = -1;
        if (proctl.equalsIgnoreCase("Movie")) {
            profile = ConstValue.PROFILE_MOVIE;
        } else if (proctl.equalsIgnoreCase("Music")) {
            profile = ConstValue.PROFILE_MUSIC;
        } else if (proctl.equalsIgnoreCase("Game")) {
            profile = ConstValue.PROFILE_GAME;
        } else if (proctl.equalsIgnoreCase("Voice")) {
            profile = ConstValue.PROFILE_VOICE;
        } else if (proctl.equalsIgnoreCase("unset")) {
            Log.d(TAG, "value not change!");
            return true;
        } else {
            Log.e(TAG, "DlbApController.handleProfileControl,invalid value = " + proctl);
            return false;
        }

        if (!mDsConnected || (!requestAFandAR())) {
            return false;
        }
        try {
            mDsFocus.setProfile(profile);
            mDsFocus.resetProfile(profile);
        } catch (Exception e) {
            Log.e(TAG, "DlbApController.handleProfileControl,fail to call setProfileSettings");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean handleMasterControl(String mastercontrol) {
        Log.d(TAG, "handleMasterControl, mastercontrol = " + mastercontrol);
        boolean on = false;

        if (mastercontrol.equalsIgnoreCase("on")) {
            on = true;
        } else if (mastercontrol.equalsIgnoreCase("off")) {
            on = false;
        } else if (mastercontrol.equalsIgnoreCase("unset")) {
            Log.d(TAG, "no need to handle this");
            return true;
        } else {
            Log.e(TAG, "DlbApController.handleMasterControl, invalid value = " + mastercontrol);
            return false;
        }

        if (!mDsConnected || (!requestAFandAR())) {
            return false;
        }
        try {
            int result = mDsFocus.setState(on);

            // Check for failure to enable the effect
            if (result != DsConstants.DS_NO_ERROR)
            {
                Log.e(TAG, "DlbApController.handleMasterControl, setDsOnChecked failed due to return code: " + result);
                return false;
            }

        } catch (Exception e) {
            Log.e(TAG, "DlbApController.handleMasterControl, setDsOnChecked failed");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private void initMsgList() {
        if (mMsgList != null) {
            mMsgList.clear();
            mMsgList = null;
        }
        mMsgList = new ArrayList<APMessage>();
        ArrayList<AutoPilotItem> aplist = mApInfoExtractor.getAutoPilotMetadata();
        if(Tag.DEBUG_DSUI)
            Log.d(TAG, "aplist.length = " + aplist.size());
        for (int i = 0; i < aplist.size(); ++i) {
            AutoPilotItem apitem = aplist.get(i);
            if(Tag.DEBUG_DSUI)
                Log.d(TAG, "obj of msg: \n" + apitem);
            Message msg = mHandler.obtainMessage(ConstValue.AP_MSG_ID, apitem);
            long delaytime = calMsgDelaytime(apitem.getTimeStamp()).longValue();
            APMessage apmsg = new APMessage(delaytime, msg);
            mMsgList.add(apmsg);
        }
    }

    private Integer calMsgDelaytime(String timestamp) {
        // The format of timestamp is HH:MM:SS:MSS e.g.02:12:00:000
        Integer ret = 0;
        String tmp = timestamp;

        // get the hour value
        int colonIdx = tmp.indexOf(':');
        if (colonIdx == -1) {
            Log.e(TAG, "the format of the timestamp is not valid");
            return -1;
        }
        String sub = tmp.substring(0, colonIdx);
        Log.d(TAG, "hour = " + sub);
        Integer hour = Integer.valueOf(sub);
        tmp = tmp.substring(colonIdx + 1, tmp.length());

        // get the minute value
        colonIdx = tmp.indexOf(':');
        sub = tmp.substring(0, colonIdx);
        Log.d(TAG, "min = " + sub);
        Integer minute = Integer.valueOf(sub);
        tmp = tmp.substring(colonIdx + 1, tmp.length());

        // get the second value
        colonIdx = tmp.indexOf(':');
        sub = tmp.substring(0, colonIdx);
        Log.d(TAG, "sec = " + sub);
        Integer second = Integer.valueOf(sub);
        tmp = tmp.substring(colonIdx + 1, tmp.length());

        // get the millisecond value
        Integer millisecond = Integer.valueOf(tmp);

        ret = hour * 60 * 60 * 1000 + minute * 60 * 1000 + second * 1000 + millisecond;
        Log.d(TAG, "time = " + ret);
        return ret;
    }

    // IDsEvents
    @Override
    public void onDsOn(boolean arg0) {

    }

    @Override
    public void onProfileSelected(int arg0) {

    }

    @Override
    public void onProfileSettingsChanged(int arg0) {

    }

    @Override
    public void onDsSuspended(boolean isSuspended) {
        // DS2API-TODO: ......???
    }
    // IDsEvents END


    // IDsAccessEvents
    @Override
    public void onAccessAvailable() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onAccessForceReleased(String arg0, int arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onAccessRequested(String arg0, int arg1) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onClientConnected() {
        Log.d(TAG, "onClientConnected");
        mDsConnected = true;
        mDsFocus.registerDsEvents(this);

        if (!requestAFandAR())
        {
            Log.d(TAG, "onClientConnected() request audio focuse failed");
            return;
        } else {
            Log.d(TAG, "onClientConnected() request audio focuse successfully!");
        }

        mHandler.sendEmptyMessage(ConstValue.DAX_SERVICE_CONNECTED);
    }

    @Override
    public void onClientDisconnected() {
        Log.d(TAG, "onClientDisConnected");
        mDsConnected = false;

    }
    // IDsAccessEvents END

    private OnAudioFocusChangeListener mAFChangeListener = new OnAudioFocusChangeListener(){

        @Override
        public void onAudioFocusChange(int focusChange) {
            Log.d(TAG, "onAudioFocusChange, focusChange = " + focusChange);
            if (null == mHandler) {
                return;
            }
            AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            //if(am.isAppInFocus("com.dolby.daxappUI"))
            //{
                //return;
            //}

            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT){
                mHandler.sendEmptyMessage(ConstValue.DAX_INSTOREDEMO_QUIT);
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS){
                mHandler.sendEmptyMessage(ConstValue.DAX_INSTOREDEMO_QUIT);
            } else { // (focusChange == AudioManager.AUDIOFOCUS_GAIN)
                Log.d(TAG, "onAudioFocusChange, do nothing for value = " + focusChange);
            }
        }
    };


    public boolean requestAFandAR() {
        boolean ret = false;
        if ((null == mContext) || (null == mDsFocus) || (!mDsConnected)) {
            return false;
        }

        // request Audio Focus.
        AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        int result = am.requestAudioFocus(mAFChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            // request Ds AccessRight.
            if (DsAccess.THIS_APP_GRANTED != mDsFocus.checkAccessRight()) {
                if (DsConstants.DS_NO_ERROR == mDsFocus.requestAccessRight()) {
                    ret = true;
                } else {
                    Log.e(TAG, "requestAFandAR(): requestAccessRight failed!");
                }
            } else {
                ret = true;
            }
        }

        return ret;
    }

    public void abandonAFandAR() {
        if ((null == mContext) || (null == mDsFocus) || (!mDsConnected)) {
            return;
        }

        // abandon Ds AccessRight.
        mDsFocus.abandonAccessRight();

        // abandon Audio Focus.
        AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        am.abandonAudioFocus(mAFChangeListener);
    }
}
