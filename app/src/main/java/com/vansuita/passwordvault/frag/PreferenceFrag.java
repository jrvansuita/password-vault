package com.vansuita.passwordvault.frag;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;

import com.vansuita.passwordvault.R;
import com.vansuita.passwordvault.act.Lock;
import com.vansuita.passwordvault.pref.Pref;
import com.vansuita.passwordvault.util.UI;

/**
 * Created by jrvansuita on 03/02/17.
 */

public class PreferenceFrag extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesName(Pref.NAME);

        addPreferencesFromResource(R.xml.prefs);

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        UI.setSummary((PreferenceGroup) findPreference(getString(R.string.key_root)));

        findPreference(getString(R.string.key_reset_lock_password)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Lock.start(getActivity(), true);
                return true;
            }
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        UI.setSummary(findPreference(key));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

}
