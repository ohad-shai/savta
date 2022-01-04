package com.ohadshai.savta.data;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;

import com.ohadshai.savta.data.firebase.RemediesModelFirebase;
import com.ohadshai.savta.data.sql.RemediesModelSql;
import com.ohadshai.savta.data.utils.OnCompleteListener;
import com.ohadshai.savta.data.utils.OnGetCompleteListener;
import com.ohadshai.savta.entities.Remedy;
import com.ohadshai.savta.utils.ApplicationContext;
import com.ohadshai.savta.utils.SharedPreferencesConsts;

import java.util.List;

/**
 * Represents the data access to both SQL (local) database and Firebase (remote) database, related to Remedies.
 */
public class RemediesModel {
    private static final RemediesModel _instance = new RemediesModel();
    private final RemediesModelSql _modelSql;
    private final RemediesModelFirebase _modelFirebase;

    LiveData<Remedy> _remedy;
    LiveData<List<Remedy>> _remediesList;
    LiveData<List<Remedy>> _userRemediesList;

    private RemediesModel() {
        _modelSql = new RemediesModelSql();
        _modelFirebase = new RemediesModelFirebase();
    }

    public static RemediesModel getInstance() {
        return _instance;
    }

    //region Public API

    /**
     * Clears all the live data members.
     */
    public void clearAllLiveData() {
        _remedy = null;
        _remediesList = null;
        _userRemediesList = null;
    }

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
     * @param id The id of the remedy to get.
     * @return Returns the LiveData object for the remedy.
     * @implNote NOTE: The main thread will never know when the updates will occur, so it is used mainly for listening (to data updates).
     */
    public LiveData<Remedy> get(String id) {
        if (_remedy == null) {
            _remedy = _modelSql.get(id);
            this.get(id, null);
        }
        return _remedy;
    }

    /**
     * Gets a remedy by the specified id and a listener for success & failure indicators.
     *
     * @param id       The id of the remedy to get.
     * @param listener The listener to set.
     * @implNote NOTE: Use this method when the main thread needs to be notified about success & failure results.
     */
    public void get(String id, OnCompleteListener listener) {
        // TODO
    }

    /**
     * Gets the list of remedies (from local DB and the cloud), and returns the LiveData object, in order to listen to data updates.
     *
     * @return Returns the LiveData object for the list of remedies.
     * @apiNote NOTE: Used mainly for listening (to data updates), because the main thread will never know when the updates will occur.
     * @implNote NOTE: This gets the data first from the local DB, and then from the cloud.
     */
    public LiveData<List<Remedy>> getAll() {
        if (_remediesList == null) {
            _remediesList = _modelSql.getAll();
            this.refreshGetAll(null);
        }
        return _remediesList;
    }

    /**
     * Gets the list of remedies (from the cloud) by a listener for success & failure indicators.
     *
     * @param listener The listener to set.
     * @apiNote NOTE: Use this method when the main thread needs to be notified about success & failure results.
     * @implNote NOTE: This gets the data only from the cloud.
     */
    public void refreshGetAll(OnCompleteListener listener) {
        // (1) Gets the local last update date indicator:
        SharedPreferences sharedPreferences = ApplicationContext.getContext().getSharedPreferences(SharedPreferencesConsts.REMEDIES_LAST_UPDATE_DATE, Context.MODE_PRIVATE);
        long lastUpdateDate = sharedPreferences.getLong(SharedPreferencesConsts.REMEDIES_LAST_UPDATE_DATE, 0);

        // (2) Gets all the updated records after the last update date, from Firebase:
        _modelFirebase.getAll(lastUpdateDate, new OnGetCompleteListener<List<Remedy>>() {
            @Override
            public void onSuccess(List<Remedy> remedies) {
                // (3) Updates the local SQL DB with the updated records:
                long highestDate = 0;
                for (Remedy remedy : remedies) {
                    _modelSql.insert(remedy, null);
                    if (remedy.getDateLastUpdated().getTime() > highestDate) {
                        highestDate = remedy.getDateLastUpdated().getTime();
                    }
                }

                // (4) Updates the local last update date indicator:
                SharedPreferences.Editor spEditor = sharedPreferences.edit();
                spEditor.putLong(SharedPreferencesConsts.REMEDIES_LAST_UPDATE_DATE, highestDate);
                spEditor.apply();

                // (5) Returns the data to the listeners:
                if (listener != null) {
                    listener.onSuccess();
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
     * Gets the list of remedies a user posted (from local DB and the cloud), and returns the LiveData object, in order to listen to data updates.
     *
     * @param userId The id of the user to get all the remedies posted.
     * @return Returns the LiveData object for the list of remedies.
     * @apiNote NOTE: Used mainly for listening (to data updates), because the main thread will never know when the updates will occur.
     * @implNote NOTE: This gets the data first from the local DB, and then from the cloud.
     */
    public LiveData<List<Remedy>> getAllByUser(String userId) {
        if (_userRemediesList == null) {
            _userRemediesList = _modelSql.getAllByUser(userId);
            this.refreshGetAllByUser(userId, null);
        }
        return _userRemediesList;
    }

    /**
     * Gets the list of remedies a user posted (from the cloud) by a listener for success & failure indicators.
     *
     * @param userId   The id of the user to get all the remedies posted.
     * @param listener The listener to set.
     * @apiNote NOTE: Use this method when the main thread needs to be notified about success & failure results.
     * @implNote NOTE: This gets the data only from the cloud.
     */
    public void refreshGetAllByUser(String userId, OnCompleteListener listener) {
        // (1) Gets the local last update date indicator:
        SharedPreferences sharedPreferences = ApplicationContext.getContext().getSharedPreferences(SharedPreferencesConsts.REMEDIES_LAST_UPDATE_DATE, Context.MODE_PRIVATE);
        long lastUpdateDate = sharedPreferences.getLong(SharedPreferencesConsts.REMEDIES_LAST_UPDATE_DATE, 0);

        // (2) Gets all the updated records after the last update date, from Firebase:
        _modelFirebase.getAllByUser(userId, lastUpdateDate, new OnGetCompleteListener<List<Remedy>>() {
            @Override
            public void onSuccess(List<Remedy> remedies) {
                // (3) Updates the local SQL DB with the updated records:
                long highestDate = 0;
                for (Remedy remedy : remedies) {
                    _modelSql.insert(remedy, null);
                    if (remedy.getDateLastUpdated().getTime() > highestDate) {
                        highestDate = remedy.getDateLastUpdated().getTime();
                    }
                }

                // (4) Updates the local last update date indicator:
                SharedPreferences.Editor spEditor = sharedPreferences.edit();
                spEditor.putLong(SharedPreferencesConsts.REMEDIES_LAST_UPDATE_DATE, highestDate);
                spEditor.apply();

                // (5) Returns the data to the listeners:
                if (listener != null) {
                    listener.onSuccess();
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
