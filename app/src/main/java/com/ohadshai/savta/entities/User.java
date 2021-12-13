package com.ohadshai.savta.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

/**
 * Represents a user in the application.
 */
@Entity(tableName = "Users", indices = {@Index(value = {"_email"}, unique = true)})
public class User implements Parcelable {
    @PrimaryKey
    @NonNull
    private int _id;
    @NonNull
    private String _firstName;
    @NonNull
    private String _lastName;
    @NonNull
    private String _email;
    @NonNull
    private String _password;
    @NonNull
    private Date _dateRegistered;

    public User() {

    }

    public User(int id, String firstName, String lastName, String email, String password, Date dateRegistered) {
        this._id = id;
        this._firstName = firstName;
        this._lastName = lastName;
        this._email = email;
        this._password = password;
        this._dateRegistered = dateRegistered;
    }

    //region Public API

    public String getFullName() {
        return (_firstName + " " + _lastName);
    }

    public int getId() {
        return _id;
    }

    public void setId(int id) {
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

    //region [Parcelable]

    private User(Parcel in) {
        _id = in.readInt();
        _firstName = in.readString();
        _lastName = in.readString();
        _email = in.readString();
        _password = in.readString();
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
        dest.writeInt(_id);
        dest.writeString(_firstName);
        dest.writeString(_lastName);
        dest.writeString(_email);
        dest.writeString(_password);
        dest.writeLong(_dateRegistered.getTime());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    //endregion

    //endregion

}
