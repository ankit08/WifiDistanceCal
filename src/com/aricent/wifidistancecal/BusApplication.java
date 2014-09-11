package com.aricent.wifidistancecal;

import android.app.Application;

import com.squareup.otto.Bus;

	/**
	 * Bus Event to post the data
	 * @author Ankit 
	 *
	 */
	public class BusApplication extends Application{
		 
	    public static Bus mBus = new Bus();
	 
	    public static Bus getEventBus() {
	        return mBus;
	    }
	 
	    @Override
	    public void onCreate() {
	        super.onCreate();
	    }
	}
