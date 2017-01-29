package com.vansuita.passwordvault.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


public class Util extends com.vansuita.pickimage.Util {

    public static boolean internet(Context context) {
        if (context == null) {
            return false;
        }

        ConnectivityManager con = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (con == null) {
            return false;
        }

        NetworkInfo inf = con.getActiveNetworkInfo();

        return inf != null && inf.isConnected();
    }

    public static void showKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (activity.getCurrentFocus() != null) {
            imm.showSoftInput(activity.getCurrentFocus(), 0);
        }
    }


    public static void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public static void hideKeyboard(Activity activity) {
        if (activity != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (activity.getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            }
        }
    }

    public static String removeAccentuation(String str) {
        return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }

    public static String[] join(String[]... arrays) {
        Set<String> set = new LinkedHashSet<String>();
        for (int i = 0; i < arrays.length; i++) {
            set.addAll(Arrays.asList(arrays[i]));
        }

        return set.toArray(new String[set.size()]);
    }


    public static String breakingNulls(String divider, String... array) {
        List<String> list = new ArrayList();

        for (int i = 0; i < array.length; i++) {
            if (array[i] != null && !array[i].isEmpty()) {
                list.add(array[i]);
            }
        }

        return breakList(divider, list);
    }

    public static String coalesce(String... array) {
        if (array != null) {
            for (int i = 0; i < array.length; i++) {
                if (array[i] != null && !array[i].isEmpty()) {
                    return array[i];
                }
            }
        }
        return null;
    }

    public static String breakArray(String divider, String... array) {
        return breakList(divider, Arrays.asList(array));
    }

    public static String breakList(String divider, List<String> list) {
        String result = "";

        for (int i = 0; i < list.size(); i++) {
            result += list.get(i) + (i == (list.size() - 1) ? "" : divider);
        }

        return result;
    }

    public static byte[] bytes(Uri selectedImage, Context context) {
        try {
            Bitmap bitmap = com.vansuita.pickimage.Util.decodeUri(selectedImage, context, 100);

            if (bitmap != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                return baos.toByteArray();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static String shorter(String s, int count) {
        if (s != null && (s.length() > count)) {
            return s.substring(0, count - 3) + "...";
        }

        return s;
    }

    public static String getDomain(String email) {
        if (Validation.isEmail(email))
            return email.split("@")[1];

        return null;
    }

    public static String getLogin(String email) {
        if (Validation.isEmail(email))
            return email.split("@")[0];

        return null;
    }

    public static boolean isColorDark(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        if (darkness < 0.5 || color == 0) {
            return false; // It's a light color
        } else {
            return true; // It's a dark color
        }
    }

    public static int darker(int color){
        int r = Color.red(color);
        int b = Color.blue(color);
        int g = Color.green(color);

        return Color.rgb((int)(r*.9), (int)(g*.9), (int)(b*.9));
    }


}
