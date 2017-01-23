package com.vansuita.passwordvault.fire.storage;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vansuita.passwordvault.lis.IOnResult;
import com.vansuita.passwordvault.util.Util;


public class ImageStorage {

    private static final String FIREBASE_STORAGE_URL = "gs://password-vault-1a3eb.appspot.com";
    private Context context;
    private String name;
    private IOnResult<Uri> onResult;

    public ImageStorage(Context context) {
        this.context = context;
    }

    public static ImageStorage with(Context context) {
        return new ImageStorage(context);
    }


    public String getName() {
        return name;
    }

    public ImageStorage setName(String name) {
        this.name = name;
        return this;
    }

    public ImageStorage setOnResult(IOnResult<Uri> onResult) {
        this.onResult = onResult;
        return this;
    }

    public void store(final Uri uri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(FIREBASE_STORAGE_URL);

        StorageReference imgRef = storageRef.child(name);

        byte[] data = Util.bytes(uri, context);
        if (data != null) {
            UploadTask uploadTask = imgRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    if (onResult != null)
                        onResult.onResult(null);
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    if (onResult != null)
                        onResult.onResult(Uri.parse(taskSnapshot.getDownloadUrl().toString()));
                }
            });
        }
    }

}
