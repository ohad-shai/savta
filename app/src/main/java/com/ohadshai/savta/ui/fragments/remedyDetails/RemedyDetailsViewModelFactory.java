package com.ohadshai.savta.ui.fragments.remedyDetails;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class RemedyDetailsViewModelFactory implements ViewModelProvider.Factory {
    private final String _remedyId;

    public RemedyDetailsViewModelFactory(String remedyId) {
        _remedyId = remedyId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> aClass) {
        return (T) new RemedyDetailsViewModel(_remedyId);
    }

}
