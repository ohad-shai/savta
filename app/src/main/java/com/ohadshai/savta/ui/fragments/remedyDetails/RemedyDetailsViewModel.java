package com.ohadshai.savta.ui.fragments.remedyDetails;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.ohadshai.savta.data.RemediesModel;
import com.ohadshai.savta.entities.Remedy;

public class RemedyDetailsViewModel extends ViewModel {

    private LiveData<Remedy> _remedy;

    public RemedyDetailsViewModel(String id) {
        _remedy = RemediesModel.getInstance().get(id);
    }

    public LiveData<Remedy> getRemedy() {
        return _remedy;
    }

}