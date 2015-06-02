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

package com.dolby.daxappCoreUI;

import android.content.Context;
import android.util.Log;

import com.dolby.daxappUI.R;

public class Configuration {

    private static Configuration dynamicInstance;
    private static final float DEFAULT_MIN_EDIT_GAIN = -12.0f;
    private static final float DEFAULT_MAX_EDIT_GAIN = 12.0f;
    private float minEditGain = DEFAULT_MIN_EDIT_GAIN;
    private float maxEditGain = DEFAULT_MAX_EDIT_GAIN;

    private Configuration(Context ctx) {
        try {
            minEditGain = Float.parseFloat(ctx.getResources().getString(R.string.min_edit_gain));
            maxEditGain = Float.parseFloat(ctx.getResources().getString(R.string.max_edit_gain));

            minEditGain_DS2 = (int)(minEditGain) << Constants.GEQ_GAIN_RIGHT_SHIFT_COUNT;
            maxEditGain_DS2 = (int)(maxEditGain) << Constants.GEQ_GAIN_RIGHT_SHIFT_COUNT;
        } catch (NumberFormatException nfe) {
            minEditGain = Float.NaN;
            maxEditGain = Float.NaN;

            minEditGain_DS2 = Integer.MAX_VALUE;
            maxEditGain_DS2 = Integer.MAX_VALUE;
            Log.e(Tag.MAIN, "Some of values from configuration.xml were not float type!");
        } catch (NullPointerException npe) {
            minEditGain = Float.NaN;
            maxEditGain = Float.NaN;

            minEditGain_DS2 = Integer.MAX_VALUE;
            maxEditGain_DS2 = Integer.MAX_VALUE;
            Log.e(Tag.MAIN, "Some of values from configuration.xml were not loaded!");
        }
    }

    /**
     * @param ctx
     *            - needs to be context but be aware not to use the Activity
     *            object. Instead use the method getApplicationContext
     * @return - Configuration object
     */
    public static Configuration getInstance(Context ctx) {
        if (dynamicInstance == null) {
            dynamicInstance = new Configuration(ctx);
        }
        return dynamicInstance;
    }

    public float getMaxEditGain() {
        if (Float.isNaN(maxEditGain))
            return DEFAULT_MAX_EDIT_GAIN;
        else
            return maxEditGain;
    }

    public float getMinEditGain() {
        if (Float.isNaN(minEditGain))
            return DEFAULT_MIN_EDIT_GAIN;
        else
            return minEditGain;
    }

    // Add for DS2API
    private static final int DEFAULT_MIN_EDIT_GAIN_DS2 = -12;
    private static final int DEFAULT_MAX_EDIT_GAIN_DS2 = 12;
    private int minEditGain_DS2 = DEFAULT_MIN_EDIT_GAIN_DS2 << Constants.GEQ_GAIN_RIGHT_SHIFT_COUNT;
    private int maxEditGain_DS2 = DEFAULT_MAX_EDIT_GAIN_DS2 << Constants.GEQ_GAIN_RIGHT_SHIFT_COUNT;

    public int getMaxEditGain_DS2() {
        if (Integer.MAX_VALUE == maxEditGain_DS2)
            return DEFAULT_MAX_EDIT_GAIN_DS2;
        else
            return maxEditGain_DS2;
    }

    public int getMinEditGain_DS2() {
        if (Integer.MAX_VALUE == minEditGain)
            return DEFAULT_MIN_EDIT_GAIN_DS2;
        else
            return minEditGain_DS2;
    }
    // Add for DS2API END
}
