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


package dentex.youtube.downloader.docs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import dentex.youtube.downloader.R;
import dentex.youtube.downloader.utils.SectionedAdapter;
import dentex.youtube.downloader.utils.Utils;

public class TranslatorsListActivity extends ListActivity {
	
	public static final String DEBUG_TAG = "TranslatorsShowActivity";
	private String json;
	public AssetManager assMan;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		
		// Theme init
    	Utils.themeInit(this);
    	
		setContentView(R.layout.activity_translators_list);
		
		setupActionBar();
		
		String[] languagesArray = getLanguages(false);
		String[] decodedLanguagesArray = getLanguages(true);
		
		for (int i = 0; i < languagesArray.length; i++) {
			String[] list = getTranslatorsNames(languagesArray[i]);
			adapter.addSection(decodedLanguagesArray[i], new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list));
		}

		setListAdapter(adapter);
	}

	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
	    if (item.getItemId() == android.R.id.home) {
	        NavUtils.navigateUpFromSameTask(this);
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}

	private String[] getLanguages(boolean chooseDecoded) {
		assMan = getAssets();
		String[] languagesArray = { "All" };
		String[] decodedLanguagesArray = { "All" };
		try {
        	InputStream in = assMan.open("languages");
        	InputStreamReader is = new InputStreamReader(in);
			StringBuilder sb = new StringBuilder();
			BufferedReader br = new BufferedReader(is);
			String read = br.readLine();
			while(read != null) {
			    sb.append(read + "_");
			    read = br.readLine();
			}
			String languages = sb.toString();
			String ianaDecodedLanguages = ianaDecode(languages);
        	languagesArray = languages.split("_");
        	decodedLanguagesArray = ianaDecodedLanguages.split("_");
        	//Log.i(DEBUG_TAG, languages);
        	//Log.i(DEBUG_TAG, ianaDecodedLanguages);
        	
		} catch (IOException e) {
			Log.e(DEBUG_TAG, e.getMessage());
		}
		if (chooseDecoded) {
			return decodedLanguagesArray;
		} else {
			return languagesArray;
		}
	}
	
	private String ianaDecode(String encoded) {
		encoded = encoded
					.replaceFirst("ar", "ar  (Arabic)")
					.replaceFirst("zh-CN", "zh-CN  (Chinese, China)")
			        .replaceFirst("zh-HK", "zh-HK  (Chinese, Hong Kong)")
			        .replaceFirst("zh-TW", "zh-TW  (Chinese, Taiwan)")
			        .replaceFirst("da", "da  (Danish)")
			        .replaceFirst("nl", "nl  (Dutch)")
			        .replaceFirst("fr", "fr  (French)")
			        .replaceFirst("de", "de  (German)")
			        .replaceFirst("grk", "grk  (Greek)")
			        .replaceFirst("he", "he  (Hebrew)")
			        .replaceFirst("hu-HU", "hu-HU  (Hungarian, Hungary)")
			        .replaceFirst("it", "it  (Italian)")
			        .replaceFirst("ko", "ko  (Korean)")
			        .replaceFirst("pl-PL", "pl-PL  (Polish, Poland)")
			        .replaceFirst("pt-BR", "pt-BR  (Portuguese, Brazil)")
			        .replaceFirst("pt-PT", "pt-PT  (Portuguese, Portugal)")
			        .replaceFirst("ru", "ru  (Russian)")
			        .replaceFirst("sk-SK", "sk-SK  (Slovak)")
			        .replaceFirst("sl-SI", "sl-SI  (Slovenian)")
			        .replaceFirst("es", "es  (Spanish)")
			        .replaceFirst("tr-TR", "tr-TR  (Turkish)")
			        .replaceFirst("vi", "vi  (Vietnamese)")
			        .replaceFirst("pes-IR", "pes-IR  (Western Farsi, Iran)");
		
		return encoded;
	}
	
	private String[] getTranslatorsNames(String assetName) {
		assMan = getAssets();
        try {
        	InputStream in = assMan.open(assetName);
			InputStreamReader is = new InputStreamReader(in);
			StringBuilder sb = new StringBuilder();
			BufferedReader br = new BufferedReader(is);
			String read = br.readLine();
			while(read != null) {
			    sb.append(read);
			    read = br.readLine();
			}
			json = sb.toString();
		} catch (IOException e) {
			Log.e(DEBUG_TAG, e.getMessage());
		}        
		
		JSONArray jArray = null;
		List<String> usernames = new ArrayList<String>();
		List<String> fullnames = new ArrayList<String>();
		List<String> profileLinks = new ArrayList<String>();
		try {
			jArray = new JSONArray(json);
	        for (int i = 0; i < jArray.length(); i++) {
	        	JSONObject jo = jArray.getJSONObject(i);
				String username = jo.getString("username");
	        	usernames.add(username);
	        	String fullname = jo.getString("fullname");
				if (!username.equals(fullname)) {
					fullnames.add(" (" + fullname + ")");
				} else {
					fullnames.add("");
				}
				profileLinks.add(jo.getString("profile_link"));
	        }
		} catch (JSONException e) {
			Log.e(DEBUG_TAG, e.getMessage());
		}
		
		Iterator<String> userIter = usernames.iterator();
		Iterator<String> fullIter = fullnames.iterator();

		List<String> listEntries = new ArrayList<String>();
		while (userIter.hasNext()) {
			try {
            	listEntries.add(userIter.next() + fullIter.next());
        	} catch (NoSuchElementException e) {
        		listEntries.add("//");
        	}
        }
		return listEntries.toArray(new String[0]);
	}
	
	/* 
	 * next method: 
	 * Copyright (c) 2008-2010 CommonsWare, LLC
	 * portions Copyright (c) 2008 Jeffrey Sharkey
	 * 
	 * see SectionedAdapter.java
	 */	
	
	SectionedAdapter adapter = new SectionedAdapter() {
		protected View getHeaderView(String caption, int index, View convertView, ViewGroup parent) {
			TextView result=(TextView)convertView;
			
			if (convertView == null) {
				result=(TextView)getLayoutInflater().inflate(R.layout.activity_translators_header, null);
			}
			
			result.setText(caption);
			
			return(result);
		}
	};

}
