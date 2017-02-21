package com.vansuita.passwordvault.act;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.vansuita.materialabout.builder.AboutBuilder;
import com.vansuita.passwordvault.R;

/**
 * Created by jrvansuita on 20/02/17.
 */

public class About extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = AboutBuilder.with(this)
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
                .addLinkedinLink(R.string.developer_linkedin)
                .addEmailLink(R.string.developer_email)
                .addGooglePlusLink(R.string.developer_gplus)
                .addFiveStarsAction()
                .setVersionAsAppTitle()
                .addShareAction(R.string.app_name)
                .addMoreFromMeAction(R.string.developer_play_store_user_name)
                .addUpdateAction()
                .addFeedbackAction(R.string.developer_email)
                .setWrapScrollView(true)
                .build();

        addContentView(view, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.setBackgroundColor(ContextCompat.getColor(this, R.color.primary));
    }
}
