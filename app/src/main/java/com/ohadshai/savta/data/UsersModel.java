package com.ohadshai.savta.data;

import com.ohadshai.savta.data.firebase.UsersModelFirebase;
import com.ohadshai.savta.data.utils.OnCompleteListener;
import com.ohadshai.savta.data.utils.OnLoginCompleteListener;
import com.ohadshai.savta.data.utils.OnRegisterCompleteListener;
import com.ohadshai.savta.entities.User;

/**
 * Represents the data access to the Firebase (remote) database, related to Users.
 */
public class UsersModel {
    private static final UsersModel _instance = new UsersModel();
    private final UsersModelFirebase _modelFirebase;

    private UsersModel() {
        _modelFirebase = new UsersModelFirebase();
    }

    public static UsersModel getInstance() {
        return _instance;
    }

    //region Public API

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
     * Logs out the current user.
     */
    public void logoutCurrentUser() {
        _modelFirebase.logoutCurrentUser();
    }

    /**
     * Gets the current user logged in.
     *
     * @return Returns the user object if logged-in, otherwise null.
     */
    public User getCurrentUser() {
        return _modelFirebase.getCurrentUser();
    }

    /**
     * Updates the full name of the current user and notifies the listener on complete.
     *
     * @param firstName The user's first name to update.
     * @param lastName  The user's last name to update.
     * @param listener  The listener to set.
     */
    public void updateFullName(String firstName, String lastName, OnCompleteListener listener) {
        _modelFirebase.updateFullName(firstName, lastName, listener);
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
     * Deletes the current user and notifies the listener on complete.
     *
     * @param password The password of the user to delete.
     * @param listener The listener to set.
     */
    public void delete(String password, OnLoginCompleteListener listener) {
        _modelFirebase.delete(password, listener);
    }

    //endregion

}
