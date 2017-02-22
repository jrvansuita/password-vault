package com.vansuita.passwordvault.act;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.vansuita.passwordvault.fire.account.Account;

/**
 * Created by jrvansuita on 27/10/16.
 */

public class Splash extends AbstractActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new LinearLayout(this));

        //When app reinstalled, getCurrentUser() is not getting null...
        //Actually, this bug went away, but will still let this code here.
        if (FirebaseAuth.getInstance().getCurrentUser() != null && getSession().getUserID().isEmpty()) {
            Account.with(Splash.this).signOut();
            return;
        } else {

            Intent i;

            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                i = Login.intent(this);
            } else {
                i = Main.intent(this);
            }

            //  i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

            startActivity(i);
            finish();
        }
    }

}
