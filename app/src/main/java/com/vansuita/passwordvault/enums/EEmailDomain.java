package com.vansuita.passwordvault.enums;

import com.vansuita.passwordvault.R;

/**
 * Created by jrvansuita on 03/02/17.
 */

public enum EEmailDomain {

    GMAIL("gmail.com", R.mipmap.gmail),
    OUTLOOK("outlook.com", R.mipmap.outlook),
    HOTMAIL("hotmail.com", R.mipmap.hotmail),
    APPLE("apple.com", R.mipmap.apple),
    MAIL("mail.com", R.mipmap.mail),
    LIVE("live.com", R.mipmap.live),
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
}
