package com.ohadshai.savta.ui.fragments.feed;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.ohadshai.savta.R;
import com.ohadshai.savta.data.RemediesModel;
import com.ohadshai.savta.data.utils.OnCompleteListener;
import com.ohadshai.savta.databinding.FragmentFeedBinding;
import com.ohadshai.savta.entities.Remedy;
import com.ohadshai.savta.ui.adapters.RemediesListAdapter;
import com.ohadshai.savta.ui.dialogs.AboutDialog;
import com.ohadshai.savta.utils.NetworkUtils;
import com.ohadshai.savta.utils.SharedElementsUtils;
import com.ohadshai.savta.utils.SharedPreferencesConsts;

import java.util.List;

public class FeedFragment extends Fragment {

    private FeedViewModel _viewModel;
    private FragmentFeedBinding _binding;
    private boolean _isFeedInitializedBefore;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        _viewModel = new ViewModelProvider(this).get(FeedViewModel.class);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        _isFeedInitializedBefore = isFeedInitializedBefore();

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
                        SharedElementsUtils.build(cardContainer, imgRemedyPhoto)
                );
            }
        });
        rvRemediesList.setAdapter(adapter);

        _binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshRemediesList();
            }
        });

        // Listens to data changes (while the fragment is alive):
        _viewModel.getRemedies().observe(getViewLifecycleOwner(), new Observer<List<Remedy>>() {
            @Override
            public void onChanged(List<Remedy> remedies) {
                adapter.notifyDataSetChanged();
                if (remedies.size() < 1 && _isFeedInitializedBefore) {
                    _binding.rvRemediesList.setVisibility(View.GONE);
                    _binding.llRemediesNotFound.setVisibility(View.VISIBLE);
                } else {
                    _binding.llRemediesNotFound.setVisibility(View.GONE);
                    _binding.rvRemediesList.setVisibility(View.VISIBLE);
                }
            }
        });

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
    public void onStart() {
        super.onStart();

        // Loads the data from the cloud:
        this.refreshRemediesList();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }

    //region Private Methods

    /**
     * Checks if the feed was initialized before.
     *
     * @return Returns true if the feed was initialized before, otherwise false.
     * @apiNote This is in order to prevent "empty results" message flicker in the initialization.
     */
    private boolean isFeedInitializedBefore() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(SharedPreferencesConsts.IS_FEED_INITIALIZED, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(SharedPreferencesConsts.IS_FEED_INITIALIZED, false);
    }

    /**
     * Sets an indicator indicating that the feed was initialized before.
     *
     * @apiNote This is in order to prevent "empty results" message flicker in the initialization.
     */
    private void setFeedInitialized() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(SharedPreferencesConsts.IS_FEED_INITIALIZED, Context.MODE_PRIVATE);
        sharedPreferences.edit()
                .putBoolean(SharedPreferencesConsts.IS_FEED_INITIALIZED, true)
                .apply();
        _isFeedInitializedBefore = true;
    }

    /**
     * Refreshes the list of remedies from the cloud.
     */
    private void refreshRemediesList() {
        if (NetworkUtils.checkIfNoNetworkToShowSnackBar(requireActivity(), requireView())) {
            _binding.swipeRefreshLayout.setRefreshing(false);
        } else {
            _binding.swipeRefreshLayout.setRefreshing(true);
            RemediesModel.getInstance().refreshGetAll(new OnCompleteListener() {
                @Override
                public void onSuccess() {
                    if (_binding != null) {
                        _binding.swipeRefreshLayout.setRefreshing(false);
                    }
                    if (!_isFeedInitializedBefore) {
                        setFeedInitialized();
                        if (_viewModel.getRemedies().getValue() != null && _viewModel.getRemedies().getValue().size() < 1) {
                            _binding.rvRemediesList.setVisibility(View.GONE);
                            _binding.llRemediesNotFound.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onFailure() {
                    if (_binding != null) {
                        _binding.swipeRefreshLayout.setRefreshing(false);
                        Snackbar.make(requireView(), R.string.failure_message, Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    //endregion

}