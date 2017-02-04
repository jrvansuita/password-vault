package com.vansuita.passwordvault.fire.database;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vansuita.passwordvault.cnt.VaultCnt;

/**
 * Created by jrvansuita on 27/01/17.
 */

public class DatabaseAccess {

    private static DatabaseReference databaseReference;

    private synchronized static DatabaseReference getDatabaseNode() {
        if (databaseReference == null) {
            databaseReference = FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid());
        }

        return databaseReference;
    }

    public synchronized static DatabaseReference getVaultNode() {
        return getDatabaseNode().child(VaultCnt.NAME);
    }

    public synchronized static DatabaseReference getTrashNode() {
        return getDatabaseNode().child(VaultCnt.TRASH);
    }

    public static void clear(){
        databaseReference = null;
    }


}
