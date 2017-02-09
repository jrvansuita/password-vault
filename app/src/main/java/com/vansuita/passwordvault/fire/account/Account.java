package com.vansuita.passwordvault.fire.account;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.vansuita.passwordvault.R;
import com.vansuita.passwordvault.act.AbstractActivity;
import com.vansuita.passwordvault.fire.database.DatabaseAccess;
import com.vansuita.passwordvault.pref.Pref;
import com.vansuita.passwordvault.pref.Session;

/**
 * Created by jrvansuita on 09/02/17.
 */

public class Account implements GoogleApiClient.OnConnectionFailedListener {

    private AbstractActivity context;
    private MaterialDialog progress;

    Account(AbstractActivity context) {
        this.context = context;
    }

    public static Account with(AbstractActivity context) {
        return new Account(context);
    }


    private void showProgress(int msg) {
        if (progress == null)
            progress = new MaterialDialog.Builder(context)
                    .contentColorRes(R.color.primary_text)
                    .widgetColorRes(R.color.primary_text)
                    .cancelable(false)
                    .progress(true, 0).build();

        progress.setContent(msg);

        if (!progress.isShowing())
            progress.show();
    }

    private void hideProgress() {
        if (progress != null && progress.isShowing())
            progress.dismiss();
    }


    public void deleteAccount(final OnCompleteListener<Void> onError) {

        showProgress(R.string.deleting_account);

        //If there's any, delete all stored content from this user on Real Time Database.
        DatabaseAccess.deleteAccountData();

        //Getting the user instance.
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            Session session = Session.with(context);

            //You need to get here the token you saved at logging-in time.
            String token = session.getAuthToken();

            AuthCredential credential;

            //This means you didn't have the token because user used like Facebook Sign-in method.
            if (token == null || token.isEmpty()) {
                credential = EmailAuthProvider.getCredential(user.getEmail(), session.getAuthPassword());
            } else {
                //Doesn't matter if it was Facebook Sign-in or others. It will always work using GoogleAuthProvider for whatever the provider.
                credential = GoogleAuthProvider.getCredential(token, null);
            }

            //We have to reauthenticate user because we don't know how long
            //it was the sign-in. Calling reauthenticate, will update the
            //user login and prevent FirebaseException (CREDENTIAL_TOO_OLD_LOGIN_AGAIN) on user.delete()
            user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            //Calling delete to remove the user and wait for a result.
                            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        //Ok, user remove
                                        signOut();
                                    } else {
                                        //Handle the exception
                                        hideProgress();

                                        if (onError != null)
                                            onError.onComplete(task);
                                    }
                                }
                            });
                        }
                    });
        }
    }


    public void signOut() {
        showProgress(R.string.signing_out);

        DatabaseAccess.clear();
        Pref.with(context).clear();
        Session.with(context).clear();

        FirebaseAuth.getInstance().signOut();

        final GoogleApiClient googleApiClient = getGoogleApiClient();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (googleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            context.goToLogin();
                        }
                    });
                } else {
                    context.goToLogin();
                }
            }
        }, 500);

    }

    private GoogleSignInOptions getGoogleSignInOptions() {
        return new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestProfile()
                .requestEmail()
                .build();
    }


    public GoogleApiClient getGoogleApiClient() {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .enableAutoManage(context, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, getGoogleSignInOptions())
                .build();

        if (!googleApiClient.isConnected())
            googleApiClient.connect();

        return googleApiClient;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(context, context.getString(R.string.google_conn_failed), Toast.LENGTH_LONG).show();
    }
}
