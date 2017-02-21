package com.vansuita.passwordvault.frag;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.vansuita.passwordvault.R;
import com.vansuita.passwordvault.act.About;
import com.vansuita.passwordvault.act.AbstractActivity;
import com.vansuita.passwordvault.act.Lock;
import com.vansuita.passwordvault.fire.account.Account;
import com.vansuita.passwordvault.pref.Pref;
import com.vansuita.passwordvault.util.UI;
import com.vansuita.passwordvault.view.Snack;

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

        findPreference(getString(R.string.key_logout_account)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new MaterialDialog.Builder(getActivity())
                        .title(R.string.logout)
                        .content(R.string.wanna_logout)
                        .cancelable(true)
                        .positiveText(R.string.yes)
                        .negativeText(R.string.no)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Account.with((AbstractActivity) getActivity()).signOut();
                            }
                        })
                        .show();

                return true;
            }
        });

        findPreference(getString(R.string.key_about)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(getActivity(), About.class));
                return true;
            }
        });

        findPreference(getString(R.string.key_delete_account)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                new MaterialDialog.Builder(getActivity())
                        .title(R.string.delete_account)
                        .content(R.string.wanna_delete_account)
                        .cancelable(true)
                        .positiveText(R.string.yes)
                        .negativeText(R.string.no)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Account.with((AbstractActivity) getActivity()).deleteAccount(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (!task.isSuccessful()) {
                                            Snack.show(getView(), task.getException().getMessage());
                                        }
                                    }
                                });
                            }
                        })
                        .show();

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
