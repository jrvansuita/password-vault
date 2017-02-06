package com.vansuita.passwordvault.pref;

import android.content.Context;

import com.vansuita.passwordvault.R;

/**
 * Created by jrvansuita on 06/02/17.
 */

public class Session extends Shared {

    private static final String NAME = "SESSION";

    Session(Context context) {
        super(context);
    }

    @Override
    String getSharedName() {
        return NAME;
    }

    public static Session with(Context context) {
        return new Session(context);
    }


    public void setUserID(String uid) {
        putStr(R.string.key_user_uid, uid);
    }

    public void setVaultPasswordHint(String pass) {
        putStr(R.string.key_vault_password_hint, pass);
    }

    public void setVaultPassword(String pass) {
        putStr(R.string.key_vault_password, pass);
    }

    public String getVaultPassword() {
        return getStr(R.string.key_vault_password, "");
    }

    public String getVaultPasswordHint() {
        return getStr(R.string.key_vault_password_hint, "");
    }

    public String getUserID() {
        return getStr(R.string.key_user_uid, "");
    }


}
