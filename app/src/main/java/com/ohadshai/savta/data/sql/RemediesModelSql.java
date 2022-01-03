package com.ohadshai.savta.data.sql;

import android.os.Handler;
import android.os.Looper;

import androidx.core.os.HandlerCompat;
import androidx.lifecycle.LiveData;

import com.ohadshai.savta.data.utils.OnCompleteListener;
import com.ohadshai.savta.entities.Remedy;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Represents the data access to the local SQL database, related to Remedies.
 */
public class RemediesModelSql {
    private final Executor executor = Executors.newFixedThreadPool(1);
    private final Handler mainThread = HandlerCompat.createAsync(Looper.getMainLooper());

    //region Public API

    public void insert(Remedy remedy, OnCompleteListener listener) {
        executor.execute(() -> {
            AppLocalDb.db.remediesDao().insert(remedy);
            mainThread.post(() -> {
                if (listener != null) {
                    listener.onSuccess();
                }
            });
        });
    }

    public LiveData<Remedy> get(String id) {
        return AppLocalDb.db.remediesDao().get(id);
    }

    public LiveData<List<Remedy>> getAll() {
        return AppLocalDb.db.remediesDao().getAll();
    }

    public void update(Remedy remedy, OnCompleteListener listener) {
        executor.execute(() -> {
            AppLocalDb.db.remediesDao().insert(remedy);
            mainThread.post(() -> {
                if (listener != null) {
                    listener.onSuccess();
                }
            });
        });
    }

    public void delete(Remedy remedy, OnCompleteListener listener) {
        executor.execute(() -> {
            AppLocalDb.db.remediesDao().delete(remedy);
            mainThread.post(() -> {
                if (listener != null) {
                    listener.onSuccess();
                }
            });
        });
    }

    //endregion

}
