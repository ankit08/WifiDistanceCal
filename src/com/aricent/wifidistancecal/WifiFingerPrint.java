package com.aricent.wifidistancecal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.os.Environment;
import android.util.Log;

/**
 * Class to read WiFi fingerPrint information
 * from File
 * @author ubuntu6
 *
 */
public class WifiFingerPrint {
	protected static final String TAG = WifiFingerPrint.class.getSimpleName()
			.toString();

	/**
	 * Read WiFi information from File for FingerPrinting
	 */
	public List<WifiFingerPrintData> readWifiFingerPrint() throws IOException{
		Log.i(TAG, "wifiFingerPrint: readWifiFingerPrint ");

		File fingerprint = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath(), "fingerPrint.txt");

		List<WifiFingerPrintData> listTest = new ArrayList<WifiFingerPrintData>();
		// Create new WifiFinger Printing Object

			BufferedReader br = new BufferedReader(new FileReader(fingerprint));
			String line;
			// Read Data Line by Line from File
			while ((line = br.readLine()) != null) {
				String[] ar = line.split(",");
				WifiFingerPrintData test = new WifiFingerPrintData(ar);

				// Add Set of WiFi FingerPrinting data to the List
				listTest.add(test);

			}
			br.close();
			Log.i(TAG, "TestData: readWifiFingerPrint List Size "
					+ listTest.size());

		return listTest;
	}

	/**
	 * Check External Storage is Readable
	 * 
	 * @return
	 */
	public boolean isExternalStorageReadable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)
				|| Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return true;
		}
		return false;
	}

	/**
	 * Check External Storage is Writable
	 * 
	 * @return
	 */
	public boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

}
