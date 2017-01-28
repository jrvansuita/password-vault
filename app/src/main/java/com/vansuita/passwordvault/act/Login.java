package com.vansuita.passwordvault.act;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
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
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
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

public class Login extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

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

    @BindView(R.id.pass_label)
    TextInputLayout tilPassword;


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
    private GoogleApiClient googleApiClient;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        ButterKnife.bind(this);

        this.progress = new MaterialDialog.Builder(this)
                .contentColorRes(R.color.primary_text)
                .widgetColorRes(R.color.primary_text)
                .content(R.string.loading)
                .progress(true, 0).build();

        onSetup();

        refresh(EScreenType.CREATE_ACCOUNT);
    }

    @OnEditorAction(R.id.password)
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
        Visible.with(edName).gone(false);
        Visible.with(tvLink).gone(false);
        Visible.with(tilPassword).gone(false);
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


    private Intent intent;

    private FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            progress.hide();

            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                if (user.getDisplayName() == null && !getName().isEmpty()) {
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(getName())
                            //.setPhotoUri(Uri.parse("https://encrypted-tbn2.gstatic.com/images?q=tbn:ANd9GcSdklJHrM3-TgPIJHfPg0vJkVAf-WmlzPD84KNzuMpOPBmMtl7YYL7jBzo"))
                            .build();

                    user.updateProfile(profileUpdates);
                } else {
                    if (intent == null) {
                        intent = new Intent(Login.this, Main.class);
                        startActivity(intent);
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

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestProfile()
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        AuthCredential credential = FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());
                        auth.signInWithCredential(credential)
                                .addOnCompleteListener(Login.this, defaultTask);
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        showSnack(exception.getMessage());
                    }
                });

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

        good = good & UI.error(tilName, Visible.is(edName) && TextUtils.isEmpty(getName()), R.string.error_field_required);
        good = good & UI.error(tilEmail, !Validation.isEmail(getEmail()), R.string.email_not_right);

        if (Visible.is(edPassword))
            if (TextUtils.isEmpty(getPassword())) {
                good = good & UI.error(tilPassword, true, R.string.error_field_required);
            } else if (!isPasswordValid(getPassword())) {
                good = good & UI.error(tilPassword, true, R.string.error_invalid_password);
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
        progress.show();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void facebookLogin() {
        //progress.show();
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
                AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
                auth.signInWithCredential(credential)
                        .addOnCompleteListener(this, defaultTask);
            } else {
                String msg = result.getStatus().getStatusMessage();

                if (msg == null)
                    msg = getString(R.string.no_internet);

                showSnack(msg);
            }
        }
    }

    private OnCompleteListener<AuthResult> defaultTask = new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (!task.isSuccessful()) {
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
        progress.dismiss();
        Snack.show(edEmail, s);
    }
}

