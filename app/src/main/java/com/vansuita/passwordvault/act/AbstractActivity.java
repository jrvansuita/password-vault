package com.vansuita.passwordvault.act;

import android.content.Intent;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;

import com.vansuita.passwordvault.App;
import com.vansuita.passwordvault.pref.Pref;
import com.vansuita.passwordvault.pref.Session;

/**
 * Created by jrvansuita on 05/02/17.
 */

public class AbstractActivity extends AppCompatActivity {


    public void goToLogin() {
        Intent intent = new Intent(this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public App getApp() {
        return ((App) getApplication());
    }

    public Pref getPref() {
        return getApp().getPref();
    }

    public Session getSession() {
        return getApp().getSession();
    }


}
