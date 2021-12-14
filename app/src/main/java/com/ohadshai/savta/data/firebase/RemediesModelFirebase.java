package com.ohadshai.savta.data.firebase;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ohadshai.savta.data.utils.OnCompleteListener;
import com.ohadshai.savta.data.utils.OnGetCompleteListener;
import com.ohadshai.savta.entities.Remedy;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the data access to the firebase remote database, related to Remedies.
 */
public class RemediesModelFirebase {

    //region Constants

    public static final String COLLECTION_NAME = "remedies";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_PROBLEM_DESC = "problem_desc";
    public static final String FIELD_TREATMENT_DESC = "treatment_desc";
    public static final String FIELD_IMAGE_URL = "image_url";
    public static final String FIELD_USER_POSTED = "user_posted";
    public static final String FIELD_DATE_POSTED = "date_posted";

    //endregion

    //region Public API

    public void create(Remedy remedy, OnCompleteListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

//        Map<String, Object> data = new HashMap<>();
//        data.put(FIELD_NAME, remedy.getName());
//        data.put(FIELD_PROBLEM_DESC, remedy.getProblemDescription());
//        data.put(FIELD_TREATMENT_DESC, remedy.getTreatmentDescription());
//        data.put(FIELD_IMAGE_URL, remedy.getImageUrl());
//        data.put(FIELD_USER_POSTED, remedy.getUserPosted());
//        data.put(FIELD_DATE_POSTED, remedy.getDatePosted());

        db.collection(COLLECTION_NAME)
                .document(String.valueOf(remedy.getId()))
                .set(remedy)
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

    public void getAll(OnGetCompleteListener<List<Remedy>> listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(COLLECTION_NAME)
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
        // Same as create, this updates the document if exist:
        this.create(remedy, listener);
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

//    private Remedy parseRemedyFromDocument(DocumentSnapshot document) {
//        Remedy remedy = new Remedy();
//        remedy.setId(Integer.parseInt(document.getId()));
//        remedy.setName(document.getString(FIELD_NAME));
//        remedy.setProblemDescription(document.getString(FIELD_PROBLEM_DESC));
//        remedy.setTreatmentDescription(document.getString(FIELD_TREATMENT_DESC));
//        remedy.setImageUrl(document.getString(FIELD_IMAGE_URL));
//        //remedy.setUserPosted(document.get(FIELD_USER_POSTED));
//        remedy.setDatePosted(new Date(document.getLong(FIELD_DATE_POSTED)));
//        return remedy;
//    }

    //endregion

}
