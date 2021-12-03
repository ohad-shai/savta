package com.ohadshai.savta.ui.fragments.feed;

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

import com.ohadshai.savta.databinding.FragmentFeedBinding;

public class FeedFragment extends Fragment {

    private FeedViewModel _viewModel;
    private FragmentFeedBinding _binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _viewModel = new ViewModelProvider(this).get(FeedViewModel.class);

        _binding = FragmentFeedBinding.inflate(inflater, container, false);
        View root = _binding.getRoot();

        final TextView textView = _binding.textHome;
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