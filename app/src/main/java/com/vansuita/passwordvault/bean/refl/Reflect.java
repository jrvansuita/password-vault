package com.vansuita.passwordvault.bean.refl;

import android.support.annotation.Keep;

import com.vansuita.passwordvault.bean.Bean;

import java.lang.reflect.Method;

/**
 * Created by jrvansuita on 21/02/17.
 */

@Keep
public class Reflect {

    private Bean object;

    public Reflect(Bean object) {
        this.object = object;
    }


    public String getDefaultSubTitle() {
        String result = getEmail();

        return result.isEmpty() ? getText() : result;
    }

    public String getEmail() {
        return getString("getEmail");
    }

    public String getLogin() {
        return getString("getLogin");
    }

    public String getPassword() {
        String s = getString("getPassword");

        return s.substring(0, Math.min(10, s.length()));
    }

    public String getText() {
        return getString("getNote");
    }


    private String getString(String methodName) {
        String result = "";
        try {
            Method m = object.getClass().getMethod(methodName);
            if (m != null)
                result = (String) m.invoke(object);
        } catch (Exception e) {
           // e.printStackTrace();
        }

        return result;
    }

}
