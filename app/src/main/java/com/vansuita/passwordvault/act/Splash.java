package com.vansuita.passwordvault.act;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by jrvansuita on 27/10/16.
 */

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new LinearLayout(this));

        Intent i;

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            i = new Intent(Splash.this, Login.class);
        } else {
            i = new Intent(Splash.this, Main.class);
        }

        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        startActivity(i);
        finish();
    }

}
