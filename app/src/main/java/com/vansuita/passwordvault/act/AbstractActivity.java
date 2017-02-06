package com.vansuita.passwordvault.act;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.vansuita.passwordvault.App;
import com.vansuita.passwordvault.fire.database.DatabaseAccess;
import com.vansuita.passwordvault.pref.Pref;
import com.vansuita.passwordvault.pref.Session;

/**
 * Created by jrvansuita on 05/02/17.
 */

public class AbstractActivity extends AppCompatActivity {

    public void signOut() {
        DatabaseAccess.clear();
        Pref.with(this).clear();
        Session.with(this).clear();

        FirebaseAuth.getInstance().signOut();

        GoogleApiClient googleApiClient = getApp().getGoogleApiClient(this);

        if (googleApiClient.isConnected()) {
            Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    goToLogin();
                }
            });
        } else {
            goToLogin();
        }
    }

    private void goToLogin() {
        Intent intent = new Intent(this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
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
