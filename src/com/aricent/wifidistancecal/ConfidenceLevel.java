package com.aricent.wifidistancecal;

import android.util.Log;

public class ConfidenceLevel {
	protected static final String TAG = ConfidenceLevel.class.getName()
			.toString();

	public int getConfidenceLevel(int level) {
		int confidenceLevel = 0;

		switch (level) {
		case 1:
			confidenceLevel = -10;
			Log.i(TAG, " Confidence Level ");
			break;
		case 2:
			confidenceLevel = -9;
			Log.i(TAG, " Confidence Level ");
			break;
		case 3:
			confidenceLevel = -8;
			Log.i(TAG, " Confidence Level ");
			break;
		case 4:
			confidenceLevel = -7;
			Log.i(TAG, " Confidence Level ");
			break;
		case 5:
			confidenceLevel = -6;
			Log.i(TAG, " Confidence Level ");
			break;
		case 6:
			confidenceLevel = -5;
			Log.i(TAG, " Confidence Level ");
			break;
		case 7:
			confidenceLevel = -4;
			Log.i(TAG, " Confidence Level ");
			break;
		case 8:
			confidenceLevel = -3;
			Log.i(TAG, " Confidence Level ");
			break;
		case 9:
			confidenceLevel = -2;
			Log.i(TAG, " Confidence Level ");
			break;
		case 10:
			confidenceLevel = -1;
			Log.i(TAG, " Confidence Level ");
			break;
		case 11:
			confidenceLevel = 0;
			Log.i(TAG, " Confidence Level ");
			break;
		case 12:
			confidenceLevel = 1;
			Log.i(TAG, " Confidence Level ");
			break;
		case 13:
			confidenceLevel = 2;
			Log.i(TAG, " Confidence Level ");
			break;
		case 14:
			confidenceLevel = 3;
			Log.i(TAG, " Confidence Level ");
			break;
		case 15:
			confidenceLevel = 4;
			Log.i(TAG, " Confidence Level ");
			break;
		case 16:
			confidenceLevel = 5;
			Log.i(TAG, " Confidence Level ");
			break;
		case 17:
			confidenceLevel = 6;
			Log.i(TAG, " Confidence Level ");
			break;
		case 18:
			confidenceLevel = 7;
			Log.i(TAG, " Confidence Level ");
			break;
		case 19:
			confidenceLevel = 8;
			Log.i(TAG, " Confidence Level ");
			break;
		case 20:
			confidenceLevel = 9;
			Log.i(TAG, " Confidence Level ");
			break;
		case 21:
			confidenceLevel = 10;
			Log.i(TAG, " Confidence Level ");
			break;

		default:
			break;
		}
		return  confidenceLevel;
	}
}
