package com.ohadshai.savta.data.firebase;

import com.ohadshai.savta.data.utils.OnCompleteListener;
import com.ohadshai.savta.data.utils.OnGetCompleteListener;
import com.ohadshai.savta.entities.User;

/**
 * Represents the data access to the firebase remote database, related to Users.
 */
public class UsersModelFirebase {

    public void register(User user, OnCompleteListener listener) {
    }

    public void login(int userId, String password, OnGetCompleteListener<User> listener) {
    }

    public void get(int id, OnGetCompleteListener<User> listener) {
    }

    public void update(User user, OnCompleteListener listener) {
    }

    public void delete(User user, OnCompleteListener listener) {
    }

}
