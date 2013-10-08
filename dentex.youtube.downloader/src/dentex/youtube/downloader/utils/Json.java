package dentex.youtube.downloader.utils;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import dentex.youtube.downloader.R;
import dentex.youtube.downloader.YTD;

public class Json {
	
	static String DEBUG_TAG = "Json";

	public static void addEntryToJsonFile(Context context, String id, String type, String ytId, int pos, String status, 
			String path, String filename, String basename, String audioExt, String size, boolean forceCopy) {
		
		// parse existing/init new JSON 
		String previousJson = Json.readJsonDashboardFile(context);
		
		// create new "complex" object
		JSONObject mO = null;
		JSONObject jO = new JSONObject();
		
		try {
			mO = new JSONObject(previousJson);
			
			JSONObject obj = mO.optJSONObject(id);
			if (obj != null) {
				if (forceCopy) {
					String newId = String.valueOf(System.currentTimeMillis());
					Utils.logger("v", "Copying existent ID " + id + " into " + newId, DEBUG_TAG);
					id = newId;
				} else {
					Utils.logger("v", "Updating existent ID " + id, DEBUG_TAG);
				}
			} else {
				Utils.logger("v", "Addind new ID " + id, DEBUG_TAG);
			}
			
			if (status.equals(YTD.ctx.getResources().getString(R.string.json_status_completed))) 
				status = YTD.JSON_DATA_STATUS_COMPLETED;
			if (status.equals(YTD.ctx.getResources().getString(R.string.json_status_in_progress))) 
				status =  YTD.JSON_DATA_STATUS_IN_PROGRESS;
			if (status.equals(YTD.ctx.getResources().getString(R.string.json_status_failed))) 
				status = YTD.JSON_DATA_STATUS_FAILED;
			if (status.equals(YTD.ctx.getResources().getString(R.string.json_status_paused))) 
				status = YTD.JSON_DATA_STATUS_PAUSED;
			if (status.equals(YTD.ctx.getResources().getString(R.string.json_status_imported))) 
				status = YTD.JSON_DATA_STATUS_IMPORTED;
			
			jO.put(YTD.JSON_DATA_TYPE, type);
			jO.put(YTD.JSON_DATA_YTID, ytId);
			jO.put(YTD.JSON_DATA_POS, pos);
			jO.put(YTD.JSON_DATA_STATUS, status);
			jO.put(YTD.JSON_DATA_PATH, path);
			jO.put(YTD.JSON_DATA_FILENAME, filename);
			jO.put(YTD.JSON_DATA_BASENAME, basename);
			jO.put(YTD.JSON_DATA_AUDIO_EXT, audioExt);
			jO.put(YTD.JSON_DATA_SIZE, size);
			mO.put(id, jO);
		} catch (JSONException e1) {
			Log.e(DEBUG_TAG, e1.getMessage());
		}
		
		// generate string from the object
		String jsonString = null;
		try {
			jsonString = mO.toString(4);
		} catch (JSONException e1) {
			Log.e(DEBUG_TAG, e1.getMessage());
		}
	
		// write back JSON file
		Utils.logger("v", jsonString, DEBUG_TAG);
		Utils.writeToFile(YTD.JSON_FILE, jsonString);
	}

	public static void removeEntryFromJsonFile(Context context, String id) {
		String previousJson = Json.readJsonDashboardFile(context);
		
		JSONObject mO = null;
		try {
			Utils.logger("v", "Removing ID " + id, DEBUG_TAG);
			mO = new JSONObject(previousJson);
			mO.remove(id);
		} catch (JSONException e1) {
			Log.e(DEBUG_TAG, e1.getMessage());
		}
		
		String jsonString = null;
		try {
			jsonString = mO.toString(4);
		} catch (JSONException e1) {
			Log.e(DEBUG_TAG, e1.getMessage());
		}
	
		Utils.logger("v", jsonString, DEBUG_TAG);
		Utils.writeToFile(YTD.JSON_FILE, jsonString);
	}

	public static String readJsonDashboardFile(Context context) {
		String jsonString = null;
		if (YTD.JSON_FILE.exists()) {
			try {
				jsonString = Utils.readFromFile(YTD.JSON_FILE);
			} catch (IOException e1) {
				jsonString = "{}";
				Log.e(DEBUG_TAG, e1.getMessage());
			}
		} else {
			jsonString = "{}";
		}
		return jsonString;
	}
}
