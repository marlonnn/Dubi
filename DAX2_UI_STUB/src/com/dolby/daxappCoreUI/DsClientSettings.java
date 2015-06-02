/******************************************************************************
 *  This program is protected under international and U.S. copyright laws as
 *  an unpublished work. This program is confidential and proprietary to the
 *  copyright owners. Reproduction or disclosure, in whole or in part, or the
 *  production of derivative works therefrom without the express permission of
 *  the copyright owners is prohibited.
 *
 *                 Copyright (C) 2014 by Dolby Laboratories,
 *                             All rights reserved.
 ******************************************************************************/

package com.dolby.daxappCoreUI;

import com.dolby.api.DsAccess;
import com.dolby.api.DsAccessException;
import com.dolby.api.DsConstants;
import com.dolby.api.DsGlobalEx;
import com.dolby.api.DsParams;
import com.dolby.daxappUI.MainActivity;

public class DsClientSettings {
    /**
     * Singleton instance.
     */
    public static final DsClientSettings INSTANCE = new DsClientSettings();

    private int [] mValues = new int[1];

    private boolean requestDsAccessRight(DsGlobalEx dsClient) {
        boolean success = false;
        if (DsAccess.THIS_APP_GRANTED != dsClient.checkAccessRight()) {
            success = (DsConstants.DS_NO_ERROR == dsClient.requestAccessRight());
        } else {
            success = true;
        }

        return success;
    }

    // --- GeqOn ---
    public boolean setGeqOn(MainActivity activity, DsGlobalEx dsClient, boolean enable) {
        if (activity.isDolbyClientConnected() == false) {
            return false;
        }
        if (false == requestDsAccessRight(dsClient)) {
            return false;
        }

        mValues[0] = (enable == false? 0 : 1);
        try {
            dsClient.setParameter(DsParams.GraphicEqualizerEnable.toInt(), mValues);
        } catch (DsAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean getGeqOn(MainActivity activity, DsGlobalEx dsClient) {
        if (activity.isDolbyClientConnected() == false) {
            return false;
        }

        int [] values = dsClient.getParameter(DsParams.GraphicEqualizerEnable.toInt());
        return (values[0] == 0? false : true);
    }

    // --- DialogEnhancerOn ---
    public boolean setDialogEnhancerOn(MainActivity activity, DsGlobalEx dsClient, boolean enable) {
        if (activity.isDolbyClientConnected() == false) {
            return false;
        }
        if (false == requestDsAccessRight(dsClient)) {
            return false;
        }

        mValues[0] = (enable == false? 0 : 1);
        try {
            dsClient.setParameter(DsParams.DialogEnhancementEnable.toInt(), mValues);
        } catch (DsAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean getDialogEnhancerOn(MainActivity activity, DsGlobalEx dsClient) {
        if (activity.isDolbyClientConnected() == false) {
            return false;
        }

        int [] values = dsClient.getParameter(DsParams.DialogEnhancementEnable.toInt());
        return (values[0] == 0? false : true);
    }

    // --- VolumeLevellerOn ---
    public boolean setVolumeLevellerOn(MainActivity activity, DsGlobalEx dsClient, boolean enable) {
        if (activity.isDolbyClientConnected() == false) {
            return false;
        }
        if (false == requestDsAccessRight(dsClient)) {
            return false;
        }

        mValues[0] = (enable == false? 0 : 1);
        try {
            dsClient.setParameter(DsParams.DolbyVolumeLevelerEnable.toInt(), mValues);
        } catch (DsAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean getVolumeLevellerOn(MainActivity activity, DsGlobalEx dsClient) {
        if (activity.isDolbyClientConnected() == false) {
            return false;
        }

        int [] values = dsClient.getParameter(DsParams.DolbyVolumeLevelerEnable.toInt());
        return (values[0] == 0? false : true);
    }

    // --- HeadphoneVirtualizerOn ---
    public boolean setHeadphoneVirtualizerOn(MainActivity activity, DsGlobalEx dsClient, boolean enable) {
        if (activity.isDolbyClientConnected() == false) {
            return false;
        }
        if (false == requestDsAccessRight(dsClient)) {
            return false;
        }

        mValues[0] = (enable == false? 0 : 1);
        try {
            dsClient.setParameter(DsParams.DolbyHeadphoneVirtualizerControl.toInt(), mValues);
        } catch (DsAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean getHeadphoneVirtualizerOn(MainActivity activity, DsGlobalEx dsClient) {
        if (activity.isDolbyClientConnected() == false) {
            return false;
        }

        int [] values = dsClient.getParameter(DsParams.DolbyHeadphoneVirtualizerControl.toInt());
        return (values[0] == 0? false : true);
    }

    // --- SpeakerVirtualizerOn ---
    public boolean setSpeakerVirtualizerOn(MainActivity activity, DsGlobalEx dsClient, boolean enable) {
        if (activity.isDolbyClientConnected() == false) {
            return false;
        }
        if (false == requestDsAccessRight(dsClient)) {
            return false;
        }

        mValues[0] = (enable == false? 0 : 1);
        try {
            dsClient.setParameter(DsParams.DolbyVirtualSpeakerVirtualizerControl.toInt(), mValues);
        } catch (DsAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean getSpeakerVirtualizerOn(MainActivity activity, DsGlobalEx dsClient) {
        if (activity.isDolbyClientConnected() == false) {
            return false;
        }

        int [] values = dsClient.getParameter(DsParams.DolbyVirtualSpeakerVirtualizerControl.toInt());
        return (values[0] == 0? false : true);
    }

    // --- GraphicEqualizerBandGains
    public boolean setGraphicEqualizerBandGains(MainActivity activity, DsGlobalEx dsClient, int[] values) {
        if (activity.isDolbyClientConnected() == false) {
            return false;
        }
        if (false == requestDsAccessRight(dsClient)) {
            return false;
        }

        try {
            dsClient.setParameter(DsParams.GraphicEqualizerBandGains.toInt(), values);
        } catch (DsAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public int[] getGraphicEqualizerBandGains(MainActivity activity, DsGlobalEx dsClient) {
        if (activity.isDolbyClientConnected() == false) {
            return null;
        }

        return dsClient.getParameter(DsParams.GraphicEqualizerBandGains.toInt());
    }

    // --- IEQ Preset
    public boolean setIeqPreset(MainActivity activity, DsGlobalEx dsClient, int ieq) {
        if (activity.isDolbyClientConnected() == false) {
            return false;
        }
        if (false == requestDsAccessRight(dsClient)) {
            return false;
        }

        try {
            dsClient.setIeqPreset(ieq);
        } catch (DsAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public int getIeqPreset(MainActivity activity, DsGlobalEx dsClient) {
        if (activity.isDolbyClientConnected() == false) {
            return -1;
        }

        return dsClient.getIeqPreset();
    }
}
