package com.vansuita.passwordvault.fire.dao;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.vansuita.passwordvault.R;
import com.vansuita.passwordvault.fire.database.DatabaseAccess;
import com.vansuita.passwordvault.pref.Pref;

import java.util.Map;

/**
 * Created by jrvansuita on 06/02/17.
 */

public class PrefDAO extends Pref {

    PrefDAO(Context context) {
        super(context);
    }

    public static PrefDAO with(Context context) {
        return new PrefDAO(context);
    }


    public void save() {
        try {
            DatabaseReference databaseReference = DatabaseAccess.getPrefNode();
            databaseReference.setValue(getShared().getAll());
        }catch (Exception e){
            if (e.getMessage().contains("Serializing Collections")){
                Toast.makeText(getContext(), R.string.serializing_collections, Toast.LENGTH_LONG).show();
            }else{
                throw e;
            }
        }
    }

    public void restore() {
        DatabaseReference databaseReference = DatabaseAccess.getPrefNode();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                restoreFromSnapshot(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void restoreFromSnapshot(DataSnapshot snapshot) {
        Map<String, ?> entries = (Map<String, ?>) snapshot.getValue();

        if (entries != null) {
            SharedPreferences.Editor editor = getShared().edit();

            for (Map.Entry<String, ?> entry : entries.entrySet()) {
                Object v = entry.getValue();
                String key = entry.getKey();

                if (v instanceof Boolean)
                    editor.putBoolean(key, ((Boolean) v).booleanValue());
                else if (v instanceof Float)
                    editor.putFloat(key, ((Float) v).floatValue());
                else if (v instanceof Integer)
                    editor.putInt(key, ((Integer) v).intValue());
                else if (v instanceof Long)
                    editor.putLong(key, ((Long) v).longValue());
                else if (v instanceof String)
                    editor.putString(key, ((String) v));
            }

            editor.apply();
        }
    }


}
