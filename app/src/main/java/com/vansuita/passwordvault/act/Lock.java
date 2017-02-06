package com.vansuita.passwordvault.act;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.vansuita.library.Icon;
import com.vansuita.passwordvault.R;
import com.vansuita.passwordvault.enums.ELockScreenType;
import com.vansuita.passwordvault.util.UI;
import com.vansuita.passwordvault.util.Util;
import com.vansuita.passwordvault.util.Validation;
import com.vansuita.passwordvault.util.Visible;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;
import static com.vansuita.passwordvault.R.menu.lock;

/**
 * Created by jrvansuita on 04/02/17.
 */

public class Lock extends AbstractActivity {

    private static final String REQUESTER_TAG = "REQUESTER_TAG";
    private static final String CREATE_PASSWORD_TAG = "CREATE_PASSWORD_TAG";

    private ELockScreenType screenType;

    private String storedVaultPassword;
    private Class<?> requesterActivityClass;

    @BindView(R.id.icon)
    ImageView ivIcon;

    @BindView(R.id.screen_title)
    TextView tvTitle;

    @BindView(R.id.intro)
    TextView tvIntro;

    @BindView(R.id.password)
    EditText edPassword;

    @BindView(R.id.password_label)
    TextInputLayout tilPassword;

    @BindView(R.id.password_retype)
    EditText edRetypePassword;

    @BindView(R.id.password_retype_label)
    TextInputLayout tilRetypePassword;

    @BindView(R.id.hint)
    EditText edHint;

    @BindView(R.id.hint_label)
    TextInputLayout tilHintPassword;

    @BindView(R.id.sub_title)
    TextView tvSubTitle;


    @BindView(R.id.toolbar)
    Toolbar toolbar;


    public static void start(Context context, boolean createPassword) {
        Intent intent = new Intent(context, Lock.class);
        intent.addFlags(FLAG_ACTIVITY_SINGLE_TOP);
        Bundle bundle = new Bundle();
        bundle.putBoolean(CREATE_PASSWORD_TAG, createPassword);
        bundle.putSerializable(REQUESTER_TAG, context.getClass());
        intent.putExtras(bundle);

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.lock_screen);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        setup();
    }

    public void setup() {

        //Get the already stored password.
        storedVaultPassword = getSession().getVaultPassword();

        //Reading current bundle.
        Bundle bundle = getIntent().getExtras();

        //Who called the lock screen?
        requesterActivityClass = (Class<?>) bundle.getSerializable(REQUESTER_TAG);

        //The caller wants to create a new password or not?
        boolean createPassword = bundle.getBoolean(CREATE_PASSWORD_TAG, false);

        //Defining screen type
        if (storedVaultPassword.isEmpty()) {
            screenType = ELockScreenType.FIRST_TIME;
        } else if (!storedVaultPassword.isEmpty() && createPassword) {
            screenType = ELockScreenType.RESET_TIME;
        } else {
            screenType = ELockScreenType.LOCK_TIME;
        }

        int intoMsg = 0;
        int passHint = 0;

        switch (screenType) {
            case FIRST_TIME:
                intoMsg = R.string.first_intro;
                passHint = R.string.access_password;

                edHint.setOnEditorActionListener(submitAction);

                break;
            case RESET_TIME:
                intoMsg = R.string.reset_intro;
                passHint = R.string.new_access_password;

                edHint.setOnEditorActionListener(submitAction);

                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                break;
            case LOCK_TIME:
                passHint = R.string.access_password;
                intoMsg = R.string.lock_intro;

                edPassword.setOnEditorActionListener(submitAction);
                break;
        }

        Visible.with(tilHintPassword).gone(screenType.equals(ELockScreenType.LOCK_TIME));
        Visible.with(tilRetypePassword).gone(screenType.equals(ELockScreenType.LOCK_TIME));

        tvIntro.setText(getString(intoMsg));
        tilPassword.setHint(getString(passHint));

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ivIcon.setSelected(true);
        tvSubTitle.setText(R.string.vault_access);

/*
        edPassword.setText("testes");
        edRetypePassword.setText("testes");
        edHint.setText("tes");
*/

    }

    private TextView.OnEditorActionListener submitAction = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                onSubmit();
                return true;
            }

            return false;
        }
    };

    public boolean isResetOk() {
        boolean good = true;

        UI.setError(tilPassword);
        UI.setError(tilRetypePassword);
        UI.setError(tilHintPassword);

        if (Validation.isEmpty(edPassword)) {
            good = good & UI.error(tilPassword, true, R.string.whats_password);
        } else {
            good = good & UI.error(tilPassword, edPassword.getText().length() < 6, R.string.error_invalid_password);
        }

        if (Validation.isEmpty(edRetypePassword)) {
            good = good & UI.error(tilRetypePassword, true, R.string.whats_password);
        } else {
            good = good & UI.error(tilRetypePassword, !edRetypePassword.getText().toString().equals(edPassword.getText().toString()), R.string.passwords_do_not_match);
        }

        if (Validation.isEmpty(edHint)) {
            good = good & UI.error(tilHintPassword, true, R.string.whats_hint);
        } else {
            good = good & UI.error(tilHintPassword, edHint.getText().length() < 3, R.string.error_invalid_hint);
        }

        return good;
    }


    private void resetPassword() {
        if (isResetOk()) {
            getSession().setVaultPassword(edPassword.getText().toString());
            getSession().setVaultPasswordHint(edHint.getText().toString());

            unlock();
        }
    }

    @OnTextChanged(value = R.id.password, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterPasswordInput(final Editable editable) {
        if (screenType.equals(ELockScreenType.LOCK_TIME)) {
            final String password = edPassword.getText().toString();

            if (password.length() > 5)
                runMatcher();

            if (refreshIcon && password.length() < 6) {
                matcherResponse(refreshIcon);

                refreshIcon = false;
            }
        }
    }


    private void runMatcher() {
        if (handler != null)
            handler.removeCallbacks(matcher);

        handler = new Handler();
        handler.postDelayed(matcher, 400);
    }

    private boolean refreshIcon = false;
    private int triesCounter = 0;
    private Handler handler;
    private boolean showingHint = false;

    private Runnable matcher = new Runnable() {
        @Override
        public void run() {
            triesCounter++;

            if (isPasswordCorrect()) {
                unlock();
            } else {
                if (!refreshIcon) {
                    matcherResponse(refreshIcon);

                    refreshIcon = true;

                    if (triesCounter > 3 && !showingHint) {
                        showingHint = true;

                        tvSubTitle.setText(getString(R.string.password_hint_fmt, getSession().getVaultPasswordHint()));
                        Icon.left(tvSubTitle, R.mipmap.tip);
                        tvSubTitle.startAnimation(AnimationUtils.loadAnimation(Lock.this, R.anim.pop_in));
                    }
                }
            }
        }
    };


    private void matcherResponse(boolean good) {
        if (good) {
            if (!ivIcon.isSelected()) {
                ivIcon.startAnimation(AnimationUtils.loadAnimation(Lock.this, R.anim.fade_in));
                Icon.on(ivIcon).icon(R.mipmap.big_green_vault).pressedEffect(false).put();
            }
        } else {
            if (ivIcon.isSelected()) {
                ivIcon.startAnimation(AnimationUtils.loadAnimation(Lock.this, R.anim.shake));
                Icon.on(ivIcon).icon(R.mipmap.big_red_vault).pressedEffect(false).put();
            }
        }

        ivIcon.setSelected(good);
    }


    private boolean isPasswordCorrect() {
        return edPassword.getText().toString().equalsIgnoreCase(storedVaultPassword);
    }


    private void unlock() {
        Util.hideKeyboard(Lock.this);
        matcherResponse(true);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (requesterActivityClass.equals(Login.class))
                    startActivity(new Intent(Lock.this, Main.class));

                Lock.this.finish();
            }
        }, 100);
    }


    @OnClick(R.id.fab)
    void fabClick(View v) {
        onSubmit();
    }


    private void onSubmit() {
        if (screenType.equals(ELockScreenType.LOCK_TIME)) {
            runMatcher();
        } else {
            resetPassword();
        }
    }

    public void onBackPressed() {
        if (screenType.equals(ELockScreenType.RESET_TIME)) {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(lock, menu);
        Icon.on(menu.findItem(R.id.action_logout)).white(R.drawable.ic_action_logout).put();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                final MaterialDialog md = new MaterialDialog.Builder(this)
                        .title(R.string.logout)
                        .content(R.string.wanna_logout)
                        .cancelable(true)
                        .positiveText(R.string.yes)
                        .negativeText(R.string.cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                signOut();
                            }
                        })
                        .show();

                return true;
        }

        return false;
    }
}
