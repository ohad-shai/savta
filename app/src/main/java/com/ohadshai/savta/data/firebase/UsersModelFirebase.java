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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.ohadshai.savta.data.RemediesModel;
import com.ohadshai.savta.data.utils.OnEmailUpdateCompleteListener;
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
                                                                    if (listener != null) {
                                                                        listener.onSuccess(user);
                                                                    }
                                                                } else {
                                                                    Log.w("UsersModelFirebase", "register:storeInUsersCollection:failure", task.getException());
                                                                    if (listener != null) {
                                                                        listener.onFailure(task.getException());
                                                                    }
                                                                }
                                                            }
                                                        });
                                            } else {
                                                Log.w("UsersModelFirebase", "register:updateProfile:failure", task.getException());
                                                if (listener != null) {
                                                    listener.onFailure(task.getException());
                                                }
                                            }
                                        }
                                    });
                        } else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                if (listener != null) {
                                    listener.onCollision();
                                }
                            } else {
                                Log.w("UsersModelFirebase", "register:createUserWithEmailAndPassword:failure", task.getException());
                                if (listener != null) {
                                    listener.onFailure(task.getException());
                                }
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
                            if (listener != null) {
                                listener.onSuccess(user);
                            }
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidUserException || task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                if (listener != null) {
                                    listener.onInvalidCredentials();
                                }
                            } else {
                                Log.w("UsersModelFirebase", "login:signInWithEmailAndPassword:failure", task.getException());
                                if (listener != null) {
                                    listener.onFailure(task.getException());
                                }
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
                                                if (listener != null) {
                                                    listener.onSuccess();
                                                }
                                            } else {
                                                Log.w("UserModelFirebase", "updateFullName:firestore-user-data:failure", task.getException());
                                                if (listener != null) {
                                                    listener.onFailure();
                                                }
                                            }
                                        }
                                    });
                        } else {
                            Log.w("UserModelFirebase", "updateFullName:updateProfile:failure", task.getException());
                            if (listener != null) {
                                listener.onFailure();
                            }
                        }
                    }
                });
    }

    public void updateEmailAddress(String password, String email, OnEmailUpdateCompleteListener listener) {
        // Re-authenticates the user, because Firebase will throw an exception if the user is not recently logged-in when performing security operations:
        this.reauthenticate(password, new OnLoginCompleteListener() {
            @Override
            public void onSuccess(User user) {
                // Updates the user's password after authenticated successfully:
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                firebaseUser.updateEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    User user = convertFirebaseUserToApplicationUser(firebaseUser);
                                    // Updates the user data in the Firebase Firestore database:
                                    Map<String, Object> updateValues = new HashMap<>();
                                    updateValues.put(FIELD_EMAIL, user.getEmail());
                                    db.collection(COLLECTION_NAME)
                                            .document(user.getId())
                                            .update(updateValues)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        if (listener != null) {
                                                            listener.onSuccess(user);
                                                        }
                                                    } else {
                                                        Log.w("UsersModelFirebase", "updateEmailAddress:firestore-user-data:failure", task.getException());
                                                        if (listener != null) {
                                                            listener.onFailure(task.getException());
                                                        }
                                                    }
                                                }
                                            });
                                } else {
                                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                        if (listener != null) {
                                            listener.onCollision();
                                        }
                                    } else {
                                        Log.w("UsersModelFirebase", "updateEmailAddress:updateEmail:failure", task.getException());
                                        if (listener != null) {
                                            listener.onFailure(task.getException());
                                        }
                                    }
                                }
                            }
                        });
            }

            @Override
            public void onInvalidCredentials() {
                if (listener != null) {
                    listener.onInvalidCredentials();
                }
            }

            @Override
            public void onFailure(Exception exception) {
                if (listener != null) {
                    listener.onFailure(exception);
                }
            }
        });
    }

    public void updatePassword(String currentPassword, String newPassword, OnLoginCompleteListener listener) {
        // Re-authenticates the user, because Firebase will throw an exception if the user is not recently logged-in when performing security operations:
        this.reauthenticate(currentPassword, new OnLoginCompleteListener() {
            @Override
            public void onSuccess(User user) {
                // Updates the user's password after authenticated successfully:
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                firebaseUser.updatePassword(newPassword)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    User user = convertFirebaseUserToApplicationUser(firebaseUser);
                                    if (listener != null) {
                                        listener.onSuccess(user);
                                    }
                                } else {
                                    Log.w("UsersModelFirebase", "updatePassword:failure", task.getException());
                                    if (listener != null) {
                                        listener.onFailure(task.getException());
                                    }
                                }
                            }
                        });
            }

            @Override
            public void onInvalidCredentials() {
                if (listener != null) {
                    listener.onInvalidCredentials();
                }
            }

            @Override
            public void onFailure(Exception exception) {
                if (listener != null) {
                    listener.onFailure(exception);
                }
            }
        });
    }

    public void deleteAccount(String password, OnLoginCompleteListener listener) {
        // Re-authenticates the user, because Firebase will throw an exception if the user is not recently logged-in when performing security operations:
        this.reauthenticate(password, new OnLoginCompleteListener() {
            @Override
            public void onSuccess(User user) {
                // (1) Deletes the user info from the Firebase Firestore database:
                db.collection(COLLECTION_NAME)
                        .document(user.getId())
                        .delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // (2) Deletes the user from Firebase Authentication:
                                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                    firebaseUser.delete()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        // (3) Deletes the remedies created by the user from the Firebase Firestore database:
                                                        RemediesModel.getInstance().deleteAllByUser(user.getId(), null);
                                                        if (listener != null) {
                                                            listener.onSuccess(null);
                                                        }
                                                    } else {
                                                        Log.w("UsersModelFirebase", "deleteAccount:firebase-auth-delete:failure", task.getException());
                                                        if (listener != null) {
                                                            listener.onFailure(task.getException());
                                                        }
                                                    }
                                                }
                                            });
                                } else {
                                    Log.w("UsersModelFirebase", "deleteAccount:firestore-users-data-delete:failure", task.getException());
                                    if (listener != null) {
                                        listener.onFailure(task.getException());
                                    }
                                }
                            }
                        });
            }

            @Override
            public void onInvalidCredentials() {
                if (listener != null) {
                    listener.onInvalidCredentials();
                }
            }

            @Override
            public void onFailure(Exception exception) {
                if (listener != null) {
                    listener.onFailure(exception);
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

    private void reauthenticate(String password, OnLoginCompleteListener listener) {
        // Gets the current user:
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            throw new IllegalStateException("FirebaseUser cannot be null when re-authenticating the user.");
        }
        // Re-authenticates the user:
        AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), password);
        firebaseUser.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            if (listener != null) {
                                User user = convertFirebaseUserToApplicationUser(firebaseUser);
                                listener.onSuccess(user);
                            }
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidUserException || task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                if (listener != null) {
                                    listener.onInvalidCredentials();
                                }
                            } else {
                                Log.w("UserModelFirebase", "reauthenticate:failure", task.getException());
                                if (listener != null) {
                                    listener.onFailure(task.getException());
                                }
                            }
                        }
                    }
                });
    }

    //endregion

}
