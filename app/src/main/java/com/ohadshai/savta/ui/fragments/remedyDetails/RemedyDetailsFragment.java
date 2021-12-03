package com.ohadshai.savta.ui.fragments.remedyDetails;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.ohadshai.savta.databinding.FragmentRemedyDetailsBinding;

public class RemedyDetailsFragment extends Fragment {

    private RemedyDetailsViewModel _viewModel;
    private FragmentRemedyDetailsBinding _binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _viewModel = new ViewModelProvider(this).get(RemedyDetailsViewModel.class);

        _binding = FragmentRemedyDetailsBinding.inflate(inflater, container, false);
        View root = _binding.getRoot();


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }

}