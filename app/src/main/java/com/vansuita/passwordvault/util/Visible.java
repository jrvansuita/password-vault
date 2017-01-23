package com.vansuita.passwordvault.util;

import android.view.View;

/**
 * Created by jrvansuita on 27/10/16.
 */

public class Visible {

    private View view;

    Visible(View view) {
        this.view = view;
    }

    public static Visible with(View view) {
        return new Visible(view);
    }

    public void gone(boolean gone) {
        if (view != null)
            if (gone) {
                view.setVisibility(View.GONE);
            } else {
                view.setVisibility(View.VISIBLE);
            }
    }

    public void invisible(boolean invisible) {
        if (view != null)
            if (invisible) {
                view.setVisibility(View.INVISIBLE);
            } else {
                view.setVisibility(View.VISIBLE);
            }
    }


    public static boolean is(View view) {
        return view.getVisibility() == View.VISIBLE;
    }


}
