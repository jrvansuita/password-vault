package com.vansuita.passwordvault.lis;

import com.vansuita.passwordvault.bean.Bean;

/**
 * Created by jrvansuita on 20/01/17.
 */

public interface IOnFireData<T extends Bean> {

    void add(T data);

    void changed(T data);

    void removed(String key);

    void moved(T data, String previousChild);
}
