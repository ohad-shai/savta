package com.ohadshai.savta.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ohadshai.savta.data.firebase.UsersModelFirebase;
import com.ohadshai.savta.data.sql.UsersModelSql;
import com.ohadshai.savta.data.utils.OnCompleteListener;
import com.ohadshai.savta.data.utils.OnGetCompleteListener;
import com.ohadshai.savta.entities.User;

import java.util.List;

/**
 * Represents the data access to both SQL (local) database and Firebase (remote) database, related to Users.
 */
public class UsersModel {
    public static final UsersModel instance = new UsersModel();

    private final UsersModelSql _modelSql;
    private final UsersModelFirebase _modelFirebase;

    //region [LiveData] Members

    MutableLiveData<User> _user = new MutableLiveData<>();

    //endregion

    private UsersModel() {
        _modelSql = new UsersModelSql();
        _modelFirebase = new UsersModelFirebase();
    }

    //region Public API

    /**
     * Registers a new user and notifies the listener on complete.
     *
     * @param user     The user to register.
     * @param listener The listener to set.
     */
    public void register(User user, OnCompleteListener listener) {
        _modelFirebase.register(user, listener);
    }

    /**
     * Logins a user and notifies the listener on complete.
     *
     * @param userId   The id of the user to login.
     * @param password The password of the user to login.
     * @param listener The listener to set.
     */
    public void login(int userId, String password, OnGetCompleteListener<User> listener) {
        _modelFirebase.login(userId, password, listener);
    }

    /**
     * Gets a user by the specified id, and returns the LiveData object, in order to listen to data updates.
     *
     * @return Returns the LiveData object for the user.
     * @implNote NOTE: The main thread will never know when the updates will occur, so it is used mainly for listening (to data updates).
     */
    public LiveData<User> get(int id) {
        _modelFirebase.get(id, new OnGetCompleteListener<User>() {
            @Override
            public void onSuccess(User user) {
                _user.setValue(user);
            }

            @Override
            public void onFailure() {
                // Do nothing.
            }
        });
        return _user;
    }

    /**
     * Gets a user by the specified id and a listener for success & failure indicators.
     *
     * @param listener The listener to set.
     * @implNote NOTE: Use this method when the main thread needs to be notified about success & failure results.
     */
    public void get(int id, OnGetCompleteListener<User> listener) {
        _modelFirebase.get(id, new OnGetCompleteListener<User>() {
            @Override
            public void onSuccess(User user) {
                _user.setValue(user);
                listener.onSuccess(user);
            }

            @Override
            public void onFailure() {
                listener.onFailure();
            }
        });
    }

    /**
     * Updates the specified user and notifies the listener on complete.
     *
     * @param user     The user to update.
     * @param listener The listener to set.
     */
    public void update(User user, OnCompleteListener listener) {
        _modelFirebase.update(user, listener);
    }

    /**
     * Deletes the specified user and notifies the listener on complete.
     *
     * @param user     The user to delete.
     * @param listener The listener to set.
     */
    public void delete(User user, OnCompleteListener listener) {
        _modelFirebase.delete(user, listener);
    }

    //endregion

}
