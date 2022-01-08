package com.ohadshai.savta.data.firebase;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ohadshai.savta.data.utils.OnImageUploadCompleteListener;

import java.io.ByteArrayOutputStream;

/**
 * Represents the data access to the Firebase Storage.
 */
public class ModelFirebaseStorage {

    private final FirebaseStorage storage = FirebaseStorage.getInstance();

    //region Public API

    public void uploadImage(Bitmap bitmap, String imageFilePath, OnImageUploadCompleteListener listener) {
        final StorageReference imageRef = storage.getReference().child(imageFilePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = imageRef.putBytes(data);
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                // Continue with the task to get the download URL:
                return imageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    if (listener != null) {
                        String imageUrl = task.getResult().toString();
                        listener.onSuccess(imageFilePath, imageUrl);
                    }
                } else {
                    Log.e("ModelFirebaseStorage", "uploadImage:failure", task.getException());
                    if (listener != null) {
                        listener.onFailure();
                    }
                }
            }
        });
    }

    public void deleteImage(String imageFilePath, com.ohadshai.savta.data.utils.OnCompleteListener listener) {
        final StorageReference imageRef = storage.getReference().child(imageFilePath);
        imageRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    if (listener != null) {
                        listener.onSuccess();
                    }
                } else {
                    Log.e("ModelFirebaseStorage", "deleteImage:failure", task.getException());
                    if (listener != null) {
                        listener.onFailure();
                    }
                }
            }
        });
    }

    //endregion

}
