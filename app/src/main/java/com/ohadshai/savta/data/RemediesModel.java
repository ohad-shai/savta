package com.ohadshai.savta.data;

import com.ohadshai.savta.entities.Remedy;
import com.ohadshai.savta.entities.User;

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
        User user = new User(1, "אוהד", "שי", "ohad@gmail.com", "1234", new Date());
        _remedies.add(new Remedy(1, "הדבש של סבתא דבורה", "גרון אדום", "דרך הטיפול כאן", "https://cdn.pixabay.com/photo/2017/05/11/12/24/nuns-2304009__340.jpg", user, new Date()));
        _remedies.add(new Remedy(2, "תה צמחים יפני", "לחץ לפני מבחן", "דרך הטיפול כאן", "https://cdn.pixabay.com/photo/2016/05/31/11/24/natural-medicine-1426647__340.jpg", user, new Date()));
        _remedies.add(new Remedy(3, "ג'ינג'ר ולימון מוסיף המון", "שיעולים חזקים", "בקלחת לערבב ג'ינג'ר, מיץ לימון, מים ודבש. להביא לידי רתיחה, להסיר מהאש, להוסיף את עלי הנענע, לכסות ולהניח בצד 30 דקות. לסנן את הנוזל ולחממו כשרוצים לשתות.", "https://cdn.pixabay.com/photo/2019/12/19/00/21/ginger-4705132__340.jpg", user, new Date()));
        _remedies.add(new Remedy(4, "הקרטושקה של בבושקה", "הפרעות עיכול", "דרך הטיפול כאן", "https://cdn.pixabay.com/photo/2014/08/06/20/32/potatoes-411975__340.jpg", user, new Date()));
        _remedies.add(new Remedy(5, "קפה שחור חזק", "נדודי שינה", "דרך הטיפול כאן", "https://cdn.pixabay.com/photo/2018/08/07/09/46/coffee-3589438__340.jpg", user, new Date()));
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
