package com.ohadshai.savta.data;

import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;

import com.ohadshai.savta.data.firebase.RemediesModelFirebase;
import com.ohadshai.savta.data.sql.RemediesModelSql;
import com.ohadshai.savta.data.utils.OnCompleteListener;
import com.ohadshai.savta.data.utils.OnGetCompleteListener;
import com.ohadshai.savta.entities.Remedy;
import com.ohadshai.savta.utils.ApplicationContext;
import com.ohadshai.savta.utils.SharedPreferencesUtils;

import java.util.List;

/**
 * Represents the data access to both SQL (local) database and Firebase (remote) database, related to Remedies.
 */
public class RemediesModel {
    public static final RemediesModel instance = new RemediesModel();

    private final RemediesModelSql _modelSql;
    private final RemediesModelFirebase _modelFirebase;

    //region [LiveData] Members

    LiveData<List<Remedy>> _remediesList;
    LiveData<Remedy> _remedy;

    //endregion

    private RemediesModel() {
        _modelSql = new RemediesModelSql();
        _modelFirebase = new RemediesModelFirebase();
    }

    //region Public API

    /**
     * Creates the specified remedy and notifies the listener on complete.
     *
     * @param remedy   The remedy to create.
     * @param listener The listener to set.
     */
    public void create(Remedy remedy, OnCompleteListener listener) {
        _modelFirebase.create(remedy, listener);
    }

    /**
     * Gets a remedy by the specified id, and returns the LiveData object, in order to listen to data updates.
     *
     * @return Returns the LiveData object for the remedy.
     * @implNote NOTE: The main thread will never know when the updates will occur, so it is used mainly for listening (to data updates).
     */
    public LiveData<Remedy> get(int id) {
        if (_remedy == null) {
            _remedy = _modelSql.get(id);
            this.get(id, null);
        }
        return _remedy;
    }

    /**
     * Gets a remedy by the specified id and a listener for success & failure indicators.
     *
     * @param listener The listener to set.
     * @implNote NOTE: Use this method when the main thread needs to be notified about success & failure results.
     */
    public void get(int id, OnGetCompleteListener<Remedy> listener) {

    }

    /**
     * Gets the list of remedies, and returns the LiveData object, in order to listen to data updates.
     *
     * @return Returns the LiveData object for the list of remedies.
     * @implNote NOTE: The main thread will never know when the updates will occur, so it is used mainly for listening (to data updates).
     */
    public LiveData<List<Remedy>> getAll() {
        if (_remediesList == null) {
            //_remediesList = _modelSql.getAll();
            this.getAll(null);
        }
        return _remediesList;
    }

    /**
     * Gets the list of remedies by a listener for success & failure indicators.
     *
     * @param listener The listener to set.
     * @implNote NOTE: Use this method when the main thread needs to be notified about success & failure results.
     */
    public void getAll(OnGetCompleteListener<List<Remedy>> listener) {
        // 1. Gets the local last update date indicator:
        SharedPreferences sharedPreferences = ApplicationContext.getContext().getSharedPreferences(SharedPreferencesUtils.REMEDIES_LAST_UPDATE_DATE, 0);
        long lastUpdateDate = sharedPreferences.getLong(SharedPreferencesUtils.REMEDIES_LAST_UPDATE_DATE, 0);

        // 2. Gets all the updated records after the last update date, from Firebase:
        _modelFirebase.getAll(lastUpdateDate, new OnGetCompleteListener<List<Remedy>>() {
            @Override
            public void onSuccess(List<Remedy> remedies) {
                // 3. Updates the local SQL DB with the updated records:
                long highestDate = 0;
                for (Remedy remedy : remedies) {
                    _modelSql.create(remedy, null);
                    if (remedy.getDateLastUpdated().getTime() > highestDate) {
                        highestDate = remedy.getDateLastUpdated().getTime();
                    }
                }

                // 4. Updates the local last update date indicator:
                SharedPreferences.Editor spEditor = sharedPreferences.edit();
                spEditor.putLong(SharedPreferencesUtils.REMEDIES_LAST_UPDATE_DATE, highestDate);
                spEditor.apply();

                // 5. Returns the data to the listeners:
                if (listener != null) {
                    listener.onSuccess(remedies);
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
     * Updates the specified remedy and notifies the listener on complete.
     *
     * @param remedy   The remedy to update.
     * @param listener The listener to set.
     */
    public void update(Remedy remedy, OnCompleteListener listener) {
        _modelFirebase.update(remedy, listener);
    }

    /**
     * Deletes the specified remedy and notifies the listener on complete.
     *
     * @param remedy   The remedy to delete.
     * @param listener The listener to set.
     */
    public void delete(Remedy remedy, OnCompleteListener listener) {
        _modelFirebase.delete(remedy, listener);
    }

    //endregion

}
