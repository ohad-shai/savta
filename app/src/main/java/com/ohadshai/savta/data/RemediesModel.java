package com.ohadshai.savta.data;

import com.ohadshai.savta.entities.Remedy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents the data access related to Remedies.
 */
public class RemediesModel {
    public static final RemediesModel instance = new RemediesModel();

    private final List<Remedy> _remedies = new ArrayList<>();
    private int _currentId;

    private RemediesModel() {
        // Initializes the list with some data:
        _remedies.add(new Remedy(1, "הדבש של סבתא דבורה", "גרון אדום", "דרך הטיפול כאן", null, new Date()));
        _remedies.add(new Remedy(2, "תה צמחים יפני", "לחץ לפני מבחן", "דרך הטיפול כאן", null, new Date()));
        _remedies.add(new Remedy(3, "ג'ינג'ר ולימון מוסיף המון", "שיעולים חזקים", "דרך הטיפול כאן", null, new Date()));
        _remedies.add(new Remedy(4, "הקרטושקה של בבושקה", "הפרעות עיכול", "דרך הטיפול כאן", null, new Date()));
        _remedies.add(new Remedy(5, "קפה שחור חזק", "נדודי שינה", "דרך הטיפול כאן", null, new Date()));
        _currentId = 5;
    }

    public void create(Remedy remedy) {
        _currentId++;
        remedy.setId(_currentId);
        _remedies.add(remedy);
    }

    public Remedy get(int id) {
        for (Remedy remedy : _remedies) {
            if (remedy.getId() == id)
                return remedy;
        }
        return null;
    }

    public List<Remedy> getAll() {
        return _remedies;
    }

    public boolean update(int id, Remedy remedy) {
        Remedy toUpdate = this.get(id);
        if (toUpdate != null) {
            toUpdate.setName(remedy.getName());
            toUpdate.setProblemDescription(remedy.getProblemDescription());
            toUpdate.setTreatmentDescription(remedy.getTreatmentDescription());
            toUpdate.setImageUrl(remedy.getImageUrl());
            toUpdate.setDatePosted(remedy.getDatePosted());
            return true;
        } else {
            return false;
        }
    }

    public Remedy delete(int id) {
        Remedy toDelete = this.get(id);
        if (toDelete != null) {
            _remedies.remove(toDelete);
            return toDelete;
        } else {
            return null;
        }
    }

}
