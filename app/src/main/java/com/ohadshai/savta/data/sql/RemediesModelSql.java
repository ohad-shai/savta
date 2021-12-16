package com.ohadshai.savta.data.sql;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.ohadshai.savta.data.utils.OnCompleteListener;
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

    public LiveData<Remedy> get(int id) {
        return AppLocalDb.db.remediesDao().get(id);
    }

    public LiveData<List<Remedy>> getAll() {
        return AppLocalDb.db.remediesDao().getAll();
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
