package com.ohadshai.savta.data.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
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
    public static final String FIELD_POSTED_BY_USER_ID = "posted_by_user_id";
    public static final String FIELD_POSTED_BY_USER_NAME = "posted_by_user_name";
    public static final String FIELD_DATE_POSTED = "date_posted";
    public static final String FIELD_DATE_LAST_UPDATED = "date_last_updated";

    //endregion

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public RemediesModelFirebase() {
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false)
                .build();
        db.setFirestoreSettings(settings);
    }

    //region Public API

    public void create(Remedy remedy, OnCompleteListener listener) {
        DocumentReference docRef = db.collection(COLLECTION_NAME).document();
        remedy.setId(docRef.getId());
        Map<String, Object> data = this.mapRemedyToDocument(remedy, true);
        docRef.set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        listener.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("RemediesModelFirebase", "create", e);
                        listener.onFailure();
                    }
                });
    }

    public void get(int id, OnGetCompleteListener<Remedy> listener) {
        db.collection(COLLECTION_NAME)
                .document(String.valueOf(id))
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot document) {
                        if (document.exists()) {
                            Remedy remedy = parseRemedyFromDocument(document);
                            listener.onSuccess(remedy);
                        } else {
                            listener.onFailure();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("RemediesModelFirebase", "get", e);
                        listener.onFailure();
                    }
                });
    }

    public void getAll(long lastUpdateDate, OnGetCompleteListener<List<Remedy>> listener) {
        db.collection(COLLECTION_NAME)
                .whereGreaterThan(FIELD_DATE_LAST_UPDATED, new Timestamp(new Date(lastUpdateDate)))
                .orderBy(FIELD_DATE_LAST_UPDATED, Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documents) {
                        List<Remedy> remedies = new ArrayList<>();
                        for (QueryDocumentSnapshot document : documents) {
                            Remedy remedy = parseRemedyFromDocument(document);
                            remedies.add(remedy);
                        }
                        listener.onSuccess(remedies);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("RemediesModelFirebase", "getAll", e);
                        listener.onFailure();
                    }
                });
    }

    public void getAllByUser(String userId, long lastUpdateDate, OnGetCompleteListener<List<Remedy>> listener) {
        db.collection(COLLECTION_NAME)
                .whereEqualTo(FIELD_POSTED_BY_USER_ID, userId)
                .whereGreaterThan(FIELD_DATE_LAST_UPDATED, new Timestamp(new Date(lastUpdateDate)))
                .orderBy(FIELD_DATE_LAST_UPDATED, Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documents) {
                        List<Remedy> remedies = new ArrayList<>();
                        for (QueryDocumentSnapshot document : documents) {
                            Remedy remedy = parseRemedyFromDocument(document);
                            remedies.add(remedy);
                        }
                        listener.onSuccess(remedies);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("RemediesModelFirebase", "getAll", e);
                        listener.onFailure();
                    }
                });
    }

    public void update(Remedy remedy, OnCompleteListener listener) {
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
                        Log.e("RemediesModelFirebase", "update", e);
                        listener.onFailure();
                    }
                });
    }

    public void delete(Remedy remedy, OnCompleteListener listener) {
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
                        Log.e("RemediesModelFirebase", "delete", e);
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
        map.put(FIELD_POSTED_BY_USER_ID, remedy.getPostedByUserId());
        map.put(FIELD_POSTED_BY_USER_NAME, remedy.getPostedByUserName());
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
        remedy.setId(document.getString(FIELD_ID));
        remedy.setName(document.getString(FIELD_NAME));
        remedy.setProblemDescription(document.getString(FIELD_PROBLEM_DESC));
        remedy.setTreatmentDescription(document.getString(FIELD_TREATMENT_DESC));
        remedy.setImageUrl(document.getString(FIELD_IMAGE_URL));
        remedy.setPostedByUserId(document.getString(FIELD_POSTED_BY_USER_ID));
        remedy.setPostedByUserName(document.getString(FIELD_POSTED_BY_USER_NAME));
        remedy.setDatePosted(document.getDate(FIELD_DATE_POSTED));
        remedy.setDateLastUpdated(document.getDate(FIELD_DATE_LAST_UPDATED));
        return remedy;
    }

    //endregion

}
