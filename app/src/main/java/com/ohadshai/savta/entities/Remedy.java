package com.ohadshai.savta.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.ohadshai.savta.data.utils.DateConverter;

import java.util.Date;

/**
 * Represents a "Grandma Remedy" that a user can post/view.
 */
@Entity(tableName = "Remedies")
public class Remedy implements Parcelable {

    //region Private Members

    @PrimaryKey
    @NonNull
    private String _id;

    @NonNull
    private String _name;

    @NonNull
    private String _problemDescription;

    @NonNull
    private String _treatmentDescription;

    private String _imageFilePath;
    private String _imageUrl;

    @NonNull
    private String _postedByUserId;

    @NonNull
    private String _postedByUserName;

    @NonNull
    @TypeConverters(DateConverter.class)
    private Date _datePosted;

    @NonNull
    @TypeConverters(DateConverter.class)
    private Date _dateLastUpdated;

    @Ignore
    private Date _dateDeleted;

    //endregion

    public Remedy() {
    }

    //region Public API

    public String getId() {
        return _id;
    }

    public void setId(String id) {
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

    public String getImageFilePath() {
        return _imageFilePath;
    }

    public void setImageFilePath(String imageFilePath) {
        this._imageFilePath = imageFilePath;
    }

    public String getImageUrl() {
        return _imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this._imageUrl = imageUrl;
    }

    public String getPostedByUserId() {
        return _postedByUserId;
    }

    public void setPostedByUserId(String postedByUserId) {
        this._postedByUserId = postedByUserId;
    }

    public String getPostedByUserName() {
        return _postedByUserName;
    }

    public void setPostedByUserName(String postedByUserName) {
        this._postedByUserName = postedByUserName;
    }

    public Date getDatePosted() {
        return _datePosted;
    }

    public void setDatePosted(Date datePosted) {
        this._datePosted = datePosted;
    }

    public Date getDateLastUpdated() {
        return _dateLastUpdated;
    }

    public void setDateLastUpdated(Date dateLastUpdated) {
        this._dateLastUpdated = dateLastUpdated;
    }

    public Date getDateDeleted() {
        return _dateDeleted;
    }

    public void setDateDeleted(Date dateDeleted) {
        this._dateDeleted = dateDeleted;
    }

    //region [Parcelable]

    private Remedy(Parcel in) {
        _id = in.readString();
        _name = in.readString();
        _problemDescription = in.readString();
        _treatmentDescription = in.readString();
        _imageFilePath = in.readString();
        _imageUrl = in.readString();
        _postedByUserId = in.readString();
        _postedByUserName = in.readString();
        _datePosted = new Date(in.readLong());
        _dateLastUpdated = new Date(in.readLong());
    }

    public static final Parcelable.Creator<Remedy> CREATOR = new Parcelable.Creator<Remedy>() {
        @Override
        public Remedy createFromParcel(Parcel in) {
            return new Remedy(in);
        }

        @Override
        public Remedy[] newArray(int size) {
            return new Remedy[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(_name);
        dest.writeString(_problemDescription);
        dest.writeString(_treatmentDescription);
        dest.writeString(_imageFilePath);
        dest.writeString(_imageUrl);
        dest.writeString(_postedByUserId);
        dest.writeString(_postedByUserName);
        dest.writeLong(_datePosted.getTime());
        dest.writeLong(_dateLastUpdated.getTime());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    //endregion

    //endregion

}
