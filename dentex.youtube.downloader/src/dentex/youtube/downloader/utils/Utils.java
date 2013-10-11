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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.OnScanCompletedListener;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import dentex.youtube.downloader.R;
import dentex.youtube.downloader.SettingsActivity.SettingsFragment;
import dentex.youtube.downloader.YTD;

public class Utils {
	
	static String DEBUG_TAG = "Utils";
	static MediaScannerConnection msc;
	
	public static void reload(Activity activity) {
    	Intent intent = activity.getIntent();
    	intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
    	activity.finish();
    	activity.overridePendingTransition(0, 0);
    	activity.startActivity(intent);
    	activity.overridePendingTransition(0, 0);
    }
    
    public static void themeInit(Context context) {
		String theme = YTD.settings.getString("choose_theme", "D");
    	if (theme.equals("D")) {
    		context.setTheme(R.style.AppThemeDark);
    	} else {
    		context.setTheme(R.style.AppThemeLight);
    	}
	}
    
    public static void langInit(Context context) {
    	String storedDefLang = YTD.settings.getString("DEF_LANG", "");
    	if (storedDefLang.isEmpty() && storedDefLang != null) {	
    		Locale defLocale = Locale.getDefault();
    		String defLang = defLocale.getLanguage();
    		YTD.settings.edit().putString("DEF_LANG", defLang).commit();
    	}
    		
		String lang  = YTD.settings.getString("lang", "default");
        Locale locale;
		if (!lang.equals("default")) {
			String[] fLang = filterLang(lang);
	        locale = new Locale(fLang[0], fLang[1]);
	        Locale.setDefault(locale);
	        Configuration config = new Configuration();
	        config.locale = locale;
        } else {
        	locale = new Locale(YTD.settings.getString("DEF_LANG", ""));
        	Locale.setDefault(locale);
        }
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config, null);
	}
    
    private static String[] filterLang(String lang) {
		if (lang.equals("el_GR") || 
			lang.equals("bg_BG") || 
			lang.equals("hu_HU") || 
			lang.equals("ja_JP") || 
			lang.equals("pl_PL") ||
			lang.equals("pt_BR") || 
			lang.equals("pt_PT") || 
			lang.equals("sl_SI") || 
			lang.equals("tr_TR") || 
			lang.equals("zh_CN") ||
			lang.equals("zh_HK") ||
			lang.equals("zh_TW")) 
				return lang.split("_");
		return new String[] { lang, "" };
	}

	public static void logger(String type, String msg, String tag) {
    	if (YTD.settings.getBoolean("enable_logging", false)) {
	    	if (type.equals("v")) {
	    		Log.v(tag, msg);
	    	} else if (type.equals("d")) {
	    		Log.d(tag, msg);
	    	} else if (type.equals("i")) {
	    		Log.i(tag, msg);
	    	} else if (type.equals("w")) {
	    		Log.w(tag, msg);
	    	}
    	}
    }
    
	public static int pathCheck(File path) {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			if (path.canWrite()) {
				return 0;
			} else {
				logger("w", "Path not writable", DEBUG_TAG);
				return 1;
			}
		} else {
			logger("w", "Path not mounted", DEBUG_TAG);
			return 2;
		}
	 }
	
	 public static void writeToFile(File file, String content) {
    	try {
	        InputStream is = new ByteArrayInputStream(content.getBytes("UTF-8"));
	        OutputStream os = new FileOutputStream(file);
	        byte[] data = new byte[is.available()];
	        is.read(data);
	        os.write(data);
	        is.close();
	        os.close();
		} catch (IOException e) {
			Log.e(DEBUG_TAG, "Error creating '" + file.getName() + "' Log file", e);
		}
	}
    
    public static void setNotificationDefaults(NotificationCompat.Builder aBuilder) {
    	String def = YTD.settings.getString("notification_defaults", "0");
    	if (aBuilder != null) {
			if (def.equals("0")) {
				aBuilder.setDefaults(Notification.DEFAULT_SOUND
						| Notification.DEFAULT_VIBRATE);
			}
			if (def.equals("1")) {
				aBuilder.setDefaults(Notification.DEFAULT_SOUND
						| Notification.DEFAULT_LIGHTS);
			}
			if (def.equals("2")) {
				aBuilder.setDefaults(Notification.DEFAULT_LIGHTS
						| Notification.DEFAULT_VIBRATE);
			}
			if (def.equals("3")) {
				aBuilder.setDefaults(Notification.DEFAULT_ALL);
			}
			if (def.equals("4")) {
				aBuilder.setDefaults(Notification.DEFAULT_SOUND);
			}
			if (def.equals("5")) {
				aBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
			}
			if (def.equals("6")) {
				aBuilder.setDefaults(Notification.DEFAULT_LIGHTS);
			}
			if (def.equals("7")) {
				// nothing...
			}
		}
    }
    
    // --------------------------------------------------------------------------
    
    /*
     * method readFromFile adapted from Stack Overflow:
	 * http://stackoverflow.com/questions/2902689/how-can-i-read-a-text-file-from-the-sd-card-in-android
	 * 
	 * Q: http://stackoverflow.com/users/349664/rsss
	 * A: http://stackoverflow.com/users/3171/dave-webb
	 */
    
    public static String readFromFile(File file) throws IOException {
 
        StringBuilder text = null;
        if(file.exists()) {   
            text = new StringBuilder();  
            BufferedReader br = new BufferedReader(new FileReader(file));  
            String line;  
            while ((line = br.readLine()) != null) {  
                text.append(line);  
                text.append('\n');  
            }
            br.close();
        }
        return text.toString();
    }
    
    /*
     * method MakeSizeHumanReadable(int bytes, boolean si) from Stack Overflow:
	 * http://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java
	 * 
	 * Q: http://stackoverflow.com/users/404615/iimuhin
	 * A: http://stackoverflow.com/users/276052/aioobe
	 */
	 
	@SuppressLint("DefaultLocale")
	public static String MakeSizeHumanReadable(long bytes, boolean decimal) {
		String hr = "-";
		int unit = decimal ? 1000 : 1024;
	    if (bytes < unit) {
	    	hr = bytes + " B";
		} else {
			int exp = (int) (Math.log(bytes) / Math.log(unit));
			String pre = (decimal ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (decimal ? "" : "i");
			hr = String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
		}
		hr = hr.replace("-1 B", "-").replace("0 B", "-");
	    return hr;
	}
    
	/* class VersionComparator from Stack Overflow:
	 * 
	 * http://stackoverflow.com/questions/198431/how-do-you-compare-two-version-strings-in-java
	 * 
	 * Q: http://stackoverflow.com/users/1288/bill-the-lizard
	 * A: http://stackoverflow.com/users/57695/peter-lawrey
	 */
	
    public static class VersionComparator {

        public static String compare(String v1, String v2) {
            String s1 = normalisedVersion(v1);
            String s2 = normalisedVersion(v2);
            int cmp = s1.compareTo(s2);
            String cmpStr = cmp < 0 ? "<" : cmp > 0 ? ">" : "==";
            return cmpStr;
        }

        public static String normalisedVersion(String version) {
            return normalisedVersion(version, ".", 4);
        }

        public static String normalisedVersion(String version, String sep, int maxWidth) {
            String[] split = Pattern.compile(sep, Pattern.LITERAL).split(version);
            StringBuilder sb = new StringBuilder();
            for (String s : split) {
                sb.append(String.format("%" + maxWidth + 's', s));
            }
            return sb.toString();
        }
    }

	public static int currentHashCode;
	
	public static int getSigHash(SettingsFragment sf) {

		try {
			Signature[] sigs = sf.getActivity().getPackageManager().getPackageInfo(sf.getActivity().getPackageName(), PackageManager.GET_SIGNATURES).signatures;
			for (Signature sig : sigs) {
				currentHashCode = sig.hashCode();
				logger("d", "getSigHash: App signature " + currentHashCode, DEBUG_TAG);
			}
		} catch (NameNotFoundException e) {
		    Log.e(DEBUG_TAG, "getSigHash: App signature not found; " + e.getMessage());
		}
		return currentHashCode;
	}

	/*
	 * checkMD5(String md5, File file)
	 * -------------------------------
	 * 
	 * Copyright (C) 2012 The CyanogenMod Project
	 *
	 * * Licensed under the GNU GPLv2 license
	 *
	 * The text of the license can be found in the LICENSE_GPL file
	 * or at https://www.gnu.org/licenses/gpl-2.0.txt
	 */
	
	public static boolean checkMD5(String md5, File file) {
        if (md5 == null || md5.equals("") || file == null) {
            Log.e(DEBUG_TAG, "MD5 String NULL or File NULL");
            return false;
        }

        String calculatedDigest = calculateMD5(file);
        if (calculatedDigest == null) {
            Log.e(DEBUG_TAG, "calculatedDigest NULL");
            return false;
        }

        Log.i(DEBUG_TAG, "Calculated digest: " + calculatedDigest);
        Log.i(DEBUG_TAG, "Provided digest: " + md5);

        return calculatedDigest.equalsIgnoreCase(md5);
    }

	/*
	 * calculateMD5(File file)
	 * -----------------------
	 * 
	 * Copyright (C) 2012 The CyanogenMod Project
	 *
	 * * Licensed under the GNU GPLv2 license
	 *
	 * The text of the license can be found in the LICENSE_GPL file
	 * or at https://www.gnu.org/licenses/gpl-2.0.txt
	 */
	
    public static String calculateMD5(File file) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            Log.e(DEBUG_TAG, "Exception while getting Digest", e);
            return null;
        }

        InputStream is;
        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            Log.e(DEBUG_TAG, "Exception while getting FileInputStream", e);
            return null;
        }

        byte[] buffer = new byte[8192];
        int read;
        try {
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
            byte[] md5sum = digest.digest();
            BigInteger bigInt = new BigInteger(1, md5sum);
            String output = bigInt.toString(16);
            // Fill to 32 chars
            output = String.format("%32s", output).replace(' ', '0');
            return output;
        } catch (IOException e) {
        	//throw new RuntimeException("Unable to process file for MD5", e);
        	Log.e(DEBUG_TAG, "Unable to process file for MD5", e); //TODO check if actually avoid FC 
        	return "00000000000000000000000000000000"; // fictional bad MD5: needed without "throw new RuntimeException"
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Log.e(DEBUG_TAG, "Exception on closing MD5 input stream", e);
            }
        }
    }
    
    /* method copyFile(File src, File dst, Context context) adapted from Stack Overflow:
	 * 
	 * http://stackoverflow.com/questions/4770004/how-to-move-rename-file-from-internal-app-storage-to-external-storage-on-android
	 * 
	 * Q: http://stackoverflow.com/users/131871/codefusionmobile
	 * A: http://stackoverflow.com/users/472270/barmaley
	 */
    
    @SuppressWarnings("resource")
	public static void copyFile(File src, File dst) throws IOException {
	    FileChannel inChannel = new FileInputStream(src).getChannel();
	    FileChannel outChannel = new FileOutputStream(dst).getChannel();
	    //if (!dst.exists()) {
		    try {
		        inChannel.transferTo(0, inChannel.size(), outChannel);
		    } finally {
		        if (inChannel != null) inChannel.close();
		        if (outChannel != null) outChannel.close();
		    }
	    /*} else {
	    	logger("w", "copyFile: destination already exists", DEBUG_TAG);
	    }*/
	}
    
    /*
     * getCpuInfo() from:
     *   http://www.roman10.net/how-to-get-cpu-information-on-android/
     * by:
     *   Liu Feipeng 
     */
    
    public static String getCpuInfo() {
    	StringBuffer sb = new StringBuffer();
    	sb.append("abi: ").append(Build.CPU_ABI).append("\n");
    	if (new File("/proc/cpuinfo").exists()) {
        	try {
        		BufferedReader br = new BufferedReader(new FileReader(new File("/proc/cpuinfo")));
	        	String aLine;
				while ((aLine = br.readLine()) != null) {
					sb.append(aLine + "\n");
				}
				if (br != null) {
		    		br.close();
		    	}
			} catch (IOException e) {
				e.printStackTrace();
			} 
        }
    	return sb.toString();
    }
    
    /*
     * scanMedia method adapted from Wolfram Rittmeyer's blog:
     * http://www.grokkingandroid.com/adding-files-to-androids-media-library-using-the-mediascanner/
     */
    
    public static void scanMedia(Context context, final String[] filePath, final String[] mime) {
    	MediaScannerConnection.scanFile(context, filePath, mime, new OnScanCompletedListener() {
    		@Override
    		public void onScanCompleted(String path, Uri uri) {
    			Log.v(DEBUG_TAG, "file " + path + " was scanned seccessfully: " + uri);
    			//YTD.videoinfo.edit().putString(path, uri.toString()).apply();
    		}
    	});
    }
    
    //-----------
    
    public static String getFileNameWithoutExt(String filename) {
    	int index = filename.lastIndexOf('.');
    	if (index > 0 && index <= filename.length() - 2) {
    		return filename.substring(0, index);
    	}  
    	return filename;
	}
    
    public static String getExtFromFileName(String filename) {
    	int index = filename.lastIndexOf('.');
    	if (index > 0 && index <= filename.length() - 2) {
    		return filename.substring(index + 1);
    	}
    	return filename;
    }
    
    
    /* 
     * 'getContentUriFromFilePath' and 'getFilePathFromContentUri' adapted from StackOverflow:
     * http://stackoverflow.com/a/11603899/1865860
     * 
     * Q: http://stackoverflow.com/users/315998/stealthcopter
     * A: http://stackoverflow.com/users/429108/jon-o
     */
    
    /**
     * Gets the MediaStore video ID of a given file on external storage
     * @param filePath The path (on external storage) of the file to resolve the ID of
     * @param contentResolver The content resolver to use to perform the query.
     * @return the video ID as a long
     */
    public static String getContentUriFromFilePath(String filePath, ContentResolver contentResolver) {
        long videoId;
        logger("d","Loading file " + filePath, DEBUG_TAG);

        String[] vprojection = {MediaStore.Video.VideoColumns._ID};
        String[] aprojection = {MediaStore.Audio.AudioColumns._ID};
        
        String ext = getExtFromFileName(filePath);
        //logger("d","ext: " + ext, DEBUG_TAG);
        
        Uri videosUri = null;
        String[] projection = null;
        String dataType = null;
        if (ext.equals("mp4") || ext.equals("3gpp") || ext.equals("webm")) {
        	videosUri = MediaStore.Video.Media.getContentUri("external");
        	projection = vprojection;
        	dataType = MediaStore.Video.VideoColumns.DATA;
        } else if (ext.equals("mp3") || ext.equals("ogg") || ext.equals("aac")){
        	videosUri = MediaStore.Audio.Media.getContentUri("external");
        	projection = aprojection;
        	dataType = MediaStore.Audio.AudioColumns.DATA;
        } else if (ext.equals("flv")) {
        	logger("w", " -> videoUri not available [FLV video]", DEBUG_TAG);
        	return null;
        }
        
		Cursor cursor = contentResolver.query(videosUri, projection, dataType + " LIKE ?", new String[] { filePath }, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(projection[0]);
        String videoUri = null;
		try {
        	videoId = cursor.getLong(columnIndex);
        	videoUri = videosUri + "/" + videoId; 
        	logger("d", " -> videoUri: " + videoUri, DEBUG_TAG);
        } catch (IndexOutOfBoundsException e) {
        	logger("w", " -> videoUri not available", DEBUG_TAG);
        } finally {
        	cursor.close();
        }
		
        return videoUri;
    }
    
    /**
     * Gets the corresponding path to a file from the given content:// URI
     * @param selectedVideoUri The content:// URI to find the file path from
     * @param contentResolver The content resolver to use to perform the query.
     * @return the file path as a string
     */
    public static String getFilePathFromContentUri(Uri selectedVideoUri, ContentResolver contentResolver) {
        String filePath;
        String[] filePathColumn = {MediaColumns.DATA};

        Cursor cursor = contentResolver.query(selectedVideoUri, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }
    
    public static void appendStringToFile(File file, String text) {
    	PrintWriter out = null;
    	try {
    	    out = new PrintWriter(new BufferedWriter(new FileWriter(file.getAbsolutePath(), true)));
    	    out.println("\n" + text);
    	} catch (IOException e) {
    	    Log.e(DEBUG_TAG, "appendStringToFile: " + e.getMessage());
    	} finally {
    	    if (out != null) {
    	        out.close();
    	    }
    	} 
    }
}

// ---------------------------------------------------------

	/*
	 *  to get the name of an executing method, 
	 *  call this from inside the method itself:
	 *  
	 *  String name = new Exception().getStackTrace()[0].getMethodName();
	 *  Log.i(DEBUG_TAG, "==> " + name);
	 */
	
