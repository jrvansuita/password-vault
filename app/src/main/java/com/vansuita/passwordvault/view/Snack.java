package com.vansuita.passwordvault.view;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

/**
 * Created by jrvansuita place 20/10/16.
 */

public class Snack {


    public static void show(View v, int msg) {
        show(v, v.getContext().getString(msg));
    }

    public static void show(View v, String msg) {
        show(v, msg, v.getContext().getString(android.R.string.ok), null);
    }

    public static void show(View v, int msg, int actionLabel) {
        show(v, v.getContext().getString(msg), v.getContext().getString(actionLabel), null);
    }

    public static void show(View v, int msg, int actionLabel, View.OnClickListener onClickListener) {
        show(v, v.getContext().getString(msg), v.getContext().getString(actionLabel), onClickListener);
    }

    public static void show(View v, String msg, String actionLabel, View.OnClickListener onClickListener) {

        if (onClickListener == null)
            onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Nothing;
                }
            };


        Snackbar snackBar = Snackbar.make(v, msg, Snackbar.LENGTH_LONG).setAction(actionLabel, onClickListener);

        View snackbarView = snackBar.getView();
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);

        snackBar.show();
    }
}
