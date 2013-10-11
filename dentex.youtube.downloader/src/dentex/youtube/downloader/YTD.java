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

package dentex.youtube.downloader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.bugsense.trace.BugSenseHandler;

import dentex.youtube.downloader.utils.PopUps;
import dentex.youtube.downloader.utils.Utils;

public class YTD extends Application {
	
	static String DEBUG_TAG = "YTD";
	public static Context ctx;

	public static String BugsenseApiKey = "00000000";
	
	public static final String JSON_FILENAME = "dashboard.json";
	public static final String JSON_FOLDER = "json";
	public static File JSON_FILE = null;
	
	public static final String JSON_DATA_ID = "id";
	public static final String JSON_DATA_YTID = "ytid";
	public static final String JSON_DATA_POS = "pos";
	public static final String JSON_DATA_TYPE = "type";
	public static final String JSON_DATA_TYPE_V = "VIDEO";
	public static final String JSON_DATA_TYPE_A_E = "AUDIO-EXTR";
	public static final String JSON_DATA_TYPE_A_M = "AUDIO-MP3";
	public static final String JSON_DATA_STATUS = "status";
	public static final String JSON_DATA_STATUS_COMPLETED = "COMPLETED";
	public static final String JSON_DATA_STATUS_IN_PROGRESS = "IN_PROGRESS";
	public static final String JSON_DATA_STATUS_FAILED = "FAILED";
	public static final String JSON_DATA_STATUS_PAUSED = "PAUSED";
	public static final String JSON_DATA_STATUS_IMPORTED = "IMPORTED";
	public static final String JSON_DATA_PATH = "path";
	public static final String JSON_DATA_FILENAME = "filename";
	public static final String JSON_DATA_BASENAME = "basename";
	public static final String JSON_DATA_AUDIO_EXT = "audio_ext";
	public static final String JSON_DATA_SIZE = "size";
	
	public static final String ffmpegBinName = "ffmpeg";
	
	public static SharedPreferences settings;
	public static SharedPreferences videoinfo;
	
	public static String PREFS_NAME = "dentex.youtube.downloader_preferences";
	public static String VIDEOINFO_NAME = "dentex.youtube.downloader_videoinfo";
	
	public static String pt1;
	public static String pt2;
	public static String noDownloads;
	public static NotificationManager mNotificationManager;
	public static NotificationCompat.Builder mBuilder;
	public static List<Long> sequence = new ArrayList<Long>();
	
	public static String USER_AGENT_FIREFOX = "Mozilla/5.0 (X11; Linux i686; rv:10.0) Gecko/20100101 Firefox/10.0";
	public static File dir_Downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
	public static File dir_DCIM = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
	public static File dir_Movies = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
	
	public static int uid;

	public static final String THUMBS_FOLDER = "thumbs";
	public static double reduceFactor;
	
	@Override
	public void onCreate() {
		Log.d(DEBUG_TAG, "onCreate");

		settings = getSharedPreferences(PREFS_NAME, 0);
		videoinfo = getSharedPreferences(VIDEOINFO_NAME, 0);
		
		BugSenseHandler.initAndStartSession(getApplicationContext(), BugsenseApiKey);
		
		//findProcessUid();
		
		ctx = getApplicationContext(); 
		JSON_FILE = new File(ctx.getDir(JSON_FOLDER, 0), JSON_FILENAME);
		
		detectFirstLaunch();

		super.onCreate();
	}

	/*private void findProcessUid() {
		uid = android.os.Process.myUid();
		Log.d(DEBUG_TAG, "YTD's UID: " + uid);
	}*/
	
	private void detectFirstLaunch() {
		if (settings.getBoolean("first_launch", true)) {
			Log.i(DEBUG_TAG, "First launch for YTD!");
			settings.edit().putBoolean("first_launch", false).apply();
			reduceFactor = detectScreenDensity();
			
			JSON_FILE.delete();
			videoinfo.edit().clear().apply();
		} else {
			reduceFactor = Double.parseDouble(settings.getString("REDUCE_FACTOR", "1"));
			Log.d(DEBUG_TAG, "Retrieved a REDUCE_FACTOR of " + reduceFactor + " from prefs");
		}
	}

	private double detectScreenDensity() {
		DisplayMetrics displayMetrics = new DisplayMetrics();
	    ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);

	    double rf = 1;
		int density = displayMetrics.densityDpi;
		
		switch (density) {
	    case DisplayMetrics.DENSITY_HIGH: 
	    	rf = 1.44;
	    	break;
	    case DisplayMetrics.DENSITY_MEDIUM:
	    	rf = 2;
	    	break;
	    case DisplayMetrics.DENSITY_LOW:
	    	rf = 3;
	    }
	    Log.d(DEBUG_TAG, "DispalyDensity: " + density + " - storing a REDUCE_FACTOR of " + rf + " into prefs");
	    settings.edit().putString("REDUCE_FACTOR", String.valueOf(rf)).apply();;
	    return rf;
	}

	public static void NoDownProvPopUp(Context context) {
		PopUps.showPopUp(context.getString(R.string.no_downloads_sys_app), context.getString(R.string.ytd_useless), "alert", context);
	}
	
    public static void NotificationHelper() {
    	pt1 = ctx.getString(R.string.notification_downloading_pt1);
    	pt2 = ctx.getString(R.string.notification_downloading_pt2);
    	noDownloads = ctx.getString(R.string.notification_no_downloads);
    	
    	mBuilder =  new NotificationCompat.Builder(ctx);
    	
    	mBuilder.setSmallIcon(R.drawable.ic_stat_ytd)
    			.setOngoing(true)
    	        .setContentTitle(ctx.getString(R.string.title_activity_share))
    	        .setContentText(pt1 + " " + sequence.size() + " " + pt2);
    	
    	mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
    	
    	Intent notificationIntent = new Intent(ctx, DashboardActivity.class);
    	notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    	PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, notificationIntent, 0);
    	mBuilder.setContentIntent(contentIntent);
    	mNotificationManager.notify(1, mBuilder.build());
	}
    
    public static void removeIdUpdateNotification(long id) {
    	try {
	    	if (id != 0) {
				if (sequence.remove(id)) {
					Utils.logger("d", "ID " + id + " REMOVED from Notification", DEBUG_TAG);
				} else {
					Utils.logger("d", "ID " + id + " Already REMOVED from Notification", DEBUG_TAG);
				}
			} else {
				Utils.logger("w", "ID  not found!", DEBUG_TAG);
			}
			
	    	Utils.setNotificationDefaults(mBuilder);

			if (sequence.size() > 0) {
				mBuilder.setContentText(pt1 + " " + sequence.size() + " " + pt2).setOngoing(true);
				mNotificationManager.notify(1, mBuilder.build());
			} else {
				mBuilder.setContentText(noDownloads).setOngoing(false);
				mNotificationManager.notify(1, mBuilder.build());
				Utils.logger("d", "No downloads in progress.", DEBUG_TAG);
			}
		} catch (NullPointerException e) {
			Log.e(DEBUG_TAG, "NPE at removeIdUpdateNotification: " + e.getMessage());
			BugSenseHandler.sendExceptionMessage("NPE at removeIdUpdateNotification", e.getMessage(), e);
		}
	}
}
