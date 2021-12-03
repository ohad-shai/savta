package com.ohadshai.savta.ui.fragments.userRemedies;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.ohadshai.savta.databinding.FragmentUserRemediesBinding;

public class UserRemediesFragment extends Fragment {

    private UserRemediesViewModel _viewModel;
    private FragmentUserRemediesBinding _binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _viewModel = new ViewModelProvider(this).get(UserRemediesViewModel.class);

        _binding = FragmentUserRemediesBinding.inflate(inflater, container, false);
        View root = _binding.getRoot();

        final TextView textView = _binding.textGallery;
        _viewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }

}