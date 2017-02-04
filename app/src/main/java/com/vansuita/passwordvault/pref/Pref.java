package com.vansuita.passwordvault.pref;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.vansuita.passwordvault.R;

/**
 * Created by jrvansuita on 03/02/17.
 */

public class Pref {

    Context context;
    SharedPreferences pref;

    Pref(Context context) {
        this.context = context;
        this.pref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static Pref with(Context context) {
        return new Pref(context);
    }

    private String getStr(int key) {
        return getStr(key, "");
    }

    private String getStr(int key, String def) {
        return pref.getString(context.getString(key), def);
    }

    private int getInt(int key) {
        return getInt(key, 0);
    }

    private int getInt(int key, int def) {
        return pref.getInt(context.getString(key), def);
    }

    private boolean getBool(int key) {
        return getBool(key, false);
    }

    private boolean getBool(int key, boolean def) {
        return pref.getBoolean(context.getString(key), def);
    }


    public boolean isSwipeToDeleteActive() {
        return getBool(R.string.key_swipe_to_delete);
    }

    public boolean canChangeItemsColor() {
        return getBool(R.string.key_changing_colors);
    }

    public boolean isLastFirst() {
        return getStr(R.string.key_items_order).equalsIgnoreCase("0");
    }

    /*

    protected float getFloat(int key) {
        return Float.parseFloat(pref.getString(getString(key), "0"));
    }

    protected int getNum(int key) {
        return Integer.parseInt(pref.getString(getString(key), "0"));
    }
*/


}
