
package com.dolby.api;



public class DsGlobal extends DsFocus
{

    public DsGlobal()
    {
        super.setConnectionInfo(2);
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
        return 2;
    }

    public int getAvailableAccessRight()
    {
        return 2;
    }

    public int getOffType()
    {
        int ret_val = -1;
        return ret_val;
    }

}
