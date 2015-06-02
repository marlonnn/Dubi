package com.dolby.api;

public abstract interface IDsAccessEvents {
  

  public abstract void onClientConnected();
  

  public abstract void onClientDisconnected();
  

  public abstract void onAccessForceReleased(java.lang.String arg0, int arg1);
  

  public abstract void onAccessAvailable();
  

  public abstract boolean onAccessRequested(java.lang.String arg0, int arg1);
}