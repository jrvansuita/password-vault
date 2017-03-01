package com.vansuita.passwordvault.bean;

import android.support.annotation.Keep;

import com.vansuita.passwordvault.enums.ECategory;

/**
 * Created by jrvansuita on 08/11/16.
 */

@Keep
public class Email extends Bean {

    private String email;
    private String password;


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public Email(String title, String email, String password) {
        super(ECategory.EMAILS);
        this.setTitle(title);
        this.email = email;
        this.password = password;
    }

    public Email(){
        this("","","");
    }
}
