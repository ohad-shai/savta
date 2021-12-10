package com.ohadshai.savta.ui.fragments.login;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ohadshai.savta.databinding.FragmentLoginBinding;

public class LoginFragment extends Fragment {

    private LoginViewModel _viewModel;
    private FragmentLoginBinding _binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        _binding = FragmentLoginBinding.inflate(inflater, container, false);
        View rootView = _binding.getRoot();


        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }

}