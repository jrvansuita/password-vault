package com.vansuita.passwordvault.fire.dao;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.vansuita.passwordvault.bean.Bean;
import com.vansuita.passwordvault.cnt.VaultCnt;
import com.vansuita.passwordvault.enums.ECategory;
import com.vansuita.passwordvault.enums.EShowType;
import com.vansuita.passwordvault.lis.IOnFireData;

import java.util.ArrayList;
import java.util.List;

import static com.vansuita.passwordvault.fire.database.DatabaseAccess.getTrashNode;
import static com.vansuita.passwordvault.fire.database.DatabaseAccess.getVaultNode;

/**
 * Created by jrvansuita on 19/01/17.
 */

public class DataAccess {

    private ECategory category;
    private EShowType showType;
    private IOnFireData listeners;

    DataAccess(ECategory category, EShowType showType) {
        this.category = category;
        this.showType = showType;
    }

    private void bindListener(ChildEventListener l) {
        DatabaseReference databaseReference = showType == EShowType.TRASH ? getTrashNode() : getVaultNode();

        if (category != null) {
            databaseReference.orderByChild(VaultCnt.CATEGORY)
                    .equalTo(category.name()).addChildEventListener(l);
        } else {
            if (showType == EShowType.FAVORITE){
                databaseReference.orderByChild(VaultCnt.FAVORITE)
                        .equalTo(true).addChildEventListener(l);
            }else {
                databaseReference.addChildEventListener(l);
            }
        }
    }

    public static DataAccess on(ECategory category, EShowType showType) {
        return new DataAccess(category, showType);
    }

    public DataAccess listeners(IOnFireData listeners) {
        this.listeners = listeners;
        return this;
    }

    public static void color(int color, Bean... items) {
        for (Bean bean : items) {
            bean.setColor(color);
            getVaultNode().child(bean.getKey()).setValue(bean);
        }
    }

    public static void favorite(Bean... items) {
        for (Bean bean : items) {
            bean.setFavorite(!bean.isFavorite());
            getVaultNode().child(bean.getKey()).setValue(bean);
        }
    }


    /**
     * Delete items based on their keys
     *
     * @param keys Keys to iterate.
     */

    public static void delete(String... keys) {
        if (keys != null && keys.length > 0) {
            DatabaseReference databaseReference = getTrashNode();

            for (int i = 0; i < keys.length; i++) {
                databaseReference.child(keys[i]).removeValue();
            }
        }
    }

    /**
     * Delete items
     *
     * @param items Items to iterate.
     */

    public static void delete(Bean... items) {
        List<String> keys = new ArrayList();

        for (Bean bean : items)
            keys.add(bean.getKey());

        delete(keys.toArray(new String[keys.size()]));
    }


    /**
     * Move items to trash or remove them from trash depending on...
     *
     * @param items Objects to iterate.
     */

    public static void trash(Bean... items) {
        if (items != null && items.length > 0) {
            final DatabaseReference vaultNode = getVaultNode();
            final DatabaseReference trashNode = getTrashNode();

            for (final Bean bean : items) {
                vaultNode.child(bean.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            trashNode.child(bean.getKey()).setValue(bean);
                            vaultNode.child(bean.getKey()).removeValue();
                        } else {
                            vaultNode.child(bean.getKey()).setValue(bean);
                            trashNode.child(bean.getKey()).removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
    }

    public static void put(Bean bean) {
        DatabaseReference databaseReference;

        if (bean.isNew()) {
            databaseReference = getVaultNode().push();
            bean.setKey(databaseReference.getKey());
        } else {
            databaseReference = getVaultNode().child(bean.getKey());
        }

        databaseReference.setValue(bean);
    }

    private ChildEventListener childEventListener;

    public void get() {
        if (childEventListener != null)
            getVaultNode().removeEventListener(childEventListener);

        childEventListener = new ChildEventListener() {

            private Bean getBean(DataSnapshot snap) {
                try {
                    String clazz = snap.child(VaultCnt.CLAZZ).getValue(String.class);
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
