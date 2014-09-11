package com.aricent.wifidistancecal;

import android.net.wifi.ScanResult;

public class WifiSignalScan {

	private ScanResult scanResult ;
	private double apAccuracy;
	public ScanResult getScanResult() {
		return scanResult;
	}
	public void setScanResult(ScanResult scanResult) {
		this.scanResult = scanResult;
	}
	public double getApAccuracy() {
		return apAccuracy;
	}
	public void setApAccuracy(double apAccuracy) {
		this.apAccuracy = apAccuracy;
	}
}
