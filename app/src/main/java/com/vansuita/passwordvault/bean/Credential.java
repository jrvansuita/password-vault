package com.vansuita.passwordvault.bean;

import android.support.annotation.Keep;

import com.vansuita.passwordvault.enums.ECategory;

/**
 * Created by jrvansuita on 08/11/16.
 */

@Keep
public class Credential extends Bean {

    private String login;
    private String email;
    private String password;
    private String website;

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

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Credential(String title, String login, String email, String password, String website) {
        super(ECategory.CREDENTIALS);

        this.setTitle(title);
        this.login = login;
        this.email = email;
        this.password = password;
        this.website = website;
    }

    public Credential() {
        this("", "", "", "", "");
    }
}
