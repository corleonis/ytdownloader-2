package com.matsuhiro.android.download;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.bugsense.trace.BugSenseHandler;

public class Maps {
	private static String DEBUG_TAG = "ADML_Maps";
	public static Map<Long, DownloadTask> dtMap = new HashMap<Long, DownloadTask>();
	public static Map<Long, Integer> mDownloadPercentMap = new HashMap<Long, Integer>();
	public static Map<Long, Long> mDownloadSizeMap = new HashMap<Long, Long>();
	public static Map<Long, Long> mTotalSizeMap = new HashMap<Long, Long>();
	public static Map<Long, Long> mNetworkSpeedMap = new HashMap<Long, Long>();
	
	public static void removeFromAllMaps(long ID) {
		try {
			dtMap.remove(ID);
			mDownloadPercentMap.remove(ID);
			mDownloadSizeMap.remove(ID);
			mTotalSizeMap.remove(ID);
			mNetworkSpeedMap.remove(ID);
		} catch (UnsupportedOperationException e) {
			Log.e(DEBUG_TAG, "ID " + ID + " removeFromAllMaps: " + e.getMessage());
			BugSenseHandler.sendExceptionMessage(DEBUG_TAG, "ID " + ID + " removeFromAllMaps: " + e.getMessage(), e);
		}
	}
	
	public static long getTotalNetworkSpeed() {
		
		return 0;
	}
}