package com.ohadshai.savta.ui.activities.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.ohadshai.savta.data.UsersModel;
import com.ohadshai.savta.entities.User;

public class MainViewModel extends ViewModel {

    private LiveData<User> _user;

    public MainViewModel() {
        _user = UsersModel.getInstance().getCurrentUser();
    }

    public LiveData<User> getCurrentUser() {
        return _user;
    }

}
