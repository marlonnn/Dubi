/******************************************************************************
 *  This program is protected under international and U.S. copyright laws as
 *  an unpublished work. This program is confidential and proprietary to the
 *  copyright owners. Reproduction or disclosure, in whole or in part, or the
 *  production of derivative works therefrom without the express permission of
 *  the copyright owners is prohibited.
 *
 *                 Copyright (C) 2014 by Dolby Laboratories,
 *                             All rights reserved.
 ******************************************************************************/
package com.dolby.daxappUI;

import java.math.BigDecimal;

import com.dolby.api.DsGlobalEx;
import com.dolby.api.IDsEvents;
import com.dolby.daxappCoreUI.Constants;
import com.dolby.daxappCoreUI.DAXApplication;
import com.dolby.daxappCoreUI.Tag;
import com.dolby.daxappCoreUI.Tools;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.ActionBar;
import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment
 * must implement the
 * {@link FragExploreDolbyAtmos.OnFragmentInteractionListener} interface to
 * handle interaction events. Use the {@link ExploreDolbyAtmos#newInstance}
 * factory method to create an instance of this fragment.
 *
 */

public class ExploreDolbyAtmos extends Activity implements
        OnCompletionListener, OnPreparedListener, OnErrorListener {
    // Data members.
    // Profile stuff.
    private String tag = "ExploreDolbyAtmos";
    private ProfilesAdapter mProfilesAdapter;

    // Tooltip stuff?
    private ViewGroup mNativeRootContainer;

    // DsClient instance.
    private DsGlobalEx mDsClient;
    // Required to know whether our local instance has connected to the service
    // or not.
    private boolean mDolbyClientConnected = false;

    // Our observers.
    // Specific Profile Presets Fragment Observer.
//    private IDsFragProfilePresetsObserver mSpecificObserver;
    // Generic Fragment Observer (error handling / generic provider).
//    private IDsFragObserver mFObserver;

    private boolean mMobileLayout = false;

//    private View v;
    // Record y postion of upimg touching down/move
    private int mUpimgTouchDownY = 0;
    // Record upimg height
    private int mUpimgHeight = 0;
    // Record the old y position of upimg touching down
    private int mUpimgOldTouhDownY = 0;
    // Record y postion of downimg touching down/move
    private int mDownimgTouchDownY = 0;
    // Record downimg height
    private int mDownimgHeight = 0;
    // Record the old y position of downimg touching down
    private int mDownimgOldTouhDownY = 0;
    // Record screen height
    private int mScreenHeight = 0;
    // Record the time of animate during(ms)
    private int mAnimateDuring = 300;
    // Record the distance of upimg/downing rollback
    private int mRollbackDisance = 100;
    // Record the status of the upimg
    private boolean mUpImgAtTop = false;
    // Record the Y position of btncontrolImg
    private int mBtnControlInitY = 0;
    // Record the max distance of the logo
    private double mExploreAtmosMaxDistanceRatio = 0.55d;
    // Record the max distance of the logo
    private double mExperienceDolbyMaxDistanceRatio = 0.6d;
    // Record the frag being opened
    private boolean mHavedOpened = false;
    // Record the image initialize height
    private int mImgInitHeight = 480;
    // Record the exploreatmoslogo
    private int mExploreAtmosLogoY = 240;
    // Record the experience Dolby enhanced apps
    private int mExperienceDolbyY = 500;
    private int screenTopD = 128;



    private ActionBar mActionBar;


    public void changeScale() {
        Configuration sys = getBaseContext().getResources().getConfiguration();

        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        float scaleNumber = metric.density;
        if (sys.smallestScreenWidthDp >= 360) {

            if (sys.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                // Record the image initialize height
                    mImgInitHeight = 230;
                   // Record the exploreatmoslogo
                    mExploreAtmosLogoY = 55;
                   // Record the experience Dolby enhanced apps
                    mExperienceDolbyY = 245;
                    mExploreAtmosMaxDistanceRatio = 0.23d;
                   // Record the max distance of the logo
                    mExperienceDolbyMaxDistanceRatio = 0.85d;
                    if(scaleNumber == 2) {
                       screenTopD = 120;
                    } else if(scaleNumber == 3){
                       screenTopD = 180;
                    } else {
                       screenTopD = 60;
                    }
                    mRollbackDisance = 90;
            } else {
                // Record the image initialize height
                    mImgInitHeight = 420;
                   // Record the exploreatmoslogo
                    mExploreAtmosLogoY = 190;
                   // Record the experience Dolby enhanced apps
                    mExperienceDolbyY = 435;
                    mExploreAtmosMaxDistanceRatio = 0.5d;
                   // Record the max distance of the logo
                    mExperienceDolbyMaxDistanceRatio = 0.65d;
                    if(scaleNumber == 2) {
                       screenTopD = 145;
                    } else if(scaleNumber == 3){
                       screenTopD = 218;
                    } else {
                       screenTopD = 74;
                    }
                    mRollbackDisance = 120;

            }
        } else if (sys.smallestScreenWidthDp >= 320) {

            if (sys.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                // Record the image initialize height
                    mImgInitHeight = 200;
                   // Record the exploreatmoslogo
                    mExploreAtmosLogoY = 25;
                   // Record the experience Dolby enhanced apps
                    mExperienceDolbyY = 210;
                    mExploreAtmosMaxDistanceRatio = 0.2d;
                   // Record the max distance of the logo
                    mExperienceDolbyMaxDistanceRatio = 0.74d;
                    screenTopD = 155;
                    mRollbackDisance = 90;
            } else {
                // Record the image initialize height
                    mImgInitHeight = 380;
                   // Record the exploreatmoslogo
                    mExploreAtmosLogoY = 150;
                   // Record the experience Dolby enhanced apps
                    mExperienceDolbyY = 400;
                    mExploreAtmosMaxDistanceRatio = 0.45d;
                   // Record the max distance of the logo
                    mExperienceDolbyMaxDistanceRatio = 0.6d;
                    screenTopD = 147;
                    mRollbackDisance = 120;

            }
        }
    }

    // Second method called by the framework.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(tag, "onCreate");
        super.onCreate(savedInstanceState);
        changeScale();


        mActionBar=getActionBar();
        mActionBar.show();
        mActionBar.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.explore_dolby_atmos);
        Typeface dolbyGustanBook = Typeface.createFromAsset (this.getAssets() , "fonts/DolbyGustan-Book.otf");
        Typeface dolbyGustanLight = Typeface.createFromAsset (this.getAssets() , "fonts/DolbyGustan-Light.otf");
        Typeface dolbyGustanMedium = Typeface.createFromAsset (this.getAssets() , "fonts/DolbyGustan-Medium.otf");

        //txtuplogo
        TextView txtUpName  = (TextView) findViewById(R.id.txtupdolbyname);
        txtUpName.setTypeface(dolbyGustanLight);
        //txtupremark
        TextView txtupremark  = (TextView) findViewById(R.id.txtupremark);
        txtupremark.setTypeface(dolbyGustanBook);
        //txtuplearnmore
         TextView txtuplearnmore  = (TextView) findViewById(R.id.txtuplearnmore);
         txtuplearnmore.setTypeface(dolbyGustanMedium);
         //txtdownname
        TextView txtdownname  = (TextView)  findViewById(R.id.txtdownname);
        txtdownname.setTypeface(dolbyGustanLight);
        //txtdownremark
        TextView txtdownremark  = (TextView) findViewById(R.id.txtdownremark);
        txtdownremark.setTypeface(dolbyGustanBook);
        txtdownremark.setAlpha(0);
        //txtdownaccess
        TextView txtdownaccess  = (TextView) findViewById(R.id.txtdownaccess);
        txtdownaccess.setTypeface(dolbyGustanMedium);

        LinearLayout exploreatmoslearnmore = (LinearLayout) findViewById(R.id.exploreatmoslearnmore);

        LinearLayout exploreatmosaccess = (LinearLayout) findViewById(R.id.exploreatmosaccess);
        exploreatmosaccess.setAlpha(0);
        //upimg
        ImageView upimg  = (ImageView)  findViewById(R.id.upimg);
        Bitmap upimgBm = BitmapFactory.decodeResource(this.getResources(), R.drawable.bg_dolby_atmos);
        //downimg
        ImageView downimg  = (ImageView)  findViewById(R.id.downimg);

        //adapt different screen start
//    	Bitmap downimgBm = BitmapFactory.decodeResource(this.getResources(), R.drawable.bg_dolby_app);
//    	FrameLayout.LayoutParams downimgLP = new FrameLayout.LayoutParams(downimgBm.getWidth(), downimgBm.getHeight());
//    	downimgLP.setMargins(0, upimgBm.getHeight(),0, 0);
//    	downimg.setLayoutParams(downimgLP);
//    	mUpimgHeight = upimgBm.getHeight();
//    	mDownimgHeight = downimgBm.getHeight();
        //adapt different screen end
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        float scaleNumber = metric.density;
        if(scaleNumber == 0) {
            scaleNumber = 1;
        } else if(scaleNumber >= 1.3f && scaleNumber <=1.5f) {
            scaleNumber = 1.5f;
        } else if (scaleNumber >= 1.0f && scaleNumber <=1.3f) {
            scaleNumber = 1.0f;
        }
        mUpimgHeight = (int)(mImgInitHeight * scaleNumber);
        mDownimgHeight = (int)(mImgInitHeight * scaleNumber);
        mExploreAtmosLogoY = (int)(mExploreAtmosLogoY * scaleNumber);
        mExperienceDolbyY = (int)(mExperienceDolbyY * scaleNumber);

//    	int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
//        int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
//        txtdownname.measure(w, h);
//        int width =txtdownname.getMeasuredWidth();
//        int height =txtdownname.getMeasuredHeight();
//        FrameLayout.LayoutParams downNameLP = new FrameLayout.LayoutParams(width, height);
//    	downNameLP.setMargins((int)(upimgBm.getWidth() / metric.density * scaleNumber) -  width - (int)(80 * scaleNumber), mExperienceDolbyY, 0,  0);
//    	txtdownname.setLayoutParams(downNameLP);
//
//    	//upimg
//    	FrameLayout.LayoutParams upimgLP = new FrameLayout.LayoutParams((int)(upimgBm.getWidth() / metric.density * scaleNumber), mUpimgHeight);
//    	upimgLP.setMargins(0, 0, 0, mUpimgHeight);
//    	upimg.setLayoutParams(upimgLP);
//
//    	Bitmap downimgBm = BitmapFactory.decodeResource(this.getResources(), R.drawable.bg_dolby_app);
//    	FrameLayout.LayoutParams downimgLP = new FrameLayout.LayoutParams((int)(downimgBm.getWidth() / metric.density * scaleNumber), mUpimgHeight);
//    	downimgLP.setMargins(0, mUpimgHeight,0, 0);
//    	downimg.setLayoutParams(downimgLP);
//
        //get the framearea height
        Rect outRect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
        mScreenHeight = outRect.bottom - outRect.top - screenTopD;

        Bitmap btnControlBm = BitmapFactory.decodeResource(this.getResources(), R.drawable.btn_scrollup);
        mBtnControlInitY = mUpimgHeight - btnControlBm.getHeight()/2;
        FrameLayout.LayoutParams btnControlLP = new FrameLayout.LayoutParams(btnControlBm.getWidth(), btnControlBm.getHeight());
        ImageView btncontrolImg  = (ImageView) findViewById(R.id.btncontrolImg);
        FrameLayout.LayoutParams btnControlOldLP = (FrameLayout.LayoutParams) btncontrolImg.getLayoutParams();
        btnControlLP.setMargins((int)(outRect.right - btnControlOldLP.rightMargin - btnControlBm.getWidth()),
                mBtnControlInitY,
                outRect.right - (int)(btnControlOldLP.rightMargin) ,
                (int)(mBtnControlInitY + btnControlBm.getHeight()));
        btnControlLP.setMarginStart((int)(outRect.right - btnControlOldLP.rightMargin - btnControlBm.getWidth()));
        btncontrolImg.setLayoutParams(btnControlLP);


        ImageView btnupgotodolbyatmos  = (ImageView) findViewById(R.id.btnupgotodolbyatmos);
        btnupgotodolbyatmos.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mHavedOpened = true;
                if(mUpImgAtTop == false) {
                     Uri uri = Uri.parse("http://www.dolby.com/us/en/technologies/dolby-atmos.html");
                     Intent it = new Intent(Intent.ACTION_VIEW, uri);
                     startActivity(it);
                 }

            }
        });

        ImageView btndowngotoaccess  = (ImageView) findViewById(R.id.btndowngotoaccess);
        btndowngotoaccess.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mHavedOpened = true;
                    if(mUpImgAtTop == true) {
                         Uri uri = Uri.parse("http://www.dolby.com/androidapps");
                         Intent it = new Intent(Intent.ACTION_VIEW, uri);
                         startActivity(it);
                    }

                }
            });
        //set the control button onclick event


        btncontrolImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnControlInitY = mUpimgHeight - v.getHeight()/2;

                if(mUpImgAtTop == false) {
                    controlBtnControlImg(0, 1, -1);
                    controlUpimgAnimate(0, 1, -1); // to down
                    controlDownimgAnimate(0, 1, -1);
                    controlExploreAtmosLogo(0, 1, -1);
                    controlExperienceDolby(0, 1, -1);
                    controlTxtUpRemark(0, 1, -1);
                    controlTxtDownRemark(0, 1, -1);
                    controlLearnMore(0, 1, -1);
                    controlExploreAtmosAccess(0, 1, -1);
                } else {
                    controlBtnControlImg(0, 1, 1);
                    controlUpimgAnimate(0, 1, 1); // to down
                    controlDownimgAnimate(0, 1, 1);
                    controlExploreAtmosLogo(0, 1, 1);
                    controlExperienceDolby(0, 1, 1);
                    controlTxtUpRemark(0, 1, 1);
                    controlTxtDownRemark(0, 1, 1);
                    controlLearnMore(0, 1, 1);
                    controlExploreAtmosAccess(0, 1, 1);
                }
            }
        });

        //set the upimg  ontouch event
        upimg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ImageView downimg  = (ImageView)  findViewById(R.id.downimg);
                ImageView btncontrolImg  = (ImageView) findViewById(R.id.btncontrolImg);
                mBtnControlInitY = mUpimgHeight - btncontrolImg.getHeight()/2;
                LinearLayout exploreatmoslogo  = (LinearLayout) findViewById(R.id.exploreatmoslogo);
                int exploreAtmosMaxDistance = (int)((mUpimgHeight - (mScreenHeight - mDownimgHeight)) * mExploreAtmosMaxDistanceRatio);
                TextView experienceDolby  = (TextView) findViewById(R.id.txtdownname);
                int experienceDolbyMaxDistance = (int)((mUpimgHeight - (mScreenHeight - mDownimgHeight)) * mExperienceDolbyMaxDistanceRatio);
                TextView txtupremark  = (TextView) findViewById(R.id.txtupremark);
                TextView txtdownremark  = (TextView) findViewById(R.id.txtdownremark);
                LinearLayout exploreatmoslearnmore = (LinearLayout) findViewById(R.id.exploreatmoslearnmore);
                LinearLayout exploreatmosaccess  = (LinearLayout) findViewById(R.id.exploreatmosaccess);

                // TODO Auto-generated method stub
                switch (event.getAction()) {
                  case MotionEvent.ACTION_DOWN:
                      mUpimgTouchDownY = (int) event.getRawY();
                      mUpimgOldTouhDownY = (int) event.getRawY();
                      ////////System.out.println("Touch Down:" + String.valueOf(mUpimgOldTouhDownY));
                      break;
                  case MotionEvent.ACTION_MOVE:
                      int dy = (int) event.getRawY() - mUpimgTouchDownY;
                      ////////System.out.println("Touch Move:" + String.valueOf((int) event.getRawY()));
                      ////////System.out.println("Touch Move bottom1:" + String.valueOf(v.getBottom()));
                      //up
                      if(dy < 0) {
                          if(v.getBottom() >= (mScreenHeight - mDownimgHeight) && v.getBottom() + dy  >= (mScreenHeight - mDownimgHeight)) {
                              v.layout(v.getLeft(), v.getTop() + dy, v.getRight(), v.getBottom() + dy);
                              downimg.layout(downimg.getLeft(), downimg.getTop() + dy, downimg.getRight(), downimg.getBottom() + dy);
                              btncontrolImg.layout(btncontrolImg.getLeft(), btncontrolImg.getTop() + dy, btncontrolImg.getRight(), btncontrolImg.getBottom() + dy);
                              if(exploreatmoslogo.getTop() + (int)Math.rint(dy * mExploreAtmosMaxDistanceRatio)>=mExploreAtmosLogoY -exploreAtmosMaxDistance) {
                                  exploreatmoslogo.layout(exploreatmoslogo.getLeft(),
                                          exploreatmoslogo.getTop() + (int)Math.rint(dy * mExploreAtmosMaxDistanceRatio),
                                          exploreatmoslogo.getRight(), exploreatmoslogo.getBottom() + (int)Math.rint(dy * mExploreAtmosMaxDistanceRatio));
                              } else {
                                  int halfDistance = (exploreatmoslogo.getTop() - (mExploreAtmosLogoY -exploreAtmosMaxDistance)) / 2;
                                  exploreatmoslogo.layout(exploreatmoslogo.getLeft(),
                                          mExploreAtmosLogoY -exploreAtmosMaxDistance + halfDistance,
                                          exploreatmoslogo.getRight(), mExploreAtmosLogoY -exploreAtmosMaxDistance + halfDistance + exploreatmoslogo.getHeight());
                              }

                              if(experienceDolby.getTop() + (int)Math.rint(dy * mExperienceDolbyMaxDistanceRatio)>=mExperienceDolbyY -experienceDolbyMaxDistance) {
                                  experienceDolby.layout(experienceDolby.getLeft(),
                                          experienceDolby.getTop() + (int)Math.rint(dy * mExperienceDolbyMaxDistanceRatio),
                                          experienceDolby.getRight(), experienceDolby.getBottom() + (int)Math.rint(dy * mExperienceDolbyMaxDistanceRatio));
                              } else {
                                  int halfDistance = (experienceDolby.getTop() - (mExperienceDolbyY -experienceDolbyMaxDistance)) / 2; // avoid jumping
                                  experienceDolby.layout(experienceDolby.getLeft(),
                                          mExperienceDolbyY -experienceDolbyMaxDistance + halfDistance,
                                          experienceDolby.getRight(), mExperienceDolbyY -experienceDolbyMaxDistance + halfDistance + experienceDolby.getHeight());
                              }

                              BigDecimal childNum = new BigDecimal((int) event.getRawY() - mUpimgOldTouhDownY);
                              BigDecimal parentNum = new BigDecimal(mUpimgHeight-(mScreenHeight - mDownimgHeight));
                              float percent = childNum.divide(parentNum, 10, BigDecimal.ROUND_HALF_UP).floatValue();
                              //////System.out.println("Touch up top:" + String.valueOf(v.getTop()));
                              //////System.out.println("dy:" + String.valueOf(dy));
                              //////System.out.println("dy:" + String.valueOf(percent));
                              if(mUpImgAtTop == true) {
                                  if(percent <= 0 && dy < 0){ //from up to down then up and extend init touch point
                                      txtupremark.setAlpha(0f);
                                      txtdownremark.setAlpha(1f);
                                      exploreatmoslearnmore.setAlpha(0f);
                                      exploreatmosaccess.setAlpha(1f);
                                  } else if(percent > 0 && dy < 0) { //from up to down then uptxtuplearnmore and not extend init touch point
                                      txtupremark.setAlpha(percent);
                                      txtdownremark.setAlpha(1-percent);
                                      exploreatmoslearnmore.setAlpha(percent);
                                      exploreatmosaccess.setAlpha(1 - percent);
                                  }
                              } else {
                                  if(percent < 0 && dy < 0){
                                      txtupremark.setAlpha(1+percent);
                                      txtdownremark.setAlpha(-percent);
                                      exploreatmoslearnmore.setAlpha(1 + percent);
                                      exploreatmosaccess.setAlpha(-percent);
                                  } else if(percent > 0 && dy < 0) {
                                      txtupremark.setAlpha(1f);
                                      txtdownremark.setAlpha(0f);
                                      exploreatmoslearnmore.setAlpha(1f);
                                      exploreatmosaccess.setAlpha(0f);
                                  }

                              }
                              mUpimgTouchDownY = (int) event.getRawY();
                          } else {
                              if(v.getBottom() >= (mScreenHeight - mDownimgHeight) && v.getBottom() + dy  < (mScreenHeight - mDownimgHeight)) {
                                  for(int i = v.getBottom(); i>=mScreenHeight - mDownimgHeight; i--) { // upimg jump to top
                                      v.layout(v.getLeft(), i - v.getHeight(), v.getRight(), i);
                                  }
                                  //////System.out.println("Touch up top2:" + String.valueOf(v.getTop()));
                                  downimg.layout(downimg.getLeft(), mScreenHeight - mDownimgHeight, downimg.getRight(), mScreenHeight);
                                  btncontrolImg.layout(btncontrolImg.getLeft(), mBtnControlInitY -( mUpimgHeight - (mScreenHeight - mDownimgHeight)),
                                          btncontrolImg.getRight(),
                                          btncontrolImg.getHeight() + mBtnControlInitY -( mUpimgHeight - (mScreenHeight - mDownimgHeight)));


                                  exploreatmoslogo.layout(exploreatmoslogo.getLeft(),
                                          mExploreAtmosLogoY -exploreAtmosMaxDistance,
                                          exploreatmoslogo.getRight(),
                                          exploreatmoslogo.getHeight() + mExploreAtmosLogoY -exploreAtmosMaxDistance);

                                  experienceDolby.layout(experienceDolby.getLeft(),
                                          mExperienceDolbyY -experienceDolbyMaxDistance,
                                          experienceDolby.getRight(),
                                          experienceDolby.getHeight() + mExperienceDolbyY -experienceDolbyMaxDistance);

                                  txtupremark.setAlpha(0f);
                                  txtdownremark.setAlpha(1f);
                                  exploreatmoslearnmore.setAlpha(0f);
                                  exploreatmosaccess.setAlpha(1f);
                                  mUpimgTouchDownY = (int) event.getRawY();
                              }

                          }
                      } else {
                          //down
                          if(v.getBottom() <= mUpimgHeight && v.getBottom() + dy  <= mUpimgHeight){
                              v.layout(v.getLeft(), v.getTop() + dy, v.getRight(), v.getBottom() + dy);
                              downimg.layout(downimg.getLeft(), downimg.getTop() + dy, downimg.getRight(), downimg.getBottom() + dy);
                              btncontrolImg.layout(btncontrolImg.getLeft(), btncontrolImg.getTop() + dy, btncontrolImg.getRight(), btncontrolImg.getBottom() + dy);


                              if(exploreatmoslogo.getTop() + (int)Math.rint(dy * mExploreAtmosMaxDistanceRatio)<=mExploreAtmosLogoY) {
                                  exploreatmoslogo.layout(exploreatmoslogo.getLeft(),
                                          exploreatmoslogo.getTop() + (int)Math.rint((dy * mExploreAtmosMaxDistanceRatio)),
                                          exploreatmoslogo.getRight(),
                                          exploreatmoslogo.getBottom() + (int)Math.rint(dy * mExploreAtmosMaxDistanceRatio));
                              } else {
                                  int halfDistance = (mExploreAtmosLogoY - exploreatmoslogo.getTop()) / 2;
                                  exploreatmoslogo.layout(exploreatmoslogo.getLeft(),
                                          mExploreAtmosLogoY - halfDistance,
                                          exploreatmoslogo.getRight(), mExploreAtmosLogoY -halfDistance + exploreatmoslogo.getHeight());
                              }

                              if(experienceDolby.getTop() + (int)Math.rint(dy * mExperienceDolbyMaxDistanceRatio)<=mExperienceDolbyY) {
                                  experienceDolby.layout(experienceDolby.getLeft(),
                                          experienceDolby.getTop() + (int)Math.rint((dy * mExperienceDolbyMaxDistanceRatio)),
                                          experienceDolby.getRight(),
                                          experienceDolby.getBottom() + (int)Math.rint(dy * mExperienceDolbyMaxDistanceRatio));
                              } else {
                                  int halfDistance = (mExperienceDolbyY - experienceDolby.getTop()) / 2;
                                  experienceDolby.layout(experienceDolby.getLeft(),
                                          mExperienceDolbyY - halfDistance,
                                          experienceDolby.getRight(), mExperienceDolbyY -halfDistance + experienceDolby.getHeight());
                              }

                              BigDecimal childNum = new BigDecimal((int) event.getRawY() - mUpimgOldTouhDownY);
                              BigDecimal parentNum = new BigDecimal(mUpimgHeight-(mScreenHeight - mDownimgHeight));
                              float percent = childNum.divide(parentNum, 10, BigDecimal.ROUND_HALF_UP).floatValue();
                              //System.out.println("Touch down percent:" + String.valueOf(percent));
                              //System.out.println("dy:" + String.valueOf(dy));
                              //System.out.println("mUpImgAtTop:" + String.valueOf(mUpImgAtTop));
                              if(mUpImgAtTop == true) {
                                  if(percent <= 0 && dy > 0){ //from up to down then up and not extend init touch point
                                      txtupremark.setAlpha(0f);
                                      txtdownremark.setAlpha(1f);
                                      exploreatmoslearnmore.setAlpha(0f);
                                      exploreatmosaccess.setAlpha(1f);
                                  } else if(percent > 0 && dy > 0) { //from up to down then up and not extend init touch point
                                      txtupremark.setAlpha(percent);
                                      txtdownremark.setAlpha(1-percent);
                                      exploreatmoslearnmore.setAlpha(percent);
                                      exploreatmosaccess.setAlpha(1 - percent);
                                  }
                              } else {
                                  if(percent < 0 && dy > 0){
                                      txtupremark.setAlpha(1+percent);
                                      txtdownremark.setAlpha(-percent);
                                      exploreatmoslearnmore.setAlpha(1 + percent);
                                      exploreatmosaccess.setAlpha(-percent);
                                  } else if(percent > 0 && dy > 0) {
                                      txtupremark.setAlpha(1f);
                                      txtdownremark.setAlpha(0f);
                                      exploreatmoslearnmore.setAlpha(1f);
                                      exploreatmosaccess.setAlpha(0f);
                                  }

                              }

                              mUpimgTouchDownY = (int) event.getRawY();
                          } else {
                              if(v.getBottom() <= mUpimgHeight && v.getBottom() + dy  > mUpimgHeight) {
                                  v.layout(v.getLeft(), 0, v.getRight(), mUpimgHeight);
                                  downimg.layout(downimg.getLeft(), mUpimgHeight, downimg.getRight(), mUpimgHeight + mDownimgHeight);
                                  btncontrolImg.layout(btncontrolImg.getLeft(), mBtnControlInitY,
                                          btncontrolImg.getRight(),
                                          btncontrolImg.getHeight() + mBtnControlInitY);
                                  exploreatmoslogo.layout(exploreatmoslogo.getLeft(),
                                          mExploreAtmosLogoY,
                                          exploreatmoslogo.getRight(),
                                          mExploreAtmosLogoY + exploreatmoslogo.getHeight());
                                  experienceDolby.layout(experienceDolby.getLeft(),
                                          mExperienceDolbyY,
                                          experienceDolby.getRight(),
                                          mExperienceDolbyY + experienceDolby.getHeight());
                                  txtupremark.setAlpha(1f);
                                  txtdownremark.setAlpha(0f);
                                  exploreatmoslearnmore.setAlpha(1f);
                                  exploreatmosaccess.setAlpha(0f);
                                  //////System.out.println("Touch down top2:" + String.valueOf(v.getTop()));
                                  mUpimgTouchDownY = (int) event.getRawY();

                              }
                          }
                      }
                      break;
                  case MotionEvent.ACTION_UP:
                      int touchUpDy = (int) event.getRawY() - mUpimgOldTouhDownY;
                      //System.out.println("Touch touchUpDy1:" + String.valueOf(touchUpDy));
                      if(touchUpDy > 0) {  // from up to down
                          if(mUpImgAtTop == true) {
                              touchUpDy = mUpimgHeight-(mScreenHeight - mDownimgHeight) + v.getTop() ;
                          } else {
                              touchUpDy = 0 - v.getTop();
                          }
                      } else {
                          if(mUpImgAtTop == true) {
                              touchUpDy = -(mUpimgHeight-(mScreenHeight - mDownimgHeight)) - v.getTop() ;
                          } else {
                              touchUpDy = v.getTop() - 0;
                          }

                      }

                      //////System.out.println("Touch touchUpDy:" + String.valueOf(touchUpDy));
                      if(touchUpDy != 0 &&(v.getTop() == 0 || v.getTop() == -(mUpimgHeight-(mScreenHeight - mDownimgHeight)))) {
                          touchUpDy = 0;
                      }
                      ////////System.out.println("Touch Up:" + String.valueOf((int) event.getRawY()));
                      BigDecimal childNum = new BigDecimal(touchUpDy);
                      BigDecimal parentNum = new BigDecimal(mUpimgHeight-(mScreenHeight - mDownimgHeight));
                      float percent = childNum.divide(parentNum, 10, BigDecimal.ROUND_HALF_UP).floatValue();
                      if(percent > 1.0) {
                          percent = 1.0f;
                      }
                      if(percent < -1.0f) {
                          percent = -1.0f;
                      }
                      if(touchUpDy > 0) { //the finger from up to down
                          ////////System.out.println("Touch Up: down");
                          if(mUpImgAtTop == true) {
                              if(touchUpDy < mRollbackDisance) {
                                  controlUpimgAnimate(percent, 0, 1); // rollback to top
                                  controlDownimgAnimate(percent, 0, 1);
                                  controlBtnControlImg(percent, 0, 1);
                                  controlExploreAtmosLogo(percent, 0, 1);
                                  controlExperienceDolby(percent, 0, 1);
                                  controlTxtUpRemark(percent, 0, 1);
                                  controlTxtDownRemark(percent, 0, 1);
                                  controlLearnMore(percent, 0, 1);
                                  controlExploreAtmosAccess(percent, 0, 1);
                              }  else {
                                  controlUpimgAnimate(percent, 1, 1); // to down
                                  controlDownimgAnimate(percent, 1, 1);
                                  controlBtnControlImg(percent, 1, 1);
                                  controlExploreAtmosLogo(percent, 1, 1);
                                  controlExperienceDolby(percent, 1, 1);
                                  controlTxtUpRemark(percent, 1, 1);
                                  controlTxtDownRemark(percent, 1, 1);
                                  controlLearnMore(percent, 1, 1);
                                  controlExploreAtmosAccess(percent, 1, 1);
                              }
                          } else {
                              controlUpimgAnimate(0, 1, 1); //mutil point touch
                              controlDownimgAnimate(0, 1, 1);
                              controlBtnControlImg(0, 1, 1);
                              controlExploreAtmosLogo(0, 1, 1);
                              controlExperienceDolby(0, 1, 1);
                              controlTxtUpRemark(0, 1, 1);
                              controlTxtDownRemark(0, 1, 1);
                              controlLearnMore(0, 1, 1);
                              controlExploreAtmosAccess(0, 1, 1);
                          }
                      } else if(touchUpDy == 0) {
                          //only click once
                          ////////System.out.println("Touch Up: 0");
                          if(mUpImgAtTop == true) {
                              if(v.getTop() == 0) {
                                  controlUpimgAnimate(1, 1, 1); // to down
                                  controlDownimgAnimate(1, 1, 1);
                                  controlBtnControlImg(1, 1, 1);
                                  controlExploreAtmosLogo(1, 1, 1);
                                  controlExperienceDolby(1, 1, 1);
                                  controlTxtUpRemark(1, 1, 1);
                                  controlTxtDownRemark(1, 1, 1);
                                  controlLearnMore(1, 1, 1);
                                  controlExploreAtmosAccess(1, 1, 1);
                              } else {
                                  controlUpimgAnimate(0, 1, 1); // to down
                                  controlDownimgAnimate(0, 1, 1);
                                  controlBtnControlImg(0, 1, 1);
                                  controlExploreAtmosLogo(0, 1, 1);
                                  controlExperienceDolby(0, 1, 1);
                                  controlTxtUpRemark(0, 1, 1);
                                  controlTxtDownRemark(0, 1, 1);
                                  controlLearnMore(0, 1, 1);
                                  controlExploreAtmosAccess(0, 1, 1);
                              }
                          } else {
                              if(v.getTop() == -(mUpimgHeight-(mScreenHeight - mDownimgHeight))) {
                                  controlBtnControlImg(1, 1, -1); //only rotate 180
                                  mUpImgAtTop = true;
                              }
                          }
                      } else {  // the finger from down to up
                          ////////System.out.println("Touch Up: up");
                          if(mUpImgAtTop == false) {
                              if(touchUpDy > -mRollbackDisance) {
                                  controlUpimgAnimate(-percent, 0, -1);  //rollback to down
                                  controlDownimgAnimate(-percent, 0, -1);
                                  controlBtnControlImg(-percent, 0, -1);
                                  controlExploreAtmosLogo(-percent, 0, -1);
                                  controlExperienceDolby(-percent, 0, -1);
                                  controlTxtUpRemark(-percent, 0, -1);
                                  controlTxtDownRemark(-percent, 0, -1);
                                  controlLearnMore(-percent, 0, -1);
                                  controlExploreAtmosAccess(-percent, 0, -1);

                              }  else {
                                  controlUpimgAnimate(-percent, 1, -1); //to up
                                  controlDownimgAnimate(-percent, 1, -1);
                                  controlBtnControlImg(-percent, 1, -1);
                                  controlExploreAtmosLogo(-percent, 1, -1);
                                  controlExperienceDolby(-percent, 1, -1);
                                  controlTxtUpRemark(-percent, 1, -1);
                                  controlTxtDownRemark(-percent,1, -1);
                                  controlLearnMore(-percent, 1, -1);
                                  controlExploreAtmosAccess(-percent, 1, -1);
                              }
                          } else {
                              controlUpimgAnimate(0, 0, 1); //mutil point touch
                              controlDownimgAnimate(0, 0, 1);
                              controlBtnControlImg(0, 0, 1);
                              controlExploreAtmosLogo(0, 0, 1);
                              controlExperienceDolby(0, 0, 1);
                              controlTxtUpRemark(0, 0, 1);
                              controlTxtDownRemark(0, 0, 1);
                              controlLearnMore(0, 0, 1);
                              controlExploreAtmosAccess(0, 0, 1);
                          }

                      }

                      break;
                  }
                return true;
            }
        });
        //set the downimg  ontouch event

        downimg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ImageView upimg  = (ImageView)  findViewById(R.id.upimg);
                ImageView btncontrolImg  = (ImageView) findViewById(R.id.btncontrolImg);
                mBtnControlInitY = mUpimgHeight - btncontrolImg.getHeight()/2;
                LinearLayout exploreatmoslogo  = (LinearLayout) findViewById(R.id.exploreatmoslogo);
                int exploreAtmosMaxDistance = (int)((mUpimgHeight - (mScreenHeight - mDownimgHeight)) * mExploreAtmosMaxDistanceRatio);
                TextView experienceDolby  = (TextView) findViewById(R.id.txtdownname);
                int experienceDolbyMaxDistance = (int)((mUpimgHeight - (mScreenHeight - mDownimgHeight)) * mExperienceDolbyMaxDistanceRatio);
                TextView txtupremark  = (TextView) findViewById(R.id.txtupremark);
                TextView txtdownremark  = (TextView) findViewById(R.id.txtdownremark);
                LinearLayout exploreatmoslearnmore = (LinearLayout) findViewById(R.id.exploreatmoslearnmore);
                LinearLayout exploreatmosaccess  = (LinearLayout) findViewById(R.id.exploreatmosaccess);
                // TODO Auto-generated method stub
                switch (event.getAction()) {
                  case MotionEvent.ACTION_DOWN:
                      mDownimgTouchDownY = (int) event.getRawY();
                      mDownimgOldTouhDownY = (int) event.getRawY();
                      ////////System.out.println("Touch Down:" + String.valueOf(mUpimgOldTouhDownY));
                      break;
                  case MotionEvent.ACTION_MOVE:
                      int dy = (int) event.getRawY() - mDownimgTouchDownY;
                      ////System.out.println("Touch Move:" + String.valueOf((int) event.getRawY()));
                      ////System.out.println("Touch Move bottom1:" + String.valueOf(v.getBottom()));
                      //up
                      //System.out.println("Touch move dy:" + String.valueOf(dy));
                      //System.out.println("Touch mUpImgAtTop dy:" + String.valueOf(mUpImgAtTop));

                      if(dy < 0) {
                          if(v.getTop() >= (mScreenHeight - mDownimgHeight) && v.getTop() + dy  >= (mScreenHeight - mDownimgHeight)) {
                              v.layout(v.getLeft(), v.getTop() + dy, v.getRight(), v.getBottom() + dy);
                              upimg.layout(upimg.getLeft(), upimg.getTop() + dy, upimg.getRight(), upimg.getBottom() + dy);
                              btncontrolImg.layout(btncontrolImg.getLeft(), btncontrolImg.getTop() + dy, btncontrolImg.getRight(), btncontrolImg.getBottom() + dy);

                              if(exploreatmoslogo.getTop() + (int)Math.rint(dy * mExploreAtmosMaxDistanceRatio)<=mExploreAtmosLogoY) {
                                  exploreatmoslogo.layout(exploreatmoslogo.getLeft(),
                                          exploreatmoslogo.getTop() + (int)Math.rint((dy * mExploreAtmosMaxDistanceRatio)),
                                          exploreatmoslogo.getRight(),
                                          exploreatmoslogo.getBottom() + (int)Math.rint(dy * mExploreAtmosMaxDistanceRatio));
                              } else {
                                  int halfDistance =  (exploreatmoslogo.getTop() - (mExploreAtmosLogoY - exploreAtmosMaxDistance))/2;
                                  exploreatmoslogo.layout(exploreatmoslogo.getLeft(),
                                          mExploreAtmosLogoY - exploreAtmosMaxDistance + halfDistance,
                                          exploreatmoslogo.getRight(), mExploreAtmosLogoY - exploreAtmosMaxDistance + halfDistance + exploreatmoslogo.getHeight());
                              }

                              if(experienceDolby.getTop() + (int)Math.rint(dy * mExperienceDolbyMaxDistanceRatio)<=mExperienceDolbyY) {
                                  experienceDolby.layout(experienceDolby.getLeft(),
                                          experienceDolby.getTop() + (int)Math.rint((dy * mExperienceDolbyMaxDistanceRatio)),
                                          experienceDolby.getRight(),
                                          experienceDolby.getBottom() + (int)Math.rint(dy * mExperienceDolbyMaxDistanceRatio));
                              } else {
                                  int halfDistance =  (experienceDolby.getTop() - (mExperienceDolbyY - experienceDolbyMaxDistance))/2;
                                  experienceDolby.layout(experienceDolby.getLeft(),
                                          mExperienceDolbyY - experienceDolbyMaxDistance + halfDistance,
                                          experienceDolby.getRight(), mExperienceDolbyY - experienceDolbyMaxDistance + halfDistance + experienceDolby.getHeight());
                              }

                              BigDecimal childNum = new BigDecimal((int) event.getRawY() - mDownimgOldTouhDownY);
                              BigDecimal parentNum = new BigDecimal(mUpimgHeight-(mScreenHeight - mDownimgHeight));
                              float percent = childNum.divide(parentNum, 10, BigDecimal.ROUND_HALF_UP).floatValue();
                              ////System.out.println("Touch move percent:" + String.valueOf(percent));
                              ////System.out.println("Touch move dy:" + String.valueOf(dy));

                              if(mUpImgAtTop == true) {
                                  if(percent <= 0 && dy < 0){ //from up to down then up and extend init touch point
                                      txtupremark.setAlpha(0f);
                                      txtdownremark.setAlpha(1f);
                                      exploreatmoslearnmore.setAlpha(0f);
                                      exploreatmosaccess.setAlpha(1f);
                                  } else if(percent > 0 && dy < 0) { //from up to down then up and not extend init touch point
                                      txtupremark.setAlpha(percent);
                                      txtdownremark.setAlpha(1-percent);
                                      exploreatmoslearnmore.setAlpha(percent);
                                      exploreatmosaccess.setAlpha(1 - percent);
                                  }
                              } else {
                                  if(percent < 0 && dy < 0){
                                      txtupremark.setAlpha(1+percent);
                                      txtdownremark.setAlpha(-percent);
                                      exploreatmoslearnmore.setAlpha(1+percent);
                                      exploreatmosaccess.setAlpha(-percent);
                                  } else if(percent > 0 && dy < 0) {
                                      txtupremark.setAlpha(1f);
                                      txtdownremark.setAlpha(0f);
                                      exploreatmoslearnmore.setAlpha(1f);
                                      exploreatmosaccess.setAlpha(0f);
                                  }

                              }
                              mDownimgTouchDownY = (int) event.getRawY();
                          } else {
                              if(v.getTop() >= (mScreenHeight - mDownimgHeight) && v.getTop() + dy  < (mScreenHeight - mDownimgHeight)) {
                                  v.layout(v.getLeft(), mScreenHeight - mDownimgHeight , v.getRight(), mScreenHeight);
                                  upimg.layout(upimg.getLeft(), -(mUpimgHeight-(mScreenHeight - mDownimgHeight)), upimg.getRight(), mScreenHeight - mDownimgHeight);
                                  btncontrolImg.layout(btncontrolImg.getLeft(), mBtnControlInitY -( mUpimgHeight - (mScreenHeight - mDownimgHeight)),
                                          btncontrolImg.getRight(),
                                          btncontrolImg.getHeight() + mBtnControlInitY -( mUpimgHeight - (mScreenHeight - mDownimgHeight)));
                                  exploreatmoslogo.layout(exploreatmoslogo.getLeft(),
                                          mExploreAtmosLogoY - exploreAtmosMaxDistance,
                                          exploreatmoslogo.getRight(),
                                          exploreatmoslogo.getHeight() + mExploreAtmosLogoY - exploreAtmosMaxDistance);
                                  experienceDolby.layout(experienceDolby.getLeft(),
                                          mExperienceDolbyY - experienceDolbyMaxDistance,
                                          experienceDolby.getRight(),
                                          experienceDolby.getHeight() + mExperienceDolbyY - experienceDolbyMaxDistance);
                                  txtupremark.setAlpha(0f);
                                  txtdownremark.setAlpha(1f);
                                  exploreatmoslearnmore.setAlpha(0f);
                                  exploreatmosaccess.setAlpha(1f);
                                  mDownimgTouchDownY = (int) event.getRawY();
                                  ////System.out.println("Touch move 1:" + String.valueOf(1));
                              }

                          }
                      } else {
                          //down
                          if(v.getTop() <=  mDownimgHeight && v.getTop() + dy  <= mDownimgHeight){
                              v.layout(v.getLeft(), v.getTop() + dy, v.getRight(), v.getBottom() + dy);
                              upimg.layout(upimg.getLeft(), upimg.getTop() + dy, upimg.getRight(), upimg.getBottom() + dy);
                              btncontrolImg.layout(btncontrolImg.getLeft(), btncontrolImg.getTop() + dy, btncontrolImg.getRight(), btncontrolImg.getBottom() + dy);

                              if(exploreatmoslogo.getTop() + (int)Math.rint(dy * mExploreAtmosMaxDistanceRatio)<=mExploreAtmosLogoY) {
                                  exploreatmoslogo.layout(exploreatmoslogo.getLeft(),
                                          exploreatmoslogo.getTop() + (int)Math.rint((dy * mExploreAtmosMaxDistanceRatio)),
                                          exploreatmoslogo.getRight(),
                                          exploreatmoslogo.getBottom() + (int)Math.rint(dy * mExploreAtmosMaxDistanceRatio));
                              } else {
                                  int halfDistance = (mExploreAtmosLogoY - exploreatmoslogo.getTop()) / 2;
                                  exploreatmoslogo.layout(exploreatmoslogo.getLeft(),
                                          mExploreAtmosLogoY - halfDistance,
                                          exploreatmoslogo.getRight(), mExploreAtmosLogoY -halfDistance + exploreatmoslogo.getHeight());
                              }

                              if(experienceDolby.getTop() + (int)Math.rint(dy * mExperienceDolbyMaxDistanceRatio)<=mExperienceDolbyY) {
                                  experienceDolby.layout(experienceDolby.getLeft(),
                                          experienceDolby.getTop() + (int)Math.rint((dy * mExperienceDolbyMaxDistanceRatio)),
                                          experienceDolby.getRight(),
                                          experienceDolby.getBottom() + (int)Math.rint(dy * mExperienceDolbyMaxDistanceRatio));
                              } else {
                                  int halfDistance = (mExperienceDolbyY - experienceDolby.getTop()) / 2;
                                  experienceDolby.layout(experienceDolby.getLeft(),
                                          mExperienceDolbyY - halfDistance,
                                          experienceDolby.getRight(), mExperienceDolbyY -halfDistance + experienceDolby.getHeight());
                              }

                              BigDecimal childNum = new BigDecimal((int) event.getRawY() - mDownimgOldTouhDownY);
                              BigDecimal parentNum = new BigDecimal(mUpimgHeight-(mScreenHeight - mDownimgHeight));
                              float percent = childNum.divide(parentNum, 10, BigDecimal.ROUND_HALF_UP).floatValue();
                              //System.out.println("Touch move percent:" + String.valueOf(percent));
                              //System.out.println("Touch move dy:" + String.valueOf(dy));
                              //System.out.println("Touch mUpImgAtTop dy:" + String.valueOf(mUpImgAtTop));
                              if(mUpImgAtTop == true) {
                                  if(percent <= 0 && dy > 0){ //from up to down then up and not extend init touch point
                                      txtupremark.setAlpha(0f);
                                      txtdownremark.setAlpha(1f);
                                      exploreatmoslearnmore.setAlpha(0f);
                                      exploreatmosaccess.setAlpha(1f);
                                  } else if(percent > 0 && dy > 0) { //from up to down then up and not extend init touch point
                                      txtupremark.setAlpha(percent);
                                      txtdownremark.setAlpha(1-percent);
                                      exploreatmoslearnmore.setAlpha(percent);
                                      exploreatmosaccess.setAlpha(1 - percent);
                                  }
                              } else {
                                  if(percent < 0 && dy > 0){
                                      txtupremark.setAlpha(1+percent);
                                      txtdownremark.setAlpha(-percent);
                                      exploreatmoslearnmore.setAlpha(1 + percent);
                                      exploreatmosaccess.setAlpha(-percent);
                                  } else if(percent > 0 && dy > 0) {
                                      txtupremark.setAlpha(1f);
                                      txtdownremark.setAlpha(0f);
                                      exploreatmoslearnmore.setAlpha(1f);
                                      exploreatmosaccess.setAlpha(0f);
                                  }

                              }
                              mDownimgTouchDownY = (int) event.getRawY();
                          } else {
                              if(v.getTop() <= mDownimgHeight && v.getTop() + dy  > mDownimgHeight) {
                                  v.layout(v.getLeft(), mDownimgHeight, v.getRight(), mDownimgHeight+mDownimgHeight);
                                  upimg.layout(upimg.getLeft(), 0, upimg.getRight(), mUpimgHeight);
                                  btncontrolImg.layout(btncontrolImg.getLeft(), mBtnControlInitY,
                                          btncontrolImg.getRight(),
                                          btncontrolImg.getHeight() + mBtnControlInitY);
                                  exploreatmoslogo.layout(exploreatmoslogo.getLeft(), mExploreAtmosLogoY,
                                          exploreatmoslogo.getRight(),
                                          exploreatmoslogo.getHeight() + mExploreAtmosLogoY);

                                  experienceDolby.layout(experienceDolby.getLeft(), mExperienceDolbyY,
                                          experienceDolby.getRight(),
                                          experienceDolby.getHeight() + mExperienceDolbyY);
                                  mDownimgTouchDownY = (int) event.getRawY();
                                  txtupremark.setAlpha(1f);
                                  txtdownremark.setAlpha(0f);
                                  exploreatmoslearnmore.setAlpha(1f);
                                  exploreatmosaccess.setAlpha(0f);
                                  ////System.out.println("Touch move 0:" + String.valueOf(0));
                              }
                          }
                      }
                      break;
                  case MotionEvent.ACTION_UP:
                      int touchUpDy = (int) event.getRawY() - mDownimgOldTouhDownY;
                      if(touchUpDy > 0) {
                          if(mUpImgAtTop == true) {
                              touchUpDy =  v.getTop() - (mScreenHeight - mUpimgHeight);
                          } else {
                              touchUpDy = mUpimgHeight - v.getTop();
                          }
                      } else {
                          if(mUpImgAtTop == true) {
                              touchUpDy = (mScreenHeight - mUpimgHeight) - v.getTop() ;
                          } else {
                              touchUpDy = v.getTop() - mUpimgHeight;
                          }

                      }
                      ////////System.out.println("Touch Up:" + String.valueOf((int) event.getRawY()));
                      BigDecimal childNum = new BigDecimal(touchUpDy);
                      BigDecimal parentNum = new BigDecimal(mDownimgHeight-(mScreenHeight - mUpimgHeight));
                      float percent = childNum.divide(parentNum, 10, BigDecimal.ROUND_HALF_UP).floatValue();
                      if(percent > 1.0) {
                          percent = 1.0f;
                      }
                      if(percent < -1.0f) {
                          percent = -1.0f;
                      }
                      if(touchUpDy > 0) { //the finger from up to down
                          ////////System.out.println("Touch Up: down");
                          if(mUpImgAtTop == true) {
                              if(touchUpDy < mRollbackDisance) {
                                  controlDownimgAnimate(percent, 0, 1); // rollback to top
                                  controlUpimgAnimate(percent, 0, 1);
                                  controlBtnControlImg(percent, 0, 1);
                                  controlExploreAtmosLogo(percent, 0, 1);
                                  controlExperienceDolby(percent, 0, 1);
                                  controlTxtUpRemark(percent, 0, 1);
                                  controlTxtDownRemark(percent, 0, 1);
                                  controlLearnMore(percent, 0, 1);
                                  controlExploreAtmosAccess(percent, 0, 1);
                              }  else {
                                  controlDownimgAnimate(percent, 1, 1); // to down
                                  controlUpimgAnimate(percent, 1, 1);
                                  controlBtnControlImg(percent, 1, 1);
                                  controlExploreAtmosLogo(percent, 1, 1);
                                  controlExperienceDolby(percent, 1, 1);
                                  controlTxtUpRemark(percent, 1, 1);
                                  controlTxtDownRemark(percent, 1, 1);
                                  controlLearnMore(percent, 1, 1);
                                  controlExploreAtmosAccess(percent, 1, 1);
                              }
                          } else {
                              controlDownimgAnimate(0, 0, -1); //mutil point touch
                              controlUpimgAnimate(0, 0, -1);
                              controlBtnControlImg(0, 0, -1);
                              controlExploreAtmosLogo(0, 0, -1);
                              controlExperienceDolby(0, 0, -1);
                              controlTxtUpRemark(0, 0, -1);
                              controlTxtDownRemark(0, 0, -1);
                              controlLearnMore(0, 0, -1);
                              controlExploreAtmosAccess(0, 0, -1);
                          }
                      } else if(touchUpDy == 0) {
                          //only click once
                          ////////System.out.println("Touch Up: 0");
                          if(mUpImgAtTop == false) {
                              if(v.getTop() == mUpimgHeight) {
                                  controlDownimgAnimate(0, 1, -1); // to top
                                  controlUpimgAnimate(0, 1, -1);
                                  controlBtnControlImg(0, 1, -1);
                                  controlExploreAtmosLogo(0, 1, -1);
                                  controlExperienceDolby(0, 1, -1);
                                  controlTxtUpRemark(0, 1, -1);
                                  controlTxtDownRemark(0, 1, -1);
                                  controlLearnMore(0, 1, -1);
                                  controlExploreAtmosAccess(0, 1, -1);
                              } else {
                                  controlDownimgAnimate(1, 1, -1); // to top
                                  controlUpimgAnimate(1, 1, -1);
                                  controlBtnControlImg(1, 1, -1);
                                  controlExploreAtmosLogo(1, 1, -1);
                                  controlExperienceDolby(1, 1, -1);
                                  controlTxtUpRemark(1, 1, -1);
                                  controlTxtDownRemark(1, 1, -1);
                                  controlLearnMore(1, 1, -1);
                                  controlExploreAtmosAccess(1, 1, -1);
                              }
                          } else {
                              if(v.getTop() == mUpimgHeight) {
                                  mUpImgAtTop = false;
                              }
                          }
                      } else {  // the finger from down to up
                          ////////System.out.println("Touch Up: up");
                          if(mUpImgAtTop == false) {
                              if(touchUpDy > -mRollbackDisance) {
                                  controlDownimgAnimate(-percent, 0, -1);  //rollback to down
                                  controlUpimgAnimate(-percent, 0, -1);
                                  controlBtnControlImg(-percent, 0, -1);
                                  controlExploreAtmosLogo(-percent, 0, -1);
                                  controlExperienceDolby(-percent, 0, -1);
                                  controlTxtUpRemark(-percent, 0, -1);
                                  controlTxtDownRemark(-percent, 0, -1);
                                  controlLearnMore(-percent, 0, -1);
                                  controlExploreAtmosAccess(-percent, 0, -1);
                              }  else {
                                  controlDownimgAnimate(-percent, 1, -1); //to up
                                  controlUpimgAnimate(-percent, 1, -1);
                                  controlBtnControlImg(-percent, 1, -1);
                                  controlExploreAtmosLogo(-percent, 1, -1);
                                  controlExperienceDolby(-percent, 1, -1);
                                  controlTxtUpRemark(-percent, 1, -1);
                                  controlTxtDownRemark(-percent, 1, -1);
                                  controlLearnMore(-percent, 1, -1);
                                  controlExploreAtmosAccess(-percent, 1, -1);
                              }
                          } else {
                              controlUpimgAnimate(0, 0, 1); //mutil point touch
                              controlDownimgAnimate(0, 0, 1);
                              controlBtnControlImg(0, 0, 1);
                              controlExploreAtmosLogo(0, 0, 1);
                              controlExperienceDolby(0, 0, 1);
                              controlTxtUpRemark(0, 0, 1);
                              controlTxtDownRemark(0, 0, 1);
                              controlLearnMore(0, 0, 1);
                              controlExploreAtmosAccess(0, 0, 1);
                          }

                      }

                      break;
                  }
                return true;
            }
        });


        // Related to tooltip display.
//        mNativeRootContainer = ViewTools.determineNativeViewContainer(getActivity());

//        return v;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            return true;

        }
        return true;
    }



    // percent:  current all view status  percent
    // target:   target status value 0(rollback)/1
    // orientation:  1 down  -1 up
    private void controlUpimgAnimate(float percent, int target, int orientation) {
        Log.d(tag, "controlUpimgAnimate");
        System.out.println("controlUpimgAnimate percent:" + String.valueOf(percent) + " target:" + String.valueOf(target) + " orient:" + String.valueOf(orientation));
        ImageView upimg  = (ImageView) findViewById(R.id.upimg);
        //////System.out.println("controlUpimgAnimate top:" + String.valueOf(upimg.getTop()));
        if(orientation == 1) { //down
            if(target == 0) {
                ////////System.out.println("move down 1:" + String.valueOf(-(int)(mUpimgHeight-(mScreenHeight - mDownimgHeight))*percent));
                ValueAnimator animation = ValueAnimator.ofInt((int)(-(mUpimgHeight-(mScreenHeight - mDownimgHeight))*(1-percent)),
                        (int)(-(mUpimgHeight-(mScreenHeight - mDownimgHeight))));
                animation.addUpdateListener(new AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        ////////System.out.println("top move:" + animation.getAnimatedValue().toString());
                        ImageView upimg  = (ImageView) findViewById(R.id.upimg);
                        upimg.layout(upimg.getLeft(), ((Integer)animation.getAnimatedValue()).intValue(),
                                upimg.getRight(), mUpimgHeight + ((Integer)animation.getAnimatedValue()).intValue());


                    }
                });
                animation.setInterpolator(new DecelerateInterpolator(4));
                animation.setDuration(mAnimateDuring);
                animation.start();

                mUpImgAtTop = true;
            } else {
                ////////System.out.println("move down 2:" + String.valueOf((int)(mUpimgHeight-(mScreenHeight - mDownimgHeight))*(1-percent)));
                ValueAnimator animation = ValueAnimator.ofInt((int)(-(mUpimgHeight-(mScreenHeight - mDownimgHeight))*(1-percent)), 0);
                animation.addUpdateListener(new AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        ////////System.out.println("top move:" + animation.getAnimatedValue().toString());
                        ImageView upimg  = (ImageView) findViewById(R.id.upimg);
                        upimg.layout(upimg.getLeft(), ((Integer)animation.getAnimatedValue()).intValue(),
                                upimg.getRight(), mUpimgHeight + ((Integer)animation.getAnimatedValue()).intValue());


                    }
                });
                animation.setInterpolator(new DecelerateInterpolator(4));
                animation.setDuration(mAnimateDuring);
                animation.start();
                mUpImgAtTop = false;
            }
        } else { //up
            if(target == 0) {
                ////////System.out.println("move up 1:" + String.valueOf((int)(mUpimgHeight-(mScreenHeight - mDownimgHeight))*percent));
                ValueAnimator animation = ValueAnimator.ofInt((int)(-(mUpimgHeight-(mScreenHeight - mDownimgHeight))*percent), 0);
                animation.addUpdateListener(new AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        //////System.out.println("top move:" + animation.getAnimatedValue().toString());
                        ImageView upimg  = (ImageView) findViewById(R.id.upimg);
                        upimg.layout(upimg.getLeft(), ((Integer)animation.getAnimatedValue()).intValue(),
                                upimg.getRight(), mUpimgHeight + ((Integer)animation.getAnimatedValue()).intValue());


                    }
                });
                animation.setInterpolator(new DecelerateInterpolator(4));
                animation.setDuration(mAnimateDuring);
                animation.start();
                mUpImgAtTop = false;
            } else {
                ////////System.out.println("move up 2:" + String.valueOf(-(int)(mUpimgHeight-(mScreenHeight - mDownimgHeight))*(1-percent)));
                ValueAnimator animation = ValueAnimator.ofInt((int)(-(mUpimgHeight-(mScreenHeight - mDownimgHeight))*percent),
                        (int)(-(mUpimgHeight-(mScreenHeight - mDownimgHeight))));
                animation.addUpdateListener(new AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        //////System.out.println("top move:" + animation.getAnimatedValue().toString());
                        ImageView upimg  = (ImageView) findViewById(R.id.upimg);
                        upimg.layout(upimg.getLeft(), ((Integer)animation.getAnimatedValue()).intValue(),
                                upimg.getRight(), mUpimgHeight + ((Integer)animation.getAnimatedValue()).intValue());


                    }
                });
                animation.setInterpolator(new DecelerateInterpolator(4));
                animation.setDuration(mAnimateDuring);
                animation.start();
                ////////System.out.println("translationY after:" + String.valueOf(upimg.getTranslationY()));

                mUpImgAtTop = true;
            }
        }
    }

    // percent:  current all view status  percent
    // target:   target status value 0(rollback)/1
    // orientation:  1 down  -1 up
    private void controlDownimgAnimate(float percent, int target, int orientation) {
        //System.out.println("controlDownimgAnimate percent:" + String.valueOf(percent) + " target:" + String.valueOf(target) + " orient:" + String.valueOf(orientation));
        ImageView downimg  = (ImageView) findViewById(R.id.downimg);
        if(orientation == 1) { //down
            if(target == 0) {
                ////////System.out.println("move down 1:" + String.valueOf(-(int)(mUpimgHeight-(mScreenHeight - mDownimgHeight))*percent));
                ValueAnimator animation = ValueAnimator.ofInt((int)((mDownimgHeight-(mScreenHeight - mUpimgHeight))*percent + mScreenHeight - mDownimgHeight),
                        mScreenHeight - mDownimgHeight);
                animation.addUpdateListener(new AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        ////////System.out.println("top move:" + animation.getAnimatedValue().toString());
                        ImageView downimg  = (ImageView) findViewById(R.id.downimg);
                        downimg.layout(downimg.getLeft(), ((Integer)animation.getAnimatedValue()).intValue(),
                                downimg.getRight(), mDownimgHeight + ((Integer)animation.getAnimatedValue()).intValue());


                    }
                });
                animation.setInterpolator(new DecelerateInterpolator(4));
                animation.setDuration(mAnimateDuring);
                animation.start();

                //mUpImgAtTop = true;
            } else {
                ////////System.out.println("move down 2:" + String.valueOf((int)(mUpimgHeight-(mScreenHeight - mDownimgHeight))*(1-percent)));
                ValueAnimator animation = ValueAnimator.ofInt((int)((mDownimgHeight-(mScreenHeight - mUpimgHeight))*percent + mScreenHeight - mDownimgHeight),
                        mDownimgHeight);
                animation.addUpdateListener(new AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        ////////System.out.println("top move:" + animation.getAnimatedValue().toString());
                        ImageView downimg  = (ImageView) findViewById(R.id.downimg);
                        downimg.layout(downimg.getLeft(), ((Integer)animation.getAnimatedValue()).intValue(),
                                downimg.getRight(), mUpimgHeight + ((Integer)animation.getAnimatedValue()).intValue());


                    }
                });

                animation.setInterpolator(new DecelerateInterpolator(4));
                animation.setDuration(mAnimateDuring);
                animation.start();
                //mUpImgAtTop = false;
            }
        } else { //up
            if(target == 0) {
                ////////System.out.println("move up 1:" + String.valueOf((int)(mUpimgHeight-(mScreenHeight - mDownimgHeight))*percent));
                ValueAnimator animation = ValueAnimator.ofInt((int)(mDownimgHeight - (mDownimgHeight-(mScreenHeight - mUpimgHeight))*percent),
                        mDownimgHeight);
                animation.addUpdateListener(new AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        ////////System.out.println("top move:" + animation.getAnimatedValue().toString());
                        ImageView downimg  = (ImageView) findViewById(R.id.downimg);
                        downimg.layout(downimg.getLeft(), ((Integer)animation.getAnimatedValue()).intValue(),
                                downimg.getRight(), mUpimgHeight + ((Integer)animation.getAnimatedValue()).intValue());


                    }
                });
                animation.setInterpolator(new DecelerateInterpolator(4));
                animation.setDuration(mAnimateDuring);
                animation.start();
                //mUpImgAtTop = false;
            } else {
                ////////System.out.println("move up 2:" + String.valueOf(-(int)(mUpimgHeight-(mScreenHeight - mDownimgHeight))*(1-percent)));
                ValueAnimator animation = ValueAnimator.ofInt(mDownimgHeight - (int)((mDownimgHeight-(mScreenHeight - mUpimgHeight))*percent),
                        (mScreenHeight - mDownimgHeight));
                animation.addUpdateListener(new AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        ////System.out.println("top move:" + animation.getAnimatedValue().toString());
                        ImageView downimg  = (ImageView) findViewById(R.id.downimg);
                        downimg.layout(downimg.getLeft(), ((Integer)animation.getAnimatedValue()).intValue(),
                                downimg.getRight(), mUpimgHeight + ((Integer)animation.getAnimatedValue()).intValue());


                    }
                });
                animation.setInterpolator(new DecelerateInterpolator(4));
                animation.setDuration(mAnimateDuring);
                animation.start();
                ////////System.out.println("translationY after:" + String.valueOf(upimg.getTranslationY()));

                //mUpImgAtTop = true;
            }
        }

    }

    // percent:  current all view status  percent
    // target:   target status value 0(rollback)/1
    // orientation:  1 down  -1 up
    private void controlBtnControlImg(float percent, int target, int orientation){
        //////System.out.println("controlBtnControlImg percent:" + String.valueOf(percent) + " target:" + String.valueOf(target) + " orient:" + String.valueOf(orientation));
        int scrollMaxDistance = mDownimgHeight-(mScreenHeight - mUpimgHeight);
        ImageView btncontrolImg  = (ImageView) findViewById(R.id.btncontrolImg);

        if(orientation == 1) { //down
            if(target == 0) {
                ////////System.out.println("move down 1:" + String.valueOf(-(int)(mUpimgHeight-(mScreenHeight - mDownimgHeight))*percent));
                ValueAnimator animation = ValueAnimator.ofInt((int)(scrollMaxDistance*percent + (mBtnControlInitY - scrollMaxDistance)),
                        (mBtnControlInitY - scrollMaxDistance));
                animation.addUpdateListener(new AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        ////////System.out.println("top move:" + animation.getAnimatedValue().toString());
                        ImageView btncontrolImg  = (ImageView) findViewById(R.id.btncontrolImg);
                        btncontrolImg.layout(btncontrolImg.getLeft(), ((Integer)animation.getAnimatedValue()).intValue(),
                                btncontrolImg.getRight(), btncontrolImg.getHeight() + ((Integer)animation.getAnimatedValue()).intValue());


                    }
                });
                animation.setInterpolator(new DecelerateInterpolator(4));
                animation.setDuration(mAnimateDuring);
                animation.start();

                //mUpImgAtTop = true;
            } else {
                ////////System.out.println("move down 2:" + String.valueOf((int)(mUpimgHeight-(mScreenHeight - mDownimgHeight))*(1-percent)));
                ValueAnimator animation = ValueAnimator.ofInt((int)(scrollMaxDistance*percent + (mBtnControlInitY - scrollMaxDistance)),
                        mBtnControlInitY);
                animation.addUpdateListener(new AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        ////////System.out.println("top move:" + animation.getAnimatedValue().toString());
                        ImageView btncontrolImg  = (ImageView) findViewById(R.id.btncontrolImg);
                        btncontrolImg.layout(btncontrolImg.getLeft(), ((Integer)animation.getAnimatedValue()).intValue(),
                                btncontrolImg.getRight(), btncontrolImg.getHeight() + ((Integer)animation.getAnimatedValue()).intValue());


                    }
                });
                animation.setInterpolator(new DecelerateInterpolator(4));
                animation.setDuration(mAnimateDuring);

                ObjectAnimator rotateAnimator = ObjectAnimator.ofFloat(btncontrolImg, "rotationX", 180f, 0f);
                rotateAnimator.setInterpolator(new DecelerateInterpolator(4));
                rotateAnimator.setDuration(mAnimateDuring);

                AnimatorSet animatorset = new AnimatorSet();
                animatorset.play(animation).before(rotateAnimator);
                animatorset.start();

                //mUpImgAtTop = false;
            }
        } else { //up
            if(target == 0) {
                ////////System.out.println("move up 1:" + String.valueOf((int)(mUpimgHeight-(mScreenHeight - mDownimgHeight))*percent));
                ValueAnimator animation = ValueAnimator.ofInt((int)(mBtnControlInitY - scrollMaxDistance*percent),
                        mBtnControlInitY);
                animation.addUpdateListener(new AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        ////////System.out.println("top move:" + animation.getAnimatedValue().toString());
                        ImageView btncontrolImg  = (ImageView) findViewById(R.id.btncontrolImg);
                        btncontrolImg.layout(btncontrolImg.getLeft(), ((Integer)animation.getAnimatedValue()).intValue(),
                                btncontrolImg.getRight(), btncontrolImg.getHeight() + ((Integer)animation.getAnimatedValue()).intValue());


                    }
                });
                animation.setInterpolator(new DecelerateInterpolator(4));
                animation.setDuration(mAnimateDuring);
                animation.start();
                //mUpImgAtTop = false;
            } else {
                ////////System.out.println("move up 2:" + String.valueOf(-(int)(mUpimgHeight-(mScreenHeight - mDownimgHeight))*(1-percent)));
                ValueAnimator animation = ValueAnimator.ofInt(mBtnControlInitY - (int)(scrollMaxDistance*percent),
                        (mBtnControlInitY - scrollMaxDistance));
                animation.addUpdateListener(new AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        ////////System.out.println("top move:" + animation.getAnimatedValue().toString());
                        ImageView btncontrolImg  = (ImageView) findViewById(R.id.btncontrolImg);
                        btncontrolImg.layout(btncontrolImg.getLeft(), ((Integer)animation.getAnimatedValue()).intValue(),
                                btncontrolImg.getRight(), btncontrolImg.getHeight() + ((Integer)animation.getAnimatedValue()).intValue());
                    }
                });
                animation.setInterpolator(new DecelerateInterpolator(4));
                animation.setDuration(mAnimateDuring);
                ObjectAnimator rotateAnimator = ObjectAnimator.ofFloat(btncontrolImg, "rotationX", 0f, 180f);
                rotateAnimator.setInterpolator(new DecelerateInterpolator(4));
                rotateAnimator.setDuration(mAnimateDuring);

                AnimatorSet animatorset = new AnimatorSet();
                animatorset.play(animation).before(rotateAnimator);
                animatorset.start();

                //mUpImgAtTop = true;
            }
        }

    }


    // percent:  current all view status  percent
    // target:   target status value 0(rollback)/1
    // orientation:  1 down  -1 up
    private void controlExploreAtmosLogo(float percent, int target, int orientation){
        //////System.out.println("controlExploreAtmosLogo percent:" + String.valueOf(percent) + " target:" + String.valueOf(target) + " orient:" + String.valueOf(orientation));
        int scrollMaxDistance = (int)((mUpimgHeight - (mScreenHeight - mDownimgHeight)) * mExploreAtmosMaxDistanceRatio);
        LinearLayout exploreatmoslogo  = (LinearLayout) findViewById(R.id.exploreatmoslogo);

        if(orientation == 1) { //down
//			TextView txtUpName  = (TextView) findViewById(R.id.txtupdolbyname);
//	        txtUpName.setTextSize(40);
            if(target == 0) {
                ////////System.out.println("move down 1:" + String.valueOf(-(int)(mUpimgHeight-(mScreenHeight - mDownimgHeight))*percent));
                ValueAnimator animation = ValueAnimator.ofInt((int)(scrollMaxDistance*percent + (mExploreAtmosLogoY - scrollMaxDistance)),
                        (mExploreAtmosLogoY - scrollMaxDistance));
                animation.addUpdateListener(new AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        ////////System.out.println("top move:" + animation.getAnimatedValue().toString());
                        LinearLayout exploreatmoslogo  = (LinearLayout) findViewById(R.id.exploreatmoslogo);
                        exploreatmoslogo.layout(exploreatmoslogo.getLeft(), ((Integer)animation.getAnimatedValue()).intValue(),
                                exploreatmoslogo.getRight(), exploreatmoslogo.getHeight() + ((Integer)animation.getAnimatedValue()).intValue());
                    }
                });
                animation.setInterpolator(new DecelerateInterpolator(4));
                animation.setDuration(mAnimateDuring);
                animation.start();


                //mUpImgAtTop = true;
            } else {
                ////////System.out.println("move down 2:" + String.valueOf((int)(mUpimgHeight-(mScreenHeight - mDownimgHeight))*(1-percent)));
                ValueAnimator animation = ValueAnimator.ofInt((int)(scrollMaxDistance*percent + (mExploreAtmosLogoY - scrollMaxDistance)),
                        mExploreAtmosLogoY);
                animation.addUpdateListener(new AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        ////////System.out.println("top move:" + animation.getAnimatedValue().toString());
                        LinearLayout exploreatmoslogo  = (LinearLayout) findViewById(R.id.exploreatmoslogo);
                        exploreatmoslogo.layout(exploreatmoslogo.getLeft(), ((Integer)animation.getAnimatedValue()).intValue(),
                                exploreatmoslogo.getRight(), exploreatmoslogo.getHeight() + ((Integer)animation.getAnimatedValue()).intValue());


                    }
                });
                animation.setInterpolator(new DecelerateInterpolator(4));
                animation.setDuration(mAnimateDuring);
                animation.start();

                //mUpImgAtTop = false;
            }
        } else { //up
//			TextView txtUpName  = (TextView) findViewById(R.id.txtupdolbyname);
//	        txtUpName.setTextSize(20);
            if(target == 0) {
                ////////System.out.println("move up 1:" + String.valueOf((int)(mUpimgHeight-(mScreenHeight - mDownimgHeight))*percent));
                ValueAnimator animation = ValueAnimator.ofInt((int)(mExploreAtmosLogoY - scrollMaxDistance*percent),
                        mExploreAtmosLogoY);
                animation.addUpdateListener(new AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        ////////System.out.println("top move:" + animation.getAnimatedValue().toString());
                        LinearLayout exploreatmoslogo  = (LinearLayout) findViewById(R.id.exploreatmoslogo);
                        exploreatmoslogo.layout(exploreatmoslogo.getLeft(), ((Integer)animation.getAnimatedValue()).intValue(),
                                exploreatmoslogo.getRight(), exploreatmoslogo.getHeight() + ((Integer)animation.getAnimatedValue()).intValue());


                    }
                });
                animation.setInterpolator(new DecelerateInterpolator(4));
                animation.setDuration(mAnimateDuring);
                animation.start();
                //mUpImgAtTop = false;
            } else {
                ////////System.out.println("move up 2:" + String.valueOf(-(int)(mUpimgHeight-(mScreenHeight - mDownimgHeight))*(1-percent)));
                ValueAnimator animation = ValueAnimator.ofInt(mExploreAtmosLogoY - (int)(scrollMaxDistance*percent),
                        (mExploreAtmosLogoY - scrollMaxDistance));
                animation.addUpdateListener(new AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        ////////System.out.println("top move:" + animation.getAnimatedValue().toString());
                        LinearLayout exploreatmoslogo  = (LinearLayout) findViewById(R.id.exploreatmoslogo);
                        exploreatmoslogo.layout(exploreatmoslogo.getLeft(), ((Integer)animation.getAnimatedValue()).intValue(),
                                exploreatmoslogo.getRight(), exploreatmoslogo.getHeight() + ((Integer)animation.getAnimatedValue()).intValue());
                    }
                });
                animation.setInterpolator(new DecelerateInterpolator(4));
                animation.setDuration(mAnimateDuring);
                animation.start();

                //mUpImgAtTop = true;
            }
        }

    }


    // percent:  current all view status  percent
    // target:   target status value 0(rollback)/1
    // orientation:  1 down  -1 up
    private void controlExperienceDolby(float percent, int target, int orientation){
        //////System.out.println("controlExperienceDolby percent:" + String.valueOf(percent) + " target:" + String.valueOf(target) + " orient:" + String.valueOf(orientation));
        int scrollMaxDistance = (int)((mUpimgHeight - (mScreenHeight - mDownimgHeight)) * mExperienceDolbyMaxDistanceRatio);
        TextView experienceDolby  = (TextView) findViewById(R.id.txtdownname);

        if(orientation == 1) { //down
            if(target == 0) {
                ////////System.out.println("move down 1:" + String.valueOf(-(int)(mUpimgHeight-(mScreenHeight - mDownimgHeight))*percent));
                ValueAnimator animation = ValueAnimator.ofInt((int)(scrollMaxDistance*percent + (mExperienceDolbyY - scrollMaxDistance)),
                        (mExperienceDolbyY - scrollMaxDistance));
                animation.addUpdateListener(new AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        ////////System.out.println("top move:" + animation.getAnimatedValue().toString());
                        TextView experienceDolby  = (TextView) findViewById(R.id.txtdownname);
                        experienceDolby.layout(experienceDolby.getLeft(), ((Integer)animation.getAnimatedValue()).intValue(),
                                experienceDolby.getRight(), experienceDolby.getHeight() + ((Integer)animation.getAnimatedValue()).intValue());
                    }
                });
                animation.setInterpolator(new DecelerateInterpolator(4));
                animation.setDuration(mAnimateDuring);
                animation.start();

                //mUpImgAtTop = true;
            } else {
                ////////System.out.println("move down 2:" + String.valueOf((int)(mUpimgHeight-(mScreenHeight - mDownimgHeight))*(1-percent)));
                ValueAnimator animation = ValueAnimator.ofInt((int)(scrollMaxDistance*percent + (mExperienceDolbyY - scrollMaxDistance)),
                        mExperienceDolbyY);
                animation.addUpdateListener(new AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        ////////System.out.println("top move:" + animation.getAnimatedValue().toString());
                        TextView experienceDolby  = (TextView) findViewById(R.id.txtdownname);
                        experienceDolby.layout(experienceDolby.getLeft(), ((Integer)animation.getAnimatedValue()).intValue(),
                                experienceDolby.getRight(), experienceDolby.getHeight() + ((Integer)animation.getAnimatedValue()).intValue());


                    }
                });
                animation.setInterpolator(new DecelerateInterpolator(4));
                animation.setDuration(mAnimateDuring);
                animation.start();

                //mUpImgAtTop = false;
            }
        } else { //up
            if(target == 0) {
                ////////System.out.println("move up 1:" + String.valueOf((int)(mUpimgHeight-(mScreenHeight - mDownimgHeight))*percent));
                ValueAnimator animation = ValueAnimator.ofInt((int)(mExperienceDolbyY - scrollMaxDistance*percent),
                        mExperienceDolbyY);
                animation.addUpdateListener(new AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        ////////System.out.println("top move:" + animation.getAnimatedValue().toString());
                        TextView experienceDolby  = (TextView) findViewById(R.id.txtdownname);
                        experienceDolby.layout(experienceDolby.getLeft(), ((Integer)animation.getAnimatedValue()).intValue(),
                                experienceDolby.getRight(), experienceDolby.getHeight() + ((Integer)animation.getAnimatedValue()).intValue());


                    }
                });
                animation.setInterpolator(new DecelerateInterpolator(4));
                animation.setDuration(mAnimateDuring);
                animation.start();
                //mUpImgAtTop = false;
            } else {
                ////////System.out.println("move up 2:" + String.valueOf(-(int)(mUpimgHeight-(mScreenHeight - mDownimgHeight))*(1-percent)));
                ValueAnimator animation = ValueAnimator.ofInt(mExperienceDolbyY - (int)(scrollMaxDistance*percent),
                        (mExperienceDolbyY - scrollMaxDistance));
                animation.addUpdateListener(new AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        ////////System.out.println("top move:" + animation.getAnimatedValue().toString());
                        TextView experienceDolby  = (TextView) findViewById(R.id.txtdownname);
                        experienceDolby.layout(experienceDolby.getLeft(), ((Integer)animation.getAnimatedValue()).intValue(),
                                experienceDolby.getRight(), experienceDolby.getHeight() + ((Integer)animation.getAnimatedValue()).intValue());
                    }
                });
                animation.setInterpolator(new DecelerateInterpolator(4));
                animation.setDuration(mAnimateDuring);
                animation.start();

                //mUpImgAtTop = true;
            }
        }

    }

    // percent:  current all view status  percent
    // target:   target status value 0(rollback)/1
    // orientation:  1 down  -1 up
    private void controlTxtUpRemark(float percent, int target, int orientation){
        //////System.out.println("controlTxtUpRemark percent:" + String.valueOf(percent) + " target:" + String.valueOf(target) + " orient:" + String.valueOf(orientation));
        TextView txtupremark  = (TextView) findViewById(R.id.txtupremark);
        //////System.out.println("top move:" + txtupremark.getAlpha());
        if(orientation == 1) { //down
            if(target == 0) {
                ValueAnimator animation = ValueAnimator.ofFloat(percent, 0f);
                animation.addUpdateListener(new AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        ////////System.out.println("top move:" + animation.getAnimatedValue().toString());
                        TextView txtupremark  = (TextView) findViewById(R.id.txtupremark);
                        txtupremark.setAlpha(((Float)animation.getAnimatedValue()));
                    }
                });
                animation.setInterpolator(new DecelerateInterpolator(4));
                animation.setDuration(mAnimateDuring);
                animation.start();

                //mUpImgAtTop = true;
            } else {
                ValueAnimator animation = ValueAnimator.ofFloat(percent, 1f);
                animation.addUpdateListener(new AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        ////////System.out.println("top move:" + animation.getAnimatedValue().toString());
                        TextView txtupremark  = (TextView) findViewById(R.id.txtupremark);
                        txtupremark.setAlpha(((Float)animation.getAnimatedValue()));


                    }
                });
                animation.setInterpolator(new DecelerateInterpolator(4));
                animation.setDuration(mAnimateDuring);
                animation.start();

                //mUpImgAtTop = false;
            }
        } else { //up
            if(target == 0) {
                ////////System.out.println("move up 1:" + String.valueOf((int)(mUpimgHeight-(mScreenHeight - mDownimgHeight))*percent));
                ValueAnimator animation = ValueAnimator.ofFloat(1-percent, 1f);
                animation.addUpdateListener(new AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        //////System.out.println("top move:" + animation.getAnimatedValue().toString());
                        TextView txtupremark  = (TextView) findViewById(R.id.txtupremark);
                        txtupremark.setAlpha(((Float)animation.getAnimatedValue()));


                    }
                });
                animation.setInterpolator(new DecelerateInterpolator(4));
                animation.setDuration(mAnimateDuring);
                animation.start();
                //mUpImgAtTop = false;
            } else {
                ////////System.out.println("move up 2:" + String.valueOf(-(int)(mUpimgHeight-(mScreenHeight - mDownimgHeight))*(1-percent)));
                ValueAnimator animation = ValueAnimator.ofFloat(1 - percent, 0f);
                animation.addUpdateListener(new AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        ////////System.out.println("top move:" + animation.getAnimatedValue().toString());
                        TextView txtupremark  = (TextView) findViewById(R.id.txtupremark);
                        txtupremark.setAlpha(((Float)animation.getAnimatedValue()));
                    }
                });
                animation.setInterpolator(new DecelerateInterpolator(4));
                animation.setDuration(mAnimateDuring);
                animation.start();

                //mUpImgAtTop = true;
            }
        }

    }

    // percent:  current all view status  percent
    // target:   target status value 0(rollback)/1
    // orientation:  1 down  -1 up
    private void controlTxtDownRemark(float percent, int target, int orientation){
        //////System.out.println("controlTxtDownRemark percent:" + String.valueOf(percent) + " target:" + String.valueOf(target) + " orient:" + String.valueOf(orientation));
        TextView txtdownremark  = (TextView) findViewById(R.id.txtdownremark);
        if(orientation == 1) { //down
            if(target == 0) {
                ValueAnimator animation = ValueAnimator.ofFloat(1-percent, 1f);
                animation.addUpdateListener(new AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        ////////System.out.println("top move:" + animation.getAnimatedValue().toString());
                        TextView txtdownremark  = (TextView) findViewById(R.id.txtdownremark);
                        txtdownremark.setAlpha(((Float)animation.getAnimatedValue()));
                    }
                });
                animation.setInterpolator(new DecelerateInterpolator(4));
                animation.setDuration(mAnimateDuring);
                animation.start();

                //mUpImgAtTop = true;
            } else {
                ValueAnimator animation = ValueAnimator.ofFloat(1-percent, 0f);
                animation.addUpdateListener(new AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        ////////System.out.println("top move:" + animation.getAnimatedValue().toString());
                        TextView txtdownremark  = (TextView) findViewById(R.id.txtdownremark);
                        txtdownremark.setAlpha(((Float)animation.getAnimatedValue()));


                    }
                });
                animation.setInterpolator(new DecelerateInterpolator(4));
                animation.setDuration(mAnimateDuring);
                animation.start();

                //mUpImgAtTop = false;
            }
        } else { //up
            if(target == 0) {
                ////////System.out.println("move up 1:" + String.valueOf((int)(mUpimgHeight-(mScreenHeight - mDownimgHeight))*percent));
                ValueAnimator animation = ValueAnimator.ofFloat(percent, 0f);
                animation.addUpdateListener(new AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        ////////System.out.println("top move:" + animation.getAnimatedValue().toString());
                        TextView txtdownremark  = (TextView) findViewById(R.id.txtdownremark);
                        txtdownremark.setAlpha(((Float)animation.getAnimatedValue()));


                    }
                });
                animation.setInterpolator(new DecelerateInterpolator(4));
                animation.setDuration(mAnimateDuring);
                animation.start();
                //mUpImgAtTop = false;
            } else {
                ////////System.out.println("move up 2:" + String.valueOf(-(int)(mUpimgHeight-(mScreenHeight - mDownimgHeight))*(1-percent)));
                ValueAnimator animation = ValueAnimator.ofFloat(percent, 1f);
                animation.addUpdateListener(new AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        ////////System.out.println("top move:" + animation.getAnimatedValue().toString());
                        TextView txtdownremark  = (TextView) findViewById(R.id.txtdownremark);
                        txtdownremark.setAlpha(((Float)animation.getAnimatedValue()));
                    }
                });
                animation.setInterpolator(new DecelerateInterpolator(4));
                animation.setDuration(mAnimateDuring);
                animation.start();

                //mUpImgAtTop = true;
            }
        }

    }


    // percent:  current all view status  percent
    // target:   target status value 0(rollback)/1
    // orientation:  1 down  -1 up
    private void controlLearnMore(float percent, int target, int orientation){
        //////System.out.println("controlLearnMore percent:" + String.valueOf(percent) + " target:" + String.valueOf(target) + " orient:" + String.valueOf(orientation));
        LinearLayout exploreatmoslearnmore  = (LinearLayout) findViewById(R.id.exploreatmoslearnmore);
        if(orientation == 1) { //down
            if(target == 0) {
                ValueAnimator animation = ValueAnimator.ofFloat(percent, 0f);
                animation.addUpdateListener(new AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        ////////System.out.println("top move:" + animation.getAnimatedValue().toString());
                        LinearLayout exploreatmoslearnmore  = (LinearLayout) findViewById(R.id.exploreatmoslearnmore);
                        exploreatmoslearnmore.setAlpha(((Float)animation.getAnimatedValue()));
                    }
                });
                animation.setInterpolator(new DecelerateInterpolator(4));
                animation.setDuration(mAnimateDuring);
                animation.start();

                //mUpImgAtTop = true;
            } else {
                ValueAnimator animation = ValueAnimator.ofFloat(percent, 1f);
                animation.addUpdateListener(new AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        ////////System.out.println("top move:" + animation.getAnimatedValue().toString());
                        LinearLayout exploreatmoslearnmore  = (LinearLayout) findViewById(R.id.exploreatmoslearnmore);
                        exploreatmoslearnmore.setAlpha(((Float)animation.getAnimatedValue()));


                    }
                });
                animation.setInterpolator(new DecelerateInterpolator(4));
                animation.setDuration(mAnimateDuring);
                animation.start();

                //mUpImgAtTop = false;
            }
        } else { //up
            if(target == 0) {
                ////////System.out.println("move up 1:" + String.valueOf((int)(mUpimgHeight-(mScreenHeight - mDownimgHeight))*percent));
                ValueAnimator animation = ValueAnimator.ofFloat(1-percent, 1f);
                animation.addUpdateListener(new AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        //////System.out.println("top move:" + animation.getAnimatedValue().toString());
                        LinearLayout exploreatmoslearnmore  = (LinearLayout) findViewById(R.id.exploreatmoslearnmore);
                        exploreatmoslearnmore.setAlpha(((Float)animation.getAnimatedValue()));


                    }
                });
                animation.setInterpolator(new DecelerateInterpolator(4));
                animation.setDuration(mAnimateDuring);
                animation.start();
                //mUpImgAtTop = false;
            } else {
                ////////System.out.println("move up 2:" + String.valueOf(-(int)(mUpimgHeight-(mScreenHeight - mDownimgHeight))*(1-percent)));
                ValueAnimator animation = ValueAnimator.ofFloat(1 - percent, 0f);
                animation.addUpdateListener(new AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        ////////System.out.println("top move:" + animation.getAnimatedValue().toString());
                        LinearLayout exploreatmoslearnmore  = (LinearLayout) findViewById(R.id.exploreatmoslearnmore);
                        exploreatmoslearnmore.setAlpha(((Float)animation.getAnimatedValue()));
                    }
                });
                animation.setInterpolator(new DecelerateInterpolator(4));
                animation.setDuration(mAnimateDuring);
                animation.start();

                //mUpImgAtTop = true;
            }
        }

    }

    // percent:  current all view status  percent
    // target:   target status value 0(rollback)/1
    // orientation:  1 down  -1 up
    private void controlExploreAtmosAccess(float percent, int target, int orientation){
        //////System.out.println("controlTxtDownRemark percent:" + String.valueOf(percent) + " target:" + String.valueOf(target) + " orient:" + String.valueOf(orientation));
        LinearLayout exploreatmosaccess  = (LinearLayout) findViewById(R.id.exploreatmosaccess);
        if(orientation == 1) { //down
            if(target == 0) {
                ValueAnimator animation = ValueAnimator.ofFloat(1-percent, 1f);
                animation.addUpdateListener(new AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        ////////System.out.println("top move:" + animation.getAnimatedValue().toString());
                        LinearLayout exploreatmosaccess  = (LinearLayout) findViewById(R.id.exploreatmosaccess);
                        exploreatmosaccess.setAlpha(((Float)animation.getAnimatedValue()));
                    }
                });
                animation.setInterpolator(new DecelerateInterpolator(4));
                animation.setDuration(mAnimateDuring);
                animation.start();

                //mUpImgAtTop = true;
            } else {
                ValueAnimator animation = ValueAnimator.ofFloat(1-percent, 0f);
                animation.addUpdateListener(new AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        ////////System.out.println("top move:" + animation.getAnimatedValue().toString());
                        LinearLayout exploreatmosaccess  = (LinearLayout) findViewById(R.id.exploreatmosaccess);
                        exploreatmosaccess.setAlpha(((Float)animation.getAnimatedValue()));


                    }
                });
                animation.setInterpolator(new DecelerateInterpolator(4));
                animation.setDuration(mAnimateDuring);
                animation.start();

                //mUpImgAtTop = false;
            }
        } else { //up
            if(target == 0) {
                ////////System.out.println("move up 1:" + String.valueOf((int)(mUpimgHeight-(mScreenHeight - mDownimgHeight))*percent));
                ValueAnimator animation = ValueAnimator.ofFloat(percent, 0f);
                animation.addUpdateListener(new AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        ////////System.out.println("top move:" + animation.getAnimatedValue().toString());
                        LinearLayout exploreatmosaccess  = (LinearLayout) findViewById(R.id.exploreatmosaccess);
                        exploreatmosaccess.setAlpha(((Float)animation.getAnimatedValue()));


                    }
                });
                animation.setInterpolator(new DecelerateInterpolator(4));
                animation.setDuration(mAnimateDuring);
                animation.start();
                //mUpImgAtTop = false;
            } else {
                ////////System.out.println("move up 2:" + String.valueOf(-(int)(mUpimgHeight-(mScreenHeight - mDownimgHeight))*(1-percent)));
                ValueAnimator animation = ValueAnimator.ofFloat(percent, 1f);
                animation.addUpdateListener(new AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        ////////System.out.println("top move:" + animation.getAnimatedValue().toString());
                        LinearLayout exploreatmosaccess  = (LinearLayout) findViewById(R.id.exploreatmosaccess);
                        exploreatmosaccess.setAlpha(((Float)animation.getAnimatedValue()));
                    }
                });
                animation.setInterpolator(new DecelerateInterpolator(4));
                animation.setDuration(mAnimateDuring);
                animation.start();

                //mUpImgAtTop = true;
            }
        }

    }
//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//    	Log.d(tag, "onActivityCreated");
//        super.onActivityCreated(savedInstanceState);
//    }

    @Override
    public void onResume() {
        Log.d(tag, "onResume");
        super.onResume();
        LinearLayout exploreatmosaccess  = (LinearLayout) findViewById(R.id.exploreatmosaccess);
        LinearLayout exploreatmoslearnmore = (LinearLayout) findViewById(R.id.exploreatmoslearnmore);
        TextView txtupremark  = (TextView) findViewById(R.id.txtupremark);
        TextView txtdownremark  = (TextView) findViewById(R.id.txtdownremark);
        ImageView btncontrolImg  = (ImageView) findViewById(R.id.btncontrolImg);
        if(mUpImgAtTop == true) {
//        	mUpImgAtTop = false;
            if(txtupremark != null) {
                txtupremark.setAlpha(0);
            }
            if(exploreatmoslearnmore != null) {
                exploreatmoslearnmore.setAlpha(0);
            }
            if(txtdownremark != null) {
                txtdownremark.setAlpha(1);
            }
            if(exploreatmosaccess != null) {
                exploreatmosaccess.setAlpha(1);
            }
            if(btncontrolImg != null) {
                controlBtnControlImg(1, 1, 0);
//        		controlBtnControlImg(1, 1, 1);
            }
        }
    }

    @Override
    public void onStart() {
        Log.d(tag, "onStart");
        super.onStart();
        // If in mobile layout, reporting for second phase initialization.
        if (mMobileLayout == true) {
//            mSpecificObserver.profilePresetsAreAlive();
        }
    }


    @Override
    public void onPause() {
        Log.d(tag, "onPause");
        if (null != mProfilesAdapter) {
            mProfilesAdapter.endEditingProfileName(true);
        }

        super.onPause();
    }

    public void onClientConnected() {
        mDolbyClientConnected = true;
        if (mMobileLayout == true) {
//            mSpecificObserver.profilePresetsAreAlive();
        }
    }

    public void onClientDisconnected() {
        mDolbyClientConnected = false;
    }

    public void onProfileNameChanged(int prefile, String name) {
        if (mProfilesAdapter != null) {
            // Redraw the list of profiles (with new profile name).
            mProfilesAdapter.scheduleNotifyDataSetChanged();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        Log.d(tag, "onSaveInstanceState");
        super.onSaveInstanceState(savedInstanceState);
        ImageView upimg  = (ImageView)  findViewById(R.id.upimg);
        savedInstanceState.putInt("UPIMGTOP", upimg.getTop());
    }

    public void setInitStatus() {
        LinearLayout exploreatmosaccess  = (LinearLayout) findViewById(R.id.exploreatmosaccess);
        LinearLayout exploreatmoslearnmore = (LinearLayout) findViewById(R.id.exploreatmoslearnmore);
        TextView txtupremark  = (TextView) findViewById(R.id.txtupremark);
        TextView txtdownremark  = (TextView) findViewById(R.id.txtdownremark);
        ImageView btncontrolImg  = (ImageView) findViewById(R.id.btncontrolImg);
        if(mUpImgAtTop == true) {
            mUpImgAtTop = false;
            if(txtupremark != null) {
                txtupremark.setAlpha(1);
            }
            if(exploreatmoslearnmore != null) {
                exploreatmoslearnmore.setAlpha(1);
            }
            if(txtdownremark != null) {
                txtdownremark.setAlpha(0);
            }
            if(exploreatmosaccess != null) {
                exploreatmosaccess.setAlpha(0);
            }
            if(btncontrolImg != null) {
                controlBtnControlImg(1, 1, 1);
            }
        }
    }

    @Override
    public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onCompletion(MediaPlayer arg0) {
        // TODO Auto-generated method stub

    }
}



