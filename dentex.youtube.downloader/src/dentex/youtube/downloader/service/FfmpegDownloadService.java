/***
 	Copyright (c) 2012-2013 Samuele Rini
 	
	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	
	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
	GNU General Public License for more details.
	
	You should have received a copy of the GNU General Public License
	along with this program. If not, see http://www.gnu.org/licenses
	
	***
	
	https://github.com/dentex/ytdownloader/
    https://sourceforge.net/projects/ytdownloader/
	
	***
	
	Different Licenses and Credits where noted in code comments.
*/


package dentex.youtube.downloader.service;

import java.io.File;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.bugsense.trace.BugSenseHandler;

import dentex.youtube.downloader.R;
import dentex.youtube.downloader.SettingsActivity;
import dentex.youtube.downloader.YTD;
import dentex.youtube.downloader.utils.Observer;
import dentex.youtube.downloader.utils.Utils;

public class FfmpegDownloadService extends Service {
	
	private static final String DEBUG_TAG = "FfmpegDownloadService";
	public static Context nContext;
	private int cpuVers;
	private String sdCardAppDir;
	public static String DIR;
	private DownloadManager dm;
	private Observer.YtdFileObserver ffmpegBinObserver;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		Utils.logger("d", "service created", DEBUG_TAG);
		BugSenseHandler.initAndStartSession(this, YTD.BugsenseApiKey);
		BugSenseHandler.leaveBreadcrumb("FfmpegDownloadService_onCreate");
		nContext = getBaseContext();	
		registerReceiver(ffmpegReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
	}

	public static Context getContext() {
        return nContext;
    }
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		cpuVers = intent.getIntExtra("CPU", 7);
		Utils.logger("d", "arm CPU version: " + cpuVers, DEBUG_TAG);
		
		sdCardAppDir = intent.getStringExtra("DIR");
		DIR = sdCardAppDir;
		
		downloadFfmpeg();
		
		super.onStartCommand(intent, flags, startId);
		return START_NOT_STICKY;
	}
	
	@Override
	public void onDestroy() {
		Utils.logger("d", "service destroyed", DEBUG_TAG);
		unregisterReceiver(ffmpegReceiver);
	}
	
	private void downloadFfmpeg() {
		String link = getString(R.string.ffmpeg_download_dialog_msg_link, cpuVers);

		Utils.logger("d", "FFmpeg download link: " + link, DEBUG_TAG);
		
        Request request = new Request(Uri.parse(link));
        request.setDestinationInExternalFilesDir(nContext, null, YTD.ffmpegBinName);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setTitle(getString(R.string.ffmpeg_download_notification));
        dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        try {
        	dm.enqueue(request);
        } catch (IllegalArgumentException e) {
	    	Log.e(DEBUG_TAG, "downloadFfmpeg: " + e.getMessage());
	    	Toast.makeText(this,  this.getString(R.string.no_downloads_sys_app), Toast.LENGTH_LONG).show();
	    	BugSenseHandler.sendExceptionMessage(DEBUG_TAG + "-> downloadFfmpeg", e.getMessage(), e);
	    } catch (SecurityException se) {
	    	request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, YTD.ffmpegBinName);
	    	dm.enqueue(request);
	    	DIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
	    	BugSenseHandler.sendExceptionMessage(DEBUG_TAG + "-> downloadFfmpeg", se.getMessage(), se);
	    } catch (NullPointerException ne) {
	    	Log.e(DEBUG_TAG, "callDownloadApk: " + ne.getMessage());
	    	BugSenseHandler.sendExceptionMessage(DEBUG_TAG + "-> callDownloadApk: ", ne.getMessage(), ne);
	    	Toast.makeText(this, getString(R.string.error), Toast.LENGTH_LONG).show();
	    }
        
		ffmpegBinObserver = new Observer.YtdFileObserver(DIR);
        ffmpegBinObserver.startWatching();
	}

	BroadcastReceiver ffmpegReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Utils.logger("d", "ffmpegReceiver: onReceive CALLED", DEBUG_TAG);
    		long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
    		
    		//if (enqueue != -1 && id != -2 && id == enqueue) {
	    		Query query = new Query();
				query.setFilterById(id);
				Cursor c = dm.query(query);
				if (c.moveToFirst()) {
				
					int statusIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
					int reasonIndex = c.getColumnIndex(DownloadManager.COLUMN_REASON);
					int status = c.getInt(statusIndex);
					int reason = c.getInt(reasonIndex);

					switch (status) {
					
					case DownloadManager.STATUS_SUCCESSFUL:
	    		
						File src = new File(DIR, YTD.ffmpegBinName);
						File dst = new File(nContext.getDir("bin", 0), YTD.ffmpegBinName);
						
						String md5 = null;
						if (cpuVers == 7) md5 = "33fcf4d5a3b2e5193bd42c2c1fc2abc7";
						if (cpuVers == 5) md5 = "0606931cfbaca351a47e59ab198bc81e";
						
						if (Utils.checkMD5(md5, src)) {
							SettingsActivity.SettingsFragment.copyFfmpegToAppDataDir(context, src, dst);
						} else {
							SettingsActivity.SettingsFragment.touchAdvPref(true, false);
							deleteBadDownload(id);
						}
						break;
						
					case DownloadManager.STATUS_FAILED:
						Log.e(DEBUG_TAG, YTD.ffmpegBinName + ", _ID " + id + " FAILED (status " + status + ")");
						Log.e(DEBUG_TAG, " Reason: " + reason);
						Toast.makeText(nContext,  YTD.ffmpegBinName + ": " + getString(R.string.download_failed), Toast.LENGTH_LONG).show();
						
						SettingsActivity.SettingsFragment.touchAdvPref(true, false);
						deleteBadDownload(id);
						break;
						
					default:
						Utils.logger("w", YTD.ffmpegBinName + ", _ID " + id + " completed with status " + status, DEBUG_TAG);
					}
				}
    		//}
    		ffmpegBinObserver.stopWatching();
    		stopSelf();
		}
	};
	
	private void deleteBadDownload (long id) {
		dm.remove(id);
		Toast.makeText(this, getString(R.string.download_failed), Toast.LENGTH_LONG).show();
		
	}
	
}
