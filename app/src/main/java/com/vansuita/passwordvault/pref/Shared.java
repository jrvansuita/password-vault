package com.vansuita.passwordvault.pref;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

/**
 * Created by jrvansuita on 05/02/17.
 */

public abstract class Shared {

    Context context;
    SharedPreferences shared;

    Shared(Context context) {
        this.context = context;
        this.shared = getShared();
    }

    protected SharedPreferences getShared() {
        return context.getSharedPreferences(getSharedName(), Context.MODE_PRIVATE);
    }


    protected Context getContext() {
        return context;
    }

    abstract String getSharedName();

    public void clear() {
        getShared().edit().clear().commit();
    }

    public Map<String, ?> getAll() {
        return getShared().getAll();
    }

    private String getKey(int key) {
        return context.getString(key);
    }

    protected void putStr(String key, String val) {
        shared.edit().putString(key, val).commit();
    }

    protected void putStr(int key, String val) {
        putStr(getKey(key), val);
    }

    protected void putBool(String key, boolean val) {
        shared.edit().putBoolean(key, val).commit();
    }

    protected void putBool(int key, boolean val) {
        putBool(getKey(key), val);
    }

    protected void putInt(String key, int val) {
        shared.edit().putInt(key, val).commit();
    }

    protected void putInt(int key, int val) {
        putInt(getKey(key), val);
    }

    protected void putFloat(String key, float val) {
        shared.edit().putFloat(key, val).commit();
    }

    protected void putFloat(int key, float val) {
        putFloat(getKey(key), val);
    }


    protected void putLong(String key, long val) {
        shared.edit().putLong(key, val).commit();
    }

    protected void putLong(int key, long val) {
        putLong(getKey(key), val);
    }

    protected String getStr(int key) {
        return getStr(key, "");
    }

    protected String getStr(int key, String def) {
        return shared.getString(getKey(key), def);
    }

    protected int getInt(int key) {
        return getInt(key, 0);
    }

    protected int getInt(int key, int def) {
        return shared.getInt(getKey(key), def);
    }

    protected boolean getBool(int key) {
        return getBool(key, false);
    }

    protected boolean getBool(int key, boolean def) {
        return shared.getBoolean(getKey(key), def);
    }





}
