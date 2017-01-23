package com.vansuita.passwordvault.frag;

import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.vansuita.passwordvault.R;
import com.vansuita.passwordvault.adapter.DomainAdapter;
import com.vansuita.passwordvault.bean.Domain;
import com.vansuita.passwordvault.bean.Email;
import com.vansuita.passwordvault.fire.database.Vault;
import com.vansuita.passwordvault.frag.base.BaseStoreFragment;
import com.vansuita.passwordvault.util.UI;
import com.vansuita.passwordvault.util.Util;
import com.vansuita.passwordvault.util.Validation;
import com.vansuita.passwordvault.view.Snack;

import butterknife.BindView;
import butterknife.OnFocusChange;

/**
 * Created by jrvansuita on 26/11/16.
 */

public class StoreEmailFrag extends BaseStoreFragment {

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
    protected void setup() {
        super.setup();


        domainAdapter = new DomainAdapter(getActivity());
        actvDomain.setAdapter(domainAdapter);
        actvDomain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                UI.applyIcon(actvDomain, domainAdapter.getItem(i).getIcon());
            }
        });
    }

    @Override
    public boolean canStore() {
        boolean good = true;

        good = good & UI.error(tilEmail, !Validation.isEmail(edEmail), R.string.email_not_right);
        good = good & UI.error(tilPassword, Validation.isEmpty(edPassword), R.string.whats_password);

        return good;
    }

    @Override
    public void onStore() {
        Email email = new Email();

        email.setTitle(getTitleValue());
        email.setEmail(edEmail.getText().toString());
        email.setPassword(edPassword.getText().toString());

        Vault.put(email);

        onClear();

        Snack.show(edEmail, R.string.saved, R.string.no, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
    }


    private void autoFillDomain() {
        String domainFromEmail = Util.getDomain(edEmail.getText().toString());

        if (domainFromEmail != null && !domainFromEmail.equalsIgnoreCase(actvDomain.getText().toString())) {
            actvDomain.setText(domainFromEmail);
            fillDomainIcon();
        }
    }

    private void fillDomainIcon() {
        Domain domain = domainAdapter.findDomain(actvDomain.getText().toString());

        if (domain != null) {
            UI.applyIcon(actvDomain, domain.getIcon());
        } else if (Validation.isEmail(edEmail)) {
            UI.applyIcon(actvDomain, R.mipmap.envelop);
        } else {
            UI.applyIcon(actvDomain, 0);
        }
    }

    @Override
    protected void onClear() {
        super.onClear();

        edEmail.setText("");
        edPassword.setText("");
        actvDomain.setText("");

        UI.applyIcon(actvDomain, 0);
        UI.applyIcon(edEmail, 0);
    }
}
