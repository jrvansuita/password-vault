package com.vansuita.passwordvault.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by jrvansuita on 22/01/17.
 */

public class NetworkStateChangeReceiver extends BroadcastReceiver {
    public static final String ACTION = "NETWORK_AVAILABLE_ACTION";

    @Override
    public void onReceive(Context context, Intent intent) {
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(ACTION));
    }

}