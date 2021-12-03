package com.ohadshai.savta.ui.fragments.userRemedies;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UserRemediesViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public UserRemediesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is user remedies fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}