package com.vansuita.passwordvault.bean;

import android.support.annotation.Keep;

import com.vansuita.passwordvault.enums.ECategory;

/**
 * Created by jrvansuita on 08/11/16.
 */

@Keep
public class Note extends Bean {
    private String note;

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Note(String title, String note) {
        super(ECategory.NOTES);
        this.setTitle(title);
        this.note = note;
    }

    public Note() {
        this("", "");
    }
}
