package com.vansuita.passwordvault.dev;

import android.content.Context;
import android.provider.Settings;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by jrvansuita on 01/03/17.
 */

public class Device {

    public static String getId(Context context) {
        String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(deviceId.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {

                String h = Integer.toHexString(0xFF & messageDigest[i]);

                while (h.length() < 2)
                    h = "0" + h;

                hexString.append(h);
            }

            deviceId = hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            deviceId = "";
        } finally {
            return deviceId.toUpperCase();
        }
    }
}
