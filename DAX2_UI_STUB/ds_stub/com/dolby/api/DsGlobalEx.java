
package com.dolby.api;


import java.util.Random;

import android.os.Handler;
import android.os.HandlerThread;

import com.dolby.daxappCoreUI.*;

public class DsGlobalEx extends DsGlobal
{
    private int profile;
    private int[][] x = new int[200][50];
    private int int102 = 0;
    private int int103 = 0;
    private int int107 = 0;
    private int int14 = 0;
    private int int22 = 0;
    private int int17 = 0;
    private int int21 = 0;
    private int int31 = 0;
    private int[] int119 = new int[20];
    private int[] int30 = new int[20];
    private int ieqPreset;
    private IDsVisualizerEvents mListener;
    private Handler mVisualizerCallbackHandler;
    public DsGlobalEx()
    {
    	for (int i = 0; i< 200; i++) {
    		for(int j = 0; j < 50; j++) {
    		x[i][j] = 0;
    		}
    	}
    }

    public int setState(boolean on) throws DsAccessException
    {
        return 0;
    }

    public int getState()
    {
        return 1;
    }
    public String getInternalVersion()
    {
         return "";
    }

    public int setProfileName(int profile, DsProfileName name)
        throws DsAccessException
    {
        return 0;
    }

    public DsProfileName getProfileName(int profile)
    {
        
        return new DsProfileName();
    }

    public void registerProfileNameEvents(IDsProfileNameEvents listener)
    {
        
    }

    public void unregisterProfileNameEvents()
    {
        
    }
    
    public int setProfile(int profile) throws DsAccessException
    {
    	this.profile = profile;
        return 0;
    }

    public int getProfile()
    {
        return profile;
    }
    
    public int setParameter(int paramId, int values[])  throws DsAccessException
    {
    	x[paramId] = values;
    	
    	if(paramId == 102) {
    		int102 = values[0];
    	} else if(paramId == 103) {
    		int103 = values[0];
    	} else if(paramId == 107) {
    		int107 = values[0];
    	} else if(paramId == 119) {
    		int119 = values;
    	} else if(paramId == 14) {
    		int14 = values[0];
    	} else if(paramId == 22) {
    		int22 = values[0];
    	} else if(paramId == 27) {
    		int17 = values[0];
    	} else if(paramId == 21) {
    		int21 = values[0];
    	} else if(paramId == 30) {
    		int30 = values;
    	} else if(paramId == 31) {
    		int31 = values[0];
    	}
        return 1;
    }

    public int[] getParameter(int paramId)
    {
    	if(paramId == 102) {
    		return new int[]{int102};
    	} else if(paramId == 103) {
    		return new int[]{int103};
    	} else if(paramId ==107) {
    		return new int[]{int107};
    	} else if(paramId == 119) {
    		return int119;
    	} else if(paramId == 14) {
    		return new int[]{int14};
    	} else if(paramId == 22) {
        	return new int[]{int22};
    	} else if(paramId == 17) {
        	return new int[]{int17};
    	} else if(paramId == 21) {
        	return new int[]{int21};
    	} else if(paramId == 30) {
        		return int30;
    	} else if(paramId == 31) {
    		return new int[]{int31};
    	} else {
    		return x[paramId];
    	}
    }
    public int setIeqPreset(int preset) throws DsAccessException
    {
    	ieqPreset = preset;
        return 0;
    }

    public int getIeqPreset()
    {
        return ieqPreset;
    }
    
    public void registerVisualizer(IDsVisualizerEvents listener)
    {
    	mListener = listener;
    	listener.onVisualizerSuspended(true);
    	controlVisualizerCallback();
    }

    private void controlVisualizerCallback() {
    	if (mVisualizerCallbackHandler == null) {
            HandlerThread ht = new HandlerThread("Visualiser update");
            ht.start();
            mVisualizerCallbackHandler = new Handler(ht.getLooper());
            mVisualizerCallbackHandler.post(mCallbackOnVisualiserUpdate);
        }
    }
    
    private final Runnable mCallbackOnVisualiserUpdate = new Runnable() {

        @Override
        public void run() {
            callbackOnVisualiserUpdate();
        }
    };

    private void callbackOnVisualiserUpdate() {
    	
    	
    	float[][] mGEqBand = new float[Constants.IEQ_PRESETS][Constants.BANDS];
        DsClientSettings[] mProfileSetting = new DsClientSettings[7];
        Random random = new Random(System.currentTimeMillis());
        float[] excitations = new float[Constants.BANDS];
        
        final float max = Constants.VISIBLE_GAIN_SPAN / 4;
        for (int i = 0; i < Constants.BANDS; ++i) {
            excitations[i] += (random.nextFloat() - 0.5f) * max;
            excitations[i] = Math.max(Constants.MIN_VISIBLE_GAIN, Math.min(Constants.MAX_VISIBLE_GAIN, excitations[i]));
        }

        float[] gains = new float[Constants.BANDS];
        for (int band = 0; band < Constants.BANDS; ++band) {
        	gains[band] = Constants.IEQ_PRESET_GAIN[1][band] + mGEqBand[1][band];;
            // shuffle gains a bit: +-1.0f
            gains[band] += random.nextFloat() * 2 - 1.0f;
        }
        mListener.onVisualizerSuspended(false);
        mListener.onVisualizerUpdate(excitations, gains);
        if (mVisualizerCallbackHandler != null) {
            mVisualizerCallbackHandler.removeCallbacks(mCallbackOnVisualiserUpdate);
            mVisualizerCallbackHandler.postDelayed(mCallbackOnVisualiserUpdate, 100);
        }
    }
    
    public void unregisterVisualizer()
    {
        
    }

    public void registerDsEvents(IDsEvents listener)
    {
    	listener.onDsOn(true);
    	
    }

    public void unregisterDsEvents()
    {
        
    }
}
