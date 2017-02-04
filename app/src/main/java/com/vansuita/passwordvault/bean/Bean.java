package com.vansuita.passwordvault.bean;

import android.graphics.Color;

import com.google.firebase.database.Exclude;
import com.vansuita.passwordvault.enums.ECategory;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by jrvansuita on 08/11/16.
 */

public class Bean implements Serializable {

    private String key;
    private String title;
    private ECategory category;
    private boolean favorite;
    private int color;

    private long time;
    private long lastTime;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ECategory getCategory() {
        return category;
    }

    public void setCategory(ECategory category) {
        this.category = category;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public String getClazz() {
        return getClass().getName();
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    public Bean(ECategory category) {
        setTitle("");
        setDate(new Date());
        setCategory(category);
        setFavorite(false);
        setColor(Color.WHITE);
        setLastDate(new Date());
    }

    //Prevent Firebase Database to serealize this method
    @Exclude
    public void setDate(Date date) {
        if (date != null) {
            setTime(date.getTime());
        } else {
            setTime(0);
        }
    }

    @Exclude
    public Date getDate() {
        return new Date(time);
    }

    @Exclude
    public void setLastDate(Date date) {
        if (date != null) {
            setLastTime(date.getTime());
        } else {
            setLastTime(0);
        }
    }

    @Exclude
    public Date getLastDate() {
        return new Date(lastTime);
    }

    @Exclude
    public boolean isNew() {
        return getKey() == null || getKey().isEmpty();
    }
}
