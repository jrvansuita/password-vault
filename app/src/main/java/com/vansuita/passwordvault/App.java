package com.vansuita.passwordvault;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.vansuita.passwordvault.act.Lock;
import com.vansuita.passwordvault.act.Login;
import com.vansuita.passwordvault.act.Preferences;
import com.vansuita.passwordvault.act.Splash;
import com.vansuita.passwordvault.fire.dao.PrefDAO;
import com.vansuita.passwordvault.pref.Pref;
import com.vansuita.passwordvault.pref.Session;

import java.util.Arrays;

/**
 * Created by jrvansuita on 08/11/16.
 */

public class App extends Application implements Application.ActivityLifecycleCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private boolean lock;
    private Handler handler;

    public Runnable lockRunner = new Runnable() {
        @Override
        public void run() {
            lock = true;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        AppEventsLogger.activateApp(this);

        //if (!BuildConfig.DEBUG)
            registerActivityLifecycleCallbacks(this);

        handler = new Handler(getMainLooper());
        lock = !getSession().getUserID().isEmpty();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        handler.removeCallbacks(lockRunner);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        handler.removeCallbacks(lockRunner);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        handler.removeCallbacks(lockRunner);

        if (isLockable(activity))

            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                int delay = getPref().autoLockDelay();

                if (lock && (delay > 0 || getSession().getVaultPassword().isEmpty())) {
                    lock = false;
                    Lock.start(activity, false);
                }
            }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (isLockable(activity))

            if (FirebaseAuth.getInstance().getCurrentUser() != null) {

                int delay = getPref().autoLockDelay();

                if (delay > 0)
                    handler.postDelayed(lockRunner, delay);

                //Sync shared Preferences
                if (activity.getClass().equals(Preferences.class)) {
                    PrefDAO.with(activity).save();
                }
            }
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    public Pref getPref() {
        return Pref.with(getApplicationContext());
    }

    public Session getSession() {
        return Session.with(getApplicationContext());
    }

    private boolean isLockable(Activity activity) {
        return activity.getClass().getName().contains(BuildConfig.APPLICATION_ID)
                && !(Arrays.asList(new Class[]{Login.class, Splash.class, Lock.class}).contains(activity.getClass()));
    }

    private GoogleApiClient googleApiClient;

    public GoogleApiClient getGoogleApiClient(FragmentActivity fragmentActivity) {
        if (googleApiClient == null)
            googleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(fragmentActivity, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, getGoogleSignInOptions())
                    .build();

        if (!googleApiClient.isConnected())
            googleApiClient.connect();

        return googleApiClient;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, getString(R.string.google_conn_failed), Toast.LENGTH_LONG).show();
    }


    private GoogleSignInOptions googleSignInOptions;

    public GoogleSignInOptions getGoogleSignInOptions() {

        if (googleSignInOptions == null)
            googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestProfile()
                    .requestEmail()
                    .build();


        return googleSignInOptions;
    }

}
