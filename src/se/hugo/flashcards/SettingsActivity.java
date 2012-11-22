package se.hugo.flashcards;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;

public class SettingsActivity extends SherlockPreferenceActivity implements OnSharedPreferenceChangeListener  {
	public static final String AUTO_SHUFFLE = "auto_shuffle";
	public static final String FONT_SIZE = "font_size";
	
	private ListPreference fontSizePref;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ActionBar bar = getSupportActionBar();
        Util.customizeActionBar(getResources(), bar);     
        bar.setDisplayHomeAsUpEnabled(true);
        
        addPreferencesFromResource(R.xml.preferences);
        
        fontSizePref = (ListPreference)getPreferenceScreen().findPreference(FONT_SIZE);
    }
    
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()) {
    		case android.R.id.home:
    	    	Intent intent = new Intent(this, FlashcardsActivity.class);
    	    	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	    	intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    	    	startActivity(intent);    			
    		break;
    	}
    	return true;
	}
	
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(FONT_SIZE)) {
            fontSizePref.setSummary(fontSizePref.getEntry());
        }
	}
	
    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = getPreferenceScreen().getSharedPreferences();
        
        fontSizePref.setSummary(fontSizePref.getEntry());
          
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
         
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);    
    }
}
