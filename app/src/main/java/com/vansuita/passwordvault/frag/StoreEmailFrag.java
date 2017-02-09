package com.vansuita.passwordvault.frag;

import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.vansuita.library.Icon;
import com.vansuita.passwordvault.R;
import com.vansuita.passwordvault.adapter.DomainAdapter;
import com.vansuita.passwordvault.bean.Email;
import com.vansuita.passwordvault.enums.EEmailDomain;
import com.vansuita.passwordvault.frag.base.BaseStoreFragment;
import com.vansuita.passwordvault.util.UI;
import com.vansuita.passwordvault.util.Util;
import com.vansuita.passwordvault.util.Validation;

import butterknife.BindView;
import butterknife.OnFocusChange;

/**
 * Created by jrvansuita on 26/11/16.
 */

public class StoreEmailFrag extends BaseStoreFragment<Email> {

    @BindView(R.id.email)
    EditText edEmail;

    @BindView(R.id.email_label)
    TextInputLayout tilEmail;

    @BindView(R.id.password)
    EditText edPassword;

    @BindView(R.id.password_label)
    TextInputLayout tilPassword;

    @BindView(R.id.domain)
    AutoCompleteTextView actvDomain;

    @BindView(R.id.domain_label)
    TextInputLayout tilDomain;

    @BindView(R.id.domain_icon)
    ImageView ivDomainIcon;


    @Override
    public int getChildViewId() {
        return R.layout.email_store;
    }

    @Override
    public int getScreenTitle() {
        return R.string.email_registering;
    }

    @Override
    public String getAutoFillTitleValue() {
        return edEmail.getText().toString();
    }

    @Override
    public TextView getSubmitElement() {
        return actvDomain;
    }


    @Override
    public void onLoad(Email email) {
        edEmail.setText(email.getEmail());
        edPassword.setText(email.getPassword());
        autoFillDomain();
    }

    @OnFocusChange(R.id.email)
    public void onEmailFocus(View v, boolean active) {
        if (!active) {
            UI.applyCheck(Validation.isEmail(edEmail), tilEmail, edEmail);
            autoFillTitle();
            autoFillDomain();
        }
    }

    @OnFocusChange(R.id.password)
    public void onPassFocus(View v, boolean active) {
        if (!active && !Validation.isEmpty(edPassword)) {
            UI.setError(tilPassword);
        }
    }

    @OnFocusChange(R.id.domain)
    public void onDomainFocus(View v, boolean active) {
        if (!active) {
            fillDomainIcon();
        }
    }

    DomainAdapter domainAdapter;

    @Override
    public void onSetup() {
        domainAdapter = new DomainAdapter(getActivity());
        actvDomain.setAdapter(domainAdapter);
        actvDomain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Icon.put(ivDomainIcon, domainAdapter.getItem(i).getIcon());
            }
        });
    }

    @Override
    public boolean onCanStore() {
        boolean good = true;

        good = good & UI.error(tilEmail, !Validation.isEmail(edEmail), R.string.email_not_right);
        good = good & UI.error(tilPassword, Validation.isEmpty(edPassword), R.string.whats_password);

        return good;
    }

    @Override
    public void onStore(Email email) {
        email.setEmail(edEmail.getText().toString());
        email.setPassword(edPassword.getText().toString());
    }


    private void autoFillDomain() {
        String domainFromEmail = Util.getDomain(edEmail.getText().toString());

        if (domainFromEmail != null && !domainFromEmail.equalsIgnoreCase(actvDomain.getText().toString())) {
            actvDomain.setText(domainFromEmail);
            fillDomainIcon();
        }
    }

    private void fillDomainIcon() {
        EEmailDomain domain = EEmailDomain.findDomain(actvDomain.getText().toString());

        if (domain != null) {
            Icon.put(ivDomainIcon, domain.getIcon());
        } else if (Validation.isEmail(edEmail)) {
            Icon.put(ivDomainIcon, R.mipmap.envelop);
        } else {
            Icon.clear(ivDomainIcon);
        }
    }

    @Override
    public void onClear() {
        edEmail.setText("");
        edPassword.setText("");
        actvDomain.setText("");

        Icon.clear(ivDomainIcon);
        Icon.clear(edEmail);
    }
}
