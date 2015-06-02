package com.dolby.api;

public abstract interface IDsEvents {
	  

	  public abstract void onDsOn(boolean arg0);
	  

	  public abstract void onProfileSelected(int arg0);
	  

	  public abstract void onProfileSettingsChanged(int arg0);
	  

	  public abstract void onDsSuspended(boolean arg0);
}