package com.vansuita.passwordvault.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.vansuita.passwordvault.R;
import com.vansuita.passwordvault.enums.EShowType;
import com.vansuita.passwordvault.frag.ListingFrag;

/**
 * Created by jrvansuita on 20/01/17.
 */

public class FavoriteListPageAdapter extends FragmentStatePagerAdapter{

    private Context context;

    public FavoriteListPageAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        return ListingFrag.newInstance(null, EShowType.FAVORITE);
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return context.getString(R.string.favorites);
    }


}
