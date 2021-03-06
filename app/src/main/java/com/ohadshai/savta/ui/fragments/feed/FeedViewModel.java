package com.ohadshai.savta.ui.fragments.feed;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.ohadshai.savta.data.RemediesModel;
import com.ohadshai.savta.entities.Remedy;

import java.util.List;

public class FeedViewModel extends ViewModel {

    private final LiveData<List<Remedy>> _remedies;

    public FeedViewModel() {
        _remedies = RemediesModel.getInstance().getAll();
    }

    public LiveData<List<Remedy>> getRemedies() {
        return _remedies;
    }

}