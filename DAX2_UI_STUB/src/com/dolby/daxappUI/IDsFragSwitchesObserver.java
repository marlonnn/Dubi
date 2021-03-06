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

package com.dolby.daxappUI;

import android.view.View;

public interface IDsFragSwitchesObserver {

    public void onProfileSettingsChanged();

    public void setUserProfilePopulated();

    public void displayTooltip(View pointToView, int idTitle, int idText);

    public abstract void switchesAreAlive();
}
