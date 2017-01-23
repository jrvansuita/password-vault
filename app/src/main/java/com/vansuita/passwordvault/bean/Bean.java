package com.vansuita.passwordvault.bean;

import com.google.firebase.database.Exclude;
import com.vansuita.passwordvault.enums.ECategory;

import java.util.Date;

/**
 * Created by jrvansuita on 08/11/16.
 */

public class Bean {

    private String key;
    private String title;
    private long time;
    private ECategory category;

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

    public Bean(ECategory category) {
        setTitle("");
        setDate(new Date());
        setCategory(category);
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

    //Prevent Firebase Database to serealize this method
    @Exclude
    public Date getDate() {
        return new Date(time);
    }

}
