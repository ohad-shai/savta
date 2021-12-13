package com.ohadshai.savta.data.firebase;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ohadshai.savta.data.utils.OnCompleteListener;
import com.ohadshai.savta.data.utils.OnGetCompleteListener;
import com.ohadshai.savta.entities.Remedy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the data access to the firebase remote database, related to Remedies.
 */
public class RemediesModelFirebase {

    public void create(Remedy remedy, OnCompleteListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> data = new HashMap<>();
        data.put("name", remedy.getName());
        data.put("problem_desc", remedy.getProblemDescription());
        data.put("treatment_desc", remedy.getTreatmentDescription());
        data.put("image_url", remedy.getImageUrl());

        db.collection("remedies")
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    public void get(int id, OnGetCompleteListener<Remedy> listener) {

    }

    public void getAll(OnGetCompleteListener<List<Remedy>> listener) {
    }

    public void update(Remedy remedy, OnCompleteListener listener) {
    }

    public void delete(Remedy remedy, OnCompleteListener listener) {
    }

}
