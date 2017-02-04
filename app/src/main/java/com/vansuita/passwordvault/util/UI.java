package com.vansuita.passwordvault.util;

import android.app.Activity;
import android.os.Build;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.vansuita.library.Icon;
import com.vansuita.passwordvault.R;

/**
 * Created by jrvansuita on 27/10/16.
 */

public class UI {

    public static void clear(TextInputLayout t, TextView tv) {
        tv.setText("");
        setError(t);
    }

    public static void setError(TextInputLayout t) {
        setError(t, null);
    }


    public static void setError(TextInputLayout t, String msg) {
        t.setError(msg == null || msg.isEmpty() ? null : msg);
        t.setErrorEnabled(!(msg == null || msg.isEmpty()));
    }

    public static boolean error(TextInputLayout til, boolean doThrow, int msg) {
        til.setErrorEnabled(doThrow);

        if (doThrow)
            til.setError(til.getContext().getString(msg));

        return !doThrow;
    }


    public static void menuVisibility(Menu menu, boolean show) {
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);

            item.setVisible(show);
        }
    }

    public static void applyCheck(boolean apply, TextInputLayout til, EditText edit) {
        Icon.clear(edit);

        if (apply) {
            setError(til);
            Icon.right(edit, R.drawable.check);
        }
    }


    public static void setStatusBarColor(Activity a, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            a.getWindow().setStatusBarColor(ContextCompat.getColor(a, color));
        }
    }


    public static void setFavorite(View view, boolean favorite) {
        view.setVisibility(favorite ? View.VISIBLE : View.GONE);
    }

    public static void setSummary(PreferenceGroup group) {
        for (int i = 0; i < group.getPreferenceCount(); i++) {
            Preference pref = group.getPreference(i);

            if (pref instanceof PreferenceGroup) {
                setSummary((PreferenceGroup) pref);
            } else {
                setSummary(pref);
            }
        }
    }

    public static void setSummary(Preference pref) {
        if (pref instanceof EditTextPreference) {
            pref.setSummary(((EditTextPreference) pref).getText());
        } else if (pref instanceof ListPreference) {
            pref.setSummary(((ListPreference) pref).getEntry());
        }
    }
}
