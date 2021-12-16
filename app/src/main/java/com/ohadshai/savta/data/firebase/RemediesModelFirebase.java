package com.ohadshai.savta.data.firebase;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ohadshai.savta.data.utils.OnCompleteListener;
import com.ohadshai.savta.data.utils.OnGetCompleteListener;
import com.ohadshai.savta.entities.Remedy;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the data access to the firebase remote database, related to Remedies.
 */
public class RemediesModelFirebase {

    //region Constants

    public static final String COLLECTION_NAME = "remedies";
    public static final String FIELD_ID = "id";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_PROBLEM_DESC = "problem_desc";
    public static final String FIELD_TREATMENT_DESC = "treatment_desc";
    public static final String FIELD_IMAGE_URL = "image_url";
    public static final String FIELD_USER_POSTED = "user_posted";
    public static final String FIELD_DATE_POSTED = "date_posted";
    public static final String FIELD_DATE_LAST_UPDATED = "date_last_updated";

    //endregion

    //region Public API

    public void create(Remedy remedy, OnCompleteListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> data = this.mapRemedyToDocument(remedy, true);

        db.collection(COLLECTION_NAME)
                .document(String.valueOf(remedy.getId()))
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        listener.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onFailure();
                    }
                });
    }

    public void get(int id, OnGetCompleteListener<Remedy> listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(COLLECTION_NAME)
                .document(String.valueOf(id))
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot document) {
                        if (document.exists()) {
                            Remedy remedy = document.toObject(Remedy.class);
                            listener.onSuccess(remedy);
                        } else {
                            listener.onFailure();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onFailure();
                    }
                });
    }

    public void getAll(long lastUpdateDate, OnGetCompleteListener<List<Remedy>> listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Timestamp ts = new Timestamp(new Date((lastUpdateDate)));

        db.collection(COLLECTION_NAME)
                .whereGreaterThanOrEqualTo(FIELD_DATE_LAST_UPDATED, ts)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documents) {
                        List<Remedy> remedies = new ArrayList<>();
                        for (QueryDocumentSnapshot document : documents) {
                            Remedy remedy = document.toObject(Remedy.class);
                            remedies.add(remedy);
                        }
                        listener.onSuccess(remedies);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onFailure();
                    }
                });
    }

    public void update(Remedy remedy, OnCompleteListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> data = this.mapRemedyToDocument(remedy, false);

        db.collection(COLLECTION_NAME)
                .document(String.valueOf(remedy.getId()))
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        listener.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onFailure();
                    }
                });
    }

    public void delete(Remedy remedy, OnCompleteListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(COLLECTION_NAME)
                .document(String.valueOf(remedy.getId()))
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        listener.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onFailure();
                    }
                });
    }

    //endregion

    //region Private Methods

    private Map<String, Object> mapRemedyToDocument(Remedy remedy, boolean isCreate) {
        Map<String, Object> map = new HashMap<>();
        map.put(FIELD_ID, remedy.getId());
        map.put(FIELD_NAME, remedy.getName());
        map.put(FIELD_PROBLEM_DESC, remedy.getProblemDescription());
        map.put(FIELD_TREATMENT_DESC, remedy.getTreatmentDescription());
        map.put(FIELD_IMAGE_URL, remedy.getImageUrl());
        map.put(FIELD_USER_POSTED, remedy.getUserPosted());
        if (isCreate) {
            map.put(FIELD_DATE_POSTED, FieldValue.serverTimestamp());
        } else {
            map.put(FIELD_DATE_POSTED, remedy.getDatePosted());
        }
        map.put(FIELD_DATE_LAST_UPDATED, FieldValue.serverTimestamp());
        return map;
    }

    private Remedy parseRemedyFromDocument(DocumentSnapshot document) {
        Remedy remedy = new Remedy();
        remedy.setId(document.getLong(FIELD_ID).intValue());
        remedy.setName(document.getString(FIELD_NAME));
        remedy.setProblemDescription(document.getString(FIELD_PROBLEM_DESC));
        remedy.setTreatmentDescription(document.getString(FIELD_TREATMENT_DESC));
        remedy.setImageUrl(document.getString(FIELD_IMAGE_URL));
        //remedy.setUserPosted(document.get(FIELD_USER_POSTED)); // TODO
        remedy.setDatePosted(document.getDate(FIELD_DATE_POSTED));
        remedy.setDateLastUpdated(document.getDate(FIELD_DATE_LAST_UPDATED));
        return remedy;
    }

    //endregion

}
