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

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Window;
import dentex.youtube.downloader.R;
import dentex.youtube.downloader.docs.ApacheShowActivity;
import dentex.youtube.downloader.docs.CcShowActivity;
import dentex.youtube.downloader.docs.ChangelogActivity;
import dentex.youtube.downloader.docs.CreditsShowActivity;
import dentex.youtube.downloader.docs.GplShowActivity;
import dentex.youtube.downloader.docs.LgplShowActivity;
import dentex.youtube.downloader.docs.MitShowActivity;
import dentex.youtube.downloader.docs.MplShowActivity;
import dentex.youtube.downloader.docs.TranslatorsListActivity;
import dentex.youtube.downloader.utils.PopUps;
import dentex.youtube.downloader.utils.Utils;

public class AboutActivity extends Activity {
	
	public static final String DEBUG_TAG = "AboutActivity";
	public static String chooserSummary;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BugSenseHandler.leaveBreadcrumb("AboutActivity_onCreate");
        this.setTitle(R.string.title_activity_about);
    	
    	getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        
    	// Theme init
    	Utils.themeInit(this);
    	
        // Language init
    	Utils.langInit(this);
        
        // Load default preferences values
        PreferenceManager.setDefaultValues(this, R.xml.about, false);
        
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new AboutFragment())
                .commit();
        setupActionBar();
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
    
    public static class AboutFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
    	
    	private Preference credits;
    	private Preference gpl;
		private Preference mit;
		private Preference lgpl;
		private Preference apache;
		private Preference cc;
		private Preference mpl;
		private Preference git;
		private Preference hg;
		private Preference share;
		private Preference cl;
		private Preference loc;
		private Preference tw;
		private Preference tr;
		private Preference xda;
		
		@Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.about);
            
            for(int i=0;i<getPreferenceScreen().getPreferenceCount();i++){
                initSummary(getPreferenceScreen().getPreference(i));
            }
            
            credits = (Preference) findPreference("credits");
	        credits.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	        	
	            public boolean onPreferenceClick(Preference preference) {
	            	Intent intent = new Intent(getActivity(),  CreditsShowActivity.class);
	        		startActivity(intent);
	                return true;
	            }
	        });
		
			gpl = (Preference) findPreference("gpl");
	        gpl.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	        	
	            public boolean onPreferenceClick(Preference preference) {
	            	Intent intent = new Intent(getActivity(),  GplShowActivity.class);
	        		startActivity(intent);
	                return true;
	            }
	        });
	        
	        mit = (Preference) findPreference("mit");
	        mit.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	        	
	            public boolean onPreferenceClick(Preference preference) {
	            	Intent intent = new Intent(getActivity(),  MitShowActivity.class);
	        		startActivity(intent);
	                return true;
	            }
	        });
	        
	        lgpl = (Preference) findPreference("lgpl");
	        lgpl.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	        	
	            public boolean onPreferenceClick(Preference preference) {
	            	Intent intent = new Intent(getActivity(),  LgplShowActivity.class);
	        		startActivity(intent);
	                return true;
	            }
	        });
	        
	        apache = (Preference) findPreference("apache");
	        apache.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	        	
	            public boolean onPreferenceClick(Preference preference) {
	            	Intent intent = new Intent(getActivity(),  ApacheShowActivity.class);
	        		startActivity(intent);
	                return true;
	            }
	        });
	        
	        cc = (Preference) findPreference("cc");
	        cc.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	        	
	            public boolean onPreferenceClick(Preference preference) {
	            	Intent intent = new Intent(getActivity(),  CcShowActivity.class);
	        		startActivity(intent);
	                return true;
	            }
	        });
	        
	        mpl = (Preference) findPreference("mpl");
	        mpl.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	        	
	            public boolean onPreferenceClick(Preference preference) {
	            	Intent intent = new Intent(getActivity(),  MplShowActivity.class);
	        		startActivity(intent);
	                return true;
	            }
	        });
	        
	        git = (Preference) findPreference("ytd_code_git");
	        git.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	        	
	            public boolean onPreferenceClick(Preference preference) {
	            	String url = "https://github.com/dentex/ytdownloader/";
	            	Intent i = new Intent(Intent.ACTION_VIEW);
	            	i.setData(Uri.parse(url));
	            	startActivity(i);
	            	return true;
	            }
	        });
	        
	        hg = (Preference) findPreference("ytd_code_hg");
	        hg.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	        	
	            public boolean onPreferenceClick(Preference preference) {
	            	String url = "https://sourceforge.net/projects/ytdownloader/";
	            	Intent i = new Intent(Intent.ACTION_VIEW);
	            	i.setData(Uri.parse(url));
	            	startActivity(i);
	            	return true;
	            }
	        });
	        
	        share = (Preference) findPreference("share");
	        share.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	        	
	            public boolean onPreferenceClick(Preference preference) {
	                try {
	                	Intent shareIntent =   
	                	new Intent(android.content.Intent.ACTION_SEND);   
	                	shareIntent.setType("text/plain");  
	                	shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "YouTube Downloader");
	                	String shareMessage = getString(R.string.share_message);
	                	shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage);
	                	startActivity(Intent.createChooser(shareIntent, getString(R.string.share_message)));
	                } catch (final ActivityNotFoundException e) {
	                	Utils.logger("d", "No suitable Apps found.", DEBUG_TAG);
	                	PopUps.showPopUp(getString(R.string.attention), getString(R.string.share_warning), "alert", AboutFragment.this.getActivity());
	                }
	            	return true;
	            }
	        });
	        
	        loc = (Preference) findPreference("help_translate");
            loc.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            	
                public boolean onPreferenceClick(Preference preference) {
                	String url = "http://www.getlocalization.com/ytdownloader/";
                	Intent i = new Intent(Intent.ACTION_VIEW);
                	i.setData(Uri.parse(url));
                	startActivity(i);
                	return true;
                }
            });
            
            xda = (Preference) findPreference("xda");
            xda.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            	
                public boolean onPreferenceClick(Preference preference) {
                	String url = "http://forum.xda-developers.com/showthread.php?p=37708791";
                	Intent i = new Intent(Intent.ACTION_VIEW);
                	i.setData(Uri.parse(url));
                	startActivity(i);
                	return true;
                }
            });
            
            cl = (Preference) findPreference("changelog");
            try {
				String version = "v" + AboutFragment.this.getActivity().getPackageManager().getPackageInfo(AboutFragment.this.getActivity().getPackageName(), 0).versionName;
				cl.setSummary(version);
				Utils.logger("v", "YTD " + version, DEBUG_TAG);
			} catch (NameNotFoundException e1) {
				Log.e(DEBUG_TAG, "version not read: " + e1.getMessage());
				cl.setSummary("");
			}
            cl.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            	
                public boolean onPreferenceClick(Preference preference) {
                	Intent intent = new Intent(getActivity(),  ChangelogActivity.class);
                	startActivity(intent);
                    return true;
                }
            });
            
            tw = (Preference) findPreference("tweet");
            tw.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            	
                public boolean onPreferenceClick(Preference preference) {
                	
                	/*
                	 * http://www.androidsnippets.com/open-twitter-via-intent
                	 * http://www.androidsnippets.com/users/hyperax
                	 */
                	try {
                		Utils.logger("d", "twitter direct link", DEBUG_TAG);
                		startActivity(new Intent(Intent.ACTION_VIEW, 
                				Uri.parse("twitter://user?screen_name=@twidentex")));
                	}catch (Exception e) {
                		Utils.logger("d", "twitter WEB link", DEBUG_TAG);
                		startActivity(new Intent(Intent.ACTION_VIEW, 
                				Uri.parse("https://twitter.com/#!/@twidentex")));
                	}
                    return true;
                }
            });
            
            tr = (Preference) findPreference("translators");
            tr.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	        	
	            public boolean onPreferenceClick(Preference preference) {
	            	Intent intent = new Intent(getActivity(),  TranslatorsListActivity.class);
	        		startActivity(intent);
	                return true;
	            }
	        });
            
		}
		
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
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
        }
    }
}
