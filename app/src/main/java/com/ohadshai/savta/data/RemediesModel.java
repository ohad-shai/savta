package com.ohadshai.savta.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ohadshai.savta.data.firebase.RemediesModelFirebase;
import com.ohadshai.savta.data.sql.RemediesModelSql;
import com.ohadshai.savta.data.utils.OnCompleteListener;
import com.ohadshai.savta.data.utils.OnGetCompleteListener;
import com.ohadshai.savta.entities.Remedy;

import java.util.List;

/**
 * Represents the data access to both SQL (local) database and Firebase (remote) database, related to Remedies.
 */
public class RemediesModel {
    public static final RemediesModel instance = new RemediesModel();

    private final RemediesModelSql _modelSql;
    private final RemediesModelFirebase _modelFirebase;

    //region [LiveData] Members

    MutableLiveData<List<Remedy>> _remediesList = new MutableLiveData<>();
    MutableLiveData<Remedy> _remedy = new MutableLiveData<>();

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
        _modelFirebase.get(id, new OnGetCompleteListener<Remedy>() {
            @Override
            public void onSuccess(Remedy remedy) {
                _remedy.setValue(remedy);
            }

            @Override
            public void onFailure() {
                // Do nothing.
            }
        });
        return _remedy;
    }

    /**
     * Gets a remedy by the specified id and a listener for success & failure indicators.
     *
     * @param listener The listener to set.
     * @implNote NOTE: Use this method when the main thread needs to be notified about success & failure results.
     */
    public void get(int id, OnGetCompleteListener<Remedy> listener) {
        _modelFirebase.get(id, new OnGetCompleteListener<Remedy>() {
            @Override
            public void onSuccess(Remedy remedy) {
                _remedy.setValue(remedy);
                listener.onSuccess(remedy);
            }

            @Override
            public void onFailure() {
                listener.onFailure();
            }
        });
    }

    /**
     * Gets the list of remedies, and returns the LiveData object, in order to listen to data updates.
     *
     * @return Returns the LiveData object for the list of remedies.
     * @implNote NOTE: The main thread will never know when the updates will occur, so it is used mainly for listening (to data updates).
     */
    public LiveData<List<Remedy>> getAll() {
        _modelFirebase.getAll(new OnGetCompleteListener<List<Remedy>>() {
            @Override
            public void onSuccess(List<Remedy> remedies) {
                _remediesList.setValue(remedies);
            }

            @Override
            public void onFailure() {
                // Do nothing.
            }
        });
        return _remediesList;
    }

    /**
     * Gets the list of remedies by a listener for success & failure indicators.
     *
     * @param listener The listener to set.
     * @implNote NOTE: Use this method when the main thread needs to be notified about success & failure results.
     */
    public void getAll(OnGetCompleteListener<List<Remedy>> listener) {
        _modelFirebase.getAll(new OnGetCompleteListener<List<Remedy>>() {
            @Override
            public void onSuccess(List<Remedy> remedies) {
                _remediesList.setValue(remedies);
                listener.onSuccess(remedies);
            }

            @Override
            public void onFailure() {
                listener.onFailure();
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
