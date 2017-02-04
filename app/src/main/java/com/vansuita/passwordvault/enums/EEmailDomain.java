package com.vansuita.passwordvault.enums;

import com.vansuita.passwordvault.R;

/**
 * Created by jrvansuita on 03/02/17.
 */

public enum EEmailDomain {

    GMAIL("gmail.com", R.mipmap.gmail),
    OUTLOOK("outlook.com", R.mipmap.outlook),
    HOTMAL("hotmail.com", R.mipmap.hotmail),
    YAHOO("yahoo.com", R.mipmap.yahoo),
    TERRA("terra.com", R.mipmap.terra),
    DEFAULT("", R.mipmap.envelop);


    final String name;
    final int icon;

    EEmailDomain(String name, int icon) {
        this.name = name;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public int getIcon() {
        return icon;
    }


    public static EEmailDomain findDomain(String name) {
        if (name != null)
            for (EEmailDomain domain : values())
                if (name.toLowerCase().contains(domain.getName()))
                    return domain;

        return DEFAULT;
    }

    public static EEmailDomain match(String name) {
        if (name != null)
            for (EEmailDomain domain : values())
                if (name.toLowerCase().equalsIgnoreCase(domain.getName()))
                    return domain;

        return null;
    }
}
