package com.ohadshai.savta.entities;

import java.util.Date;

/**
 * Represents a "Grandma Remedy" that a user can post/view.
 */
public class Remedy {
    private int _id;
    private String _name;
    private String _problemDescription;
    private String _treatmentDescription;
    private String _imageUrl;
    private Date _datePosted;

    public Remedy() {

    }

    public Remedy(int id, String name, String problemDescription, String treatmentDescription, String imageUrl, Date datePosted) {
        this._id = id;
        this._name = name;
        this._problemDescription = problemDescription;
        this._treatmentDescription = treatmentDescription;
        this._imageUrl = imageUrl;
        this._datePosted = datePosted;
    }

    //region Public API

    public int getId() {
        return _id;
    }

    public void setId(int id) {
        this._id = id;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        this._name = name;
    }

    public String getProblemDescription() {
        return _problemDescription;
    }

    public void setProblemDescription(String problemDescription) {
        this._problemDescription = problemDescription;
    }

    public String getTreatmentDescription() {
        return _treatmentDescription;
    }

    public void setTreatmentDescription(String treatmentDescription) {
        this._treatmentDescription = treatmentDescription;
    }

    public String getImageUrl() {
        return _imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this._imageUrl = imageUrl;
    }

    public Date getDatePosted() {
        return _datePosted;
    }

    public void setDatePosted(Date datePosted) {
        this._datePosted = datePosted;
    }

    //endregion

}
