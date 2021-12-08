package com.ohadshai.savta.ui.fragments.register;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.ohadshai.savta.databinding.FragmentRegisterBinding;

public class RegisterFragment extends Fragment {

    private RegisterViewModel _viewModel;
    private FragmentRegisterBinding _binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        _binding = FragmentRegisterBinding.inflate(inflater, container, false);
        View root = _binding.getRoot();


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }

}