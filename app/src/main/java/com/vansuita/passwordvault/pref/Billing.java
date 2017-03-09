package com.vansuita.passwordvault.pref;

import android.content.Context;

import com.vansuita.passwordvault.R;

/**
 * Created by jrvansuita on 06/02/17.
 */

public class Billing extends Shared {

    private static final String NAME = "BILLING";

    Billing(Context context) {
        super(context);
    }

    @Override
    String getSharedName() {
        return NAME;
    }

    public static Billing with(Context context) {
        return new Billing(context);
    }


    public void setRemoveAdsPurchased(boolean value) {
        putBool(R.string.key_remove_ads_purchased, value);
    }

    public boolean isRemoveAdsPurchased() {
        //return getBool(R.string.key_remove_ads_purchased, false);
        return true;
    }

}
