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

package dentex.youtube.downloader.menu;

import com.bugsense.trace.BugSenseHandler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.Window;
import dentex.youtube.downloader.R;
import dentex.youtube.downloader.utils.Utils;

public class DonateActivity extends Activity {
	
	public static final String DEBUG_TAG = "DonateActivity";
	public static String chooserSummary;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BugSenseHandler.leaveBreadcrumb("DonateActivity_onCreate");
        this.setTitle(R.string.title_activity_donate);
    	
    	getWindow().requestFeature(Window.FEATURE_PROGRESS);
    	getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        
    	// Theme init
    	Utils.themeInit(this);
    	
        // Language init
    	Utils.langInit(this);
        
        // Load default preferences values
        PreferenceManager.setDefaultValues(this, R.xml.donate, false);
        
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new DonateFragment())
                .commit();
        setupActionBar();
	}
	
	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
    
    @SuppressLint("SetJavaScriptEnabled")
	public static class DonateFragment extends PreferenceFragment /*implements OnSharedPreferenceChangeListener*/ {
    	
		private Preference pp;
		private Preference fl;
		//private WebView webview;
		
		@Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            
            //webview = new WebView(getActivity());
            //webview.getSettings().setJavaScriptEnabled(true);

            addPreferencesFromResource(R.xml.donate);
            
            /*for(int i=0;i<getPreferenceScreen().getPreferenceCount();i++){
                initSummary(getPreferenceScreen().getPreference(i));
            }*/
	        
	        pp = (Preference) findPreference("paypal");
	        pp.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	        	
	        	/*
	        	 * uriBuilder code adapted from:
	        	 *   https://github.com/dschuermann/ad-away [...] DonationsFragment.java
	        	 *   
	        	 *   Licensed under the Apache License, Version 2.0 (the "License");
	        	 * by:
	        	 *   Dominik Schürmann <dominik@dominikschuermann.de>
	        	 *   
	        	 *   ----------------------------------------------------------------
	        	 *   
	        	 * webview code adapted from:
	        	 *   http://misha.beshkin.lv/android-add-paypal-donation-page-to-app/
	        	 * by:
	        	 *   Moishe Beshkin
	        	 */
	        	
	            public boolean onPreferenceClick(Preference preference) {
	            	Uri.Builder uriBuilder = new Uri.Builder();
	                uriBuilder.scheme("https").authority("www.paypal.com").path("cgi-bin/webscr"); // uncomment this for browser
	                uriBuilder.appendQueryParameter("cmd", "_donations");
	                uriBuilder.appendQueryParameter("business", getString(R.string.paypal_user));
	                uriBuilder.appendQueryParameter("lc", "US");
	                uriBuilder.appendQueryParameter("item_name", getString(R.string.paypal_item_name));
	                uriBuilder.appendQueryParameter("no_note", "1");
	                uriBuilder.appendQueryParameter("no_shipping", "1");
	                uriBuilder.appendQueryParameter("currency_code", getString(R.string.paypal_currency_code));
	                
	                Uri payPalUri = uriBuilder.build();
	                
	                // ================= webview alternative: =======================
	                /*String postText = payPalUri.getQuery();
	                byte[] post = null;
	        		post = EncodingUtils.getBytes(postText, "BASE64");

	                final Activity activity = getActivity();
	                webview.setWebChromeClient(new WebChromeClient() {
	                	public void onProgressChanged(WebView view, int progress) {
	                		activity.setProgress(progress * 100);
	                	}
	                });
	                webview.setWebViewClient(new WebViewClient() {  
	                    @Override  
	                    public boolean shouldOverrideUrlLoading(WebView view, String url) {  
	                    	view.loadUrl(url);  
	                    	return true;  
	                    }
	                    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
	                        if (isAdded()) {
	                        	Toast.makeText(activity, getString(R.string.error) + description, Toast.LENGTH_SHORT).show();
	                        }
	                    }
	                });  
	                activity.setContentView(webview);

	        		webview.postUrl("https://www.paypal.com/cgi-bin/webscr", post);*/
	                // ==============================================================

	                // choose between ^  or  v
	                
	                // ================= browser alternative: =======================
	                Intent viewIntent = new Intent(Intent.ACTION_VIEW, payPalUri);
	                startActivity(viewIntent);
	        		// ==============================================================

	                return true;
	            }
	        });
	        
	        fl = (Preference) findPreference("flattr");
	        fl.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	        	
	        	public boolean onPreferenceClick(Preference preference) {
	        		
	        		Uri.Builder uriBuilder = new Uri.Builder();
	                uriBuilder.scheme("https").authority("flattr.com").path("thing/1814655/YouTubeDownloader-for-Android");
	                
	                Uri flattrUri = uriBuilder.build();
	        		Intent viewIntent = new Intent(Intent.ACTION_VIEW, flattrUri);
	                
	        		startActivity(viewIntent);
	        		return true;
	        	}
	        });
		}
		
		/*public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        	updatePrefSummary(findPreference(key));
		}

		private void initSummary(Preference p){
        	if (p instanceof PreferenceCategory){
        		PreferenceCategory pCat = (PreferenceCategory)p;
        		for(int i=0;i<pCat.getPreferenceCount();i++){
        			initSummary(pCat.getPreference(i));
        	    }
        	}else{
        		updatePrefSummary(p);
        	}
        }
        
        private void updatePrefSummary(Preference p){
        	if (p instanceof ListPreference) {
        		ListPreference listPref = (ListPreference) p;
        	    p.setSummary(listPref.getEntry());
        	}
        }*/
    }
}
