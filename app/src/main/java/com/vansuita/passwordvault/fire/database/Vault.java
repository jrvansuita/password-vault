package com.vansuita.passwordvault.fire.database;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vansuita.passwordvault.bean.Bean;
import com.vansuita.passwordvault.cnt.BeanCnt;
import com.vansuita.passwordvault.enums.ECategory;
import com.vansuita.passwordvault.lis.IOnFireData;

/**
 * Created by jrvansuita on 19/01/17.
 */

public class Vault {

    private ECategory category;
    private IOnFireData listeners;

    Vault(ECategory category) {
        this.category = category;
    }

    private static DatabaseReference databaseReference;

    private synchronized static DatabaseReference getDatabaseNode() {
        if (databaseReference == null) {
            databaseReference = FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(BeanCnt.NAME);
        }

        return databaseReference;
    }

    private void bindListener(ChildEventListener l) {
        if (category != null) {
            getDatabaseNode().orderByChild(BeanCnt.CATEGORY).equalTo(category.name())
                    .addChildEventListener(l);
        } else {
            getDatabaseNode().
                    orderByChild(BeanCnt.TITLE).startAt("m").
                    addChildEventListener(l);
        }
    }

    public static Vault on(String e) {
        return new Vault(ECategory.valueOf(e));
    }

    public static Vault on(ECategory e) {
        return new Vault(e);
    }

    public Vault listeners(IOnFireData listeners) {
        this.listeners = listeners;
        return this;
    }


    public static void delete(String key) {
        getDatabaseNode().child(key).removeValue();
    }

    public static void put(Bean bean) {
        DatabaseReference databaseReference = null;

        if (bean.isNew()){
            databaseReference = getDatabaseNode().push();
            bean.setKey(databaseReference.getKey());
        }else{
            databaseReference = getDatabaseNode().child(bean.getKey());
        }

        databaseReference.setValue(bean);
    }

    private ChildEventListener childEventListener;

    public void get() {
        if (childEventListener != null)
            getDatabaseNode().removeEventListener(childEventListener);

        childEventListener = new ChildEventListener() {

            private Bean getBean(DataSnapshot snap) {
                try {
                    String clazz =  snap.child(BeanCnt.CLAZZ).getValue(String.class);
                    return (Bean) snap.getValue(Class.forName(clazz));
                } catch (ClassNotFoundException e) {
                    return null;
                }
            }

            @Override
            public void onChildAdded(DataSnapshot snap, String s) {
                listeners.add(getBean(snap));
            }

            @Override
            public void onChildChanged(DataSnapshot snap, String s) {
                listeners.changed(getBean(snap));
            }

            @Override
            public void onChildRemoved(DataSnapshot snap) {
                listeners.removed(snap.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot snap, String previousChildName) {
                listeners.moved(getBean(snap), previousChildName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        bindListener(childEventListener);
    }


}
