package com.aricent.wifidistancecal;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVWriter;

public class WriteDataToCsv {

	public void dataToCsv(List<WifiSignalScan> wifiList , int event) {
		String csv = android.os.Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/csvTest.csv";

		try {
			CSVWriter writer = new CSVWriter(new FileWriter(csv));
			List<String[]> data = new ArrayList<String[]>();
			data.add(new String[] { "India", "New Delhi" });
			data.add(new String[] { "United States", "Washington D.C" });
			data.add(new String[] { "Germany", "Berlin" });
			data.add(new String[] {"",wifiList.get(0).getScanResult().SSID , String.valueOf(wifiList.get(0).getScanResult().level) ,String.valueOf(wifiList.get(0).getApAccuracy()) ,});

			writer.writeAll(data);

			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
