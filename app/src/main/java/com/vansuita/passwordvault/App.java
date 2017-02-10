package com.vansuita.passwordvault;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;

import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.vansuita.passwordvault.act.Lock;
import com.vansuita.passwordvault.act.Preferences;
import com.vansuita.passwordvault.fire.dao.PrefDAO;
import com.vansuita.passwordvault.pref.Pref;
import com.vansuita.passwordvault.pref.Session;

/**
 * Created by jrvansuita on 08/11/16.
 */

public class App extends Application implements Application.ActivityLifecycleCallbacks {

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

        if (Lock.isLockable(activity))

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
        if (Lock.isLockable(activity))

            if ((FirebaseAuth.getInstance().getCurrentUser() != null)) {

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


}
