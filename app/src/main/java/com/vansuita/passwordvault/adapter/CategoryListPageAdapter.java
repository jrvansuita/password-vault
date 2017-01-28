package com.vansuita.passwordvault.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.vansuita.passwordvault.R;
import com.vansuita.passwordvault.enums.ECategory;
import com.vansuita.passwordvault.frag.ListingFrag;

/**
 * Created by jrvansuita on 20/01/17.
 */

public class CategoryListPageAdapter extends FragmentPagerAdapter {

    private Context context;

    public CategoryListPageAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        return ListingFrag.newInstance(getCategory(position));
    }

    @Override
    public int getCount() {
        return 1 + ECategory.values().length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return position == 0 ? context.getString(R.string.all) : getCategory(position).name();
    }

    private ECategory getCategory(int position) {
        return position == 0 ? null : ECategory.values()[position - 1];
    }


}
