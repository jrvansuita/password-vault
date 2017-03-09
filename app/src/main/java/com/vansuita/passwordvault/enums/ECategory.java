package com.vansuita.passwordvault.enums;

import android.support.v4.app.Fragment;

import com.vansuita.passwordvault.R;
import com.vansuita.passwordvault.frag.store.StoreCredentialFrag;
import com.vansuita.passwordvault.frag.store.StoreEmailFrag;
import com.vansuita.passwordvault.frag.store.StoreNoteFrag;


/**
 * Created by jrvansuita on 08/11/16.
 */

public enum ECategory {


    EMAILS(R.string.email, R.mipmap.envelop, StoreEmailFrag.class),
    CREDENTIALS(R.string.credential, R.mipmap.credential, StoreCredentialFrag.class),
    NOTES(R.string.note, R.mipmap.note, StoreNoteFrag.class);

    private int titleRes;
    private int iconRes;
    private Class<? extends Fragment> fragClass;

    ECategory(int titleRes, int iconRes, Class<? extends Fragment> fragClass) {
        this.titleRes = titleRes;
        this.iconRes = iconRes;
        this.fragClass = fragClass;
    }

    public Class<? extends Fragment> getFragClass() {
        return fragClass;
    }

    public int getTitleRes() {
        return titleRes;
    }

    public int getIconRes() {
        return iconRes;
    }

    public static final String TAG = "ECATEGORY";



}
