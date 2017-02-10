package com.vansuita.passwordvault.act;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.vansuita.passwordvault.R;
import com.vansuita.passwordvault.fire.account.Account;
import com.vansuita.passwordvault.fire.dao.PrefDAO;
import com.vansuita.passwordvault.pref.Session;
import com.vansuita.passwordvault.util.UI;
import com.vansuita.passwordvault.util.Validation;
import com.vansuita.passwordvault.util.Visible;
import com.vansuita.passwordvault.view.Snack;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnFocusChange;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

public class Login extends AbstractActivity implements GoogleApiClient.OnConnectionFailedListener {

    @BindView(R.id.name)
    EditText edName;

    @BindView(R.id.name_label)
    TextInputLayout tilName;

    @BindView(R.id.email)
    EditText edEmail;

    @BindView(R.id.email_label)
    TextInputLayout tilEmail;

    @BindView(R.id.password)
    EditText edPassword;

    @BindView(R.id.password_label)
    TextInputLayout tilPassword;

    @BindView(R.id.password_retype)
    EditText edPasswordRetype;

    @BindView(R.id.password_retype_label)
    TextInputLayout tilPasswordRetype;

    @BindView(R.id.submit)
    AppCompatButton btSubmit;

    @BindView(R.id.link)
    TextView tvLink;

    @BindView(R.id.forgot)
    TextView tvForgotPassword;


    @BindView(R.id.social_buttons)
    View vSocialButtons;


    private FirebaseAuth auth;
    private MaterialDialog progress;
    private CallbackManager callbackManager;

    public static Intent intent(Context context) {
        Intent intent = new Intent(context, Login.class);
        intent.addFlags(FLAG_ACTIVITY_SINGLE_TOP);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        ButterKnife.bind(this);

        this.progress = new MaterialDialog.Builder(this)
                .contentColorRes(R.color.primary_text)
                .widgetColorRes(R.color.primary_text)
                .content(R.string.signing_in)
                .cancelable(false)
                .progress(true, 0).build();

        onSetup();

        refresh(EScreenType.CREATE_ACCOUNT);
    }


    @OnEditorAction({R.id.password, R.id.password_retype})
    public boolean onPasswordEnter(int id) {
        if (id == EditorInfo.IME_ACTION_DONE) {
            submit();
            return true;
        }

        return false;
    }

    @OnClick(R.id.submit)
    public void onSubmit(View v) {
        submit();
    }

    @OnClick(R.id.google)
    public void onGoogle(View v) {
        googleLogin();
    }

    @OnClick(R.id.facebook)
    public void onFacebook(View v) {
        facebookLogin();
    }

    @OnClick(R.id.forgot)
    public void onForgot(View v) {
        refresh(EScreenType.FORGOT_PASSWORD);
    }

    @OnClick(R.id.link)
    public void onLink(View v) {
        if (lastType == EScreenType.CREATE_ACCOUNT) {
            refresh(EScreenType.SIGN_IN);
        } else {
            refresh(EScreenType.CREATE_ACCOUNT);
        }
    }

    @OnFocusChange(R.id.name)
    public void onNameFocus(View v, boolean active) {
        if (!active && !Validation.isEmpty(edName)) {
            UI.setError(tilName);
        }
    }

    @OnFocusChange(R.id.email)
    public void onEmailFocus(View v, boolean active) {
        if (!active && Validation.isEmail(edEmail)) {
            UI.setError(tilEmail);
        }
    }

    @OnFocusChange(R.id.password)
    public void onPassFocus(View v, boolean active) {
        if (!active && !Validation.isEmail(edPassword)) {
            UI.setError(tilPassword);
        }

        if (active && lastType.equals(EScreenType.CREATE_ACCOUNT)) {
            Visible.with(tilPasswordRetype).gone(false);
            edPassword.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        showSnack(getString(R.string.conn_failed));
    }

    private enum EScreenType {
        CREATE_ACCOUNT, SIGN_IN, FORGOT_PASSWORD
    }

    private EScreenType lastType;

    private void refresh(EScreenType type) {
        edPassword.setImeOptions(EditorInfo.IME_ACTION_DONE);

        Visible.with(edName).gone(false);
        Visible.with(tvLink).gone(false);
        Visible.with(tilPassword).gone(false);
        Visible.with(tilPasswordRetype).gone(true);

        Visible.with(vSocialButtons).gone(true);
        Visible.with(tvForgotPassword).gone(true);


        switch (type) {
            case CREATE_ACCOUNT:
                tvLink.setText(R.string.already_member_link);
                btSubmit.setText(R.string.create_account);
                edName.requestFocus();
                Visible.with(vSocialButtons).gone(false);

                break;
            case SIGN_IN:
                edEmail.requestFocus();
                Visible.with(tvForgotPassword).gone(false);
                Visible.with(edName).gone(true);
                tvLink.setText(R.string.new_account_link);
                btSubmit.setText(R.string.sign_in);

                break;
            case FORGOT_PASSWORD:
                Visible.with(edName).gone(true);
                Visible.with(tvForgotPassword).gone(true);
                edEmail.requestFocus();
                btSubmit.setText(R.string.renew_password);
                Visible.with(tilPassword).gone(true);
                break;
        }

        lastType = type;

        clearView();
    }


    private FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                if (user.getDisplayName() == null && !getName().isEmpty()) {
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(getName())
                            .build();

                    user.updateProfile(profileUpdates);
                } else {
                    progress.hide();

                    if (getSession().getUserID().isEmpty()) {

                        PrefDAO.with(Login.this).restore();

                        getSession().setUserID(user.getUid());
                        Lock.start(Login.this, true);
                        finish();
                    }
                }
            }
        }
    };


    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

    private void onSetup() {
        this.auth = FirebaseAuth.getInstance();

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        String token = loginResult.getAccessToken().getToken();

                        Session.with(Login.this).setAuthToken(token);

                        AuthCredential credential = FacebookAuthProvider.getCredential(token);
                        auth.signInWithCredential(credential).addOnCompleteListener(Login.this, defaultTask);
                    }

                    @Override
                    public void onCancel() {
                        progress.dismiss();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        showSnack(exception.getMessage());
                    }
                }

        );

    }

    private void clearView() {
        UI.clear(tilName, edName);
        UI.clear(tilEmail, edEmail);
        UI.clear(tilPassword, edPassword);
    }


    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }

    private String getName() {
        return edName.getText().toString();
    }

    private String getEmail() {
        return edEmail.getText().toString();
    }

    private String getPassword() {
        return edPassword.getText().toString();
    }

    private boolean checkFields() {
        boolean good = true;

        good = good & UI.error(tilName, Visible.is(edName) && TextUtils.isEmpty(getName()), R.string.whats_name);
        good = good & UI.error(tilEmail, !Validation.isEmail(getEmail()), R.string.email_not_right);

        if (Visible.is(tilPassword))
            if (TextUtils.isEmpty(getPassword())) {
                good = good & UI.error(tilPassword, true, R.string.whats_password);
            } else if (!isPasswordValid(getPassword())) {
                good = good & UI.error(tilPassword, true, R.string.error_invalid_password);
            }

        if (Visible.is(tilPasswordRetype))
            if (TextUtils.isEmpty(edPasswordRetype.getText().toString())) {
                good = good & UI.error(tilPasswordRetype, true, R.string.whats_password);
            } else {
                good = good & UI.error(tilPasswordRetype, !edPasswordRetype.getText().toString().equals(edPassword.getText().toString()), R.string.passwords_do_not_match);
            }

        return good;
    }

    private void submit() {
        if (!com.vansuita.passwordvault.util.Util.internet(this)) {
            showSnack(getString(R.string.no_internet));
        } else {

            btSubmit.requestFocus();

            if (checkFields()) {
                progress.show();

                switch (lastType) {
                    case CREATE_ACCOUNT:
                        createAccountEmailPassword();
                        break;
                    case SIGN_IN:
                        signInEmailPassword();
                        break;
                    case FORGOT_PASSWORD:
                        forgotPassword();
                        break;
                }
            }
        }
    }

    private void createAccountEmailPassword() {
        auth.createUserWithEmailAndPassword(getEmail(), getPassword())
                .addOnCompleteListener(this, defaultTask);
    }

    private void signInEmailPassword() {
        auth.signInWithEmailAndPassword(getEmail(), getPassword())
                .addOnCompleteListener(this, defaultTask);
    }


    private static final int RC_SIGN_IN = 990;

    private void googleLogin() {
        if (!progress.isShowing())
            progress.show();

        startActivityForResult(Auth.GoogleSignInApi.getSignInIntent(Account.with(this).getGoogleApiClient()), RC_SIGN_IN);
    }

    private void facebookLogin() {
        if (!progress.isShowing())
            progress.show();

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();

                String token = acct.getIdToken();

                Session.with(Login.this).setAuthToken(acct.getIdToken());

                AuthCredential credential = GoogleAuthProvider.getCredential(token, null);
                auth.signInWithCredential(credential)
                        .addOnCompleteListener(this, defaultTask);
            } else {
                showSnack(result.getStatus().getStatusMessage());
            }
        }
    }

    private OnCompleteListener<AuthResult> defaultTask = new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                Session.with(Login.this).setAuthPassword(getPassword());
            } else {
                showSnack(task.getException().getMessage());
            }
        }
    };


    private void forgotPassword() {
        auth.sendPasswordResetEmail(getEmail()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                showSnack(getString(R.string.renew_password_msg));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showSnack(e.getMessage());
            }
        });
    }


    private void showSnack(String s) {
        if (!com.vansuita.passwordvault.util.Util.internet(this))
            s = getString(R.string.no_internet);

        if (progress.isShowing())
            progress.dismiss();

        if (s != null)
            Snack.show(edEmail, s);
    }
}

