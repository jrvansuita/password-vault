package com.vansuita.passwordvault.util;

import android.util.Patterns;
import android.widget.EditText;

/**
 * Created by jrvansuita on 27/10/16.
 */

public class Validation {

    public static boolean isEmail(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isEmail(EditText edit) {
        return isEmail(edit.getText().toString());
    }

    public static boolean isEmpty(EditText edit) {
        return edit.getText().toString().isEmpty();
    }


    public static boolean isWebsite(EditText edit) {
        return Patterns.WEB_URL.matcher((edit.getText().toString())).matches();
    }


    public static boolean isWebsite(String s) {
        return Patterns.WEB_URL.matcher((s)).matches();
    }


}
