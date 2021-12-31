package com.ohadshai.savta.data.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ohadshai.savta.data.utils.OnLoginCompleteListener;
import com.ohadshai.savta.data.utils.OnRegisterCompleteListener;
import com.ohadshai.savta.entities.User;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the data access to the Firebase remote database, related to Users.
 */
public class UsersModelFirebase {

    //region Constants

    public static final String COLLECTION_NAME = "users";
    public static final String FIELD_ID = "id";
    public static final String FIELD_FIRST_NAME = "first_name";
    public static final String FIELD_LAST_NAME = "last_name";
    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_DATE_REGISTERED = "date_registered";

    //endregion

    //region Public API

    public void register(String firstName, String lastName, String email, String password, OnRegisterCompleteListener listener) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Stores additional user's data to Firebase Authentication:
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(firstName + " " + lastName)
                                    .build();
                            firebaseUser.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                User user = convertFirebaseUserToApplicationUser(firebaseUser);
                                                // Stores the user data to the Firebase Firestore database:
                                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                Map<String, Object> userToEnter = mapUserToDocument(user, true);
                                                db.collection(COLLECTION_NAME)
                                                        .document(user.getId())
                                                        .set(userToEnter)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                listener.onSuccess(user);
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.w("firebase:register", "storeInUsersCollection:failure", e);
                                                                listener.onFailure(e);
                                                            }
                                                        });
                                            } else {
                                                Log.w("firebase:register", "user:updateProfile:failure", task.getException());
                                                listener.onFailure(task.getException());
                                            }
                                        }
                                    });
                        } else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                listener.onCollision();
                            } else {
                                Log.w("firebase:register", "createUserWithEmailAndPassword:failure", task.getException());
                                listener.onFailure(task.getException());
                            }
                        }
                    }
                });
    }

    public void login(String email, String password, OnLoginCompleteListener listener) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            User user = convertFirebaseUserToApplicationUser(firebaseUser);
                            listener.onSuccess(user);
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidUserException || task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                listener.onInvalidCredentials();
                            } else {
                                Log.w("firebase:login", "signInWithEmailAndPassword:failure", task.getException());
                                listener.onFailure(task.getException());
                            }
                        }
                    }
                });
    }

    public void logoutCurrentUser() {
        FirebaseAuth.getInstance().signOut();
    }

    public User getCurrentUser() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            return null;
        } else {
            return this.convertFirebaseUserToApplicationUser(firebaseUser);
        }
    }

//    public void update(User user, OnCompleteListener listener) {
//        // Same as register, this updates the document if exist:
//        this.register(user, listener);
//    }
//
//    public void delete(User user, OnCompleteListener listener) {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//        db.collection(COLLECTION_NAME)
//                .document(String.valueOf(user.getId()))
//                .delete()
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        listener.onSuccess();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        listener.onFailure();
//                    }
//                });
//    }

    //endregion

    //region Private Methods

    private User convertFirebaseUserToApplicationUser(FirebaseUser firebaseUser) {
        User user = new User();
        user.setId(firebaseUser.getUid());
        user.setFullName(firebaseUser.getDisplayName());
        user.setEmail(firebaseUser.getEmail());
        return user;
    }

    private Map<String, Object> mapUserToDocument(User user, boolean isCreate) {
        Map<String, Object> map = new HashMap<>();
        map.put(FIELD_ID, user.getId());
        map.put(FIELD_FIRST_NAME, user.getFirstName());
        map.put(FIELD_LAST_NAME, user.getLastName());
        map.put(FIELD_EMAIL, user.getEmail());
        if (isCreate) {
            map.put(FIELD_DATE_REGISTERED, FieldValue.serverTimestamp());
        } else {
            map.put(FIELD_DATE_REGISTERED, user.getDateRegistered());
        }
        return map;
    }

    //endregion

}
