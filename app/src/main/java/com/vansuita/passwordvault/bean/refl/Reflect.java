package com.vansuita.passwordvault.bean.refl;

import com.vansuita.passwordvault.bean.Bean;

import java.lang.reflect.Method;

/**
 * Created by jrvansuita on 21/02/17.
 */

public class Reflect {

    private Bean object;

    public Reflect(Bean object) {
        this.object = object;
    }


    public String getDefaultSubTitle() {
        String result = getEmail();

        return result.isEmpty() ? getString("getNote") : result;
    }

    public String getEmail() {
        return getString("getEmail");
    }


    public String getPassword() {
        return getString("getPassword");
    }

    private String getString(String methodName) {
        String result = "";
        try {
            Method m = object.getClass().getMethod(methodName);
            if (m != null)
                result = (String) m.invoke(object);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

}
