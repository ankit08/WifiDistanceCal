package com.aricent.wifidistancecal;

public class FingerPrintCordinates {
	private String x;
	private String y;
	private String bssid;
	private int confidenceLevel;
	private int rssi;

	public int getRssi() {
		return rssi;
	}

	public void setRssi(int rssi) {
		this.rssi = rssi;
	}

	public String getBssid() {
		return bssid;
	}

	public void setBssid(String bssid) {
		this.bssid = bssid;
	}

	public FingerPrintCordinates(String bssid, int rssi ) {
		this.bssid = bssid;
		this.rssi = rssi;
	}

	public FingerPrintCordinates(String x, String y) {
		this.x = x;
		this.y = y;
	}

	
	public FingerPrintCordinates(String x, String y, String bssid) {
		this.x = x;
		this.y = y;
		this.bssid = bssid;
	}

	public FingerPrintCordinates(String x, String y, String bssid, int level) {
		this.x = x;
		this.y = y;
		this.bssid = bssid;
		this.confidenceLevel = level;
	}

	public String getX() {
		return x;
	}

	public void setX(String x) {
		this.x = x;
	}

	public String getY() {
		return y;
	}

	public void setY(String y) {
		this.y = y;
	}

	public int getConfidenceLevel() {
		return confidenceLevel;
	}

	public void setConfidenceLevel(int confidenceLevel) {
		this.confidenceLevel = confidenceLevel;
	}
}
