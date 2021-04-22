package com.wizzapps.android.newsapp.settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.wizzapps.android.newsapp.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public static class NewsPreferenceFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.settings_pref);
            Preference topicPreference = findPreference(getString(R.string.settings_topic_key));
            Preference apiKeyPreference = findPreference(getString(R.string.settings_api_key_key));
            Preference dateKeyPreference = findPreference(getString(R.string.settings_date_key));
            topicPreference.setOnPreferenceChangeListener(this);
            apiKeyPreference.setOnPreferenceChangeListener(this);
            dateKeyPreference.setOnPreferenceChangeListener(this);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(topicPreference.getContext());
            String topicValue = sharedPreferences.getString(getString(R.string.settings_topic_key), getString(R.string.settings_topic_default_value));
            String apiKeyValue = sharedPreferences.getString(getString(R.string.settings_api_key_key), getString(R.string.settings_api_key_default_value));
            String dateValue = sharedPreferences.getString(getString(R.string.settings_date_key), getString(R.string.settings_date_default_value));
            onPreferenceChange(topicPreference, topicValue);
            onPreferenceChange(apiKeyPreference, apiKeyValue);
            onPreferenceChange(dateKeyPreference, dateValue);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            preference.setSummary(newValue.toString());
            return true;
        }
    }
}