package com.aricent.wifidistancecal;

/**
 * Class to get and set BeaconScannerParseable object
 * 
 * @author Ankit
 * 
 */
public class WifiScanEvent {
	private WifiScannerParseable mWifiParseable;

	private String mWifi;

	public String getmWifi() {
		return mWifi;
	}

	public void setmWifi(String mWifi) {
		this.mWifi = mWifi;
	}

	// Getter and Setter Method
	public WifiScannerParseable getmWifiParseable() {
		return mWifiParseable;
	}

	public void setmWifiParseable(WifiScannerParseable mWifiParseable) {
		this.mWifiParseable = mWifiParseable;
	}

}
