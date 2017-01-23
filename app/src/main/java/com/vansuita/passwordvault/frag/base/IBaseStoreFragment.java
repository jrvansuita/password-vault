package com.vansuita.passwordvault.frag.base;

import android.widget.TextView;

/**
 * Created by jrvansuita on 17/01/17.
 */

public interface IBaseStoreFragment {

    int getScreenTitle();

    String getAutoFillTitleValue();


    int getChildViewId();


    boolean canStore();

    void onStore();

    TextView getSubmitElement();

}
