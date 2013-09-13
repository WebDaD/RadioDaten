package webdad.apps.radiodaten;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class preferences extends PreferenceActivity {
	public AlertDialog alertDialog;
	public SharedPreferences sharedPref ;
    private ListPreference startingwave;
    private ListPreference reloadTime;
    private OnSharedPreferenceChangeListener listen;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	 alertDialog = new AlertDialog.Builder(this).create();
    	 sharedPref =  PreferenceManager.getDefaultSharedPreferences(this);
        super.onCreate(savedInstanceState);
        try 
	    	{
	        addPreferencesFromResource(R.xml.preferences);
	        startingwave = (ListPreference)getPreferenceScreen().findPreference("pref_startingWave");
	        reloadTime = (ListPreference)getPreferenceScreen().findPreference("pref_reloadTime");
	        }
        catch (Exception e) 
	    	{
	        alertDialog.setMessage(e.getMessage());
	        alertDialog.show();
	        }
        listen = new OnSharedPreferenceChangeListener() {
			
			public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
					String key) {
				if (key.equals("pref_startingWave")) 
				{
					startingwave.setSummary(sharedPref.getString("pref_startingWave", getString(R.string.starting_Wave)));
		        }
				else if (key.equals("pref_reloadTime"))
				{
					 reloadTime.setSummary("Nachladen alle "+sharedPref.getString("pref_reloadTime", getString(R.string.def_reloadTime))+" Sekunden"); 
				}
			}
		};
    }
    
    @Override
    protected void onResume() {
        super.onResume();

        // Setup the initial values
        startingwave.setSummary(sharedPref.getString("pref_startingWave", getString(R.string.starting_Wave))); 
        reloadTime.setSummary("Nachladen alle "+sharedPref.getString("pref_reloadTime", getString(R.string.def_reloadTime))+" Sekunden"); 
        // Set up a listener whenever a key changes            
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(listen);
    }
    
    @Override
    protected void onPause() {
        super.onPause();

        // Unregister the listener whenever a key changes            
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(listen);    
    }
    
    
}