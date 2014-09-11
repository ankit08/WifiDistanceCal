package com.aricent.wifidistancecal;

import java.util.Comparator;


public class  Sorting implements Comparable<Sorting> {

	private WifiSignalScan beacons;
	private double accuracy;
	
	public Sorting(WifiSignalScan beacons, double accuracy) {
		super();
		this.beacons = beacons;
		this.accuracy = accuracy;
	}
	
	public WifiSignalScan getBeacons() {
		return beacons;
	}
	public void setBeacons(WifiSignalScan beacons) {
		this.beacons = beacons;
	}
	public double getAccuracy() {
		return accuracy;
	}
	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}
	
	public int compareTo(Sorting compareBeacon) {
		 
		double compareQuantity = ((Sorting) compareBeacon).getAccuracy(); 
 
		//ascending order
		return (int) (this.accuracy - compareQuantity);

	}
	
	/**
     * Comparator to sort employees list or array in order of Age
     */
    public static Comparator<WifiSignalScan> wifiCompar = new Comparator<WifiSignalScan>() {
 
        @Override
        public int compare(WifiSignalScan e1, WifiSignalScan e2) {
           // return (int) (e1.getScanResult().level - e2.getScanResult().level);
            
            return (int) (e2.getScanResult().level - e1.getScanResult().level);
        }
    };
    
    
    /**
     * Comparator to sort employees list or array in order of Age
     */
    public static Comparator<FingerPrintCordinates> fingerSignalComp = new Comparator<FingerPrintCordinates>() {
 
        @Override
        public int compare(FingerPrintCordinates e1, FingerPrintCordinates e2) {
           // return (int) (e1.getScanResult().level - e2.getScanResult().level);
            
            return (int) (e1.getRssi() - e2.getRssi());
        }
    };

}
