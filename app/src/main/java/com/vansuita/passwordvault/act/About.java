package com.vansuita.passwordvault.act;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import com.vansuita.materialabout.builder.AboutBuilder;
import com.vansuita.passwordvault.R;


/**
 * Created by jrvansuita on 20/02/17.
 */

public class About extends AppCompatActivity {


    private static final String REMOVE_ADS_ID = "remove_ads";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity);
        setup();

        AboutBuilder builder = AboutBuilder.with(this)
                .setPhoto(R.mipmap.jr)
                .setCover(R.mipmap.cover)
                .setName(R.string.developer_name)
                .setSubTitle(R.string.developer_subtitle)
                .setBrief(R.string.developer_brief)
                .setAppIcon(R.mipmap.green_vault)
                .setAppName(R.string.app_name)
                .addGooglePlayStoreLink(R.string.developer_play_store)
                .addGitHubLink(R.string.developer_github)
                .addInstagramLink(R.string.developer_instagram)
                .addLinkedInLink(R.string.developer_linkedin)
                .addEmailLink(R.string.developer_email)
                .addFiveStarsAction()
                .setVersionNameAsAppSubTitle()
                .addShareAction(R.string.app_name)
                .addMoreFromMeAction(R.string.developer_play_store_user_name)
                .addUpdateAction()
                .addFeedbackAction(getString(R.string.developer_email), getString(R.string.feedback, getString(R.string.app_name)))
                .setWrapScrollView(true);

        ((ViewGroup) findViewById(R.id.holder)).addView(builder.build());

    }

    private void setup() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.about);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }

        return true;
    }

}

