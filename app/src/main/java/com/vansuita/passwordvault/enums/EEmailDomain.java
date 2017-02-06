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
    AOL("aol.com", R.mipmap.aol),
    APPLE("apple.com", R.mipmap.apple),
    MAIL("mail.com", R.mipmap.mail),
    ZOHO("zoho.com", R.mipmap.zoho),
    LIVE("live.com", R.mipmap.live),
    YANDEX("yandex.com", R.mipmap.yandex),
    DEFAULT("", R.mipmap.envelop);


    final String domain;
    final int icon;

    EEmailDomain(String domain, int icon) {
        this.domain = domain;
        this.icon = icon;
    }

    public String getDomain() {
        return domain;
    }

    public int getIcon() {
        return icon;
    }


    public static EEmailDomain findDomain(String name) {
        if (name != null)
            for (EEmailDomain domain : values())
                if (name.toLowerCase().contains(domain.getDomain()))
                    return domain;

        return DEFAULT;
    }

    public static EEmailDomain match(String name) {
        if (name != null)
            for (EEmailDomain domain : values())
                if (name.toLowerCase().equalsIgnoreCase(domain.getDomain()))
                    return domain;

        return null;
    }

}
