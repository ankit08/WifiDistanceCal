package com.aricent.wifidistancecal;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Parseable class for Beacon to send the arrayList through BUS
 * 
 * @author Ankit
 * 
 */
public class WifiScannerParseable implements Parcelable {
	private List<WifiSignalScan> mWifiData;

	public static final Parcelable.Creator<WifiScannerParseable> CREATOR = new Parcelable.Creator<WifiScannerParseable>() {
		public WifiScannerParseable createFromParcel(Parcel in) {
			return new WifiScannerParseable(in);
		}

		public WifiScannerParseable[] newArray(int size) {
			return new WifiScannerParseable[size];
		}
	};

	public List<WifiSignalScan> getmWifiData() {
		return mWifiData;
	}

	public void setmWifiData(List<WifiSignalScan> mWifiData) {
		this.mWifiData = mWifiData;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeList(mWifiData);

	}

	// write this constructor to initialize the arrayList
	public WifiScannerParseable() {
		mWifiData = new ArrayList<WifiSignalScan>();

	}

	/**
	 * This will be used only by the MyCreator
	 * 
	 * @param source
	 *            source where to read the parceled data
	 */
	public WifiScannerParseable(Parcel source) {

		// you have to call the other constructor to initialize the arrayList
		this();

		// reconstruct from the parcel
		source.readList(mWifiData, null);
	}

}
