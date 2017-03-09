package com.vansuita.passwordvault.frag.store.base;

import android.widget.TextView;

import com.vansuita.passwordvault.bean.Bean;

/**
 * Created by jrvansuita on 17/01/17.
 */

public interface IBaseStoreFragment<T extends Bean> {

    int getScreenTitle();

    String getAutoFillTitleValue();


    int getChildViewId();

    TextView getSubmitElement();

    void onSetup();

    boolean onCanStore();

    void onLoad(T object);

    void onStore(T object);

    void onClear();


}
