package com.ohadshai.savta.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ohadshai.savta.data.firebase.UsersModelFirebase;
import com.ohadshai.savta.data.utils.OnCompleteListener;
import com.ohadshai.savta.data.utils.OnEmailUpdateCompleteListener;
import com.ohadshai.savta.data.utils.OnLoginCompleteListener;
import com.ohadshai.savta.data.utils.OnRegisterCompleteListener;
import com.ohadshai.savta.data.utils.OnUserRefreshCompleteListener;
import com.ohadshai.savta.entities.User;

/**
 * Represents the data access to the Firebase (remote) database, related to Users.
 */
public class UsersModel {
    private static final UsersModel _instance = new UsersModel();
    private final UsersModelFirebase _modelFirebase;

    private MutableLiveData<User> _user = new MutableLiveData<>();

    private UsersModel() {
        _modelFirebase = new UsersModelFirebase();
    }

    public static UsersModel getInstance() {
        return _instance;
    }

    //region Public API

    /**
     * Clears all the live data members.
     */
    public void clearAllLiveData() {
        _user.setValue(null);
    }

    /**
     * Registers a new user and notifies the listener on complete.
     *
     * @param firstName The first name of the user to set.
     * @param lastName  The last name of the user to set.
     * @param email     The email of the user to set.
     * @param password  The password of the user to set.
     * @param listener  The listener to set.
     */
    public void register(String firstName, String lastName, String email, String password, OnRegisterCompleteListener listener) {
        _modelFirebase.register(firstName, lastName, email, password, listener);
    }

    /**
     * Logins a user and notifies the listener on complete.
     *
     * @param email    The email of the user to login.
     * @param password The password of the user to login.
     * @param listener The listener to set.
     */
    public void login(String email, String password, OnLoginCompleteListener listener) {
        _modelFirebase.login(email, password, listener);
    }

    /**
     * Logs out the current user and notifies the listener on complete.
     *
     * @param listener The listener to set.
     */
    public void logoutCurrentUser(OnCompleteListener listener) {
        _modelFirebase.logoutCurrentUser();

        // Clears all the live data members (in order to prevent a data leak with the next user to login in the session):
        this.clearAllLiveData();
        RemediesModel.getInstance().clearAllLiveData();
    }

    /**
     * Gets the current user info (from local storage).
     *
     * @return Returns the livedata object containing user object if logged-in or null if logged out.
     */
    public LiveData<User> getCurrentUser() {
        if (_user.getValue() == null) {
            User user = _modelFirebase.getCurrentUser();
            if (user != null) {
                _user.setValue(user);
            }
        }
        return _user;
    }

    /**
     * Gets the current user info (from the cloud).
     *
     * @param listener The listener to set.
     * @apiNote NOTE: Use this method when it is needed to refresh the data from the cloud.
     */
    public void refreshCurrentUser(OnUserRefreshCompleteListener listener) {
        _modelFirebase.refreshCurrentUser(listener);
    }

    /**
     * Updates the full name of the current user and notifies the listener on complete.
     *
     * @param firstName The user's first name to update.
     * @param lastName  The user's last name to update.
     * @param listener  The listener to set.
     */
    public void updateFullName(String firstName, String lastName, OnCompleteListener listener) {
        _modelFirebase.updateFullName(firstName, lastName, new OnCompleteListener() {
            @Override
            public void onSuccess() {
                User user = _user.getValue();
                if (user != null) {
                    user.setFirstName(firstName);
                    user.setLastName(lastName);
                    _user.setValue(user);
                }
                if (listener != null) {
                    listener.onSuccess();
                }
            }

            @Override
            public void onFailure() {
                if (listener != null) {
                    listener.onFailure();
                }
            }
        });
    }

    /**
     * Updates the email address of the current user and notifies the listener on complete.
     *
     * @param password The user's password to re-authenticate.
     * @param email    The user's email address to update.
     * @param listener The listener to set.
     */
    public void updateEmailAddress(String password, String email, OnEmailUpdateCompleteListener listener) {
        _modelFirebase.updateEmailAddress(password, email, new OnEmailUpdateCompleteListener() {
            @Override
            public void onSuccess(User user) {
                if (_user.getValue() != null) {
                    _user.setValue(user);
                }
                if (listener != null) {
                    listener.onSuccess(user);
                }
            }

            @Override
            public void onCollision() {
                if (listener != null) {
                    listener.onCollision();
                }
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

    /**
     * Updates the password of the current user and notifies the listener on complete.
     *
     * @param currentPassword The current password of the user.
     * @param newPassword     The user's new password to update.
     * @param listener        The listener to set.
     */
    public void updatePassword(String currentPassword, String newPassword, OnLoginCompleteListener listener) {
        _modelFirebase.updatePassword(currentPassword, newPassword, listener);
    }

    /**
     * Deletes the current user account and notifies the listener on complete.
     *
     * @param password The user's password to re-authenticate.
     * @param listener The listener to set.
     */
    public void deleteAccount(String password, OnLoginCompleteListener listener) {
        _modelFirebase.deleteAccount(password, listener);

        // Clears all the live data members (in order to prevent a data leak with the next user to login in the session):
        this.clearAllLiveData();
        RemediesModel.getInstance().clearAllLiveData();
    }

    //endregion

}
