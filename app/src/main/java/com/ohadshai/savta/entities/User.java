package com.ohadshai.savta.entities;

import java.util.Date;

/**
 * Represents a user in the application.
 */
public class User {
    private int _id;
    private String _firstName;
    private String _lastName;
    private String _email;
    private String _password;
    private Date _dateRegistered;

    public User() {

    }

    public User(int id, String firstName, String lastName, String email) {
        this._id = id;
        this._firstName = firstName;
        this._lastName = lastName;
        this._email = email;
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

    //endregion

}
