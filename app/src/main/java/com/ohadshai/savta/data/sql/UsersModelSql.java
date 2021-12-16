package com.ohadshai.savta.data.sql;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.ohadshai.savta.data.utils.OnCompleteListener;
import com.ohadshai.savta.data.utils.OnGetCompleteListener;
import com.ohadshai.savta.entities.User;

/**
 * Represents the data access to the local SQL database, related to Users.
 */
public class UsersModelSql {

    public void register(User user, OnCompleteListener listener) {
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                AppLocalDb.db.usersDao().register(user);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                listener.onSuccess();
            }
        };
        task.execute();
    }

    public void login(int userId, String password, OnGetCompleteListener<User> listener) {
        AsyncTask task = new AsyncTask() {
            User user;

            @Override
            protected Object doInBackground(Object[] objects) {
                user = AppLocalDb.db.usersDao().login(userId, password);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                listener.onSuccess(user);
            }
        };
        task.execute();
    }

    public LiveData<User> get(int id) {
        return AppLocalDb.db.usersDao().get(id);
    }

    public void update(User user, OnCompleteListener listener) {
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                AppLocalDb.db.usersDao().update(user);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                listener.onSuccess();
            }
        };
        task.execute();
    }

    public void delete(User user, OnCompleteListener listener) {
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                AppLocalDb.db.usersDao().delete(user);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                listener.onSuccess();
            }
        };
        task.execute();
    }

}
