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

package dentex.youtube.downloader.utils;

import java.io.File;

import android.os.FileObserver;
import dentex.youtube.downloader.SettingsActivity;
import dentex.youtube.downloader.service.FfmpegDownloadService;

// reference:
// https://gist.github.com/shirou/659180
// https://github.com/shirou
	
public class Observer {
	
	static final String DEBUG_TAG = "Observer";
	public static String observedPath;

	public static class YtdFileObserver extends FileObserver {
	    static final String TAG="FileObserver: ";
	
		static final int mask = (FileObserver.CREATE | FileObserver.DELETE); 
		
		public YtdFileObserver(String root){
			super(root, mask);
			observedPath = root;
			if (! root.endsWith(File.separator)){
				root += File.separator;
			}
		}
	
		public void onEvent(int event, String path) {
			//Utils.logger("d", TAG + "onEvent " + event + ", " + path, DEBUG_TAG);
			
			if (event == FileObserver.CREATE) {
				Utils.logger("d", TAG + "file " + path + " CREATED", DEBUG_TAG);
				
				if (observedPath.equals(FfmpegDownloadService.DIR)) {
					SettingsActivity.SettingsFragment.touchAdvPref(false, false);
				}
			}
			
			if (event == FileObserver.DELETE) {
				Utils.logger("d", TAG + "file " + path + " DELETED", DEBUG_TAG);

				if (observedPath.equals(FfmpegDownloadService.DIR)) {
					SettingsActivity.SettingsFragment.touchAdvPref(true, false);
				}
			}
		}
	
		public void close(){
			super.finalize();
		}
	}
}
