package com.ohadshai.savta.ui.fragments.userRemedies;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.ohadshai.savta.data.RemediesModel;
import com.ohadshai.savta.data.UsersModel;
import com.ohadshai.savta.entities.Remedy;
import com.ohadshai.savta.entities.User;

import java.util.List;

public class UserRemediesViewModel extends ViewModel {

    private final LiveData<List<Remedy>> _userRemedies;

    public UserRemediesViewModel() {
        User user = UsersModel.getInstance().getCurrentUser();
        if (user == null) {
            throw new IllegalStateException("User cannot be null in UserRemediesViewModel");
        }
        _userRemedies = RemediesModel.getInstance().getAllByUser(user.getId());
    }

    public LiveData<List<Remedy>> getUserRemedies() {
        return _userRemedies;
    }

}