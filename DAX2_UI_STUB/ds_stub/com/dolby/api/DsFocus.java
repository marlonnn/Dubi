package com.dolby.api;

import java.util.ArrayList;

import android.content.Context;

public class DsFocus {

	private int[][] x = new int[50][50];
	private int profile;
    private int ieqPreset;
    

    public DsFocus(){
    	for (int i = 0; i< 50; i++) {
    		for(int j = 0; j < 50; j++) {
    		x[i][j] = 1;
    		}
    	}
    }
    public void registerVisualizer(IDsVisualizerEvents listener)
    {
        listener.onVisualizerSuspended(true);
    }

    public void unregisterVisualizer()
    {
        
    }

    public void registerDsEvents(IDsEvents listener)
    {
        
    }

    public void unregisterDsEvents()
    {
        
    }

    public int setState(boolean on) throws DsAccessException
    {
        return 0;
    }

    public int getState()
    {
        return 1;
    }

    public int setParameter(int paramId, int values[])  throws DsAccessException
    {
    	x[paramId] = values;
        return 1;
    }

    public int[] getParameter(int paramId)
    {
        return x[paramId];
    }

    public int getIeqPresetCount()
    {
        return 0;
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

    public int getProfileCount()
    {
        return 7;
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

    public boolean isProfileSettingsModified(int profile)
    {
        return true;
    }

    public int resetProfile(int profile)
        
    {
        return 0;
    }

    public boolean isMonoSpeaker()
    {
        return false;
    }

    public int requestAccessRight()
    {
        return 0;
    }

    public int abandonAccessRight()
    {
        return 0;
    }

    public int checkAccessRight()
    {
        return 0;
    }

    public int getAvailableAccessRight()
    {
        return 0;
    }

    protected void setConnectionInfo(int access)
    {
        
    }

    public void unregisterClient()
    {
        
    }

    public  int checkAccessRight(int x0)
    {
        return 0;
    }

    public  int abandonAccessRight(int x0)
    {
        return 0;
    }

    public  int requestAccessRight(int x0)
    {
        return 0;
    }

    public  int getApiVersion()
    {
        return 1;
    }

    public  String getDsVersion()
    {
        return "";
    }

    public  boolean registerClient(Context x0, IDsAccessEvents x1)
    {
    	x1.onClientConnected();
        return true;
    }

}
