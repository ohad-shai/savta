package com.ohadshai.savta.data.sql;

import android.os.AsyncTask;

import com.ohadshai.savta.data.utils.OnCompleteListener;
import com.ohadshai.savta.data.utils.OnGetCompleteListener;
import com.ohadshai.savta.entities.Remedy;

import java.util.List;

/**
 * Represents the data access to the local SQL database, related to Remedies.
 */
public class RemediesModelSql {
    
    public void create(Remedy remedy, OnCompleteListener listener) {
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                AppLocalDb.db.remediesDao().create(remedy);
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

    public void get(int id, OnGetCompleteListener<Remedy> listener) {
        AsyncTask task = new AsyncTask() {
            Remedy remedy;

            @Override
            protected Object doInBackground(Object[] objects) {
                remedy = AppLocalDb.db.remediesDao().get(id);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                listener.onSuccess(remedy);
            }
        };
        task.execute();
    }

    public void getAll(OnGetCompleteListener<List<Remedy>> listener) {
        AsyncTask task = new AsyncTask() {
            List<Remedy> remedies;

            @Override
            protected Object doInBackground(Object[] objects) {
                remedies = AppLocalDb.db.remediesDao().getAll();
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                listener.onSuccess(remedies);
            }
        };
        task.execute();
    }

    public void update(Remedy remedy, OnCompleteListener listener) {
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                AppLocalDb.db.remediesDao().create(remedy);
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

    public void delete(Remedy remedy, OnCompleteListener listener) {
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                AppLocalDb.db.remediesDao().delete(remedy);
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
