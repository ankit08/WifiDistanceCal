package com.aricent.wifidistancecal;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Bus;

public class WifiSignalDis extends Activity {

	protected static final String TAG = WifiSignalDis.class.getSimpleName()
			.toString();
	private Button button;
	private TextView editText;
	private Context mContext;
	private WifiManager mWifi;
	private final int interval = 3000; // 3 Second
	int time = 20;
	private Timer t;
	private TimerTask task;
	private Handler mHandler = new Handler();
	private Bus mBus;
	private List<WifiFingerPrintData> fingerPrintData = new ArrayList<WifiFingerPrintData>();
	@SuppressLint("UseSparseArrays")
	private Map<Integer, ArrayList<FingerPrintCordinates>> fingerPrint = new HashMap<Integer, ArrayList<FingerPrintCordinates>>();
	@SuppressWarnings("unchecked")
	private ArrayList<FingerPrintCordinates>[] finger = (ArrayList<FingerPrintCordinates>[]) new ArrayList[100];
	private boolean isBssidFound = false;
	
	private List<List<WifiSignalScan>> finalScan = new ArrayList<List<WifiSignalScan>>();
	private Queue<List<WifiSignalScan>> msgque = new LinkedList<List<WifiSignalScan>>();
	private ConcurrentLinkedQueue<List<WifiSignalScan>> wifiQue = new ConcurrentLinkedQueue<List<WifiSignalScan>>();
	
	private Map<Integer,ArrayList<String>> signalhasMap = new HashMap<Integer,ArrayList<String>>();



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wifi_signal_dis);
		this.mContext = getApplicationContext();
		mBus = BusApplication.mBus;
		getWifiFingerPrintData();
		//addListenerOnButton();
		getShortedFingerPrintList();
	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, " On onDestroy Called");

		// TODO Auto-generated method stub
		super.onDestroy();
		if (t != null) {
			t.cancel();
		}
	}

	@Override
	protected void onPause() {
		Log.d(TAG, " On Pause Called");
		// TODO Auto-generated method stub
		super.onPause();
		if (t != null) {
			t.cancel();
		}
	}

	@Override
	protected void onResume() {
		Log.d(TAG, " On onResume Called");

		// TODO Auto-generated method stub
		super.onResume();
	}

	public void addListenerOnButton() {
		button = (Button) findViewById(R.id.WifiScan);
		editText = (TextView) findViewById(R.id.WifiList);
		editText.setSingleLine(false);
		editText.setMovementMethod(ScrollingMovementMethod.getInstance());
		editText.setTextColor(Color.MAGENTA);
		editText.setBackgroundColor(Color.LTGRAY);

		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mWifi = (WifiManager) mContext
						.getSystemService(Context.WIFI_SERVICE);

				startWifiTimer();

			}
		});
	}

	/**
	 * Method to start WiFi Scan
	 * 
	 * @throws InterruptedException
	 */
	public void startWifiScan() throws InterruptedException {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				List<ScanResult> scanList;
				List<WifiSignalScan> wifiScanData = new ArrayList<WifiSignalScan>();
				Set<WifiSignalScan> wifiList = new HashSet<WifiSignalScan>();

				scanList = mWifi.getScanResults();
				for (ScanResult s : scanList) {
					DecimalFormat df = new DecimalFormat("#.##");
					WifiSignalScan wifiSignal = new WifiSignalScan();

					wifiSignal.setScanResult(s);
					wifiSignal.setApAccuracy(Double.parseDouble(df
							.format(calculateDistance((double) s.level,
									s.frequency))));
					wifiScanData.add(wifiSignal);
				}

				wifiList = addWifiToList(wifiScanData);
				if (wifiList.size() > 0) {
					sendWifiList(wifiList);
				}

			}
		});

	}


	/**
	 * Method to calculate Distance from AP
	 * 
	 * @param levelInDb
	 * @param freqInMHz
	 * @return
	 */
	public double calculateDistance(double levelInDb, double freqInMHz) {
		double exp = (27.55 - (20 * Math.log10(freqInMHz)) + Math
				.abs(levelInDb)) / 20.0;
		// Log.d(TAG, "calculateDistance Value " + exp);
		return Math.pow(10.0, exp);
	}

	/**
	 * Timer Event to call WiFi Scan Trigger after every 2 Seconds
	 */
	public void startWifiTimer() {
		Log.d(TAG, "startWifiTimer");
		t = new Timer();
		task = new TimerTask() {

			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mWifi.startScan();
						try {
							startWifiScan();
						} catch (Exception e) {
							Log.e(TAG, " Exception " + e.getMessage());
						}
					}
				});
			}
		};
		t.scheduleAtFixedRate(task, 0, interval);
	}

	/**
	 * Method to add the WiFi Scan List into HashSet to ignore duplicate values
	 * 
	 * @param beacons
	 * @return List of Beacons
	 */
	private Set<WifiSignalScan> addWifiToList(List<WifiSignalScan> wifiSignal) {
		Log.d(TAG, " addWifiToList " + wifiSignal.size());

		Iterator<WifiSignalScan> beaconIterator = wifiSignal.iterator();
		Set<WifiSignalScan> signalScan = new HashSet<WifiSignalScan>();

		while (beaconIterator.hasNext()) {
			WifiSignalScan wifi = beaconIterator.next();
			signalScan.add(wifi);
		}
		Log.d(TAG, " addtestBeacon Testing Beacon Size " + signalScan.size());
		return signalScan;
	}

	/**
	 * Method to filter the WiFi based on signal Level and Call postBeaconEvent
	 * method
	 * 
	 * @param test
	 */
	private void sendWifiList(Set<WifiSignalScan> test) {

		List<WifiSignalScan> wifiList = new ArrayList<WifiSignalScan>();

		try {
			WifiSignalScan value;
			Iterator<WifiSignalScan> it = test.iterator();
			if (test.size() > 0) {
				while (it.hasNext()) {
					value = (WifiSignalScan) it.next();
					wifiList.add(value);
				}
				Collections.sort(wifiList, Sorting.wifiCompar);
				// wifiFingerPrint(wifiList);

				// testFingerPrintdata(wifiList);
				// testData();
				
				
				
				//getFingerPrint(wifiList);
				
				//enQueueWifiList(wifiList);

				/*
				 * for (int temp = 0; temp < wifiList.size(); temp++) {
				 * 
				 * 
				 * 
				 * Log.i(TAG, " Sorted Wifi List Accuracy  " +
				 * wifiList.get(temp).getApAccuracy() + " Sorted Wifi " +
				 * wifiList.get(temp).getScanResult().SSID + " Sigal Level " +
				 * Math.abs(wifiList.get(temp) .getScanResult().level) +
				 * " BSSID " + wifiList.get(temp).getScanResult().BSSID);
				 * 
				 * if (wifiList.get(temp).getScanResult().SSID
				 * .contentEquals("VISITED-AP") ||
				 * wifiList.get(temp).getScanResult().SSID
				 * .contentEquals("IDCC_NETGEAR_LAB1") ||
				 * wifiList.get(temp).getScanResult
				 * ().SSID.contentEquals("CoovaTesting")) { editText.append("("
				 * + event + ")" + " SSID " +
				 * wifiList.get(temp).getScanResult().SSID + " Signal Level " +
				 * wifiList.get(temp).getScanResult().level + "  Accuracy  " +
				 * wifiList.get(temp).getApAccuracy() + " m");
				 * editText.append("\n\n"); event++; data.add(new String[] {"("+
				 * String
				 * .valueOf(event)+")",wifiList.get(temp).getScanResult().SSID ,
				 * String.valueOf(wifiList.get(temp).getScanResult().level)
				 * ,String.valueOf(wifiList.get(temp).getApAccuracy()) +
				 * " meters"}); writer.writeAll(data);
				 * 
				 * }
				 * 
				 * }
				 */

				// writer.close();
				/*
				 * WifiScannerParseable wifiParseable = new
				 * WifiScannerParseable(); wifiParseable.setmWifiData(wifiList);
				 * WifiScanEvent wifiScanEvent = new WifiScanEvent();
				 * wifiScanEvent.setmWifiParseable(wifiParseable);
				 */
				// postWifiEvent(beaconScanEvent);

			}

		} catch (Exception e) {
			Log.e(TAG, " Exception " + e.getMessage());
		}

	}

	/**
	 * Method to Post WifiList to the ImageMapActivity
	 * 
	 * @param beaconScanEvent
	 */
	private void postWifiEvent(final WifiScanEvent wifiScanEvent) {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				Log.i(TAG, " BUS EVENT CALLED");
				// publish a new event
				mBus.post(wifiScanEvent);
			}
		});
	}

	/**
	 * Parse the Scan WiFi List and add the data to the list
	 * 
	 * @param wifiData
	 */
	private void wifiFingerPrint(List<WifiSignalScan> wifiData) {
		Log.i(TAG, " WifiFingerPrint ");

		List<String> bssid = new ArrayList<String>();
		List<Integer> rssi = new ArrayList<Integer>();

		int event = 0;
		// WifiFingerPrintData test = new WifiFingerPrintData();

		for (int temp = 0; temp < wifiData.size(); temp++) {
			if (wifiData.get(temp).getScanResult().SSID
					.contentEquals("Aricent Employee")
					|| wifiData.get(temp).getScanResult().SSID
							.contentEquals("Aricent Mobile")
					|| wifiData.get(temp).getScanResult().SSID
							.contentEquals("Aricent Guest")) {
				Log.i(TAG, " Data Collection " + " SSID "
						+ wifiData.get(temp).getScanResult().SSID + " BSSID "
						+ wifiData.get(temp).getScanResult().BSSID
						+ " Signal level "
						+ wifiData.get(temp).getScanResult().level
						+ " Time Stamp in Microseconds "
						+ wifiData.get(temp).getScanResult().timestamp);

				bssid.add(wifiData.get(temp).getScanResult().BSSID);
				rssi.add(wifiData.get(temp).getScanResult().level);

				editText.append("(" + event + ")" + " Signal level "
						+ wifiData.get(temp).getScanResult().level + " BSSID "
						+ wifiData.get(temp).getScanResult().BSSID + " SSID "
						+ wifiData.get(temp).getScanResult().SSID);
				editText.append("\n\n");
				event++;
			}
		}
		// locateWifiFingerPrint(bssid, rssi);

	}

	/**
	 * Read WiFi Finger Print data , fetched from the server
	 */
	private void getWifiFingerPrintData() {
		Log.i(TAG, " getWifiFingerPrintData");
		try {
			WifiFingerPrint wifiFinger = new WifiFingerPrint();
			if (wifiFinger.isExternalStorageWritable()) {
				fingerPrintData = wifiFinger.readWifiFingerPrint();
				Log.i(TAG, " wifiFingerPrint size " + fingerPrintData.size());
			}
		} catch (Exception e) {
			Log.e(TAG, " Exception " + e.getMessage());
			Toast.makeText(getApplicationContext(),
					" Error in reading WifiFingerprint from file ",
					Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * Method to locate WiFi FingerPrint
	 * 
	 * @param bssid
	 * @param rssi
	 */
	private void locateWifiFingerPrint(List<String> bssid, List<Integer> rssi) {
		Log.i(TAG,
				"  BSSID Size " + bssid.size() + " RSSI  Size " + rssi.size());
		try {
			for (int temp = 0; temp < fingerPrintData.size(); temp++) {
				if (fingerPrintData.get(temp).isBssidContain(bssid)
						&& fingerPrintData.get(temp).isValidRssi(bssid, rssi)) {
					Log.i(TAG,
							" wifiFingerPrint RSSI Matched in the Wifi finger Print ");
					String xCord = fingerPrintData.get(temp).getCordinates()
							.getX();
					String yCord = fingerPrintData.get(temp).getCordinates()
							.getY();
					Log.i(TAG, " wifiFingerPrint Cordinates X " + xCord
							+ " Cordinates " + yCord);
					editText.append(" xCord " + xCord + " yCord " + yCord);
					editText.append("\n");

				}

			}

		} catch (Exception e) {
			Log.e(TAG, " Exception in locate Wifimanager " + e.getMessage());
		}

	}

	public void getFingerPrintData() {
		String key, value, xCord, yCord;
		DecimalFormat df = new DecimalFormat("0.00");
		Log.i(TAG, " addSignal " + fingerPrintData.size());
		for (int temp = 0; temp < fingerPrintData.size(); temp++) {
			xCord = df.format(Double.parseDouble(fingerPrintData.get(temp)
					.getCordinates().getX()));
			yCord = df.format(Double.parseDouble(fingerPrintData.get(temp)
					.getCordinates().getY()));

			for (Entry<String, String> e : fingerPrintData.get(temp).mBssid
					.entrySet()) {
				key = e.getKey();
				value = e.getValue();
				Log.i(TAG, " addSignal  Key " + key + " Value " + value
						+ " XCord " + xCord + " yCord " + yCord);
				addSignalValue(key, value, xCord, yCord);
			}
		}
	}

	public void addSignalValue(String key, String value, String xCord,
			String yCord) {
		int signal = Math.abs(Integer.parseInt(value));

		if (finger[signal] == null) {
			finger[signal] = new ArrayList<FingerPrintCordinates>();
			Log.i(TAG, " ArrayList added for the first Time " + signal);
			finger[signal].add(new FingerPrintCordinates(xCord, yCord, key));
		} else {
			Log.i(TAG, " ArrayList Entry already exist " + signal);
			finger[signal].add(new FingerPrintCordinates(xCord, yCord, key));
		}
	}



	private List<FingerPrintCordinates> getFingerPrint(List<WifiSignalScan> wifiScanList) {
		Log.i(TAG, " ArrayList getFingerPrint Size " + wifiScanList.size());

		//List<WifiSignalScan> wifiScanList = new ArrayList<WifiSignalScan>();
		List<FingerPrintCordinates> fingerCoordinates = new ArrayList<FingerPrintCordinates>();
		
		ConfidenceLevel confLevel = new ConfidenceLevel();
/*		wifiScanList.add(wifiData.get(0));
		wifiScanList.add(wifiData.get(1));
		wifiScanList.add(wifiData.get(2));
*/
		Log.d(TAG, " ArrayList test List Size " + wifiScanList.size());

		for (int temp = 0; temp < wifiScanList.size(); temp++) {
			String bssid = null;
			int signal =0;
			Log.d(TAG," Initial RSSI Value " + Math.abs(wifiScanList.get(temp).getScanResult().level));
			int accSignal=0,accSignal1=0;
			int tempcount =0 ;
			bssid =  wifiScanList.get(temp).getScanResult().BSSID;

			signal = Math.abs(wifiScanList.get(temp).getScanResult().level);
			accSignal = Math.abs(wifiScanList.get(temp).getScanResult().level)-10;
			accSignal1 = Math.abs(wifiScanList.get(temp).getScanResult().level)+10;
			
			
			Log.i(TAG," compareWifiConfidence Index " + temp + " isBssidFound " 
					+ isBssidFound + " BSSID " + bssid  + 
						" Signal Level " + signal +" Lowest Signal Level "
								+ accSignal + " Highest Signal level " + accSignal1 + "Confidence ");
			
			for(int junk = accSignal ;junk<= accSignal1;junk++){
				tempcount++;
				int level = confLevel.getConfidenceLevel(tempcount);
				
				Log.i(TAG," Confidence of iterate " + tempcount);
				if(finger[junk]!= null){
					Log.i(TAG," Signal Level " + junk 
							+ " Available 	 size " + finger[junk].size() + " isBssidFound "+
							isBssidFound + " Confidence Level " +  level);
					if(isBssidFound == false ){
						FingerPrintCordinates fingerCord = getFingerPrintCordinates(bssid, junk ,level);
							if(fingerCord!= null){
								fingerCoordinates.add(fingerCord);
							}
					}
				}
			}
			
			isBssidFound = false ;
		}
		
		Log.i(TAG, " isBssidFound Coordinates size " + fingerCoordinates.size());
		return fingerCoordinates;
		
	/*	if(fingerCoordinates.size()>0){
			calculateTrilateration(fingerCoordinates);
		}*/
	}
	
	private FingerPrintCordinates getFingerPrintCordinates(String bssid, int rssi,int level ) {
		Log.i(TAG,"Signal Level  getCordinates  " + rssi );
		FingerPrintCordinates fingerCord = null;
			if (finger[rssi].size() > 0) {
				Log.i(TAG,
						"Signal Level  getFingerPrint array contain data  size "
								+ finger[rssi].size());

				for (int i = 0; i < finger[rssi].size(); i++) {
					Log.i(TAG,
							" Signal Level  getFingerPrint array contain data  size "
									+ finger[rssi].get(i).getBssid()
									+ " Scan Wifi SSID " + bssid);
					if (bssid.contentEquals(finger[rssi].get(i).getBssid())) {
						isBssidFound = true;
						Log.i(TAG, " Wifi Queue Poll " + " isBssidFound " + isBssidFound +  " Bssid  Matched "
								+ finger[rssi].get(i).getBssid()
								+ " x Cordinates " + finger[rssi].get(i).getX()
								+ " YCord " + finger[rssi].get(i).getY() + " Confidence Level " + level);
					
						fingerCord = new FingerPrintCordinates(finger[rssi].get(i).getX(),
									finger[rssi].get(i).getY(), finger[rssi].get(i).getBssid(),level);
						break;
					}
					
				}
			}
			return fingerCord;
	}
	
	
	private void calculateTrilateration(final List<FingerPrintCordinates> fingerPrint) {
		Log.i(TAG, " isBssidFound calculateTrilateration " + fingerPrint.size());
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				Intersect3Circles trilateration = new Intersect3Circles();
				if(fingerPrint.size()==2){
					trilateration.intersectionOf2Circles(Double.parseDouble(fingerPrint.get(0).getX()), 
							Double.parseDouble(fingerPrint.get(0).getY()), 5, Double.parseDouble(fingerPrint.get(1).getX()), Double.parseDouble(fingerPrint.get(1).getY()), 10);
					editText.append(" Trilateration 2 Circle  "+" xCord " + trilateration.getxCord() +
							 " yCord "+ trilateration.getyCord());
					editText.append("\n");
				} else if (fingerPrint.size()>2){
					trilateration.findThreeCirclesIntersection(Double.parseDouble(fingerPrint.get(0).getX()), 
							Double.parseDouble(fingerPrint.get(0).getY()), 5, Double.parseDouble(fingerPrint.get(1).getX()), Double.parseDouble(fingerPrint.get(1).getY()), 10 ,
							Double.parseDouble(fingerPrint.get(2).getX()), Double.parseDouble(fingerPrint.get(2).getY()), 12);
					editText.append(" Trilateration 3 Circle "+" xCord " + trilateration.getxCord() +
							 " yCord "+ trilateration.getyCord());
					editText.append("\n");
				}
			}
		});
	}
	
	private void enQueueWifiList(List<WifiSignalScan> wifiList){
		Log.i(TAG, " enQueueWifiList ");
		List<WifiSignalScan> wifiScanList = new ArrayList<WifiSignalScan>();
		wifiScanList.add(wifiList.get(0));
		wifiScanList.add(wifiList.get(1));
		wifiScanList.add(wifiList.get(2));
		Log.i(TAG, " enQueueWifiList " +  wifiScanList.size()); 
		
		if(wifiQue.isEmpty()){
			wifiQue.add(wifiScanList);
			Log.i(TAG, " enQueueWifiList : Queue Size " +  wifiQue.size()); 
		}else{
			wifiQue.offer(wifiScanList);
			Log.i(TAG, " enQueueWifiList : Queue Size " +  wifiQue.size()); 
		}	
		deQueWifiList();
	}
	
	private void deQueWifiList() {
		Log.i(TAG, " DeQueueWifiList Size " + wifiQue.size());

		if (wifiQue.size() == 3) {
			List<List<FingerPrintCordinates>> test = new ArrayList<List<FingerPrintCordinates>>();
			Log.i(TAG, "Wifi Queue Poll 1 ");
			test.add(getFingerPrint(wifiQue.poll()));
			Log.i(TAG, "Wifi Queue Poll 2 " );
			test.add(getFingerPrint(wifiQue.poll()));
			Log.i(TAG, "Wifi Queue Poll 3 " );
			test.add(getFingerPrint(wifiQue.poll()));

			compareWifiConfidence(test);
		}
	}

	private void compareWifiConfidence(final List<List<FingerPrintCordinates>> fingerPrint){
		Log.i(TAG, "compareWifiConfidence");
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				if(fingerPrint.size()==3){
					for(int temp=0 ;temp<fingerPrint.size();temp++){
							List<FingerPrintCordinates> object = fingerPrint.get(temp);
							
								if(object.size()==3){
									int level = object.get(0).getConfidenceLevel();
									int level1 = object.get(1).getConfidenceLevel();
									int level2 = object.get(2).getConfidenceLevel();

										if((level==0 && level1==0) ||(level1==0 && level2==0) || (level2==0 && level==0)){
											Log.i(TAG,"compareWifiConfidence : Confidence Level Matches " + level + " level 1 " +level1 );
											editText.append(" Index "+ temp + " Bssid 1 " + object.get(0).getBssid() + " Confidence Level 1 " + object.get(0).getConfidenceLevel() 
													+ " Bssid 2" + object.get(1).getBssid() + " Confidence Level 2 " + object.get(1).getConfidenceLevel()
													+ " Bssid 3 " +  object.get(2).getBssid() + " Confidence Level 3 "
													+ object.get(2).getConfidenceLevel());
											editText.append("\n");
											break;
										} else if((level== 1 && level1== 1) ||(level1==1 && level2==1) || (level2==1 && level==1)){
											Log.i(TAG,"compareWifiConfidence  : Confidence Level Matches " + level + " level 1 " +level1 );
											editText.append(" Index "+ temp + " Bssid 1 " + object.get(0).getBssid() + " Confidence Level 1 " + object.get(0).getConfidenceLevel() 
													+ " Bssid 2" + object.get(1).getBssid() + " Confidence Level 2 " + object.get(1).getConfidenceLevel()
													+ " Bssid 3 " +  object.get(2).getBssid() + " Confidence Level 3 "
													+ object.get(2).getConfidenceLevel());
											editText.append("\n");
											break;
										} else if((level== -1 && level1== -1) ||(level1== -1 && level2==-1) || (level2==-1 && level==-1)){
											Log.i(TAG,"compareWifiConfidence : Level Confidence Level Matches " + level + " level 1 " +level1 );
											editText.append(" Index "+ temp + " Bssid 1 " + object.get(0).getBssid() + " Confidence Level 1 " + object.get(0).getConfidenceLevel() 
													+ " Bssid 2" + object.get(1).getBssid() + " Confidence Level 2 " + object.get(1).getConfidenceLevel()
													+ " Bssid 3 " +  object.get(2).getBssid() + " Confidence Level 3 "
													+ object.get(2).getConfidenceLevel());
											editText.append("\n");
											break;
										} else if((level==2 && level1==2) ||(level1==2 && level2==2) || (level2==2 && level==2)){
											Log.i(TAG,"compareWifiConfidence Wifi Queue Poll :  Confidence Level Matches " + level + " level 1 " +level1 );
											editText.append(" Index "+ temp + " Bssid 1 " + object.get(0).getBssid() + " Confidence Level 1 " + object.get(0).getConfidenceLevel() 
													+ " Bssid 2" + object.get(1).getBssid() + " Confidence Level 2 " + object.get(1).getConfidenceLevel()
													+ " Bssid 3 " +  object.get(2).getBssid() + " Confidence Level 3 "
													+ object.get(2).getConfidenceLevel());
											editText.append("\n");
											break;
										} else if((level==-2 && level1==-2) ||(level1==-2 && level2==-2) || (level2==-2 && level==-2)){
											Log.i(TAG,"compareWifiConfidence :  Confidence Level Matches " + level + " level 1 " +level1 );
											editText.append(" Index "+ temp + " Bssid 1 " + object.get(0).getBssid() + " Confidence Level 1 " + object.get(0).getConfidenceLevel() 
													+ " Bssid 2" + object.get(1).getBssid() + " Confidence Level 2 " + object.get(1).getConfidenceLevel()
													+ " Bssid 3 " +  object.get(2).getBssid() + " Confidence Level 3 "
													+ object.get(2).getConfidenceLevel());
											editText.append("\n");
											break;
										}  else if((level==3 && level1==3) ||(level1==3 && level2==3) || (level2==3 && level==3)){
											Log.i(TAG,"compareWifiConfidence :  Confidence Level Matches " + level + " level 1 " +level1 );
											editText.append(" Index "+ temp + " Bssid 1 " + object.get(0).getBssid() + " Confidence Level 1 " + object.get(0).getConfidenceLevel() 
													+ " Bssid 2" + object.get(1).getBssid() + " Confidence Level 2 " + object.get(1).getConfidenceLevel()
													+ " Bssid 3 " +  object.get(2).getBssid() + " Confidence Level 3 "
													+ object.get(2).getConfidenceLevel());
											editText.append("\n");
											break;
										} else if((level==-3 && level1==-3) ||(level1==-3 && level2==-3) || (level2==-3 && level==-3)){
											Log.i(TAG,"compareWifiConfidence :  Confidence Level Matches " + level + " level 1 " +level1 );
											editText.append(" Index "+ temp + " Bssid 1 " + object.get(0).getBssid() + " Confidence Level 1 " + object.get(0).getConfidenceLevel() 
													+ " Bssid 2" + object.get(1).getBssid() + " Confidence Level 2 " + object.get(1).getConfidenceLevel()
													+ " Bssid 3 " +  object.get(2).getBssid() + " Confidence Level 3 "
													+ object.get(2).getConfidenceLevel());
											editText.append("\n");
											break;
										} else if((level==4 && level1==4) ||(level1==4 && level2==4) || (level2==4 && level==4)){
											Log.i(TAG,"compareWifiConfidence :  Confidence Level Matches " + level + " level 1 " +level1 );
											editText.append(" Index "+ temp + " Bssid 1 " + object.get(0).getBssid() + " Confidence Level 1 " + object.get(0).getConfidenceLevel() 
													+ " Bssid 2" + object.get(1).getBssid() + " Confidence Level 2 " + object.get(1).getConfidenceLevel()
													+ " Bssid 3 " +  object.get(2).getBssid() + " Confidence Level 3 "
													+ object.get(2).getConfidenceLevel());
											editText.append("\n");
											break;
										} else if((level==-4 && level1==-4) ||(level1==-4 && level2==-4) || (level2==-4 && level==-4)){
											Log.i(TAG,"compareWifiConfidence :  Confidence Level Matches " + level + " level 1 " +level1 );
											editText.append(" Index "+ temp + " Bssid 1 " + object.get(0).getBssid() + " Confidence Level 1 " + object.get(0).getConfidenceLevel() 
													+ " Bssid 2" + object.get(1).getBssid() + " Confidence Level 2 " + object.get(1).getConfidenceLevel()
													+ " Bssid 3 " +  object.get(2).getBssid() + " Confidence Level 3 "
													+ object.get(2).getConfidenceLevel());
											editText.append("\n");
											break;
										} else if((level==5 && level1==5) ||(level1==5 && level2==5) || (level2==5 && level==5)){
											Log.i(TAG,"compareWifiConfidence :  Confidence Level Matches " + level + " level 1 " +level1 );
											editText.append(" Index "+ temp + " Bssid 1 " + object.get(0).getBssid() + " Confidence Level 1 " + object.get(0).getConfidenceLevel() 
													+ " Bssid 2" + object.get(1).getBssid() + " Confidence Level 2 " + object.get(1).getConfidenceLevel()
													+ " Bssid 3 " +  object.get(2).getBssid() + " Confidence Level 3 "
													+ object.get(2).getConfidenceLevel());
											editText.append("\n");
											break;
										} else if((level==-5 && level1==-5) ||(level1==-5 && level2==-5) || (level2==-5 && level==-5)){
											Log.i(TAG,"compareWifiConfidence :  Confidence Level Matches " + level + " level 1 " +level1 );
											editText.append(" Index "+ temp + " Bssid 1 " + object.get(0).getBssid() + " Confidence Level 1 " + object.get(0).getConfidenceLevel() 
													+ " Bssid 2" + object.get(1).getBssid() + " Confidence Level 2 " + object.get(1).getConfidenceLevel()
													+ " Bssid 3 " +  object.get(2).getBssid() + " Confidence Level 3 "
													+ object.get(2).getConfidenceLevel());
											editText.append("\n");
											break;
										} else if((level==6 && level1==6) ||(level1==6 && level2==6) || (level2==6 && level==6)){
											Log.i(TAG,"compareWifiConfidence :  Confidence Level Matches " + level + " level 1 " +level1 );
											editText.append(" Index "+ temp + " Bssid 1 " + object.get(0).getBssid() + " Confidence Level 1 " + object.get(0).getConfidenceLevel() 
													+ " Bssid 2" + object.get(1).getBssid() + " Confidence Level 2 " + object.get(1).getConfidenceLevel()
													+ " Bssid 3 " +  object.get(2).getBssid() + " Confidence Level 3 "
													+ object.get(2).getConfidenceLevel());
											editText.append("\n");
											break;
										} else if((level==-6 && level1==-6) ||(level1==-6 && level2==-6) || (level2==-6 && level==-6)){
											Log.i(TAG,"compareWifiConfidence :  Confidence Level Matches " + level + " level 1 " +level1 );
											editText.append(" Index "+ temp + " Bssid 1 " + object.get(0).getBssid() + " Confidence Level 1 " + object.get(0).getConfidenceLevel() 
													+ " Bssid 2" + object.get(1).getBssid() + " Confidence Level 2 " + object.get(1).getConfidenceLevel()
													+ " Bssid 3 " +  object.get(2).getBssid() + " Confidence Level 3 "
													+ object.get(2).getConfidenceLevel());
											editText.append("\n");
											break;
										} else if((level==7 && level1==7) ||(level1==7 && level2==7) || (level2==7 && level==7)){
											Log.i(TAG,"compareWifiConfidence :  Confidence Level Matches " + level + " level 1 " +level1 );
											editText.append(" Index "+ temp + " Bssid 1 " + object.get(0).getBssid() + " Confidence Level 1 " + object.get(0).getConfidenceLevel() 
													+ " Bssid 2" + object.get(1).getBssid() + " Confidence Level 2 " + object.get(1).getConfidenceLevel()
													+ " Bssid 3 " +  object.get(2).getBssid() + " Confidence Level 3 "
													+ object.get(2).getConfidenceLevel());
											editText.append("\n");
											break;
										} else if((level==-7 && level1==-7) ||(level1==-7 && level2==-7) || (level2==-7 && level==-7)){
											Log.i(TAG,"compareWifiConfidence :  Confidence Level Matches " + level + " level 1 " +level1 );
											editText.append(" Index "+ temp + " Bssid 1 " + object.get(0).getBssid() + " Confidence Level 1 " + object.get(0).getConfidenceLevel() 
													+ " Bssid 2" + object.get(1).getBssid() + " Confidence Level 2 " + object.get(1).getConfidenceLevel()
													+ " Bssid 3 " +  object.get(2).getBssid() + " Confidence Level 3 "
													+ object.get(2).getConfidenceLevel());
											editText.append("\n");
											break;
										}else if((level==8 && level1==8) ||(level1==8 && level2==8) || (level2==8 && level==8)){
											Log.i(TAG,"compareWifiConfidence :  Confidence Level Matches " + level + " level 1 " +level1 );
											editText.append(" Index "+ temp + " Bssid 1 " + object.get(0).getBssid() + " Confidence Level 1 " + object.get(0).getConfidenceLevel() 
													+ " Bssid 2" + object.get(1).getBssid() + " Confidence Level 2 " + object.get(1).getConfidenceLevel()
													+ " Bssid 3 " +  object.get(2).getBssid() + " Confidence Level 3 "
													+ object.get(2).getConfidenceLevel());
											editText.append("\n");
											break;
										} else if((level==-8 && level1==-8) ||(level1==-8 && level2==-8) || (level2==-8 && level==-8)){
											Log.i(TAG,"compareWifiConfidence :  Confidence Level Matches " + level + " level 1 " +level1 );
											editText.append(" Index "+ temp + " Bssid 1 " + object.get(0).getBssid() + " Confidence Level 1 " + object.get(0).getConfidenceLevel() 
													+ " Bssid 2" + object.get(1).getBssid() + " Confidence Level 2 " + object.get(1).getConfidenceLevel()
													+ " Bssid 3 " +  object.get(2).getBssid() + " Confidence Level 3 "
													+ object.get(2).getConfidenceLevel());
											editText.append("\n");
											break;
										}else if((level==9 && level1==9) ||(level1==9 && level2==9) || (level2==9 && level==9)){
											Log.i(TAG,"compareWifiConfidence :  Confidence Level Matches " + level + " level 1 " +level1 );
											editText.append(" Index "+ temp + " Bssid 1 " + object.get(0).getBssid() + " Confidence Level 1 " + object.get(0).getConfidenceLevel() 
													+ " Bssid 2" + object.get(1).getBssid() + " Confidence Level 2 " + object.get(1).getConfidenceLevel()
													+ " Bssid 3 " +  object.get(2).getBssid() + " Confidence Level 3 "
													+ object.get(2).getConfidenceLevel());
											editText.append("\n");
											break;
										} else if((level==-9 && level1==-9) ||(level1==-9 && level2==-9) || (level2==-9 && level==-9)){
											Log.i(TAG,"compareWifiConfidence :  Confidence Level Matches " + level + " level 1 " +level1 );
											editText.append(" Index "+ temp + " Bssid 1 " + object.get(0).getBssid() + " Confidence Level 1 " + object.get(0).getConfidenceLevel() 
													+ " Bssid 2" + object.get(1).getBssid() + " Confidence Level 2 " + object.get(1).getConfidenceLevel()
													+ " Bssid 3 " +  object.get(2).getBssid() + " Confidence Level 3 "
													+ object.get(2).getConfidenceLevel());
											editText.append("\n");
											break;
										} else if((level==10 && level1==10) ||(level1==10 && level2==10) || (level2==10 && level==10)){
											Log.i(TAG,"compareWifiConfidence :  Confidence Level Matches " + level + " level 1 " +level1 );
											editText.append(" Index "+ temp + " Bssid 1 " + object.get(0).getBssid() + " Confidence Level 1 " + object.get(0).getConfidenceLevel() 
													+ " Bssid 2" + object.get(1).getBssid() + " Confidence Level 2 " + object.get(1).getConfidenceLevel()
													+ " Bssid 3 " +  object.get(2).getBssid() + " Confidence Level 3 "
													+ object.get(2).getConfidenceLevel());
											editText.append("\n");
											break;
										} else if((level==-10 && level1==-10) ||(level1==-10 && level2==-10) || (level2==-10 && level==-10)){
											Log.i(TAG,"compareWifiConfidence :  Confidence Level Matches " + level + " level 1 " +level1 );
											editText.append(" Index "+ temp + " Bssid 1 " + object.get(0).getBssid() + " Confidence Level 1 " + object.get(0).getConfidenceLevel() 
													+ " Bssid 2" + object.get(1).getBssid() + " Confidence Level 2 " + object.get(1).getConfidenceLevel()
													+ " Bssid 3 " +  object.get(2).getBssid() + " Confidence Level 3 "
													+ object.get(2).getConfidenceLevel());
											editText.append("\n");
											break;
										}
								}
					}
				}

			}
		});
	}

	
	private void getShortedFingerPrintList() {
		String bssidKey;
		int signalValue;
		String key, value, xCord = null, yCord = null;
		DecimalFormat df = new DecimalFormat("0.00");
		List<FingerPrintCordinates> signalFinger = new ArrayList<FingerPrintCordinates>();

		for (int temp = 0; temp < fingerPrintData.size(); temp++) {
			Log.i(TAG," Signal Value XCord " + xCord);
			for (Entry<String, String> e : fingerPrintData.get(temp).mBssid
					.entrySet()) {
				bssidKey = e.getKey();
				signalValue = Math.abs(Integer.parseInt(e.getValue()));
				FingerPrintCordinates finger = new FingerPrintCordinates(bssidKey, signalValue);
				signalFinger.add(finger);
				//getSignalMap(+, bssidKey);
			}
		}
		//sortFingerPrintMap();
		
		//Collections.sort(signal);
		Collections.sort(signalFinger, Sorting.fingerSignalComp);
		
		for(int temp = 0 ;temp <signalFinger.size();temp++){
			Log.i(TAG," Signal Value " + signalFinger.size() + " Signal " + signalFinger.get(temp).getRssi() +
				" Bssid "	+ signalFinger.get(temp).getBssid() );
		}
	}
}
