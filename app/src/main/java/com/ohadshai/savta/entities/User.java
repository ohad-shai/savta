package com.ohadshai.savta.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Represents a user in the application.
 */
public class User implements Parcelable {

    //region Private Members

    private String _id;
    private String _firstName;
    private String _lastName;
    private String _email;
    private Date _dateRegistered;

    //endregion

    public User() {
    }

    //region Public API

    public String getFullName() {
        return (_firstName + " " + _lastName);
    }

    public void setFullName(String fullName) {
        int fullNameDividerIndex = fullName.indexOf(' ');
        this._firstName = fullName.substring(0, fullNameDividerIndex);
        this._lastName = fullName.substring(fullNameDividerIndex + 1);
    }

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    public String getFirstName() {
        return _firstName;
    }

    public void setFirstName(String firstName) {
        this._firstName = firstName;
    }

    public String getLastName() {
        return _lastName;
    }

    public void setLastName(String lastName) {
        this._lastName = lastName;
    }

    public String getEmail() {
        return _email;
    }

    public void setEmail(String email) {
        this._email = email;
    }

    public Date getDateRegistered() {
        return _dateRegistered;
    }

    public void setDateRegistered(Date dateRegistered) {
        this._dateRegistered = dateRegistered;
    }

    //region [Parcelable]

    private User(Parcel in) {
        _id = in.readString();
        _firstName = in.readString();
        _lastName = in.readString();
        _email = in.readString();
        _dateRegistered = new Date(in.readLong());
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(_firstName);
        dest.writeString(_lastName);
        dest.writeString(_email);
        dest.writeLong(_dateRegistered.getTime());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    //endregion

    //endregion

}
