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
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.Window;
import dentex.youtube.downloader.R;
import dentex.youtube.downloader.utils.PopUps;
import dentex.youtube.downloader.utils.Utils;

public class TutorialsActivity extends Activity {
	
	public static final String DEBUG_TAG = "TutorialsActivity";
	public static String chooserSummary;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BugSenseHandler.leaveBreadcrumb("TutorialsActivity_onCreate");
        this.setTitle(R.string.title_activity_tutorials);
    	
    	getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        
    	// Theme init
    	Utils.themeInit(this);
    	
        // Language init
    	Utils.langInit(this);
        
        // Load default preferences values
        PreferenceManager.setDefaultValues(this, R.xml.tutorials, false);
        
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new TutorialsFragment())
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

    public static class TutorialsFragment extends PreferenceFragment /*implements OnSharedPreferenceChangeListener */{
    	
    	private Preference quickStart;
    	//private Preference audioTutorial;
    	
    	@Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.tutorials);
            
            /*for(int i=0;i<getPreferenceScreen().getPreferenceCount();i++){
                initSummary(getPreferenceScreen().getPreference(i));
            }*/
            
            quickStart = (Preference) getPreferenceScreen().findPreference("quick_start");
            quickStart.setOnPreferenceClickListener(new OnPreferenceClickListener() {
				
				public boolean onPreferenceClick(Preference preference) {
					PopUps.showPopUp(getString(R.string.quick_start_title), getString(R.string.quick_start_text), "info", getActivity());
					return true;
				}
			});
            
            /*audioTutorial = (Preference) getPreferenceScreen().findPreference("audio_tutorial");
            audioTutorial.setOnPreferenceClickListener(new OnPreferenceClickListener() {
				
				public boolean onPreferenceClick(Preference preference) {
					PopUps.showPopUp(getString(R.string.audio_tutorial_title), getString(R.string.audio_tutorial_text), "info", getActivity());
					return true;
				}
			});*/
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
