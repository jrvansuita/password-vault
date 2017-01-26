package com.vansuita.passwordvault.frag.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.vansuita.passwordvault.R;
import com.vansuita.passwordvault.bean.Bean;
import com.vansuita.passwordvault.cnt.BeanCnt;
import com.vansuita.passwordvault.util.UI;
import com.vansuita.passwordvault.util.Util;
import com.vansuita.passwordvault.util.Validation;
import com.vansuita.passwordvault.view.Snack;

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

    @BindView(R.id.save)
    Button btSave;

    FrameLayout vChildHolder;

    private Bean bean;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.base_store, null, false);

        vChildHolder = (FrameLayout) view.findViewById(R.id.child_holder);
        inflater.inflate(getChildViewId(), vChildHolder);

        ButterKnife.bind(this, view);

        setup();

        bean = getObject(Bean.class);

        if (bean != null)
            onLoad(bean);

        return view;
    }

    protected void onLoad(Bean bean) {
        if (bean.isNew()) {
            btSave.setText(R.string.save);
        } else {
            btSave.setText(R.string.update);
        }

        edTitle.setText(bean.getTitle());
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
        bean = null;
        getArguments().remove(BeanCnt.NAME);
        btSave.setText(R.string.save);
    }

    public <T> T getObject(Class<T> clazz) {
        if (getArguments() != null) {
            Object o = getArguments().getSerializable(BeanCnt.NAME);
            if (o != null)
                return (T) o;
        }

        try {
            return clazz.newInstance();
        } catch (Exception e) {
            return null;
        }
    }

    protected void onFinish() {
        Snack.show(edTitle, bean == null || bean.isNew() ? R.string.saved : R.string.updated, R.string.no, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        onClear();
    }

}
