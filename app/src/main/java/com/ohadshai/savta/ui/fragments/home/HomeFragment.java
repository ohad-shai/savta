package com.ohadshai.savta.ui.fragments.home;

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

import com.ohadshai.savta.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private HomeViewModel _homeVM;
    private FragmentHomeBinding _binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _homeVM = new ViewModelProvider(this).get(HomeViewModel.class);

        _binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = _binding.getRoot();

        final TextView textView = _binding.textHome;
        _homeVM.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
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