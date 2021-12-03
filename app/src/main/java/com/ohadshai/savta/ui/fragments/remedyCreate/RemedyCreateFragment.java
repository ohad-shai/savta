package com.ohadshai.savta.ui.fragments.remedyCreate;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ohadshai.savta.databinding.FragmentRemedyCreateBinding;

public class RemedyCreateFragment extends Fragment {

    private RemedyCreateViewModel _viewModel;
    private FragmentRemedyCreateBinding _binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _viewModel = new ViewModelProvider(this).get(RemedyCreateViewModel.class);

        _binding = FragmentRemedyCreateBinding.inflate(inflater, container, false);
        View root = _binding.getRoot();


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }

}