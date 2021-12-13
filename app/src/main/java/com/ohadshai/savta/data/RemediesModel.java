package com.ohadshai.savta.data;

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

    private RemediesModel() {
        _modelSql = new RemediesModelSql();
        _modelFirebase = new RemediesModelFirebase();
    }

    public void create(Remedy remedy, OnCompleteListener listener) {
        _modelFirebase.create(remedy, listener);
    }

    public void get(int id, OnGetCompleteListener<Remedy> listener) {
        _modelFirebase.get(id, listener);
    }

    public void getAll(OnGetCompleteListener<List<Remedy>> listener) {
        _modelFirebase.getAll(listener);
    }

    public void update(Remedy remedy, OnCompleteListener listener) {
        _modelFirebase.update(remedy, listener);
    }

    public void delete(Remedy remedy, OnCompleteListener listener) {
        _modelFirebase.delete(remedy, listener);
    }

}
