package com.vansuita.passwordvault.frag.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.vansuita.passwordvault.R;
import com.vansuita.passwordvault.util.UI;
import com.vansuita.passwordvault.util.Util;
import com.vansuita.passwordvault.util.Validation;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jrvansuita on 17/01/17.
 */

public abstract class BaseStoreFragment extends Fragment implements IBaseStoreFragment {

    @BindView(R.id.screen_title)
    TextView tvTitle;

    @BindView(R.id.title)
    EditText edTitle;

    @BindView(R.id.title_label)
    TextInputLayout tilTitle;

    FrameLayout vChildHolder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.base_store, null, false);

        vChildHolder = (FrameLayout) view.findViewById(R.id.child_holder);
        inflater.inflate(getChildViewId(), vChildHolder);

        ButterKnife.bind(this, view);

        setup();

        return view;
    }

    protected void setup() {
        tvTitle.setText(getScreenTitle());

        if (getSubmitElement() != null)
            getSubmitElement().setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int i, KeyEvent e) {
                    if ((e.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                        onSave(null);
                        return true;
                    }
                    return false;
                }
            });
    }

    @OnClick(R.id.save)
    protected void onSave(View v) {
        autoFillTitle();

        boolean titleOk = UI.error(tilTitle, Validation.isEmpty(edTitle), R.string.forgot_title);

        if (titleOk & canStore()) {
            onStore();
            Util.hideKeyboard(getActivity());
        }
    }

    @Override
    public String getAutoFillTitleValue() {
        return "";
    }

    protected void autoFillTitle() {
        if (Validation.isEmpty(edTitle) && !getAutoFillTitleValue().isEmpty())
            edTitle.setText(getAutoFillTitleValue());
    }

    public String getTitleValue() {
        return edTitle.getText().toString();
    }


    protected void onClear() {
        edTitle.setText("");
    }

}
