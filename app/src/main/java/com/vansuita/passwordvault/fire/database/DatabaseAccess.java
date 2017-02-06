package com.vansuita.passwordvault.fire.database;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by jrvansuita on 27/01/17.
 */

public class DatabaseAccess {

    private static final String VAULT_NODE = "VAULT";
    private static final String TRASH_NODE = "TRASH";
    private static final String PREF_NODE = "PREF";

    private static DatabaseReference databaseReference;

    private synchronized static DatabaseReference getDatabaseNode() {
        if (databaseReference == null) {
            databaseReference = FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid());
        }

        return databaseReference;
    }

    public synchronized static DatabaseReference getVaultNode() {
        return getDatabaseNode().child(VAULT_NODE);
    }

    public synchronized static DatabaseReference getTrashNode() {
        return getDatabaseNode().child(TRASH_NODE);
    }

    public synchronized static DatabaseReference getPrefNode() {
        return getDatabaseNode().child(PREF_NODE);
    }


    public static void clear(){
        databaseReference = null;
    }


}
