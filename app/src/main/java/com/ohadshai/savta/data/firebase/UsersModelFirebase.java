package com.ohadshai.savta.data.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;
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

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public UsersModelFirebase() {
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false)
                .build();
        db.setFirestoreSettings(settings);
    }

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
                                                Map<String, Object> userToEnter = mapUserToDocument(user, true);
                                                db.collection(COLLECTION_NAME)
                                                        .document(user.getId())
                                                        .set(userToEnter)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    listener.onSuccess(user);
                                                                } else {
                                                                    Log.w("firebase:register", "storeInUsersCollection:failure", task.getException());
                                                                    listener.onFailure(task.getException());
                                                                }
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

    public void updateFullName(String firstName, String lastName, com.ohadshai.savta.data.utils.OnCompleteListener listener) {
        // Gets the current user:
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            throw new IllegalStateException("FirebaseUser cannot be null when updating user data.");
        }
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(firstName + " " + lastName)
                .build();
        firebaseUser.updateProfile(profileUpdates)
                .addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Updates the user data in the Firebase Firestore database:
                            Map<String, Object> updateValues = new HashMap<>();
                            updateValues.put(FIELD_FIRST_NAME, firstName);
                            updateValues.put(FIELD_LAST_NAME, lastName);
                            db.collection(COLLECTION_NAME)
                                    .document(firebaseUser.getUid())
                                    .update(updateValues)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                listener.onSuccess();
                                            } else {
                                                Log.w("firebase:updateFullName", "users:update:failure", task.getException());
                                                listener.onFailure();
                                            }
                                        }
                                    });
                        } else {
                            Log.w("firebase:updateFullName", "firebaseUser:updateProfile:failure", task.getException());
                            listener.onFailure();
                        }
                    }
                });
    }

    public void updatePassword(String currentPassword, String newPassword, OnLoginCompleteListener listener) {
        // Gets the current user:
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            throw new IllegalStateException("FirebaseUser cannot be null when updating user data.");
        }
        // Re-authenticates the user, because Firebase will throw an exception if the user is not recently logged-in:
        AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), currentPassword);
        firebaseUser.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Updates the user's password after authenticated successfully:
                            firebaseUser.updatePassword(newPassword)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                User user = convertFirebaseUserToApplicationUser(firebaseUser);
                                                listener.onSuccess(user);
                                            } else {
                                                Log.w("firebase:updatePassword", "firebaseUser:updatePassword:failure", task.getException());
                                                listener.onFailure(task.getException());
                                            }
                                        }
                                    });
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidUserException || task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                listener.onInvalidCredentials();
                            } else {
                                Log.w("firebase:updatePassword", "firebaseUser:reauthenticate:failure", task.getException());
                                listener.onFailure(task.getException());
                            }
                        }
                    }
                });
    }

    public void delete(String password, OnLoginCompleteListener listener) {
        // Gets the current user:
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            throw new IllegalStateException("FirebaseUser cannot be null when deleting the user.");
        }
        // Re-authenticates the user, because Firebase will throw an exception if the user is not recently logged-in:
        AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), password);
        firebaseUser.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // (1) Deletes the remedies created by the user from the Firebase Firestore database:
                            CollectionReference remediesCollection = db.collection(RemediesModelFirebase.COLLECTION_NAME);
                            remediesCollection.whereEqualTo(RemediesModelFirebase.FIELD_POSTED_BY_USER_ID, firebaseUser.getUid())
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                QuerySnapshot taskResult = task.getResult();
                                                if (taskResult != null) {
                                                    for (DocumentSnapshot document : taskResult) {
                                                        remediesCollection.document(document.getId()).delete();
                                                    }
                                                }
                                                // (2) Deletes the user info from the Firebase Firestore database:
                                                db.collection(COLLECTION_NAME)
                                                        .document(firebaseUser.getUid())
                                                        .delete()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    // (3) Deletes the user from Firebase Authentication:
                                                                    firebaseUser.delete()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        listener.onSuccess(null);
                                                                                    } else {
                                                                                        Log.w("firebase:delete", "firebaseUser:delete:failure", task.getException());
                                                                                        listener.onFailure(task.getException());
                                                                                    }
                                                                                }
                                                                            });
                                                                } else {
                                                                    Log.w("firebase:delete", "users:delete:failure", task.getException());
                                                                    listener.onFailure(task.getException());
                                                                }
                                                            }
                                                        });
                                            } else {
                                                Log.w("firebase:delete", "remedies:delete:failure", task.getException());
                                                listener.onFailure(task.getException());
                                            }
                                        }
                                    });
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidUserException || task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                listener.onInvalidCredentials();
                            } else {
                                Log.w("firebase:delete", "firebaseUser:reauthenticate:failure", task.getException());
                                listener.onFailure(task.getException());
                            }
                        }
                    }
                });
    }

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
