package com.ohadshai.savta.data.firebase;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ohadshai.savta.data.utils.OnCompleteListener;
import com.ohadshai.savta.data.utils.OnGetCompleteListener;
import com.ohadshai.savta.entities.User;

/**
 * Represents the data access to the firebase remote database, related to Users.
 */
public class UsersModelFirebase {

    //region Constants

    public static final String COLLECTION_NAME = "users";
    public static final String FIELD_ID = "id";
    public static final String FIELD_FIRST_NAME = "first_name";
    public static final String FIELD_LAST_NAME = "last_name";
    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_PASSWORD = "password";
    public static final String FIELD_DATE_REGISTERED = "date_registered";

    //endregion

    //region Public API

    public void register(User user, OnCompleteListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(COLLECTION_NAME)
                .document(String.valueOf(user.getId()))
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        listener.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onFailure();
                    }
                });
    }

    public void login(int userId, String password, OnGetCompleteListener<User> listener) {
        // TODO
    }

    public void get(int id, OnGetCompleteListener<User> listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(COLLECTION_NAME)
                .document(String.valueOf(id))
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot document) {
                        if (document.exists()) {
                            User user = document.toObject(User.class);
                            listener.onSuccess(user);
                        } else {
                            listener.onFailure();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onFailure();
                    }
                });
    }

    public void update(User user, OnCompleteListener listener) {
        // Same as register, this updates the document if exist:
        this.register(user, listener);
    }

    public void delete(User user, OnCompleteListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(COLLECTION_NAME)
                .document(String.valueOf(user.getId()))
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        listener.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onFailure();
                    }
                });
    }

    //endregion

}
