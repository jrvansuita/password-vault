package com.vansuita.passwordvault.frag;

import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.vansuita.library.Icon;
import com.vansuita.passwordvault.R;
import com.vansuita.passwordvault.bean.Credential;
import com.vansuita.passwordvault.frag.base.BaseStoreFragment;
import com.vansuita.passwordvault.util.UI;
import com.vansuita.passwordvault.util.Util;
import com.vansuita.passwordvault.util.Validation;
import com.vansuita.passwordvault.view.FavIconWebView;

import butterknife.BindView;
import butterknife.OnFocusChange;

/**
 * Created by jrvansuita on 26/11/16.
 */

public class StoreCredentialFrag extends BaseStoreFragment<Credential> {

    @BindView(R.id.login)
    EditText edLogin;
    @BindView(R.id.email)
    EditText edEmail;
    @BindView(R.id.password)
    EditText edPassword;
    @BindView(R.id.website)
    EditText edWebsite;
    @BindView(R.id.website_favicon)
    FavIconWebView favIconWebView;

    @BindView(R.id.email_label)
    TextInputLayout tilEmail;
    @BindView(R.id.login_label)
    TextInputLayout tilLogin;
    @BindView(R.id.password_label)
    TextInputLayout tilPassword;
    @BindView(R.id.website_label)
    TextInputLayout tilWebsite;

    @Override
    public int getChildViewId() {
        return R.layout.credential_store;
    }

    @Override
    public int getScreenTitle() {
        return R.string.credential_registering;
    }

    @Override
    public String getAutoFillTitleValue() {
        return edLogin.getText().toString();
    }

    @Override
    public void onLoad(Credential credential) {
        edLogin.setText(credential.getLogin());
        edEmail.setText(credential.getEmail());
        edPassword.setText(credential.getPassword());
        edWebsite.setText(credential.getWebsite());

        onWebsiteFocus(edWebsite, false);
    }

    @OnFocusChange(R.id.login)
    public void onLoginFocus(View v, boolean active) {
        if (!active && !Validation.isEmpty(edLogin)) {
            autoFillTitle();
            UI.setError(tilLogin);
        }
    }

    @Override
    public TextView getSubmitElement() {
        return edWebsite;
    }

    @Override
    public void onSetup() {
        applyPassword(edPassword);
    }

    @OnFocusChange(R.id.password)
    public void onPassFocus(View v, boolean active) {
        if (!active && !Validation.isEmpty(edPassword)) {
            UI.setError(tilPassword);
        }
    }

    @OnFocusChange(R.id.email)
    public void onEmailFocus(View v, boolean active) {
        if (!active) {
            UI.applyCheck(Validation.isEmail(edEmail), tilEmail, edEmail);
            autoFillLogin();
        }
    }

    @OnFocusChange(R.id.website)
    public void onWebsiteFocus(View v, boolean active) {
        if (!active && !Validation.isEmpty(edWebsite)) {
            favIconWebView.loadUrl(edWebsite.getText().toString());
        }
    }


    @Override
    public boolean onCanStore() {
        boolean good = true;

        autoFillLogin();

        good = good & UI.error(tilLogin, Validation.isEmpty(edLogin), R.string.login_is_empty);
        good = good & UI.error(tilPassword, Validation.isEmpty(edPassword), R.string.whats_password);

        if (!Validation.isEmpty(edEmail))
            good = good & UI.error(tilEmail, !Validation.isEmail(edEmail), R.string.email_not_right);


        if (!Validation.isEmpty(edWebsite))
            good = good & UI.error(tilWebsite, !Validation.isWebsite(edWebsite), R.string.website_not_right);


        return good;
    }

    @Override
    public void onStore(Credential credential) {
        credential.setLogin(edLogin.getText().toString());
        credential.setEmail(edEmail.getText().toString());
        credential.setPassword(edPassword.getText().toString());
        credential.setWebsite(edWebsite.getText().toString());
    }

    private void autoFillLogin() {
        if (Validation.isEmpty(edLogin)) {
            edLogin.setText(Util.getLogin(edEmail.getText().toString()));
        }
    }

    @Override
    public void onClear() {
        edLogin.setText("");
        edEmail.setText("");
        edPassword.setText("");
        edWebsite.setText("");

        Icon.clear(edEmail);
        UI.setError(tilEmail);
        favIconWebView.clear();
    }
}
