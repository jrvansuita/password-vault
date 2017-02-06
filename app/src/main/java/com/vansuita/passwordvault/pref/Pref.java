package com.vansuita.passwordvault.pref;

import android.content.Context;

import com.vansuita.passwordvault.R;

/**
 * Created by jrvansuita on 03/02/17.
 */

public class Pref extends Shared{

    public static final String NAME = "PREF";

    protected Pref(Context context) {
        super(context);
    }

    public static Pref with(Context context) {
        return new Pref(context);
    }

    @Override
    String getSharedName() {
        return NAME;
    }

    public boolean isSwipeToDeleteActive() {
        return getBool(R.string.key_swipe_to_delete);
    }

    public boolean canChangeItemsColor() {
        return getBool(R.string.key_changing_colors);
    }

    public boolean showEmailDomainsIcons() {
        return getBool(R.string.key_domain_icons, true);
    }

    public int autoLockDelay() {
        return Integer.valueOf(getStr(R.string.key_time_to_lock, "1000"));
    }

    public boolean isLastFirst() {
        return getStr(R.string.key_items_order, "0").equalsIgnoreCase("0");
    }



}
