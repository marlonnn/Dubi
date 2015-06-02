package com.dolby.api;

public abstract interface IDsDeathHandler extends android.os.IInterface {  
	  public abstract void onClientDied() throws android.os.RemoteException;
}