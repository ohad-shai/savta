package com.ohadshai.savta.data;

import com.ohadshai.savta.data.firebase.UsersModelFirebase;
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

//    /**
//     * Updates the specified user and notifies the listener on complete.
//     *
//     * @param user     The user to update.
//     * @param listener The listener to set.
//     */
//    public void update(User user, OnCompleteListener listener) {
//        _modelFirebase.update(user, listener);
//    }
//
//    /**
//     * Deletes the specified user and notifies the listener on complete.
//     *
//     * @param user     The user to delete.
//     * @param listener The listener to set.
//     */
//    public void delete(User user, OnCompleteListener listener) {
//        _modelFirebase.delete(user, listener);
//    }

    //endregion

}
