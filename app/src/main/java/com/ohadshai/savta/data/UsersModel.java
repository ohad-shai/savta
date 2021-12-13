package com.ohadshai.savta.data;

import com.ohadshai.savta.data.firebase.UsersModelFirebase;
import com.ohadshai.savta.data.sql.RemediesModelSql;
import com.ohadshai.savta.data.sql.UsersModelSql;
import com.ohadshai.savta.data.utils.OnCompleteListener;
import com.ohadshai.savta.data.utils.OnGetCompleteListener;
import com.ohadshai.savta.entities.User;

/**
 * Represents the data access to both SQL (local) database and Firebase (remote) database, related to Users.
 */
public class UsersModel {
    public static final UsersModel instance = new UsersModel();

    private final UsersModelSql _modelSql;
    private final UsersModelFirebase _modelFirebase;

    private UsersModel() {
        _modelSql = new UsersModelSql();
        _modelFirebase = new UsersModelFirebase();
    }

    public void register(User user, OnCompleteListener listener) {
        _modelFirebase.register(user, listener);
    }

    public void login(int userId, String password, OnGetCompleteListener<User> listener) {
        _modelFirebase.login(userId, password, listener);
    }

    public void get(int id, OnGetCompleteListener<User> listener) {
        _modelFirebase.get(id, listener);
    }

    public void update(User user, OnCompleteListener listener) {
        _modelFirebase.update(user, listener);
    }

    public void delete(User user, OnCompleteListener listener) {
        _modelFirebase.delete(user, listener);
    }

}
