package com.vansuita.passwordvault.bean;

/**
 * Created by jrvansuita on 18/01/17.
 */

public class Domain {
    private String name;
    private int icon;

    public String getName() {
        return name;
    }

    public int getIcon() {
        return icon;
    }

    public Domain(String name, int icon) {
        super();
        this.name = name;
        this.icon = icon;
    }

    public String toString() {
        return getName();
    }

}
