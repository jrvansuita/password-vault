package com.vansuita.passwordvault.act;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.vansuita.passwordvault.R;

/**
 * Created by jrvansuita on 07/03/17.
 */

public class Intro extends AppIntro2 {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        addSlide(AppIntroFragment.newInstance(getString(R.string.keep_it_organized), getString(R.string.keep_it_organized_msg), R.mipmap.tags, ContextCompat.getColor(this, R.color.accent)));
        addSlide(AppIntroFragment.newInstance(getString(R.string.keep_it_noted),  getString(R.string.keep_it_noted_msg), R.mipmap.book_open, ContextCompat.getColor(this, R.color.accent)));
        addSlide(AppIntroFragment.newInstance(getString(R.string.keep_it_synced),  getString(R.string.keep_it_synced_msg), R.mipmap.cloud_refresh, ContextCompat.getColor(this, R.color.accent)));
        addSlide(AppIntroFragment.newInstance(getString(R.string.keep_it_safe),  getString(R.string.keep_it_safe_msg), R.mipmap.vault, ContextCompat.getColor(this, R.color.accent)));

        showSkipButton(false);
        setProgressButtonEnabled(true);
    }


    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Lock.start(Intro.this, true);
        finish();
    }


}
