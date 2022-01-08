package com.ohadshai.savta.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;

import com.ohadshai.savta.data.firebase.RemediesModelFirebase;
import com.ohadshai.savta.data.sql.RemediesModelSql;
import com.ohadshai.savta.data.utils.OnCompleteListener;
import com.ohadshai.savta.data.utils.OnGetCompleteListener;
import com.ohadshai.savta.data.utils.OnImageUploadCompleteListener;
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
     * @apiNote NOTE: Used mainly for listening (to data updates), because the main thread will never know when the updates will occur.
     * @implNote NOTE: This gets the data first from the local DB, and then from the cloud.
     */
    public LiveData<Remedy> get(String id) {
        if (_remedy == null || _remedy.getValue() == null || !_remedy.getValue().getId().equals(id)) {
            _remedy = _modelSql.get(id);
            this.refreshGet(id, null);
        }
        return _remedy;
    }

    /**
     * Gets a remedy by the specified id (from the cloud).
     *
     * @param id       The id of the remedy to get.
     * @param listener The listener to set.
     * @apiNote NOTE: Use this method when it is needed to refresh the data from the cloud.
     */
    public void refreshGet(String id, OnCompleteListener listener) {
        // (1) Gets the local last update date indicator:
        SharedPreferences sharedPreferences = ApplicationContext.getContext().getSharedPreferences(SharedPreferencesConsts.REMEDIES_LAST_UPDATE_DATE, Context.MODE_PRIVATE);
        long lastUpdateDate = sharedPreferences.getLong(SharedPreferencesConsts.REMEDIES_LAST_UPDATE_DATE, 0);

        // (2) Gets the record from Firebase, only if it's updated (higher than the last update date):
        _modelFirebase.get(id, lastUpdateDate, new OnGetCompleteListener<Remedy>() {
            @Override
            public void onSuccess(Remedy remedy) {
                // If we get a remedy then it means we need to update our local DB:
                if (remedy != null) {
                    // (3) Updates the local SQL DB with the updated records, performs insert or delete if marked as deleted:
                    long highestDate = lastUpdateDate;
                    if (remedy.getDateDeleted() != null) {
                        _modelSql.delete(remedy.getId(), null);
                    } else {
                        _modelSql.insert(remedy, null);
                    }
                    if (remedy.getDateLastUpdated().getTime() > highestDate) {
                        highestDate = remedy.getDateLastUpdated().getTime();
                    }

                    // (4) Updates the local last update date indicator:
                    if (highestDate > lastUpdateDate) {
                        SharedPreferences.Editor spEditor = sharedPreferences.edit();
                        spEditor.putLong(SharedPreferencesConsts.REMEDIES_LAST_UPDATE_DATE, highestDate);
                        spEditor.apply();
                    }
                }
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
     * Gets the list of remedies (from the cloud).
     *
     * @param listener The listener to set.
     * @apiNote NOTE: Use this method when it is needed to refresh the data from the cloud.
     */
    public void refreshGetAll(OnCompleteListener listener) {
        // (1) Gets the local last update date indicator:
        SharedPreferences sharedPreferences = ApplicationContext.getContext().getSharedPreferences(SharedPreferencesConsts.REMEDIES_LAST_UPDATE_DATE, Context.MODE_PRIVATE);
        long lastUpdateDate = sharedPreferences.getLong(SharedPreferencesConsts.REMEDIES_LAST_UPDATE_DATE, 0);

        // (2) Gets all the updated records after the last update date, from Firebase:
        _modelFirebase.getAll(lastUpdateDate, new OnGetCompleteListener<List<Remedy>>() {
            @Override
            public void onSuccess(List<Remedy> remedies) {
                // (3) Updates the local SQL DB with the updated records, performs insert or delete if marked as deleted:
                long highestDate = lastUpdateDate;
                for (Remedy remedy : remedies) {
                    if (remedy.getDateDeleted() != null) {
                        _modelSql.delete(remedy.getId(), null);
                    } else {
                        _modelSql.insert(remedy, null);
                    }
                    if (remedy.getDateLastUpdated().getTime() > highestDate) {
                        highestDate = remedy.getDateLastUpdated().getTime();
                    }
                }

                // (4) Updates the local last update date indicator:
                if (highestDate > lastUpdateDate) {
                    SharedPreferences.Editor spEditor = sharedPreferences.edit();
                    spEditor.putLong(SharedPreferencesConsts.REMEDIES_LAST_UPDATE_DATE, highestDate);
                    spEditor.apply();
                }

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
                // (3) Updates the local SQL DB with the updated records, performs insert or delete if marked as deleted:
                long highestDate = 0;
                for (Remedy remedy : remedies) {
                    if (remedy.getDateDeleted() != null) {
                        _modelSql.delete(remedy.getId(), null);
                    } else {
                        _modelSql.insert(remedy, null);
                    }
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
     * @param id       The id of the remedy to delete.
     * @param listener The listener to set.
     */
    public void delete(String id, OnCompleteListener listener) {
        _modelFirebase.delete(id, listener);
    }

    /**
     * Deletes all the remedies that the specified user posted and notifies the listener on complete.
     *
     * @param userId   The id of the user to delete all the remedies.
     * @param listener The listener to set.
     */
    public void deleteAllByUser(String userId, OnCompleteListener listener) {
        // Performs local delete:
        _modelSql.deleteAllByUser(userId, new OnCompleteListener() {
            @Override
            public void onSuccess() {
                // Performs cloud delete:
                _modelFirebase.deleteAllByUser(userId, listener);
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
     * Uploads a remedy image to the Firebase Storage.
     *
     * @param bitmap   The image bitmap to upload.
     * @param listener The listener to set.
     */
    public void uploadRemedyImage(Bitmap bitmap, OnImageUploadCompleteListener listener) {
        _modelFirebase.uploadRemedyImage(bitmap, listener);
    }

    //endregion

}
