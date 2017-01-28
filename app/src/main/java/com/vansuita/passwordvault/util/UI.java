package com.vansuita.passwordvault.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.vansuita.passwordvault.R;

/**
 * Created by jrvansuita on 27/10/16.
 */

public class UI {

    public static void clear(TextInputLayout t, TextView tv) {
        tv.setText("");
        setError(t);
    }

    public static void setError(TextInputLayout t) {
        setError(t, null);
    }


    public static void setError(TextInputLayout t, String msg) {
        t.setError(msg == null || msg.isEmpty() ? null : msg);
        t.setErrorEnabled(!(msg == null || msg.isEmpty()));
    }

    /*public static void clearErrors(ViewGroup holder) {
        for (int i = 0; i < holder.getChildCount(); i++) {
            View v = holder.getChildAt(i);

            if (v instanceof TextInputLayout) {
                setError(((TextInputLayout) v));
            } else if (v instanceof EditText) {
                ((EditText) v).setError(null);
            } else if (v instanceof ViewGroup) {
                clearErrors((ViewGroup) v);
            }
        }
    }*/

    public static boolean error(TextInputLayout til, boolean doThrow, int msg) {
        til.setErrorEnabled(doThrow);

        if (doThrow)
            til.setError(til.getContext().getString(msg));

        return !doThrow;
    }


    public static void applyIcon(EditText edit, int res) {
        applyIcon(edit, res == 0 ? null : ContextCompat.getDrawable(edit.getContext(), res));
    }

    public static void applyIcon(EditText edit, Bitmap res) {
        Drawable drawable = new BitmapDrawable(edit.getResources(), res);
        applyIcon(edit, drawable);
    }

    public static void applyIcon(EditText edit, Drawable res) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            edit.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, res, null);
        } else {
            edit.setCompoundDrawablesWithIntrinsicBounds(null, null, res, null);
        }
    }

    public static void applyCheck(boolean apply, TextInputLayout til, EditText edit) {
        applyIcon(edit, apply ? R.drawable.check : 0);

        if (apply)
            setError(til);
    }


    public static void setStatusBarColor(Activity a, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            a.getWindow().setStatusBarColor(ContextCompat.getColor(a, color));
        }
    }


    public static void setFavorite(View view, boolean favorite){
        view.setVisibility(favorite ? View.VISIBLE: View.GONE);
    }

}
