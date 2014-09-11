package com.aricent.wifidistancecal;

import java.util.HashMap;
import java.util.List;

import android.util.Log;

/**
 * Class for WiFi FingerPrinting Data
 * Add and save the data
 * 
 * @author Ankit
 * 
 */
public class WifiFingerPrintData {
	protected static final String TAG = WifiFingerPrintData.class
			.getSimpleName().toString();

	private FingerPrintCordinates fingerPrintCordinates;

	public HashMap<String, String> mBssid = new HashMap<String, String>();

	public WifiFingerPrintData(String[] ar) {
		Log.i(TAG, "WifiFingerPrintData: r" + ar[0] + "  " + ar[1] + "  "
				+ ar[2] + "  " + ar[3] + "  " + ar[4] + " " + " " + ar[5] + " "
				+ ar[6]);
		
		Log.i(TAG," TestData Array Size " + ar.length);
		
		
		for(int temp = 0 ;temp <ar.length-3;temp+=2){
			Log.i(TAG," TestData temp Size " + temp);
			mBssid.put(ar[temp], ar[temp+1]);
		}
		fingerPrintCordinates = new FingerPrintCordinates(ar[ar.length-2],ar[ar.length-1]);
		
		Log.i(TAG," TestData HashMap " + mBssid.size() + " Finger Print XCor " + fingerPrintCordinates.getX() + " yCor " +fingerPrintCordinates.getY());
	}

	public WifiFingerPrintData() {
		Log.i(TAG, " Empty Wifi Finger Constructor");
	}

	/**
	 * check whether Bssid available 
	 * in HashMap
	 * @param bssids
	 * @return
	 */
	public boolean isBssidContain(List<String> bssids) {
		boolean isFound = true;
		for (String string : bssids) {
			if (!mBssid.containsKey(string)) {
				isFound = false;
				break;
			}
		}
		return isFound;
	}

	/**
	 * Check whether RSSI is valid for BSSID
	 * @param bssid
	 * @param rssi
	 * @return
	 */
	private boolean isValidRssi(String bssid, int rssi) {
		boolean isValid = false;
		int currentRssi = Integer.parseInt(mBssid.get(bssid));
		Log.i(TAG, " RSSI Matched Data " + " Bssid " + bssid
				+ " RSSI from Scan " + rssi + " Fingerprint RSSI "
				+ currentRssi);
		if (rssi <= (currentRssi + 3) && rssi >= (currentRssi - 3)) {
			isValid = true;
		}
		Log.i(TAG, "isValid" + isValid);
		return isValid;
	}

	/**
	 * To Parse the List of Bssid and RSSI
	 * @param bssids
	 * @param rssis
	 * @return
	 */
	public boolean isValidRssi(List<String> bssids, List<Integer> rssis) {
		boolean isValid = true;
		Log.i(TAG, " isValidRssi BSSID  " + bssids + "isValidRssi RSSI  "
				+ rssis);
		for (int i = 0; i < bssids.size(); i++) {
			if (!isValidRssi(bssids.get(i), rssis.get(i))) {
				isValid = false;
			}
		}
		Log.i(TAG, "final isValid" + isValid);
		return isValid;
	}

	/**
	 * Get Coordinates from the FingerPrint 
	 * @return
	 */
	public FingerPrintCordinates getCordinates() {
		return fingerPrintCordinates;
	}

}
