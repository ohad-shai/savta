package com.ohadshai.savta.ui.fragments.userRemedies;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.ohadshai.savta.data.RemediesModel;
import com.ohadshai.savta.entities.Remedy;

import java.util.List;

public class UserRemediesViewModel extends ViewModel {

    private final LiveData<List<Remedy>> _remedies = RemediesModel.instance.getAll();

    public LiveData<List<Remedy>> getRemedies() {
        return _remedies;
    }

}