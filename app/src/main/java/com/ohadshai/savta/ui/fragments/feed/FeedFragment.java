package com.ohadshai.savta.ui.fragments.feed;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.ohadshai.savta.R;
import com.ohadshai.savta.data.RemediesModel;
import com.ohadshai.savta.data.utils.OnGetCompleteListener;
import com.ohadshai.savta.databinding.FragmentFeedBinding;
import com.ohadshai.savta.entities.Remedy;
import com.ohadshai.savta.ui.adapters.RemediesListAdapter;
import com.ohadshai.savta.ui.dialogs.AboutDialog;
import com.ohadshai.savta.utils.SharedElementsUtil;

import java.util.List;

public class FeedFragment extends Fragment {

    private FeedViewModel _viewModel;
    private FragmentFeedBinding _binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _viewModel = new ViewModelProvider(this).get(FeedViewModel.class);

        _binding = FragmentFeedBinding.inflate(inflater, container, false);

        RecyclerView rvRemediesList = _binding.rvRemediesList;
        rvRemediesList.setHasFixedSize(true);
        rvRemediesList.setLayoutManager(new LinearLayoutManager(getContext()));
        RemediesListAdapter adapter = new RemediesListAdapter(_viewModel.getRemedies());
        adapter.setOnItemClickListener(new RemediesListAdapter.OnItemClickListener() {
            @Override
            public void onClick(Remedy remedy, View view) {
                // Navigates to the details fragment of the remedy (with a transition of shared elements):
                CardView cardContainer = view.findViewById(R.id.item_remedy_card);
                ImageView imgRemedyPhoto = view.findViewById(R.id.item_remedy_imgPhoto);

                Navigation.findNavController(view).navigate(
                        FeedFragmentDirections.actionNavFeedToNavRemedyDetails(remedy),
                        SharedElementsUtil.build(cardContainer, imgRemedyPhoto)
                );
            }
        });
        rvRemediesList.setAdapter(adapter);

        // Listens to data changes (while the fragment is alive):
        _viewModel.getRemedies().observe(getViewLifecycleOwner(), new Observer<List<Remedy>>() {
            @Override
            public void onChanged(List<Remedy> remedies) {
                adapter.notifyDataSetChanged();
            }
        });

        // Loads the data and shows UI indicators:
        this.reloadData();

        return _binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_about) {
            AboutDialog.make((AppCompatActivity) requireActivity()).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }

    //region Private Methods

    private void reloadData() {
        //_binding.progressBar.setVisibility(View.VISIBLE);

        RemediesModel.instance.getAll(new OnGetCompleteListener<List<Remedy>>() {
            @Override
            public void onSuccess(List<Remedy> remedies) {
                // Updates the UI:
                //_binding.progressBar.setVisibility(View.GONE);
                if (remedies.size() < 1) {
                    _binding.rvRemediesList.setVisibility(View.GONE);
                    //_binding.btnMoviesNotFound.setVisibility(View.VISIBLE);
                } else {
                    //_binding.btnMoviesNotFound.setVisibility(View.GONE);
                    _binding.rvRemediesList.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure() {
                Snackbar.make(requireView(), R.string.failure_message, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    //endregion

}