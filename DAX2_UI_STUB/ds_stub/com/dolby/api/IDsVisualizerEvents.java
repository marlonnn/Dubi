package com.dolby.api;

public abstract interface IDsVisualizerEvents {
	  

	  public abstract void onVisualizerUpdate(float[] arg0, float[] arg1);
	  
	  public abstract void onVisualizerSuspended(boolean arg0);
}
