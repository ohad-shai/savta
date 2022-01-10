package com.ohadshai.savta.data.firebase;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ohadshai.savta.data.utils.ImageActionRequest;
import com.ohadshai.savta.data.utils.OnCompleteListener;
import com.ohadshai.savta.data.utils.OnGetCompleteListener;
import com.ohadshai.savta.data.utils.OnImageUploadCompleteListener;
import com.ohadshai.savta.entities.Remedy;
import com.ohadshai.savta.utils.BitmapUtils;

import java.util.ArrayList;
import java.util.Calendar;
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
    public static final String FIELD_IMAGE_FILE_PATH = "image_file_path";
    public static final String FIELD_IMAGE_URL = "image_url";
    public static final String FIELD_POSTED_BY_USER_ID = "posted_by_user_id";
    public static final String FIELD_POSTED_BY_USER_NAME = "posted_by_user_name";
    public static final String FIELD_DATE_POSTED = "date_posted";
    public static final String FIELD_DATE_LAST_UPDATED = "date_last_updated";
    public static final String FIELD_DATE_DELETED = "date_deleted";

    public static final String STORAGE_REMEDY_IMAGES_FOLDER_PATH = "/remedies";
    public static final int STORAGE_REMEDY_IMAGES_MAX_WIDTH = 640;

    //endregion

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final ModelFirebaseStorage _modelFirebaseStorage = new ModelFirebaseStorage();

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
                        if (listener != null) {
                            listener.onSuccess();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("RemediesModelFirebase", "create", e);
                        if (listener != null) {
                            listener.onFailure();
                        }
                    }
                });
    }

    public void createWithImage(Remedy remedy, Bitmap remedyImageBitmap, OnCompleteListener listener) {
        // (1) Uploads the remedy image first:
        this.uploadRemedyImage(remedyImageBitmap, new OnImageUploadCompleteListener() {
            @Override
            public void onSuccess(String imageFilePath, String imageUrl) {
                // (2) Creates the remedy:
                remedy.setImageFilePath(imageFilePath);
                remedy.setImageUrl(imageUrl);
                create(remedy, listener);
            }

            @Override
            public void onFailure() {
                if (listener != null) {
                    listener.onFailure();
                }
            }
        });
    }

    public void get(String id, long lastUpdateDate, OnGetCompleteListener<Remedy> listener) {
        db.collection(COLLECTION_NAME)
                .whereEqualTo(FIELD_ID, id)
                .whereGreaterThan(FIELD_DATE_LAST_UPDATED, new Timestamp(new Date(lastUpdateDate)))
                .limit(1)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documents) {
                        Remedy remedy = null;
                        if (!documents.isEmpty()) {
                            remedy = parseRemedyFromDocument(documents.getDocuments().get(0));
                        }
                        if (listener != null) {
                            listener.onSuccess(remedy);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("RemediesModelFirebase", "get", e);
                        if (listener != null) {
                            listener.onFailure();
                        }
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
                        if (listener != null) {
                            listener.onSuccess(remedies);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("RemediesModelFirebase", "getAll", e);
                        if (listener != null) {
                            listener.onFailure();
                        }
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
                        if (listener != null) {
                            listener.onSuccess(remedies);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("RemediesModelFirebase", "getAll", e);
                        if (listener != null) {
                            listener.onFailure();
                        }
                    }
                });
    }

    public void update(Remedy remedy, OnCompleteListener listener) {
        Map<String, Object> data = this.mapRemedyToDocument(remedy, false);

        db.collection(COLLECTION_NAME)
                .document(remedy.getId())
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        if (listener != null) {
                            listener.onSuccess();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("RemediesModelFirebase", "update", e);
                        if (listener != null) {
                            listener.onFailure();
                        }
                    }
                });
    }

    public void updateWithImage(Remedy remedy, ImageActionRequest imageActionRequest, Bitmap remedyImageBitmap, OnCompleteListener listener) {
        // Image None:
        if (imageActionRequest == ImageActionRequest.NONE) {
            // Just updates the remedy without touching the image:
            update(remedy, listener);
        }
        // Image Create:
        else if (imageActionRequest == ImageActionRequest.CREATE) {
            // (1) Uploads the image first:
            uploadRemedyImage(remedyImageBitmap, new OnImageUploadCompleteListener() {
                @Override
                public void onSuccess(String imageFilePath, String imageUrl) {
                    // (2) Updates the remedy:
                    remedy.setImageFilePath(imageFilePath);
                    remedy.setImageUrl(imageUrl);
                    update(remedy, listener);
                }

                @Override
                public void onFailure() {
                    if (listener != null) {
                        listener.onFailure();
                    }
                }
            });
        }
        // Image Replace:
        else if (imageActionRequest == ImageActionRequest.REPLACE) {
            // (1) Deletes the current remedy image first:
            _modelFirebaseStorage.deleteImage(remedy.getImageFilePath(), new OnCompleteListener() {
                @Override
                public void onSuccess() {
                    // (2) Uploads the new remedy image:
                    uploadRemedyImage(remedyImageBitmap, new OnImageUploadCompleteListener() {
                        @Override
                        public void onSuccess(String imageFilePath, String imageUrl) {
                            // (3) Updates the remedy:
                            remedy.setImageFilePath(imageFilePath);
                            remedy.setImageUrl(imageUrl);
                            update(remedy, listener);
                        }

                        @Override
                        public void onFailure() {
                            if (listener != null) {
                                listener.onFailure();
                            }
                        }
                    });
                }

                @Override
                public void onFailure() {
                    if (listener != null) {
                        listener.onFailure();
                    }
                }
            });
        }
        // Image Delete:
        else if (imageActionRequest == ImageActionRequest.DELETE) {
            // (1) Deletes the current remedy image first:
            _modelFirebaseStorage.deleteImage(remedy.getImageFilePath(), new OnCompleteListener() {
                @Override
                public void onSuccess() {
                    // (2) Updates the remedy:
                    remedy.setImageFilePath(null);
                    remedy.setImageUrl(null);
                    update(remedy, listener);
                }

                @Override
                public void onFailure() {
                    if (listener != null) {
                        listener.onFailure();
                    }
                }
            });
        }
        // Undefined:
        else {
            throw new IndexOutOfBoundsException("ImageActionRequest: " + imageActionRequest + " is undefined.");
        }
    }

    public void delete(String id, OnCompleteListener listener) {
        Map<String, Object> data = new HashMap<>();
        data.put(FIELD_DATE_LAST_UPDATED, FieldValue.serverTimestamp());
        data.put(FIELD_DATE_DELETED, FieldValue.serverTimestamp());
        db.collection(COLLECTION_NAME)
                .document(id)
                .update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (listener != null) {
                            listener.onSuccess();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("RemediesModelFirebase", "delete", e);
                        if (listener != null) {
                            listener.onFailure();
                        }
                    }
                });
    }

    public void deleteAllByUser(String userId, OnCompleteListener listener) {
        db.collection(COLLECTION_NAME)
                .whereEqualTo(FIELD_POSTED_BY_USER_ID, userId)
                .get()
                .addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot taskResult = task.getResult();
                            if (taskResult != null) {
                                List<DocumentSnapshot> docs = taskResult.getDocuments();
                                for (int i = 0; i < docs.size(); i++) {
                                    // Checks if it's the last document to delete, then sets a listener:
                                    if (i == docs.size() - 1) {
                                        delete(docs.get(i).getId(), new OnCompleteListener() {
                                            @Override
                                            public void onSuccess() {
                                                if (listener != null) {
                                                    listener.onSuccess();
                                                }
                                            }

                                            @Override
                                            public void onFailure() {
                                                Log.w("RemediesModelFirebase", "deleteAllByUser:specific-delete:failure", task.getException());
                                                if (listener != null) {
                                                    listener.onFailure();
                                                }
                                            }
                                        });
                                    } else {
                                        delete(docs.get(i).getId(), null);
                                    }
                                }
                            }
                        } else {
                            Log.w("RemediesModelFirebase", "deleteAllByUser:failure", task.getException());
                            if (listener != null) {
                                listener.onFailure();
                            }
                        }
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
        map.put(FIELD_IMAGE_FILE_PATH, remedy.getImageFilePath());
        map.put(FIELD_IMAGE_URL, remedy.getImageUrl());
        map.put(FIELD_POSTED_BY_USER_ID, remedy.getPostedByUserId());
        map.put(FIELD_POSTED_BY_USER_NAME, remedy.getPostedByUserName());
        if (isCreate) {
            map.put(FIELD_DATE_POSTED, FieldValue.serverTimestamp());
            map.put(FIELD_DATE_DELETED, null);
        } else {
            map.put(FIELD_DATE_POSTED, remedy.getDatePosted());
            map.put(FIELD_DATE_DELETED, remedy.getDateDeleted());
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
        remedy.setImageFilePath(document.getString(FIELD_IMAGE_FILE_PATH));
        remedy.setImageUrl(document.getString(FIELD_IMAGE_URL));
        remedy.setPostedByUserId(document.getString(FIELD_POSTED_BY_USER_ID));
        remedy.setPostedByUserName(document.getString(FIELD_POSTED_BY_USER_NAME));
        remedy.setDatePosted(document.getDate(FIELD_DATE_POSTED));
        remedy.setDateLastUpdated(document.getDate(FIELD_DATE_LAST_UPDATED));
        remedy.setDateDeleted(document.getDate(FIELD_DATE_DELETED));
        return remedy;
    }

    private void uploadRemedyImage(Bitmap bitmap, OnImageUploadCompleteListener listener) {
        String filePath = (STORAGE_REMEDY_IMAGES_FOLDER_PATH + "/" + Calendar.getInstance().getTimeInMillis() + ".jpg");
        Bitmap resizedBitmap = BitmapUtils.resizeAndKeepRatio(bitmap, STORAGE_REMEDY_IMAGES_MAX_WIDTH);
        _modelFirebaseStorage.uploadImage(resizedBitmap, filePath, listener);
    }

    //endregion

}
