package com.ohadshai.savta.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

/**
 * Represents a "Grandma Remedy" that a user can post/view.
 */
@Entity(tableName = "Remedies")
public class Remedy implements Parcelable {

    //region Private Members

    @PrimaryKey
    @NonNull
    private int _id;
    @NonNull
    private String _name;
    @NonNull
    private String _problemDescription;
    @NonNull
    private String _treatmentDescription;
    private String _imageUrl;
    @NonNull
    private User _userPosted;
    @NonNull
    private Date _datePosted;

    //endregion

    public Remedy() {
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

    public User getUserPosted() {
        return _userPosted;
    }

    public void setUserPosted(User userPosted) {
        this._userPosted = userPosted;
    }

    public Date getDatePosted() {
        return _datePosted;
    }

    public void setDatePosted(Date datePosted) {
        this._datePosted = datePosted;
    }

    //region [Parcelable]

    private Remedy(Parcel in) {
        _id = in.readInt();
        _name = in.readString();
        _problemDescription = in.readString();
        _treatmentDescription = in.readString();
        _imageUrl = in.readString();
        _userPosted = in.readParcelable(User.class.getClassLoader());
        _datePosted = new Date(in.readLong());
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
        dest.writeInt(_id);
        dest.writeString(_name);
        dest.writeString(_problemDescription);
        dest.writeString(_treatmentDescription);
        dest.writeString(_imageUrl);
        dest.writeParcelable(_userPosted, flags);
        dest.writeLong(_datePosted.getTime());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    //endregion

    //endregion

}
