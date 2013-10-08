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

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.bugsense.trace.BugSenseHandler;

import dentex.youtube.downloader.utils.PopUps;

public class YTD extends Application {
	
	static String DEBUG_TAG = "YTD";

	public static String BugsenseApiKey = "874942f2";

	public static SharedPreferences settings;
	public static final String PREFS_NAME = "dentex.youtube.downloader_preferences";
	
	@Override
    public void onCreate() {
		Log.d(DEBUG_TAG, "onCreate");
		settings = getSharedPreferences(PREFS_NAME, 0);
		
        BugSenseHandler.initAndStartSession(getApplicationContext(), BugsenseApiKey);
		
		checkDownloadProvider(".apk");
		checkDownloadProvider("Ui.apk");
        	
        super.onCreate();
	}

	private void checkDownloadProvider(String suffix) {
		File downProvSysApp = new File("/system/app/DownloadProvider" + suffix);
		if (downProvSysApp.exists()) {
			settings.edit().putBoolean("DOWNLOAD_PROVIDER_" + suffix, true).commit();
		} else {
			settings.edit().putBoolean("DOWNLOAD_PROVIDER_" + suffix, false).commit();
		}
		Log.i(DEBUG_TAG, "DownloadProvider" + suffix + " system app present: " + downProvSysApp.exists());
		BugSenseHandler.leaveBreadcrumb("DownloadProvider" + suffix + ": " + downProvSysApp.exists());
	}

	public static void NoDownProvPopUp(Context context) {
		PopUps.showPopUp(context.getString(R.string.no_downloads_sys_app), context.getString(R.string.ytd_useless), "alert", context);
	}
}
