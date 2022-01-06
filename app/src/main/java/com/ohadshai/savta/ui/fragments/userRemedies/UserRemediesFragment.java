package com.ohadshai.savta.ui.fragments.userRemedies;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
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
import com.ohadshai.savta.data.UsersModel;
import com.ohadshai.savta.data.utils.OnCompleteListener;
import com.ohadshai.savta.databinding.FragmentUserRemediesBinding;
import com.ohadshai.savta.entities.Remedy;
import com.ohadshai.savta.entities.User;
import com.ohadshai.savta.ui.adapters.RemediesListAdapter;
import com.ohadshai.savta.utils.NetworkUtils;
import com.ohadshai.savta.utils.SharedElementsUtils;

import java.util.List;

public class UserRemediesFragment extends Fragment {

    private UserRemediesViewModel _viewModel;
    private FragmentUserRemediesBinding _binding;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        _viewModel = new ViewModelProvider(this).get(UserRemediesViewModel.class);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _binding = FragmentUserRemediesBinding.inflate(inflater, container, false);

        RecyclerView rvRemediesList = _binding.rvRemediesList;
        rvRemediesList.setHasFixedSize(true);
        rvRemediesList.setLayoutManager(new LinearLayoutManager(getContext()));
        RemediesListAdapter adapter = new RemediesListAdapter(_viewModel.getUserRemedies());
        adapter.setOnItemClickListener(new RemediesListAdapter.OnItemClickListener() {
            @Override
            public void onClick(Remedy remedy, View view) {
                // Navigates to the details fragment of the remedy (with a transition of shared elements):
                CardView cardContainer = view.findViewById(R.id.item_remedy_card);
                ImageView imgRemedyPhoto = view.findViewById(R.id.item_remedy_imgPhoto);

                Navigation.findNavController(view).navigate(
                        UserRemediesFragmentDirections.actionNavUserRemediesToNavRemedyDetails(remedy),
                        SharedElementsUtils.build(cardContainer, imgRemedyPhoto)
                );
            }
        });
        rvRemediesList.setAdapter(adapter);

        _binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshUserRemediesList();
            }
        });

        // Listens to data changes (while the fragment is alive):
        _viewModel.getUserRemedies().observe(getViewLifecycleOwner(), new Observer<List<Remedy>>() {
            @Override
            public void onChanged(List<Remedy> remedies) {
                adapter.notifyDataSetChanged();
                if (remedies.size() < 1) {
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
    public void onStart() {
        super.onStart();

        // Loads the data from the cloud:
        this.refreshUserRemediesList();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }

    //region Private Methods

    /**
     * Refreshes the list of user remedies from the cloud.
     */
    private void refreshUserRemediesList() {
        if (NetworkUtils.checkIfNoNetworkToShowSnackBar(requireActivity(), requireView())) {
            _binding.swipeRefreshLayout.setRefreshing(false);
        } else {
            _binding.swipeRefreshLayout.setRefreshing(true);
            User user = UsersModel.getInstance().getCurrentUser().getValue();
            if (user == null) {
                throw new IllegalStateException("User cannot be null in UserRemediesFragment");
            }
            RemediesModel.getInstance().refreshGetAllByUser(user.getId(), new OnCompleteListener() {
                @Override
                public void onSuccess() {
                    _binding.swipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onFailure() {
                    _binding.swipeRefreshLayout.setRefreshing(false);
                    Snackbar.make(requireView(), R.string.failure_message, Snackbar.LENGTH_SHORT).show();
                }
            });
        }
    }

    //endregion

}