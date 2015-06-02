package com.dolby.api;
import android.content.Context;

public class DsBase {

    DsBase()
    {
        
    }

    protected void convertErrorCodeToException(int errorCode)
    {
        
    }

    protected void handleException(Exception e, String methodName)
    {
        
    }

    protected void setConnectionInfo(int access)
    {
        
    }

    public boolean registerClient(Context context, IDsAccessEvents listener)
    {
        return true;
    }

    public void unregisterClient()
    {
        
    }

    public String getDsVersion()
    {
        return "";  
    }

    public int getApiVersion()
    {
        return 1;
    }

    public int requestAccessRight(int accessRight)
    {
        return 1;
    }

    public int abandonAccessRight(int accessRight)
    {
        return 1;
    }

    public int getAvailableAccessRight()
    {
        return 1;
    }

    public int checkAccessRight(int accessRight)
    {
        return 1;
    }

}
