package com.vansuita.passwordvault.view.floating;

import android.content.Context;
import android.support.design.widget.CheckableImageButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.vansuita.passwordvault.R;
import com.vansuita.passwordvault.bean.Bean;
import com.vansuita.passwordvault.bean.refl.Reflect;
import com.vansuita.passwordvault.serv.FloatingPasswordService;
import com.vansuita.passwordvault.util.Util;

/**
 * Created by jrvansuita on 07/03/17.
 */

public class FloatingPasswordView extends CardView implements View.OnClickListener {

    private TextView tvTitle;
    private TextView tvEmail;
    private TextView tvLogin;
    private TextView tvPassword;
    private CheckableImageButton tbPassword;

    public FloatingPasswordView(Context context) {
        this(context, null);
    }

    public FloatingPasswordView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatingPasswordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.accent));
        setRadius(5);
        setCardElevation(4);
        setPreventCornerOverlap(true);

        View view = inflate(R.layout.floating_pass);
        bind(view);
        addView(view);

        setup(view);
    }

    private void bind(View view) {
        tvTitle = (TextView) view.findViewById(R.id.title);
        tvEmail = (TextView) view.findViewById(R.id.email);
        tvLogin = (TextView) view.findViewById(R.id.login);
        tvPassword = (TextView) view.findViewById(R.id.password);
        tbPassword = (CheckableImageButton) view.findViewById(R.id.password_eye);
    }

    private void setup(View view) {
        view.findViewById(R.id.close).setOnClickListener(this);
        view.findViewById(R.id.email_copy).setOnClickListener(this);
        view.findViewById(R.id.password_copy).setOnClickListener(this);
        view.findViewById(R.id.login_copy).setOnClickListener(this);
        tbPassword.setOnClickListener(this);
        tbPassword.setChecked(false);
    }

    private View inflate(int res) {
        ContextThemeWrapper ctx = new ContextThemeWrapper(getContext(), R.style.AppTheme);
        return LayoutInflater.from(ctx).inflate(res, null);
    }

    public void load(Bean bean) {
        if (bean != null) {
            Reflect r = new Reflect(bean);
            tvTitle.setText(bean.getTitle());
            tvEmail.setText(r.getEmail());
            tvLogin.setText(r.getLogin());
            tvPassword.setTag(r.getPassword());

            loadPassword(tbPassword.isChecked());

            if (r.getEmail().isEmpty())
                findViewById(R.id.email_holder).setVisibility(GONE);

            if (r.getLogin().isEmpty())
                findViewById(R.id.login_holder).setVisibility(GONE);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close:
                FloatingPasswordService.stop(getContext());
                break;
            case R.id.password_eye:
                tbPassword.setChecked(!tbPassword.isChecked());
                loadPassword(tbPassword.isChecked());
                break;

            case R.id.email_copy:
                Util.toClipboard(getContext(), getContext().getString(R.string.email), tvEmail.getText().toString());
                break;

            case R.id.login_copy:
                Util.toClipboard(getContext(), getContext().getString(R.string.login), tvLogin.getText().toString());
                break;

            case R.id.password_copy:
                Util.toClipboard(getContext(), getContext().getString(R.string.password), (String) tvPassword.getTag());
                break;
        }
    }

    private void loadPassword(boolean show) {
        tvPassword.setText((String) (show ? tvPassword.getTag() : Util.hidePass(0, (String) tvPassword.getTag())));
    }




}
