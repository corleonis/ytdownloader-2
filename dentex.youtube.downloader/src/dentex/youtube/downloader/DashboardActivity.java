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

import group.pals.android.lib.ui.filechooser.FileChooserActivity;
import group.pals.android.lib.ui.filechooser.io.localfile.LocalFile;
import group.pals.android.lib.ui.filechooser.services.IFileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cmc.music.common.ID3WriteException;
import org.cmc.music.metadata.MusicMetadata;
import org.cmc.music.metadata.MusicMetadataSet;
import org.cmc.music.myid3.MyID3;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.os.Parcelable;
import android.provider.MediaStore.Video.Thumbnails;
import android.support.v4.app.NotificationCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bugsense.trace.BugSenseHandler;
import com.matsuhiro.android.download.DownloadTask;
import com.matsuhiro.android.download.DownloadTaskListener;
import com.matsuhiro.android.download.InvalidYoutubeLinkException;
import com.matsuhiro.android.download.Maps;

import dentex.youtube.downloader.ffmpeg.FfmpegController;
import dentex.youtube.downloader.ffmpeg.ShellUtils.ShellCallback;
import dentex.youtube.downloader.utils.Json;
import dentex.youtube.downloader.utils.PopUps;
import dentex.youtube.downloader.utils.Utils;

public class DashboardActivity extends Activity{
	
	private final static String DEBUG_TAG = "DashboardActivity";
	public static boolean isDashboardRunning;
	private ContextThemeWrapper boxThemeContextWrapper = new ContextThemeWrapper(this, R.style.BoxTheme);
	private NotificationCompat.Builder aBuilder;
	private NotificationManager aNotificationManager;
	private int totSeconds;
	private int currentTime;
	protected File audioFile;
	protected String basename;
	private String aSuffix = ".audio";
	private String vfilename;
	private boolean removeVideo;
	private boolean removeAudio;
	private ListView lv;
	private Editable searchText;
	
	static List<String> idEntries = new ArrayList<String>();
	static List<String> typeEntries = new ArrayList<String>();
	static List<String> linkEntries = new ArrayList<String>();
	static List<Integer> posEntries = new ArrayList<Integer>();
	static List<String> statusEntries = new ArrayList<String>();
	static List<String> pathEntries = new ArrayList<String>();
	static List<String> filenameEntries = new ArrayList<String>();
	static List<String> basenameEntries = new ArrayList<String>();
	static List<String> audioExtEntries = new ArrayList<String>();
	static List<String> sizeEntries = new ArrayList<String>();
	static List<String> partSizeEntries = new ArrayList<String>();
	static List<Integer> progressEntries = new ArrayList<Integer>();
	static List<Long> speedEntries = new ArrayList<Long>();
	
	private static int entries;
	
	private static List<DashboardListItem> itemsList = new ArrayList<DashboardListItem>();
	private static DashboardAdapter da;
	private boolean isSearchBarVisible;
	private DashboardListItem currentItem = null;
	private TextView userFilename;
	private boolean extrTypeIsMp3Conv;
	int posX;
	private String type;
	private boolean isFfmpegRunning = false;
	private boolean isAnyAsyncInProgress = false;
	
	private String tagArtist;
	private String tagAlbum;
	private String tagTitle;
	private String tagYear;
	private String tagGenre;
	
    private String ogg = "OGG Vorbis";
    private String aac = "AAC (Advanced Audio Codec)";
    private String mp3 = "MP3 (low quality)";
    //private String aac_mp3 = "AAC / MP3";
    
	private boolean newClick;
	public static long countdown;
	
	public static Activity sDashboard;
	
	//long BeforeTime = System.currentTimeMillis();
	//long TotalRxBeforeTest = TrafficStats.getUidRxBytes(YTD.uid);
	//long TotalTxBeforeTest = TrafficStats.getUidTxBytes(YTD.uid);
	
	private Timer autoUpdate;
	public static boolean isLandscape;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BugSenseHandler.leaveBreadcrumb("DashboardActivity_onCreate");
		//Utils.logger("v", "TotalRxBeforeTest: " + TotalRxBeforeTest, DEBUG_TAG);
		
		// Theme init
    	Utils.themeInit(this);
    	
		setContentView(R.layout.activity_dashboard);
		
		// Language init
    	Utils.langInit(this);
    	
    	// Detect screen orientation
    	int or = this.getResources().getConfiguration().orientation;
    	isLandscape = (or == 2) ? true : false;

    	sDashboard = DashboardActivity.this;
    	
    	if (da != null) {
    		clearAdapterAndLists();
    	}
    	
    	countdown = 10;
    	
    	parseJson(this);
    	updateProgressBars();
    	buildList();
    	
    	lv = (ListView) findViewById(R.id.dashboard_list);
    	
    	da = new DashboardAdapter(itemsList, this);
    	
    	if (da.isEmpty()) {
            showEmptyListInfo(this);
    	} else {
    		lv.setAdapter(da);
    	}
    	
    	/*Log.i(DEBUG_TAG, "ADML Maps:" +
    			"\ndtMap:                 " + Maps.dtMap +
    			"\nmDownloadPercentMap:   " + Maps.mDownloadPercentMap +
    			"\nmDownloadSizeMap:      " + Maps.mDownloadSizeMap + 
    			"\nmTotalSizeMap:         " + Maps.mTotalSizeMap);*/
    	
    	lv.setTextFilterEnabled(true);
    	
    	lv.setClickable(true);
    	
    	lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				if (!isAnyAsyncInProgress) {
					currentItem = da.getItem(position); // in order to refer to the filtered item
					newClick = true;
        		
	        		final boolean ffmpegEnabled = YTD.settings.getBoolean("enable_advanced_features", false);				
	        		
	        		AlertDialog.Builder builder = new AlertDialog.Builder(boxThemeContextWrapper);
	        		builder.setTitle(currentItem.getFilename());
	        		
	        		if (currentItem.getStatus().equals(getString(R.string.json_status_completed)) && !isFfmpegRunning || 
	        				currentItem.getStatus().equals(getString(R.string.json_status_imported))) {
	        		
		        		if (currentItem.getType().equals(YTD.JSON_DATA_TYPE_V)) {
		        			
		        			// handle click on a **VIDEO** file entry
			        		builder.setItems(R.array.dashboard_click_entries, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
			
				    				final File in = new File (currentItem.getPath(), currentItem.getFilename());
				    				boolean audioIsSupported = !currentItem.getAudioExt().equals("unsupported");
				    				
			    					switch (which) {
			    					case 0: // open
			    						BugSenseHandler.leaveBreadcrumb("video_open");
			    						Intent openIntent = new Intent(Intent.ACTION_VIEW);
			    						openIntent.setDataAndType(Uri.fromFile(in), "video/*");
			    						startActivity(Intent.createChooser(openIntent, getString(R.string.open_chooser_title)));
			    						break;
					    			case 1: // extract audio only
					    				BugSenseHandler.leaveBreadcrumb("video_ffmpeg_extract");
					    				if (audioIsSupported) {
						    				if (ffmpegEnabled) {
							    				AlertDialog.Builder builder0 = new AlertDialog.Builder(boxThemeContextWrapper);
							    			    LayoutInflater inflater0 = getLayoutInflater();
							    			    final View view0 = inflater0.inflate(R.layout.dialog_audio_extr_only, null);
							    			    
							    			    String type = null;
							    			    if (currentItem.getAudioExt().equals(".aac")) type = aac;
							    			    if (currentItem.getAudioExt().equals(".ogg")) type = ogg;
							    			    if (currentItem.getAudioExt().equals(".mp3")) type = mp3;
							    			    //if (currentItem.getAudioExt().equals(".auto")) type = aac_mp3;
							    			    
							    			    TextView info = (TextView) view0.findViewById(R.id.audio_extr_info);
							    			    info.setText(getString(R.string.audio_extr_info) + "\n\n" + type);
				
							    			    builder0.setView(view0)
							    			           .setPositiveButton("OK", new DialogInterface.OnClickListener() {
							    			               @Override
							    			               public void onClick(DialogInterface dialog, int id) {
							    			            	   
							    			            	   CheckBox cb0 = (CheckBox) view0.findViewById(R.id.rem_video_0);
							    			            	   removeVideo = cb0.isChecked();
				
							    			            	   Utils.logger("v", "Launching FFmpeg on: " + in +
							    			            			   "\n-> mode: extraction only" +
							    			            			   "\n-> remove video: " + removeVideo, DEBUG_TAG);
							    			            	   
							    			            	   ffmpegJob(in, null, null);
							    			               }
							    			           })
							    			           .setNegativeButton(R.string.dialogs_negative, new DialogInterface.OnClickListener() {
							    			               public void onClick(DialogInterface dialog, int id) {
							    			                   // cancel
							    			               }
							    			           });      
							    			    
							    			    secureShowDialog(builder0);
						    				} else {
						    					notifyFfmpegNotInstalled();
						    				}
					    				} else {
					    					notifyOpsNotSupported();
					    				}
					    			    
			    						break;
					    			case 2: // extract audio and convert to mp3
					    				BugSenseHandler.leaveBreadcrumb("video_ffmpeg_mp3");
					    				if (audioIsSupported) {
						    				if (ffmpegEnabled) {
							    				AlertDialog.Builder builder1 = new AlertDialog.Builder(boxThemeContextWrapper);
							    			    LayoutInflater inflater1 = getLayoutInflater();
							    			    
							    			    final View view1 = inflater1.inflate(R.layout.dialog_audio_extr_mp3_conv, null);
				
							    			    builder1.setView(view1)
							    			           .setPositiveButton("OK", new DialogInterface.OnClickListener() {
							    			               @Override
							    			               public void onClick(DialogInterface dialog, int id) {
							    			            	   
							    			            	   final Spinner sp = (Spinner) view1.findViewById(R.id.mp3_spinner);
							    			            	   String[] bitrateData = retrieveBitrateValueFromSpinner(sp);
							    			            	   
							    			            	   CheckBox cb1 = (CheckBox) view1.findViewById(R.id.rem_video_1);
							    			            	   removeVideo = cb1.isChecked();
							    			            	   
							    			            	   Utils.logger("v", "Launching FFmpeg on: " + in +
							    			            			   "\n-> mode: conversion to mp3 from video file" +
							    			            			   "\n-> remove video: " + removeVideo, DEBUG_TAG);
							    			            	   
							    			            	   ffmpegJob(in, bitrateData[0], bitrateData[1]);
							    			               }
							    			           })
							    			           .setNegativeButton(R.string.dialogs_negative, new DialogInterface.OnClickListener() {
							    			               public void onClick(DialogInterface dialog, int id) {
							    			                   //
							    			               }
							    			           });      
							    			    
							    			    secureShowDialog(builder1);
						    				} else {
						    					notifyFfmpegNotInstalled();
						    				}
					    				} else {
					    					notifyOpsNotSupported();
					    				}
			    					}
								}
			        		});
			        		
			        		secureShowDialog(builder);
				    		
						} else if (currentItem.getType().equals(YTD.JSON_DATA_TYPE_A_E) ||
								currentItem.getType().equals(YTD.JSON_DATA_TYPE_A_M)) {
							
							// handle click on a **AUDIO** file entry
							builder.setItems(R.array.dashboard_click_entries_audio, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
			
				    				final File in = new File (currentItem.getPath(), currentItem.getFilename());
				    				if (ffmpegEnabled) {
				    					switch (which) {
				    					case 0: // open
				    						BugSenseHandler.leaveBreadcrumb("audio_open");
				    						Intent openIntent = new Intent(Intent.ACTION_VIEW);
				    						openIntent.setDataAndType(Uri.fromFile(in), "audio/*");
				    						startActivity(Intent.createChooser(openIntent, getString(R.string.open_chooser_title)));
				    						break;
						    			case 1: // convert to mp3
						    				BugSenseHandler.leaveBreadcrumb("audio_ffmpeg_mp3");
						    				AlertDialog.Builder builder0 = new AlertDialog.Builder(boxThemeContextWrapper);
						    			    LayoutInflater inflater0 = getLayoutInflater();
						    			    final View view2 = inflater0.inflate(R.layout.dialog_audio_mp3_conv, null);
			
						    			    builder0.setView(view2)
						    			           .setPositiveButton("OK", new DialogInterface.OnClickListener() {
						    			               @Override
						    			               public void onClick(DialogInterface dialog, int id) {
						    			            	   
						    			            	   final Spinner sp = (Spinner) view2.findViewById(R.id.mp3_spinner_a);
						    			            	   String[] bitrateData = retrieveBitrateValueFromSpinner(sp);
						    			            	   
						    			            	   CheckBox cb2 = (CheckBox) view2.findViewById(R.id.rem_original_audio);
						    			            	   removeAudio = cb2.isChecked();
			
						    			            	   Utils.logger("v", "Launching FFmpeg on: " + in +
						    			            			   "\n-> mode: conversion to mp3 from audio file" +
						    			            			   "\n-> remove audio: " + removeAudio, DEBUG_TAG);
						    			            	   
						    			            	   ffmpegJob(in, bitrateData[0], bitrateData[1]);
						    			               }
						    			           })
						    			           .setNegativeButton(R.string.dialogs_negative, new DialogInterface.OnClickListener() {
						    			               public void onClick(DialogInterface dialog, int id) {
						    			                   //
						    			               }
						    			           });      
						    			    
						    			    secureShowDialog(builder0);
				    					}
			    					} else {
			    						notifyFfmpegNotInstalled();
				    				}
								}
			        		});
			        		
			        		secureShowDialog(builder);
						}
	        		} /*else if (currentItem.getStatus().equals(getString(R.string.json_status_imported))) {
	        			Utils.logger("v", "IMPORTED video clicked", DEBUG_TAG);
	        			
	        			// handle click on an  **IMPORTED VIDEO** entry
		        		builder.setItems(R.array.dashboard_click_entries_imported, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
		
			    				final File in = new File (currentItem.getPath(), currentItem.getFilename());
			    				
		    					switch (which) {
		    					case 0: // open
		    						Intent openIntent = new Intent(Intent.ACTION_VIEW);
		    						openIntent.setDataAndType(Uri.fromFile(in), "video/*");
		    						startActivity(Intent.createChooser(openIntent, getString(R.string.open_chooser_title)));
		    						break;
		    					}
							}
		        		});
	
		        		secureShowDialog(builder);
	        		}*/
				}
			}
    	});
    	
    	lv.setLongClickable(true);
    	lv.setOnItemLongClickListener(new OnItemLongClickListener() {

        	@Override
        	public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

        		if (!isAnyAsyncInProgress) {
	        		currentItem = da.getItem(position); // in order to refer to the filtered item
	
	        		int COPY = 0;
	        		int MOVE = 1;
	        		int RENAME = 2;
	        		int REDOWNLOAD = 3;
	        		int REMOVE = 4;
	        		int DELETE = 5;
	        		int PAUSERESUME = 6;
	        		
	        		int[] disabledItems = null;
	
	        		if (currentItem.getStatus().equals(getString(R.string.json_status_in_progress)) ||
	        				currentItem.getStatus().equals(getString(R.string.json_status_paused))) {
	        			// show: DELETE and  PAUSERESUME
	        			disabledItems = new int[] { COPY, MOVE, RENAME, REDOWNLOAD, REMOVE };
	        		} else if (currentItem.getStatus().equals(getString(R.string.json_status_failed))) {
	        			// check if the item has a real YouTube ID, otherwise it's an imported video.
	        			if (currentItem.getYtId().length() == 11) {
	        				// show: REMOVE and REDOWNLOAD
	        				disabledItems = new int[] { COPY, MOVE, RENAME, DELETE, PAUSERESUME};
	        			} else {
	        				// show: REMOVE only
	        				disabledItems = new int[] { COPY, MOVE, RENAME, REDOWNLOAD, DELETE, PAUSERESUME };
	        			}
	        			
	        		} else if (currentItem.getStatus().equals(getString(R.string.json_status_imported)) ||
	        					//case for audio entries _completed but from _imported
	        					(currentItem.getStatus().equals(getString(R.string.json_status_completed)) &&
	    	        			!(currentItem.getYtId().length() == 11))) {
	        			// show: COPY, MOVE, RENAME, REMOVE and DELETE
	        			disabledItems = new int[] { REDOWNLOAD, PAUSERESUME };
	        		}  else if (currentItem.getStatus().equals(getString(R.string.json_status_completed))) {
	        			// show: all items except PAUSERESUME
	        			disabledItems = new int[] { PAUSERESUME };
	        		}
	
	        		AlertDialog.Builder builder = new AlertDialog.Builder(boxThemeContextWrapper);
	        		builder.setTitle(currentItem.getFilename());
	
	    			final ArrayAdapter<CharSequence> cla = DashboardLongClickAdapter.createFromResource(
	    					boxThemeContextWrapper,
	    					R.array.dashboard_long_click_entries,
	    		            android.R.layout.simple_list_item_1, 
	    		            disabledItems);
	    			
	    			builder.setAdapter(cla, new DialogInterface.OnClickListener() {
	
						public void onClick(DialogInterface dialog, int which) {
				    		switch (which) {
				    			case 0:
				    				BugSenseHandler.leaveBreadcrumb("copy");
				    				copy(currentItem);
				    				break;
				    			case 1:
				    				BugSenseHandler.leaveBreadcrumb("move");
				    				move(currentItem);
				    				break;
				    			case 2:
				    				BugSenseHandler.leaveBreadcrumb("rename");
				    				rename(currentItem);
				    				break;
				    			case 3:
				    				if (currentItem.getStatus().equals(getString(R.string.json_status_failed))) {
				    					BugSenseHandler.leaveBreadcrumb("reDownload_RESTART");
				    					reDownload(currentItem, "RESTART");
				    				} else {
				    					BugSenseHandler.leaveBreadcrumb("reDownload");
				    					reDownload(currentItem, "-");
				    				}
				    				break;
				    			case 4:
				    				BugSenseHandler.leaveBreadcrumb("removeFromDashboard");
				    				removeFromDashboard(currentItem);
				    				break;
				    			case 5:
				    				BugSenseHandler.leaveBreadcrumb("delete");
				    				delete(currentItem);
				    				break;
				    			case 6:
				    				BugSenseHandler.leaveBreadcrumb("pauseresume");
				    				pauseresume(currentItem);
				    		}
	
						}	
	        		});
	        		
		        	secureShowDialog(builder);
        		}
        		
        		return true;
        	}
    	});
	}
	
	private void notifyOpsNotSupported() {
		PopUps.showPopUp(getString(R.string.information), getString(R.string.unsupported_operation), "alert", sDashboard);
		Utils.logger("d", "notifyOpsNotSupported()", DEBUG_TAG);
	}
	
	private void toastOpsNotExecuted() {
		Toast.makeText(sDashboard, getString(R.string.long_press_warning_title) + 
				"\n- " + getString(R.string.notification_downloading_pt1) + " (" + 
				getString(R.string.json_status_paused) + "/" + getString(R.string.json_status_in_progress) + " )" + 
				"\n- " + getString(R.string.empty_dashboard), 
				Toast.LENGTH_LONG).show();
		Utils.logger("d", "toastOpsNotExecuted()", DEBUG_TAG);
	}
	
	private void copy(DashboardListItem currentItem) {
		Intent intent = new Intent(DashboardActivity.this,  FileChooserActivity.class);
    	if (intent != null) {
    		intent.putExtra(FileChooserActivity._Rootpath, (Parcelable) new LocalFile(Environment.getExternalStorageDirectory()));
    		intent.putExtra(FileChooserActivity._FilterMode, IFileProvider.FilterMode.DirectoriesOnly);
    		startActivityForResult(intent, 1);
    	}
	}
	
	private void move(DashboardListItem currentItem) {
		Intent intent = new Intent(DashboardActivity.this,  FileChooserActivity.class);
    	if (intent != null) {
    		intent.putExtra(FileChooserActivity._Rootpath, (Parcelable) new LocalFile(Environment.getExternalStorageDirectory()));
    		intent.putExtra(FileChooserActivity._FilterMode, IFileProvider.FilterMode.DirectoriesOnly);
    		startActivityForResult(intent, 2);
    	}
	}
	
	private void rename(final DashboardListItem currentItem) {
		AlertDialog.Builder adb = new AlertDialog.Builder(boxThemeContextWrapper);
		LayoutInflater adbInflater = LayoutInflater.from(DashboardActivity.this);
	    View inputFilename = adbInflater.inflate(R.layout.dialog_input_filename, null);
	    userFilename = (TextView) inputFilename.findViewById(R.id.input_filename);
	    userFilename.setText(currentItem.getFilename());
	    adb.setView(inputFilename);
	    adb.setTitle(getString(R.string.rename_dialog_title));
	    //adb.setMessage(getString(R.string.rename_dialog_msg));
	    
	    adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	    	public void onClick(DialogInterface dialog, int which) {
	    		String input = userFilename.getText().toString();
	    		File in = new File(currentItem.getPath(), currentItem.getFilename());
	    		File renamed = new File(currentItem.getPath(), input);
	    		if (in.renameTo(renamed)) {
	    			// set new name to the list item
	    			currentItem.setFilename(input);
	    			
	    			// update the JSON file entry
	    			Json.addEntryToJsonFile(
							DashboardActivity.this, 
							currentItem.getId(), 
							currentItem.getType(), 
							currentItem.getYtId(), 
							currentItem.getPos(),
							currentItem.getStatus(), 
							currentItem.getPath(), 
							input, 
							Utils.getFileNameWithoutExt(input), 
							currentItem.getAudioExt(), 
							currentItem.getSize(),
							false);
	    			
	    			// remove references for the old file
	    			String mediaUriString = Utils.getContentUriFromFilePath(in.getAbsolutePath(), getContentResolver());
	    			//Utils.logger("d", "mediaString: " + mediaUriString, DEBUG_TAG);
	    			removeFromMediaStore(in, mediaUriString);
	    			
	    			// scan the new file
	    			Utils.scanMedia(DashboardActivity.this, 
							new String[]{ renamed.getAbsolutePath() }, 
							new String[]{ "video/*" });
	    			
	    			// refresh the dashboard
	    			refreshlist(DashboardActivity.this);
	    			
	    			Utils.logger("d", "'" + in.getName() + "' renamed to '" + input + "'", DEBUG_TAG);
	    		} else {
	    			Log.e(DEBUG_TAG, "'" + in.getName() + "' NOT renamed");
	    		}
	    		
	    		// hide keyboard
	    		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
	    		imm.hideSoftInputFromWindow(userFilename.getWindowToken(), 0);
	    	}
	    });
	    
	    adb.setNegativeButton(getString(R.string.dialogs_negative), new DialogInterface.OnClickListener() {
        	public void onClick(DialogInterface dialog, int which) {
                // cancel
            }
        });
	    
	    secureShowDialog(adb);
	}
	
	public void  removeFromDashboard(final DashboardListItem currentItem) {
		AlertDialog.Builder rem = new AlertDialog.Builder(boxThemeContextWrapper);
		//rem.setTitle(getString(R.string.attention));
		rem.setTitle(currentItem.getFilename());
		rem.setMessage(getString(R.string.remove_video_confirm));
		rem.setIcon(android.R.drawable.ic_dialog_alert);
		rem.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				Json.removeEntryFromJsonFile(DashboardActivity.this, currentItem.getId());
				refreshlist(DashboardActivity.this);
				
				YTD.videoinfo.edit().remove(currentItem.getId() + "_link").apply();
				//YTD.videoinfo.edit().remove(currentItem.getId() + "_position").apply();
			}
		});
		
		rem.setNegativeButton(R.string.dialogs_negative, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// cancel
			}
		});
		
		secureShowDialog(rem);
	}

	public void delete(final DashboardListItem currentItem) {
		AlertDialog.Builder del = new AlertDialog.Builder(boxThemeContextWrapper);
		//del.setTitle(getString(R.string.attention));
		del.setTitle(currentItem.getFilename());
		del.setMessage(getString(R.string.delete_video_confirm));
		del.setIcon(android.R.drawable.ic_dialog_alert);
		del.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				final File fileToDel = new File(currentItem.getPath(), currentItem.getFilename());
				new AsyncDelete().execute(fileToDel);
			}
		});
		
		del.setNegativeButton(R.string.dialogs_negative, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// cancel
			}
		});
		
		secureShowDialog(del);
	}
	
	private void pauseresume(final DashboardListItem currentItem) {
		
		final String itemID = currentItem.getId();
		long itemIDlong = Long.parseLong(itemID);
		
		Utils.logger("d", "pauseresume on id " + itemID, DEBUG_TAG);
		
		if (currentItem.getStatus().equals(getString(R.string.json_status_in_progress))) {
			BugSenseHandler.leaveBreadcrumb("...pausing");
			
			try {
				if (Maps.dtMap.containsKey(itemIDlong)) {
					DownloadTask dt = Maps.dtMap.get(itemIDlong);
					dt.cancel();
				} else {
					if (Maps.dtMap.size() > 0) {
						// cancel (pause) every dt found
						Utils.logger("w", "pauseresume: id not found into 'dtMap'; canceling all tasks", DEBUG_TAG);
						for (Iterator<DownloadTask> iterator = Maps.dtMap.values().iterator(); iterator.hasNext();) {
							DownloadTask dt = (DownloadTask) iterator.next();
							dt.cancel();
						}
					}
				}
			} catch (NullPointerException e) {
		    	Log.e(DEBUG_TAG, "dt.cancel() @ pauseresume: " + e.getMessage());
		    	BugSenseHandler.sendExceptionMessage(DEBUG_TAG + "-> dt.cancel() @ pauseresume: ", e.getMessage(), e);
		    }
			
			YTD.removeIdUpdateNotification(itemIDlong);
			
			// update the JSON file entry
			Json.addEntryToJsonFile(
					DashboardActivity.this,
					itemID, 
					currentItem.getType(),
					currentItem.getYtId(), 
					currentItem.getPos(),
					YTD.JSON_DATA_STATUS_PAUSED,
					currentItem.getPath(), 
					currentItem.getFilename(),
					currentItem.getBasename(), 
					currentItem.getAudioExt(),
					currentItem.getSize(), 
					false);
		}
		
		if (currentItem.getStatus().equals(getString(R.string.json_status_paused))) {
			BugSenseHandler.leaveBreadcrumb("...resuming");
			String link = YTD.videoinfo.getString(String.valueOf(itemID) + "_link", null);
					
			if (link != null) {
				DownloadTaskListener dtl = new DownloadTaskListener() {
					
					@Override
					public void preDownload(DownloadTask task) {
						long ID = task.getDownloadId();
						Utils.logger("d", "__preDownload on ID: " + ID, DEBUG_TAG);
						
						Maps.mNetworkSpeedMap.put(ID, (long) 0);
						
						Json.addEntryToJsonFile(
								sDashboard,
								String.valueOf(ID),
								currentItem.getType(),
								currentItem.getYtId(), 
								currentItem.getPos(),
								YTD.JSON_DATA_STATUS_IN_PROGRESS,
								currentItem.getPath(), 
								currentItem.getFilename(),
								currentItem.getBasename(), 
								currentItem.getAudioExt(),
								currentItem.getSize(), 
								false);
						
						YTD.sequence.add(ID);
						
						YTD.NotificationHelper();
					}
					
					@Override
					public void updateProcess(DownloadTask task) {
						/*YTD.downloadPercentMap = task.getDownloadPercentMap();
						YTD.downloadTotalSizeMap = task.getTotalSizeMap();
						YTD.downloadPartialSizeMap = task.getDownloadSizeMap();*/
					}
					
					@Override
					public void finishDownload(DownloadTask task) {
						long ID = task.getDownloadId();
						Utils.logger("d", "__finishDownload on ID: " + ID, DEBUG_TAG);
						
						Utils.scanMedia(getApplicationContext(), 
								new String[] { currentItem.getPath() + File.separator + currentItem.getFilename() }, 
								new String[] {"video/*"});
						
						long downloadTotalSize = Maps.mTotalSizeMap.get(ID);
						String size = String.valueOf(Utils.MakeSizeHumanReadable(downloadTotalSize, false));
						
						Json.addEntryToJsonFile(
								sDashboard, 
								String.valueOf(ID), 
								currentItem.getType(),
								currentItem.getYtId(), 
								currentItem.getPos(),
								YTD.JSON_DATA_STATUS_COMPLETED, 
								currentItem.getPath(), 
								currentItem.getFilename(),
								currentItem.getBasename(), 
								currentItem.getAudioExt(), 
								size, 
								false);
						
						if (DashboardActivity.isDashboardRunning)
							DashboardActivity.refreshlist(sDashboard);
						
						YTD.removeIdUpdateNotification(ID);
						
						YTD.videoinfo.edit().remove(ID + "_link").apply();
						//YTD.videoinfo.edit().remove(ID + "_position").apply();
						
						Maps.removeFromAllMaps(ID);
					}
					
					@Override
					public void errorDownload(DownloadTask task, Throwable error) {
						String nameOfVideo = task.getDownloadedFileName();
						long ID = task.getDownloadId();
							
						Utils.logger("w", "__errorDownload on ID: " + ID, DEBUG_TAG);
						
						if (error != null && error instanceof InvalidYoutubeLinkException) {
							Toast.makeText(sDashboard,  nameOfVideo
									+ ": " + getString(R.string.downloading) 
									+ "\n"+ getString(R.string.wait), 
									Toast.LENGTH_LONG).show();
							
							Json.addEntryToJsonFile(
									sDashboard, 
									String.valueOf(ID), 
									YTD.JSON_DATA_TYPE_V, 
									currentItem.getYtId(), 
									currentItem.getPos(),
									YTD.JSON_DATA_STATUS_PAUSED, 
									currentItem.getPath(), 
									nameOfVideo, 
									currentItem.getBasename(), 
									currentItem.getAudioExt(), 
									currentItem.getSize(), 
									false);
							
							reDownload(currentItem, "AUTO");
						} else {
							Toast.makeText(sDashboard,  nameOfVideo + ": " + getString(R.string.download_failed), 
									Toast.LENGTH_LONG).show();
							
							Json.addEntryToJsonFile(
									sDashboard, 
									String.valueOf(ID),  
									YTD.JSON_DATA_TYPE_V, 
									currentItem.getYtId(), 
									currentItem.getPos(),
									YTD.JSON_DATA_STATUS_PAUSED, 
									currentItem.getPath(), 
									nameOfVideo, 
									currentItem.getBasename(), 
									currentItem.getAudioExt(), 
									currentItem.getSize(), 
									false);
							
							if (DashboardActivity.isDashboardRunning)
								DashboardActivity.refreshlist(sDashboard);
							
							YTD.removeIdUpdateNotification(ID);
						}
					}
				};
				
				//TODO
				try {
					DownloadTask dt = new DownloadTask(this, itemIDlong, link, 
							currentItem.getFilename(), currentItem.getPath(), 
							dtl, true);
					Maps.dtMap.put(itemIDlong, dt);
					dt.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} catch (MalformedURLException e) {
					Log.e(DEBUG_TAG, "unable to start Download Manager -> " + e.getMessage());
				}
			} else {
				//notifyOpsNotSupported();
				reDownload(currentItem, "AUTO");
			}
		}
		refreshlist(sDashboard);
	}
	
	private void reDownload(DashboardListItem currentItem, String category) {
		String ytLink = "http://www.youtube.com/watch?v=" + currentItem.getYtId();
		Intent rdIntent = new Intent(this, ShareActivity.class);
		rdIntent.setData(Uri.parse(ytLink));
		rdIntent.addCategory(category);
		rdIntent.putExtra("id", currentItem.getId());
		rdIntent.putExtra("position", currentItem.getPos());
		rdIntent.putExtra("filename", currentItem.getFilename());
		rdIntent.setAction(Intent.ACTION_VIEW);
		//rdIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(rdIntent);
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_dashboard, menu);
        return true;
    }
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        
        String previousJson = Json.readJsonDashboardFile(sDashboard);
        boolean smtInProgressOrPaused = (previousJson.contains(YTD.JSON_DATA_STATUS_IN_PROGRESS) || 
				 previousJson.contains(YTD.JSON_DATA_STATUS_PAUSED)) ;
        
        switch(item.getItemId()){
        	case R.id.menu_search:
        		BugSenseHandler.leaveBreadcrumb("ShareActivity_menu_search");
    			if (!isSearchBarVisible) {
    				spawnSearchBar();
    			} else {
    				hideSearchBar();
    			}
    			return true;
        	case R.id.menu_backup:
        		BugSenseHandler.leaveBreadcrumb("ShareActivity_menu_backup");
        		if (YTD.JSON_FILE.exists() && !previousJson.equals("{}\n") && !smtInProgressOrPaused) {
	        		boolean backupCheckboxEnabled = YTD.settings.getBoolean("dashboard_backup_info", true);
				    if (backupCheckboxEnabled == true) {
				    	
			        	AlertDialog.Builder adb = new AlertDialog.Builder(boxThemeContextWrapper);
			        	
			        	LayoutInflater adbInflater = LayoutInflater.from(DashboardActivity.this);
					    View showAgainView = adbInflater.inflate(R.layout.dialog_show_again_checkbox, null);
					    final CheckBox showAgain = (CheckBox) showAgainView.findViewById(R.id.showAgain2);
					    showAgain.setChecked(true);
					    adb.setView(showAgainView);
					    
			    		adb.setTitle(getString(R.string.information));
			    		adb.setMessage(getString(R.string.menu_backup_info));
			    		adb.setIcon(android.R.drawable.ic_dialog_info);
			    		
			    		adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int which) {
			    				if (!showAgain.isChecked()) {
					    			YTD.settings.edit().putBoolean("dashboard_backup_info", false).apply();
					    			Utils.logger("d", "dashboard backup info checkbox disabled", DEBUG_TAG);
					    		}
			    				launchFcForBackup();
			    			}
			    		});
			    		
			    		adb.setNegativeButton(R.string.dialogs_negative, new DialogInterface.OnClickListener() {
			    			public void onClick(DialogInterface dialog, int which) {
			    				// cancel
			    			}
			    		});
			    		
			    		secureShowDialog(adb);
				    } else {
				    	launchFcForBackup();
				    }
	        	} else {
        			toastOpsNotExecuted();
        		}
        		return true;
        	case R.id.menu_restore:
        		BugSenseHandler.leaveBreadcrumb("ShareActivity_menu_restore");
        		if (!smtInProgressOrPaused) {
	        		boolean restoreCheckboxEnabled = YTD.settings.getBoolean("dashboard_restore_info", true);
				    if (restoreCheckboxEnabled == true) {
				    	
			        	AlertDialog.Builder adb = new AlertDialog.Builder(boxThemeContextWrapper);
			        	
			        	LayoutInflater adbInflater = LayoutInflater.from(DashboardActivity.this);
					    View showAgainView = adbInflater.inflate(R.layout.dialog_show_again_checkbox, null);
					    final CheckBox showAgain = (CheckBox) showAgainView.findViewById(R.id.showAgain2);
					    showAgain.setChecked(true);
					    adb.setView(showAgainView);
					    
			    		adb.setTitle(getString(R.string.information));
			    		adb.setMessage(getString(R.string.menu_restore_info) + ".\n" + getString(R.string.menu_restore_info_msg));
			    		adb.setIcon(android.R.drawable.ic_dialog_info);
			    		
			    		adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	
							public void onClick(DialogInterface dialog, int which) {
			    				if (!showAgain.isChecked()) {
					    			YTD.settings.edit().putBoolean("dashboard_restore_info", false).apply();
					    			Utils.logger("d", "dashboard restore info checkbox disabled", DEBUG_TAG);
					    		}
			    				launchFcForRestore();
			    			}
			    		});
			    		
			    		adb.setNegativeButton(R.string.dialogs_negative, new DialogInterface.OnClickListener() {
			    			public void onClick(DialogInterface dialog, int which) {
			    				// cancel
			    			}
			    		});
			    		
			    		secureShowDialog(adb);
				    } else {
				    	launchFcForRestore();
				    }
        		} else {
        			toastOpsNotExecuted();
        		}
        		return true;
        	case R.id.menu_import:
        		BugSenseHandler.leaveBreadcrumb("ShareActivity_menu_import");
        		boolean importCheckboxEnabled1 = YTD.settings.getBoolean("dashboard_import_info", true);
			    if (importCheckboxEnabled1 == true) {
			    	
		        	AlertDialog.Builder adb = new AlertDialog.Builder(boxThemeContextWrapper);
		        	
		        	LayoutInflater adbInflater = LayoutInflater.from(DashboardActivity.this);
				    View showAgainView = adbInflater.inflate(R.layout.dialog_show_again_checkbox, null);
				    final CheckBox showAgain = (CheckBox) showAgainView.findViewById(R.id.showAgain2);
				    showAgain.setChecked(true);
				    adb.setView(showAgainView);
				    
		    		adb.setTitle(getString(R.string.information));
		    		adb.setMessage(getString(R.string.menu_import_info));
		    		adb.setIcon(android.R.drawable.ic_dialog_info);
		    		
		    		adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
		    				if (!showAgain.isChecked()) {
				    			YTD.settings.edit().putBoolean("dashboard_import_info", false).apply();
				    			Utils.logger("d", "dashboard import info checkbox disabled", DEBUG_TAG);
				    		}
		    				launchFcForImport();
		    			}
		    		});
		    		
		    		adb.setNegativeButton(R.string.dialogs_negative, new DialogInterface.OnClickListener() {
		    			public void onClick(DialogInterface dialog, int which) {
		    				// cancel
		    			}
		    		});
		    		
		    		secureShowDialog(adb);
			    } else {
			    	launchFcForImport();
			    }
        		return true;
        	default:
        		return super.onOptionsItemSelected(item);
        }
    }

	private void launchFcForBackup() {
		Intent intent3 = new Intent(DashboardActivity.this,  FileChooserActivity.class);
		if (intent3 != null) {
			intent3.putExtra(FileChooserActivity._Rootpath, (Parcelable) new LocalFile(Environment.getExternalStorageDirectory()));
			intent3.putExtra(FileChooserActivity._FilterMode, IFileProvider.FilterMode.DirectoriesOnly);
			startActivityForResult(intent3, 3);
		}
	}

	private void launchFcForRestore() {
		Intent intent4 = new Intent(DashboardActivity.this,  FileChooserActivity.class);
		if (intent4 != null) {
			intent4.putExtra(FileChooserActivity._Rootpath, (Parcelable) new LocalFile(Environment.getExternalStorageDirectory()));
			intent4.putExtra(FileChooserActivity._FilterMode, IFileProvider.FilterMode.FilesOnly);
			startActivityForResult(intent4, 4);
		}
	}

	private void launchFcForImport() {
		Intent intent5 = new Intent(DashboardActivity.this,  FileChooserActivity.class);
		if (intent5 != null) {
			intent5.putExtra(FileChooserActivity._Rootpath, (Parcelable) new LocalFile(Environment.getExternalStorageDirectory()));
			intent5.putExtra(FileChooserActivity._FilterMode, IFileProvider.FilterMode.FilesOnly);
			startActivityForResult(intent5, 5);
		}
	}
	
	@Override
    public void onResume(){
		super.onResume();
    	Utils.logger("v", "_onResume", DEBUG_TAG);
    	isDashboardRunning = true;
    	
    	/*
    	 * Timer() adapted from Stack Overflow:
    	 * http://stackoverflow.com/questions/3701106/periodically-refresh-reload-activity
    	 * 
    	 * Q: http://stackoverflow.com/users/446413/raffe
    	 * A: http://stackoverflow.com/users/244296/cristian
    	 */
    	autoUpdate = new Timer();
        autoUpdate.schedule(new TimerTask() {
        	@Override
        	public void run() {
        		runOnUiThread(new Runnable() {
        			public void run() {
        				
        				int inProgressIndex = 0;
        				
        				for (int i = 0; i < statusEntries.size(); i++ ) {
        					if (statusEntries.get(i).equals(YTD.JSON_DATA_STATUS_IN_PROGRESS)) {
        						inProgressIndex++;
        					}
        				}
        				
        				if (inProgressIndex > 0) {
        					//Utils.logger("v", "refreshing...", DEBUG_TAG);
        					refreshlist(sDashboard);
        				}
        			}
        		});
        	}
        }, 500, 500);
    }
	
    @Override
    public void onPause() {
    	super.onPause();
    	Utils.logger("v", "_onPause", DEBUG_TAG);
    	isDashboardRunning = false;
    	
    	autoUpdate.cancel();
    }
    
	public static void showEmptyListInfo(Activity activity) {
		TextView info = (TextView) activity.findViewById(R.id.dashboard_activity_info);
		info.setVisibility(View.VISIBLE);
		//Utils.logger("v", "__dashboard is empty__", DEBUG_TAG);
	}
	
	private class AsyncDelete extends AsyncTask<File, Void, Boolean> {

		File fileToDelete;
		
		protected void onPreExecute() {
			isAnyAsyncInProgress = true;
		}
		
		protected Boolean doInBackground(File... fileToDel) {
			fileToDelete = fileToDel[0];
			return doDelete(currentItem, fileToDel[0], true);
		}
		
		@Override
		protected void onPostExecute(Boolean success) {
			if (success) {
				notifyDeletionOk(currentItem, fileToDelete);
			} else {
				notifyDeletionUnsuccessful(currentItem, fileToDelete);
			}
			isAnyAsyncInProgress = false;
		}
	}
	
	private boolean doDelete(final DashboardListItem currentItem, File fileToDel, boolean removeFromJsonAlso) {
		Utils.logger("v", "----------> BEGIN delete", DEBUG_TAG);
		boolean isResultOk = false;
		long id = Long.parseLong(currentItem.getId());

		if (currentItem.getStatus().equals(getString(R.string.json_status_in_progress))) {
			// stop download, remove temp file and update notification
			try {
				if (Maps.dtMap.containsKey(id)) {
					DownloadTask dt = Maps.dtMap.get(id);
					dt.cancel();
				} else {
					if (Maps.dtMap.size() > 0) {
						// cancel (pause) every dt found
						Utils.logger("w", "doDelete: id not found into 'dtMap'; canceling all tasks", DEBUG_TAG);
						for (Iterator<DownloadTask> iterator = Maps.dtMap.values().iterator(); iterator.hasNext();) {
							DownloadTask dt = (DownloadTask) iterator.next();
							dt.cancel();
						}
					}
				}
				
				isResultOk = removeTemp(fileToDel, id);
			} catch (NullPointerException e) {
				Log.e(DEBUG_TAG, "dt.cancel(): " + e.getMessage());
		    	BugSenseHandler.sendExceptionMessage(DEBUG_TAG + "-> dt.cancel() @ doDelete: ", e.getMessage(), e);
			}
		} else if (currentItem.getStatus().equals(getString(R.string.json_status_paused))) {
			isResultOk = removeTemp(fileToDel, id);
		} else {
			// remove file and library reference
			isResultOk = removeCompleted(fileToDel);
		}
		
		if (removeFromJsonAlso && isResultOk) {
			// remove entry from JSON and reload Dashboard
			Json.removeEntryFromJsonFile(DashboardActivity.this, currentItem.getId());
		}
		
		refreshlist(DashboardActivity.this);
		Utils.logger("v", "----------> END delete", DEBUG_TAG);
		
		return isResultOk;
	}

	private boolean removeTemp(File fileToDel, long id) {
		// update notification
		YTD.removeIdUpdateNotification(id);
		
		//remove YouTube link from prefs
		YTD.videoinfo.edit().remove(String.valueOf(id) + "_link").apply();
		//YTD.videoinfo.edit().remove(String.valueOf(id) + "_position").apply();
		
		// delete temp file
		File temp = new File(fileToDel.getAbsolutePath() + DownloadTask.TEMP_SUFFIX);
		if (temp.exists()) {
			return (temp.delete()) ? true : false;
		} else {
			return true;
		}
	}

	public boolean removeCompleted(File fileToDel) {
		// remove file
		if (fileToDel.delete()) {
			// remove library reference
			String mediaUriString;
			try {
				mediaUriString = Utils.getContentUriFromFilePath(fileToDel.getAbsolutePath(), getContentResolver());
				removeFromMediaStore(fileToDel, mediaUriString);
			} catch (NullPointerException e) {
				Utils.logger("w", fileToDel.getName() + " UriString NOT found", DEBUG_TAG);
			}
			return true;
		} else {
			return false;
		}
	}

	private void removeFromMediaStore(File fileToDel, String mediaUriString) {
		if (mediaUriString != null) {
			Uri mediaUri = Uri.parse(mediaUriString);
			// remove media file reference from MediaStore library via ContentResolver
			if (getContentResolver().delete(mediaUri, null, null) > 0) {
				Utils.logger("d", mediaUri.toString() + " Removed", DEBUG_TAG);
			} else {
				Utils.logger("w", mediaUri.toString() + " NOT removed", DEBUG_TAG);
			}
		} else {
			Utils.logger("w", "mediaUriString for " + fileToDel.getName() + " null", DEBUG_TAG);
		}
	}

	public void notifyDeletionUnsuccessful(final DashboardListItem currentItem, File fileToDel) {
		Utils.logger("w", fileToDel.getPath() + " NOT deleted.", DEBUG_TAG);
		Toast.makeText(DashboardActivity.this, 
				getString(R.string.delete_video_failed, currentItem.getFilename()), 
				Toast.LENGTH_LONG).show();
	}

	public void notifyDeletionOk(final DashboardListItem currentItem, File fileToDel) {
		Utils.logger("d", fileToDel.getPath() + " successfully deleted.", DEBUG_TAG);
		Toast.makeText(DashboardActivity.this, 
				getString(R.string.delete_video_ok, currentItem.getFilename()), 
				Toast.LENGTH_LONG).show();
	}

	public void spawnSearchBar() {
		Utils.logger("d", "showing searchbar...", DEBUG_TAG);
		
		EditText inputSearch = new EditText(DashboardActivity.this);
		LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		inputSearch.setLayoutParams(layoutParams);
		
		if (TextUtils.isEmpty(searchText)) {
			inputSearch.setHint(R.string.menu_search);
		} else {
			inputSearch.setText(searchText);
		}
		
		inputSearch.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
		inputSearch.setSingleLine();
		inputSearch.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
		inputSearch.setId(999);
		
		LinearLayout layout = (LinearLayout) findViewById(R.id.dashboard);
		layout.addView(inputSearch, 0);
		isSearchBarVisible = true;
		
    	inputSearch.addTextChangedListener(new TextWatcher() {
        
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				Utils.logger("d", "Text ["+s+"] - Start ["+start+"] - Before ["+before+"] - Count ["+count+"]", DEBUG_TAG);
				
				if (count < before) da.resetData();
				da.getFilter().filter(s.toString());
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
    	});
	}
	
	public void hideSearchBar() {
		Utils.logger("d", "hiding searchbar...", DEBUG_TAG); 
		
		LinearLayout layout = (LinearLayout) findViewById(R.id.dashboard);
		EditText inputSearch = (EditText) findViewById(999);
		
		// hide keyboard
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(inputSearch.getWindowToken(), 0);
		
		// store text and remove EditText
		searchText = inputSearch.getEditableText();
		layout.removeView(inputSearch);
		
		Utils.reload(DashboardActivity.this);
		
		isSearchBarVisible = false;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			BugSenseHandler.leaveBreadcrumb("DashboardActivity_filechooser_RESULT_OK");
            @SuppressWarnings("unchecked")
			List<LocalFile> files = (List<LocalFile>) data.getSerializableExtra(FileChooserActivity._Results);
            	
        	final File chooserSelection = files.get(0);
        	//Utils.logger("d", "file-chooser selection: " + chooserFolder.getPath(), DEBUG_TAG);
        	//Utils.logger("d", "origin file's folder:   " + currentItem.getPath(), DEBUG_TAG);
			
	        switch (requestCode) {
	        
	        case 1: // ------------- > COPY
	        	File in1 = new File(currentItem.getPath(), currentItem.getFilename());
				File out1 = new File(chooserSelection, currentItem.getFilename());
				
	        	if (chooserSelection.getPath().equals(currentItem.getPath())) {
	        		out1 = new File(chooserSelection, "copy_" + currentItem.getFilename());
	        	}

    			if (!out1.exists()) {
		        	switch (Utils.pathCheck(chooserSelection)) {
		    		case 0:
		    			// Path on standard sdcard
		    			new AsyncCopy().execute(in1, out1);
		        		break;
		    		case 1:
		    			// Path not writable
		    			PopUps.showPopUp(getString(R.string.system_warning_title), 
		    					getString(R.string.system_warning_msg), "alert", DashboardActivity.this);
		    			break;
		    		case 2:
		    			// Path not mounted
		    			Toast.makeText(DashboardActivity.this, 
		    					getString(R.string.sdcard_unmounted_warning), 
		    					Toast.LENGTH_SHORT).show();
		        	}
    			} else {
	        		PopUps.showPopUp(getString(R.string.long_press_warning_title), 
	        				getString(R.string.long_press_warning_msg2), "info", DashboardActivity.this);
	        	}
    			break;
    			
	        case 2: // ------------- > MOVE
	        	File in2 = new File(currentItem.getPath(), currentItem.getFilename());
				File out2 = new File(chooserSelection, currentItem.getFilename());
				
	        	if (!chooserSelection.getPath().equals(currentItem.getPath())) {
	        		if (!out2.exists()) {
			        	switch (Utils.pathCheck(chooserSelection)) {
			    		case 0:
			    			// Path on standard sdcard
			    			new AsyncMove().execute(in2, out2);		
			        		break;
			    		case 1:
			    			// Path not writable
			    			PopUps.showPopUp(getString(R.string.system_warning_title), 
			    					getString(R.string.system_warning_msg), "alert", DashboardActivity.this);
			    			break;
			    		case 2:
			    			// Path not mounted
			    			Toast.makeText(DashboardActivity.this, 
			    					getString(R.string.sdcard_unmounted_warning), 
			    					Toast.LENGTH_SHORT).show();
			        	}
	        		} else {
		        		PopUps.showPopUp(getString(R.string.long_press_warning_title), 
		        				getString(R.string.long_press_warning_msg2), "info", DashboardActivity.this);
		        	}
	        	} else {
	        		PopUps.showPopUp(getString(R.string.long_press_warning_title), 
	        				getString(R.string.long_press_warning_msg), "info", DashboardActivity.this);
	        	}
	        	break;
	        	
	        case 3: // ------------- > MENU_BACKUP
	        	new Thread(new Runnable() {
    				@Override
    				public void run() {
    					Looper.prepare();
    					
			        	String date = new SimpleDateFormat("yyyy-MM-dd'_'HH-mm-ss", Locale.US).format(new Date());
			        	final File backup = new File(chooserSelection, date + "_" + YTD.JSON_FILENAME);
			        	
    					try {
							Utils.copyFile(YTD.JSON_FILE, backup);
							Toast.makeText(sDashboard, 
									getString(R.string.menu_backup_result_ok), 
									Toast.LENGTH_SHORT).show();
						} catch (IOException e) {
							Log.e(DEBUG_TAG, e.getMessage());
							Toast.makeText(sDashboard, 
									getString(R.string.menu_backup_result_failed), 
									Toast.LENGTH_LONG).show();
						}
			        	
			        	Looper.loop();
    				}
    			}).start();
	        	break;
	        	
	        case 4: // ------------- > MENU_RESTORE
				AsyncRestore ar = new AsyncRestore();
				ar.execute(chooserSelection);

	        	break;
	        	
	        case 5: // ------------- > MENU_IMPORT
	        	AsyncImport ai = new AsyncImport();
				ai.execute(chooserSelection);
	        }
		}
    }

	private class AsyncImport extends AsyncTask<File, Void, String> {
		
		private ProgressBar progressBar;

		@Override
		protected void onPreExecute() {
			TextView info = (TextView) findViewById(R.id.dashboard_activity_info);
			info.setVisibility(View.GONE);
			
			ListView list = (ListView) findViewById(R.id.dashboard_list);
			list.setVisibility(View.GONE);
			
			progressBar = (ProgressBar) findViewById(R.id.dashboard_progressbar);
    	    progressBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(File... params) {
			File chooserSelection = params[0];
			String previousJson = Json.readJsonDashboardFile(sDashboard);
        	String filename = chooserSelection.getName();
        	
			if (previousJson.contains(filename)) {
				return "e1";
			} else {
				String id = String.valueOf(System.currentTimeMillis());
	        	String type = YTD.JSON_DATA_TYPE_V;
	        	String path = chooserSelection.getParent();
	        	String basename = Utils.getFileNameWithoutExt(filename);
	        	String size = Utils.MakeSizeHumanReadable((int) chooserSelection.length(), false);
	        	
	        	String ext = Utils.getExtFromFileName(filename).toUpperCase(Locale.ENGLISH);
	        	String aExt = "unsupported";
	        	boolean go = false;
	        	
	        	if (ext.equals("WEBM")) {
	        		aExt = ".ogg";
	        		go = true;
	        	} else if (ext.equals("MP4") || ext.equals("3GPP")) {
	        		aExt = ".aac";
	        		go = true;
	        	} /*else if (ext.equals("FLV")) {
	        		aExt = ".auto";
	        		go = true;
	        	} */else {
	        		go = false;
	        	}
	        	
	        	if (go) {
	    			writeThumbToDiskForSelectedFile(chooserSelection, id);
	        		
					Json.addEntryToJsonFile(
							sDashboard, 
							id, 
							type, 
							id,
							-1,
							YTD.JSON_DATA_STATUS_IMPORTED, 
							path, 
							filename,
							basename, 
							aExt, 
							size, 
							false);
					
					return filename;
				} else {
					return "e2";
				}
			}
		}
		
		@Override
		protected void onPostExecute(String res) {
			progressBar.setVisibility(View.GONE);
			restartDashboard();
			
			if (res.equals("e1")) {
				Toast.makeText(DashboardActivity.this, 
						getString(R.string.menu_import_double), 
						Toast.LENGTH_LONG).show();
			} else if (res.equals("e2")) {
				Toast.makeText(DashboardActivity.this, 
						getString(R.string.unsupported_operation), 
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(DashboardActivity.this, 
						res + " " + getString(R.string.json_status_imported), 
						Toast.LENGTH_LONG).show();
			}
		}
	}
	
	private class AsyncRestore extends AsyncTask<File, Void, String> {
		
		private ProgressBar progressBar;

		@Override
		protected void onPreExecute() {
			TextView info = (TextView) findViewById(R.id.dashboard_activity_info);
			info.setVisibility(View.GONE);
			
			ListView list = (ListView) findViewById(R.id.dashboard_list);
			list.setVisibility(View.GONE);
			
			progressBar = (ProgressBar) findViewById(R.id.dashboard_progressbar);
    	    progressBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(File... params) {
			File chooserSelection = params[0];
			if (Utils.getExtFromFileName(chooserSelection.getName()).equals("json")) {
				Utils.logger("v", "ext => .json", DEBUG_TAG);
				try {
					// copy file
					Utils.copyFile(chooserSelection, YTD.JSON_FILE);
					
					// validate the JSON file 
					String previousJson = Json.readJsonDashboardFile(sDashboard);
					new JSONObject(previousJson);

					// empty the Lists
					idEntries.clear();
					typeEntries.clear();
					linkEntries.clear();
					posEntries.clear();
					statusEntries.clear();
					pathEntries.clear();
					filenameEntries.clear();
					basenameEntries.clear();
					audioExtEntries.clear();
					sizeEntries.clear();
					partSizeEntries.clear();
					progressEntries.clear();
					speedEntries.clear();
					
					// refill the lists
					int  entries = parseJson(DashboardActivity.this);

					Utils.logger("d", "idEntries: " + entries, DEBUG_TAG);

					for (int i = 0; i < entries; i++ ) {
						writeThumbToDiskForSelectedFile(new File(pathEntries.get(i), filenameEntries.get(i)), linkEntries.get(i));
					}
					return String.valueOf(entries);
				} catch (JSONException e1) {
					Log.e(DEBUG_TAG, e1.getMessage());
					return "e1";
				} catch (IOException e2) {
					Log.e(DEBUG_TAG, e2.getMessage());
					return "e2";
				}
			} else {
				return "e3";
			}
		}
		
		@Override
		protected void onPostExecute(String res) {
			progressBar.setVisibility(View.GONE);
			restartDashboard();
			
			if (res.equals("e1")) {
				//JSONException e1
				Toast.makeText(sDashboard, 
						sDashboard.getString(R.string.menu_restore_result_failed), 
						Toast.LENGTH_LONG).show();
			} else if (res.equals("e2")) {
				//IOException e2
				Toast.makeText(sDashboard, 
						sDashboard.getString(R.string.menu_restore_result_failed), 
						Toast.LENGTH_LONG).show();
			} else if (res.equals("e3")) {
				//file ext not .json
				Toast.makeText(sDashboard, 
						sDashboard.getString(R.string.invalid_data), 
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(sDashboard, 
						getString(R.string.menu_restore_result_ok) + " (" + res + ")", 
						Toast.LENGTH_SHORT).show();
				Utils.logger("d", "Restored " + res + " entries", DEBUG_TAG);
			}
		}
	}

	private void writeThumbToDiskForSelectedFile(final File selectedFile, String pngBasename) {
		Bitmap bmThumbnail = ThumbnailUtils.createVideoThumbnail(selectedFile.getAbsolutePath(), Thumbnails.MINI_KIND);
		File bmFile = new File(getDir(YTD.THUMBS_FOLDER, 0), pngBasename + ".png");
		try {
			Utils.logger("d", "trying to write thumbnail for " + selectedFile.getName() + " -> " + pngBasename, DEBUG_TAG);
			FileOutputStream out = new FileOutputStream(bmFile);
			bmThumbnail.compress(Bitmap.CompressFormat.PNG, 90, out);
		} catch (Exception e) {
			Log.e(DEBUG_TAG, "writeThumbToDiskForImportedVideo -> " + e.getMessage());
		}
	}

	private void restartDashboard() {
		Intent intent = DashboardActivity.this.getIntent();
		DashboardActivity.this.finish();
		DashboardActivity.this.startActivity(intent);
	}
	
	private class AsyncMove extends AsyncTask<File, Void, Integer> {
		
		File out;
		private boolean delResOk;
		
		protected void onPreExecute() {
			isAnyAsyncInProgress = true;
			Utils.logger("d", currentItem.getFilename() + " ---> BEGIN move", DEBUG_TAG);
			Toast.makeText(DashboardActivity.this, 
					currentItem.getFilename() + ": " + getString(R.string.move_progress), 
					Toast.LENGTH_SHORT).show();
		}
		
		protected Integer doInBackground(File... file) {
			out = file[1];
			try {
				Utils.copyFile(file[0], file[1]);
				delResOk = doDelete(currentItem, file[0], false);
				return 0;
			} catch (IOException e) {
				return 1;
			}
		}
		
		@Override
		protected void onPostExecute(Integer res) {
			switch (res) {
			case 0:
				Toast.makeText(DashboardActivity.this, 
						currentItem.getFilename() + ": " + getString(R.string.move_ok), 
						Toast.LENGTH_LONG).show();
				Utils.logger("i", currentItem.getFilename() + " --> END move: OK", DEBUG_TAG);
				
				Utils.scanMedia(DashboardActivity.this, 
						new String[]{ out.getAbsolutePath() }, 
						new String[]{ "video/*" });
				
				Json.addEntryToJsonFile(
						DashboardActivity.this, 
						currentItem.getId(), 
						currentItem.getType(), 
						currentItem.getYtId(), 
						currentItem.getPos(),
						currentItem.getStatus(), 
						out.getParent(), 
						out.getName(), 
						currentItem.getBasename(), 
						currentItem.getAudioExt(), 
						currentItem.getSize(), 
						false);
				break;
				
			case 1:
				Toast.makeText(DashboardActivity.this, 
						currentItem.getFilename() + ": " + getString(R.string.move_error), 
						Toast.LENGTH_LONG).show();
				Log.e(DEBUG_TAG, currentItem.getFilename() + " --> END move: FAILED");
			}
			
			refreshlist(DashboardActivity.this);
		
			if (!delResOk) {
				Utils.logger("w", currentItem.getFilename() + " --> Copy OK (but not Deletion: original file still in place)", DEBUG_TAG);
			}
			
			isAnyAsyncInProgress = false;
		}
	}
	
	private class AsyncCopy extends AsyncTask<File, Void, Integer> {
		
		File out;
		
		protected void onPreExecute() {
			isAnyAsyncInProgress = true;
			Utils.logger("d", currentItem.getFilename() + " ---> BEGIN copy", DEBUG_TAG);
			Toast.makeText(DashboardActivity.this, 
					currentItem.getFilename() + ": " + getString(R.string.copy_progress), 
					Toast.LENGTH_SHORT).show();
		}
		
		protected Integer doInBackground(File... file) {
			out = file[1];
			try {
				Utils.copyFile(file[0], file[1]);
				return 0;
			} catch (IOException e) {
				return 1;
			}
		}
		
		@Override
		protected void onPostExecute(Integer res) {
			switch (res) {
			case 0:
				Toast.makeText(DashboardActivity.this, 
						currentItem.getFilename() + ": " + getString(R.string.copy_ok), 
						Toast.LENGTH_LONG).show();
				Utils.logger("i", currentItem.getFilename() + " --> END copy: OK", DEBUG_TAG);
				
				Utils.scanMedia(DashboardActivity.this, 
						new String[]{ out.getAbsolutePath() }, 
						new String[]{ "video/*" });
				
				Json.addEntryToJsonFile(
						DashboardActivity.this, 
						currentItem.getId(), 
						currentItem.getType(), 
						currentItem.getYtId(), 
						currentItem.getPos(),
						currentItem.getStatus(), 
						out.getParent(), 
						out.getName(), 
						currentItem.getBasename(), 
						currentItem.getAudioExt(), 
						currentItem.getSize(), 
						true);
				break;
			
			case 1:
				Toast.makeText(DashboardActivity.this, 
						currentItem.getFilename() + ": " + getString(R.string.copy_error), 
						Toast.LENGTH_LONG).show();
				Log.e(DEBUG_TAG, currentItem.getFilename() + " --> END copy: FAILED");
			}
			
			refreshlist(DashboardActivity.this);
			isAnyAsyncInProgress = false;
		}
	}
	
	public static int refreshlist(final Activity activity) {
		entries = 0;
		activity.runOnUiThread(new Runnable() {
			public void run() {
				
				clearAdapterAndLists();
			    
			    // refill the Lists and re-populate the adapter
			    entries = parseJson((Context) activity);
			    updateProgressBars();
				buildList();
				
				if (da.isEmpty()) {
		            showEmptyListInfo(activity);
		    	}
				
				// refresh the list view
				da.notifyDataSetChanged();
			}
		});
		return entries;
	}
	
	public static void clearAdapterAndLists() {
		// clear the adapter
		da.clear();
		
		// empty the Lists
		idEntries.clear();
		typeEntries.clear();
		linkEntries.clear();
		posEntries.clear();
		statusEntries.clear();
		pathEntries.clear();
		filenameEntries.clear();
		basenameEntries.clear();
		audioExtEntries.clear();
		sizeEntries.clear();
		partSizeEntries.clear();
		progressEntries.clear();
		speedEntries.clear();
	}
	
	private static int parseJson(Context context) {
		// read existing/init new JSON 
		String previousJson = Json.readJsonDashboardFile(context);
				
		JSONObject jV = null;
		try {
			jV = new JSONObject(previousJson);
			//Utils.logger("v", "current json:\n" + previousJson, DEBUG_TAG);
			@SuppressWarnings("unchecked")
			Iterator<Object> ids = jV.keys();
			while (ids.hasNext()) {
				String id = (String) ids.next();
				JSONObject jO = new JSONObject();
				jO = jV.getJSONObject(id);
				idEntries.add(id);
				typeEntries.add(jO.getString(YTD.JSON_DATA_TYPE));
				linkEntries.add(jO.getString(YTD.JSON_DATA_YTID));
				posEntries.add(jO.getInt(YTD.JSON_DATA_POS));
				statusEntries.add(jO.getString(YTD.JSON_DATA_STATUS));
				pathEntries.add(jO.getString(YTD.JSON_DATA_PATH));
				filenameEntries.add(jO.getString(YTD.JSON_DATA_FILENAME));
				basenameEntries.add(jO.getString(YTD.JSON_DATA_BASENAME));
				audioExtEntries.add(jO.getString(YTD.JSON_DATA_AUDIO_EXT));
				sizeEntries.add(jO.getString(YTD.JSON_DATA_SIZE));
			}
		} catch (JSONException e) {
			Log.e(DEBUG_TAG, e.getMessage());
			Toast.makeText(sDashboard, 
					sDashboard.getString(R.string.invalid_data), 
					Toast.LENGTH_LONG).show();
			YTD.JSON_FILE.delete();
		}
		
		// do sort by filenames
		List<String> oldFilenameEntries = new ArrayList<String>(filenameEntries);
		List<String> oldIdEntries = new ArrayList<String>(idEntries);
		List<String> oldTypeEntries = new ArrayList<String>(typeEntries);
		List<String> oldLinkEntries = new ArrayList<String>(linkEntries);
		List<Integer> oldPosEntries = new ArrayList<Integer>(posEntries);
		List<String> oldStatusEntries = new ArrayList<String>(statusEntries);
		List<String> oldPathEntries = new ArrayList<String>(pathEntries);
		List<String> oldBasenameEntries = new ArrayList<String>(basenameEntries);
		List<String> oldAudioExtEntries = new ArrayList<String>(audioExtEntries);
		List<String> oldSizeEntries = new ArrayList<String>(sizeEntries);
		
		idEntries.clear();
		typeEntries.clear();
		linkEntries.clear();
		posEntries.clear();
		statusEntries.clear();
		pathEntries.clear();
		basenameEntries.clear();
		audioExtEntries.clear();
		sizeEntries.clear();

		Collections.sort(filenameEntries, String.CASE_INSENSITIVE_ORDER);
		
		for (int i = 0; i < filenameEntries.size(); i++ ) {
			for (int j = 0; j < oldFilenameEntries.size(); j++ ) {
				if (oldFilenameEntries.get(j) == filenameEntries.get(i)) {
					idEntries.add(oldIdEntries.get(j));
					typeEntries.add(oldTypeEntries.get(j));
					linkEntries.add(oldLinkEntries.get(j));
					posEntries.add(oldPosEntries.get(j));
					statusEntries.add(oldStatusEntries.get(j));
					pathEntries.add(oldPathEntries.get(j));
					basenameEntries.add(oldBasenameEntries.get(j));
					audioExtEntries.add(oldAudioExtEntries.get(j));
					sizeEntries.add(oldSizeEntries.get(j));
				}
			}
		}
		return idEntries.size();
	}
	
	private static void updateProgressBars() {
		for (int i = 0; i < idEntries.size(); i++ ) {
			try {
				if (statusEntries.get(i).equals(YTD.JSON_DATA_STATUS_IN_PROGRESS)) {
					
					String idstr = idEntries.get(i);
					long idlong = Long.parseLong(idstr);
					long bytes_downloaded = 0;
					long bytes_total = 0;
					int progress = 0;
					long speed = 0;
					
					try {
						if (Maps.mDownloadSizeMap.get(idlong) != null) {
							bytes_downloaded = Maps.mDownloadSizeMap.get(idlong); //YTD.downloadPartialSizeMap.get(idlong);
							bytes_total = Maps.mTotalSizeMap.get(idlong);			//YTD.downloadTotalSizeMap.get(idlong);
							progress = (int) Maps.mDownloadPercentMap.get(idlong);	//YTD.downloadPercentMap.get(idlong);
							speed = Maps.mNetworkSpeedMap.get(idlong);
						} else {
							countdown--;
							Utils.logger("w", "updateProgressBars: waiting for DM Maps on id " + idstr + " # " + countdown, DEBUG_TAG);
							progress = 0;
							bytes_downloaded = 0;
							bytes_total = 0;
							speed = 0;
							
							DownloadTask dt = Maps.dtMap.get(idlong);
							
							if (countdown == 0 && dt == null) {
								Utils.logger("w", "countdown == 0 && dt == null; setting STATUS_PAUSED on id " + idstr, DEBUG_TAG);
								Json.addEntryToJsonFile(
										sDashboard,
										idstr, 
										typeEntries.get(i),
										linkEntries.get(i), 
										posEntries.get(i),
										YTD.JSON_DATA_STATUS_PAUSED,
										pathEntries.get(i), 
										filenameEntries.get(i),
										basenameEntries.get(i), 
										audioExtEntries.get(i),
										sizeEntries.get(i), 
										false);
							}
						}
					} catch (NullPointerException e) {
						Log.e(DEBUG_TAG, "NPE @ updateProgressBars");
					}
					
					String readableBytesDownloaded = Utils.MakeSizeHumanReadable(bytes_downloaded, false);
					String readableBytesTotal = Utils.MakeSizeHumanReadable(bytes_total, false);
					
					String progressRatio;
					if (readableBytesTotal.equals("-")) {
						progressRatio = "";
					} else {
						progressRatio = readableBytesDownloaded + "/" + readableBytesTotal;
					}

					progressEntries.add(progress);
					partSizeEntries.add(progressRatio + " (" + String.valueOf(progress) + "%)");
					speedEntries.add(speed);
				} else {
					progressEntries.add(100);
					partSizeEntries.add("-/-");
					speedEntries.add((long) 0);
				}
			} catch (IndexOutOfBoundsException e) {
				Utils.logger("w", "updateProgressBars: " + e.getMessage(), DEBUG_TAG);
			}
		}
	}
	
	private static void buildList() {
		for (int i = 0; i < idEntries.size(); i++) {
			String thisSize;
			try {
				String thisStatus = statusEntries.get(i);

				if (thisStatus.equals(YTD.JSON_DATA_STATUS_IN_PROGRESS) && speedEntries.get(i) != 0) {
					thisSize = partSizeEntries.get(i);
				} else {
					thisSize = sizeEntries.get(i);
				}

				itemsList.add(new DashboardListItem(
						idEntries.get(i),
						typeEntries.get(i),
						linkEntries.get(i), 
						posEntries.get(i), 
						thisStatus
							.replace(YTD.JSON_DATA_STATUS_COMPLETED, sDashboard.getString(R.string.json_status_completed))
							.replace(YTD.JSON_DATA_STATUS_IN_PROGRESS, sDashboard.getString(R.string.json_status_in_progress))
							.replace(YTD.JSON_DATA_STATUS_FAILED, sDashboard.getString(R.string.json_status_failed))
							.replace(YTD.JSON_DATA_STATUS_IMPORTED, sDashboard.getString(R.string.json_status_imported))
							.replace(YTD.JSON_DATA_STATUS_PAUSED, sDashboard.getString(R.string.json_status_paused)),
						pathEntries.get(i), 
						filenameEntries.get(i), 
						basenameEntries.get(i),
						audioExtEntries.get(i), 
						thisSize, 
						progressEntries.get(i),
						speedEntries.get(i)));
			} catch (IndexOutOfBoundsException e) {
				Utils.logger("w", "buildList: " + e.getMessage(), DEBUG_TAG);
			}
		}
	}
	
	// #####################################################################
	
	public void editId3Tags(View view) {
		BugSenseHandler.leaveBreadcrumb("editId3Tags");
		if (newClick) {
			tagArtist = "";
			tagAlbum = "";
			tagTitle = "";
			tagGenre = "";
			tagYear = "";
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(boxThemeContextWrapper);
	    LayoutInflater inflater0 = getLayoutInflater();
	    final View id3s = inflater0.inflate(R.layout.dialog_edit_id3, null);
	    
	    final EditText artistEt = (EditText) id3s.findViewById(R.id.id3_et_artist);
	    final EditText titleEt = (EditText) id3s.findViewById(R.id.id3_et_title);	
	    final EditText albumEt = (EditText) id3s.findViewById(R.id.id3_et_album);
	    final EditText genreEt = (EditText) id3s.findViewById(R.id.id3_et_genre);
	    final EditText yearEt = (EditText) id3s.findViewById(R.id.id3_et_year);
	    
	    if (tagTitle.isEmpty()) {
			titleEt.setText(currentItem.getBasename());
		} else {
			titleEt.setText(tagTitle);
		}
	    
	    if (tagYear.isEmpty()) {
			Calendar cal = new GregorianCalendar();
			int y = cal.get(Calendar.YEAR);
			yearEt.setText(String.valueOf(y));
		} else {
			yearEt.setText(tagYear);
		}
	    
		artistEt.setText(tagArtist);
		albumEt.setText(tagAlbum);
		genreEt.setText(tagGenre);
	    
		builder.setView(id3s)
	           .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	            	   tagArtist = artistEt.getText().toString();
	            	   tagAlbum = albumEt.getText().toString();
	            	   tagTitle = titleEt.getText().toString();
	            	   tagGenre = genreEt.getText().toString();
	            	   tagYear = yearEt.getText().toString();
	               }
	           })
	           .setNegativeButton(R.string.dialogs_negative, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	                   // cancel
	               }
	           });      
	    
	    secureShowDialog(builder);
	    newClick = false;
	}

	public void ffmpegJob(final File fileToConvert, final String bitrateType, final String bitrateValue) {
		BugSenseHandler.leaveBreadcrumb("ffmpegJob");
		isFfmpegRunning = true;
		
		vfilename = currentItem.getFilename();
		
		// audio job notification init
		aBuilder =  new NotificationCompat.Builder(this);
		aNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		aBuilder.setSmallIcon(R.drawable.ic_stat_ytd);
		aBuilder.setContentTitle(vfilename);
		
		String aExt = currentItem.getAudioExt();
		basename = currentItem.getBasename();
		
		final String audioFileName;
		// "compose" the audio file
		if (bitrateValue != null) {
			extrTypeIsMp3Conv = true;
			audioFileName = basename + "_" + bitrateType + "-" + bitrateValue + ".mp3";
		} else {
			extrTypeIsMp3Conv = false;
			audioFileName = basename + aExt;
			
		}
		
		audioFile = new File(fileToConvert.getParent(), audioFileName);

		if (!audioFile.exists()) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					Looper.prepare();

					FfmpegController ffmpeg = null;
					try {
						ffmpeg = new FfmpegController(DashboardActivity.this);

						// Toast + Notification + Log ::: Audio job in progress...
						String text = null;
						if (!extrTypeIsMp3Conv) {
							text = getString(R.string.audio_extr_progress);
							type = YTD.JSON_DATA_TYPE_A_E;
						} else {
							text = getString(R.string.audio_conv_progress);
							type = YTD.JSON_DATA_TYPE_A_M;
						}
						Toast.makeText(DashboardActivity.this, "YTD: " + text,
								Toast.LENGTH_LONG).show();

						/*
						 * Utils.addEntryToJsonFile( DashboardActivity.this,
						 * currentItem.getId(), type, YTD.JSON_DATA_STATUS_IN_PROGRESS,
						 * currentItem.getPath(), audioFile.getName(),
						 * currentItem.getBasename(), "", "-", true);
						 * 
						 * refreshlist(DashboardActivity.this);
						 */

						aBuilder.setContentTitle(audioFileName);
						aBuilder.setContentText(text);
						aNotificationManager.notify(2, aBuilder.build());
						Utils.logger("i", vfilename + " " + text, DEBUG_TAG);
					} catch (IOException ioe) {
						Log.e(DEBUG_TAG,
								"Error loading ffmpeg. " + ioe.getMessage());
					}

					ShellDummy shell = new ShellDummy();

					try {
						ffmpeg.extractAudio(fileToConvert, audioFile,
								bitrateType, bitrateValue, shell);
					} catch (IOException e) {
						Log.e(DEBUG_TAG,
								"IOException running ffmpeg" + e.getMessage());
					} catch (InterruptedException e) {
						Log.e(DEBUG_TAG, "InterruptedException running ffmpeg"
								+ e.getMessage());
					}
					Looper.loop();
				}
			}).start();
		} else {
			PopUps.showPopUp(getString(R.string.long_press_warning_title), getString(R.string.audio_extr_warning_msg), "info", DashboardActivity.this);
		}
	}
	
	private class ShellDummy implements ShellCallback {

		@Override
		public void shellOut(String shellLine) {
			findAudioSuffix(shellLine);
			if (extrTypeIsMp3Conv) {
				getAudioJobProgress(shellLine);
			}
			Utils.logger("d", shellLine, DEBUG_TAG);
		}

		@Override
		public void processComplete(int exitValue) {
			Utils.logger("i", "FFmpeg process exit value: " + exitValue, DEBUG_TAG);
			String text = null;
			Intent audioIntent = new Intent(Intent.ACTION_VIEW);
			if (exitValue == 0) {

				// Toast + Notification + Log ::: Audio job OK
				if (!extrTypeIsMp3Conv) {
					text = getString(R.string.audio_extr_completed);
				} else {
					text = getString(R.string.audio_conv_completed);
				}
				Utils.logger("d", vfilename + " " + text, DEBUG_TAG);
				
				audioFile = addSuffixToAudioFile(basename, audioFile);
				Toast.makeText(DashboardActivity.this,  audioFile.getName() + ": " + text, Toast.LENGTH_LONG).show();
				aBuilder.setContentTitle(audioFile.getName());
				aBuilder.setContentText(text);			
				audioIntent.setDataAndType(Uri.fromFile(audioFile), "audio/*");
				PendingIntent contentIntent = PendingIntent.getActivity(DashboardActivity.this, 0, audioIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        		aBuilder.setContentIntent(contentIntent);
        		
        		// write id3 tags
				if (extrTypeIsMp3Conv) {
					try {
						Utils.logger("d", "writing ID3 tags...", DEBUG_TAG);
						addId3Tags(audioFile, tagArtist, tagAlbum, tagTitle, tagGenre, tagYear);
					} catch (ID3WriteException e) {
						Log.e(DEBUG_TAG, "Unable to write id3 tags", e);
					} catch (IOException e) {
						Log.e(DEBUG_TAG, "Unable to write id3 tags", e);
					}
				}
				
				Utils.scanMedia(getApplicationContext(), 
						new String[] {audioFile.getAbsolutePath()}, 
						new String[] {"audio/*"});
				
				// remove selected video upon successful audio extraction
				if (removeVideo || removeAudio) {
					final File fileToDel = new File(currentItem.getPath(), currentItem.getFilename());
					new AsyncDelete().execute(fileToDel);
				}
				
				// add audio file to the JSON file entry
				Json.addEntryToJsonFile(
						DashboardActivity.this, 
						currentItem.getId(), 
						type, 
						currentItem.getYtId(), 
						currentItem.getPos(),
						YTD.JSON_DATA_STATUS_COMPLETED,
						currentItem.getPath(), 
						audioFile.getName(), 
						currentItem.getBasename(), 
						"", 
						Utils.MakeSizeHumanReadable((int) audioFile.length(), false), 
						true);
				
				refreshlist(DashboardActivity.this);
				
				Utils.setNotificationDefaults(aBuilder);
			} else {
				setNotificationForAudioJobError();
				
				Json.addEntryToJsonFile(
						DashboardActivity.this, 
						currentItem.getId(), 
						type, 
						currentItem.getYtId(),
						currentItem.getPos(),
						YTD.JSON_DATA_STATUS_FAILED,
						currentItem.getPath(), 
						audioFile.getName(), 
						currentItem.getBasename(), 
						"", 
						"-", 
						true);
				
				refreshlist(DashboardActivity.this);
			}
			
			aBuilder.setProgress(0, 0, false);
			aNotificationManager.cancel(2);
			aNotificationManager.notify(2, aBuilder.build());
			
			isFfmpegRunning = false;
		}
		
		@Override
		public void processNotStartedCheck(boolean started) {
			if (!started) {
				Utils.logger("w", "FFmpeg process not started or not completed", DEBUG_TAG);

				// Toast + Notification + Log ::: Audio job error
				setNotificationForAudioJobError();
			}
			aNotificationManager.notify(2, aBuilder.build());
			isFfmpegRunning = false;
		}
    }
    
	public File addSuffixToAudioFile(String aBaseName, File extractedAudioFile) {
		// Rename audio file to add a more detailed suffix, 
		// but only if it has been matched from the ffmpeg console output
		if (!extrTypeIsMp3Conv &&
				extractedAudioFile.exists() && 
				!aSuffix.equals(".audio")) {
			String newName = aBaseName + aSuffix;
			File newFile = new File(currentItem.getPath(), newName);
			if (extractedAudioFile.renameTo(newFile)) {
				Utils.logger("i", "'" + extractedAudioFile.getName() + "' renamed to: '" + newName + "'", DEBUG_TAG);
				return newFile;
			} else {
				Log.e(DEBUG_TAG, "Unable to rename '" + extractedAudioFile.getName() + "' to: '" + aSuffix + "'");
			}
		}
		return extractedAudioFile;
	}

	/* method addId3Tags adapted from Stack Overflow:
	 * 
	 * http://stackoverflow.com/questions/9707572/android-how-to-get-and-setchange-id3-tagmetadata-of-audio-files/9770646#9770646
	 * 
	 * Q: http://stackoverflow.com/users/849664/chirag-shah
	 * A: http://stackoverflow.com/users/903469/mkjparekh
	 */

	public void addId3Tags(File src, String artist, String album, String title, String genre, String year ) 
			throws IOException, ID3WriteException {
        MusicMetadataSet src_set = new MyID3().read(src);
        if (src_set == null) {
            Utils.logger("w", "no metadata", DEBUG_TAG);
        } else {
        	MusicMetadata meta = new MusicMetadata("ytd");
        	
        	if (artist == null || artist.isEmpty()) artist = "YTD";
        	meta.setArtist(artist);
        	
        	if (album == null || album.isEmpty()) album = "YTD Extracted Audio";
        	meta.setAlbum(album);
        	
        	if (title == null || title.isEmpty()) title = basename;
        	meta.setSongTitle(title);
	        
        	if (genre != null) meta.setGenre(genre);
        	
        	if (year != null) meta.setYear(year);
        	
        	Utils.logger("d", "metadata used for last id3tag:" +
        			"\n  artist: " + artist +
        			"\n  album: " + album +
        			"\n  title: " + title +
        			"\n  genre: " + genre +
        			"\n  year: " + year, DEBUG_TAG);
	        new MyID3().update(src, src_set, meta);
        }
	}

	private void findAudioSuffix(String shellLine) {
		Pattern audioPattern = Pattern.compile("#0:0.*: Audio: (.+), .+?(mono|stereo .default.|stereo)(, .+ kb|)"); 
		Matcher audioMatcher = audioPattern.matcher(shellLine);
		if (audioMatcher.find() && !extrTypeIsMp3Conv) {
			String oggBr = "a";
			String groupTwo = "n";
			if (audioMatcher.group(2).equals("stereo (default)")) {
				if (vfilename.contains("hd")) {
					oggBr = "192k";
				} else {
					oggBr = "128k";
				}
				groupTwo = "stereo";
			} else {
				oggBr = "";
				groupTwo = audioMatcher.group(2);
			}
			
			aSuffix = "_" +
					groupTwo + 
					"_" + 
					audioMatcher.group(3).replace(", ", "").replace(" kb", "k") + 
					oggBr + 
					"." +
					audioMatcher.group(1).replaceFirst(" (.*/.*)", "").replace("vorbis", "ogg");
			
			Utils.logger("i", "Audio suffix found: " + aSuffix, DEBUG_TAG);
		}
	}

	public void setNotificationForAudioJobError() {
		BugSenseHandler.leaveBreadcrumb("setNotificationForAudioJobError");
		String text;
		if (!extrTypeIsMp3Conv) {
			text = getString(R.string.audio_extr_error);
		} else {
			text = getString(R.string.audio_conv_error);
		}
		Log.e(DEBUG_TAG, vfilename + " " + text);
		Toast.makeText(DashboardActivity.this,  "YTD: " + text, Toast.LENGTH_LONG).show();
		aBuilder.setContentText(text);
	}
	
	private void getAudioJobProgress(String shellLine) {
		Pattern totalTimePattern = Pattern.compile("Duration: (..):(..):(..)\\.(..)");
		Matcher totalTimeMatcher = totalTimePattern.matcher(shellLine);
		if (totalTimeMatcher.find()) {
			totSeconds = getTotSeconds(totalTimeMatcher);
		}
		
		Pattern currentTimePattern = Pattern.compile("time=(..):(..):(..)\\.(..)");
		Matcher currentTimeMatcher = currentTimePattern.matcher(shellLine);
		if (currentTimeMatcher.find()) {
			currentTime = getTotSeconds(currentTimeMatcher);
		}
		
		if (totSeconds != 0) {
			aBuilder.setProgress(totSeconds, currentTime, false);
			aNotificationManager.notify(2, aBuilder.build());
		}
	}

	private int getTotSeconds(Matcher timeMatcher) {
		int h = Integer.parseInt(timeMatcher.group(1));
		int m = Integer.parseInt(timeMatcher.group(2));
		int s = Integer.parseInt(timeMatcher.group(3));
		int f = Integer.parseInt(timeMatcher.group(4));
		
		long hToSec = TimeUnit.HOURS.toSeconds(h);
		long mToSec = TimeUnit.MINUTES.toSeconds(m);
		
		int tot = (int) (hToSec + mToSec + s);
		if (f > 50) tot = tot + 1;
		
		Utils.logger("i", "h=" + h + " m=" + m + " s=" + s + "." + f + " -> tot=" + tot,	DEBUG_TAG);
		return tot;
	}

	public void secureShowDialog(AlertDialog.Builder adb) {
		//builder.create();
		if (! ((Activity) DashboardActivity.this).isFinishing()) {
			adb.show();
		}
	}

	public void notifyFfmpegNotInstalled() {
		Utils.logger("w", "FFmpeg not installed/enabled", DEBUG_TAG);
		BugSenseHandler.leaveBreadcrumb("notifyFfmpegNotInstalled");
		AlertDialog.Builder adb = new AlertDialog.Builder(boxThemeContextWrapper);
		adb.setTitle(getString(R.string.ffmpeg_not_enabled_title));
		adb.setMessage(getString(R.string.ffmpeg_not_enabled_msg));
		
		adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				startActivity(new Intent(DashboardActivity.this,  SettingsActivity.class));
			}
		
		});
		
		adb.setNegativeButton(getString(R.string.dialogs_negative), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
		        // cancel
		    }
		});
		
		secureShowDialog(adb);
	}
	
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

	    if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
	    	isLandscape = true;
	    } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
	    	isLandscape = false;
	    }
	}

	private String[] retrieveBitrateValueFromSpinner(final Spinner sp) {
		String bitrateEntry = String.valueOf(sp.getSelectedItem());
		   
		String[] bitrateValues = sDashboard.getResources()
				   .getStringArray(R.array.mp3_bitrate_entry_values);
		   
		String[] bitrateEntries = sDashboard.getResources()
				   .getStringArray(R.array.mp3_bitrate_entries);
		
		String bitrateType = null;
		if (bitrateEntry.contains("CBR")) {
			bitrateType = "CBR";
		} else {
			bitrateType = "VBR";
		}
		   
		String bitrateValue = null;
		for (int i = 0; i < bitrateValues.length; i++ ) {
			if (bitrateEntry.equals(bitrateEntries[i]))
			 bitrateValue = bitrateValues[i];
		}
		Utils.logger("d", "selected bitrate value: " + bitrateValue + 
						"\nselected bitrate entry: " + bitrateEntry , DEBUG_TAG);
		
		return new String[] { bitrateType, bitrateValue };
	}
}
