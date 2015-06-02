package com.dolby.api;

public class DsAccess {
	public static final int ACCESS_NONE = 0;
    public static final int ACCESS_FOCUS = 1;
    public static final int ACCESS_GLOBAL = 2;
    public static final int ACCESS_TUNING = 4;
    public static final int ERROR_ACCESS_AREADY_GRANTED = -1;
    public static final int ERROR_ACCESS_NOT_PERMITTED = -2;
    public static final int ERROR_ACCESS_NO_AUDIOFOCUS = -3;
    public static final int ERROR_ACCESS_NOT_AGREED = -4;
    public static final int NONE_APP_GRANTED = 0;
    public static final int OTHER_APP_GRANTED = 1;
    public static final int THIS_APP_GRANTED = 2;
}
