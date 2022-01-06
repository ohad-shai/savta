package com.ohadshai.savta.ui.fragments.remedyDetails;

import android.content.Context;
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
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.transition.TransitionInflater;

import com.google.android.material.snackbar.Snackbar;
import com.ohadshai.savta.R;
import com.ohadshai.savta.data.RemediesModel;
import com.ohadshai.savta.data.UsersModel;
import com.ohadshai.savta.data.utils.OnCompleteListener;
import com.ohadshai.savta.databinding.FragmentRemedyDetailsBinding;
import com.ohadshai.savta.entities.Remedy;
import com.ohadshai.savta.entities.User;
import com.ohadshai.savta.utils.NetworkUtils;
import com.ohadshai.savta.utils.SharedElementsUtils;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class RemedyDetailsFragment extends Fragment {

    private RemedyDetailsViewModel _viewModel;
    private FragmentRemedyDetailsBinding _binding;
    private ActionBar _actionBar;
    private Remedy _remedy;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        // Gets the remedy information:
        _remedy = RemedyDetailsFragmentArgs.fromBundle(getArguments()).getRemedy();
        _viewModel = new ViewModelProvider(this, new RemedyDetailsViewModelFactory(_remedy.getId())).get(RemedyDetailsViewModel.class);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Doing some cool transitions here:
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setAllowEnterTransitionOverlap(false);
        setSharedElementEnterTransition(inflater.inflateTransition(android.R.transition.move));
        setSharedElementReturnTransition(inflater.inflateTransition(android.R.transition.move));
        setAllowReturnTransitionOverlap(false);
        setExitTransition(inflater.inflateTransition(android.R.transition.fade));
        setReturnTransition(inflater.inflateTransition(android.R.transition.fade));

        // Checks if the remedy was posted by the current user, in order to allow him to update/delete:
        User user = UsersModel.getInstance().getCurrentUser();
        if (user == null) {
            throw new IllegalStateException("User cannot be null in RemedyDetailsFragment.");
        }
        if (_remedy.getPostedByUserId().equals(user.getId())) {
            setHasOptionsMenu(true);
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _binding = FragmentRemedyDetailsBinding.inflate(inflater, container, false);
        View rootView = _binding.getRoot();

        _actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        _binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshRemedy();
            }
        });

        // Binds the data from the fragment args:
        this.bindData(_remedy);

        // Listens to data changes (while the fragment is alive):
        _viewModel.getRemedy().observe(getViewLifecycleOwner(), new Observer<Remedy>() {
            @Override
            public void onChanged(Remedy remedy) {
                if (remedy != null) {
                    bindData(remedy);
                } else {
                    // Remedy is deleted so notifies the user and exits:
                    Snackbar.make(requireView(), R.string.remedy_not_exist_anymore, Snackbar.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).popBackStack();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_remedy_details_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_edit) {
            // Edits the remedy:
            View rootView = requireView();
            // Navigates to the edit fragment of the remedy (with a transition of shared elements):
            ImageView imgRemedyPhoto = rootView.findViewById(R.id.imgRemedyPhoto);

            Navigation.findNavController(rootView).navigate(
                    RemedyDetailsFragmentDirections.actionNavRemedyDetailsToNavRemedyEdit(_remedy),
                    SharedElementsUtils.build(imgRemedyPhoto)
            );
            return true;
        } else if (id == R.id.action_delete) {
            // Deletes the remedy:
            _binding.swipeRefreshLayout.setRefreshing(true);
            RemediesModel.getInstance().delete(_remedy.getId(), new OnCompleteListener() {
                @Override
                public void onSuccess() {
                    _binding.swipeRefreshLayout.setRefreshing(false);
                    Navigation.findNavController(requireView()).popBackStack();
                    Snackbar.make(requireView(), R.string.remedy_deleted_successfully, Snackbar.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure() {
                    _binding.swipeRefreshLayout.setRefreshing(false);
                    Snackbar.make(requireView(), R.string.failure_message, Snackbar.LENGTH_SHORT).show();
                }
            });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Loads the data from the cloud:
        this.refreshRemedy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }

    //region Private Methods

    /**
     * Binds the remedy data to the screen.
     *
     * @param remedy The remedy to bind.
     */
    private void bindData(Remedy remedy) {
        _remedy = remedy;
        _actionBar.setTitle(remedy.getName());
        ViewCompat.setTransitionName(_binding.swipeRefreshLayout, ("remedy_container_" + remedy.getId()));
        ViewCompat.setTransitionName(_binding.imgRemedyPhoto, ("remedy_image_" + remedy.getId()));
        Picasso.get()
                .load(remedy.getImageUrl())
                .placeholder(R.drawable.remedy_default_image)
                .noFade()
                .into(_binding.imgRemedyPhoto);
        _binding.txtProblemDescription.setText(remedy.getProblemDescription());
        _binding.txtName.setText(remedy.getName());
        _binding.txtTreatmentDescription.setText(remedy.getTreatmentDescription());
        _binding.txtPostedBy.setText(remedy.getPostedByUserName());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String dateString = dateFormat.format(remedy.getDatePosted());
        _binding.txtPostedOnDate.setText(dateString);
    }

    /**
     * Refreshes the remedy info from the cloud.
     */
    private void refreshRemedy() {
        if (NetworkUtils.checkIfNoNetworkToShowSnackBar(requireActivity(), requireView())) {
            _binding.swipeRefreshLayout.setRefreshing(false);
        } else {
            _binding.swipeRefreshLayout.setRefreshing(true);
            RemediesModel.getInstance().refreshGet(_remedy.getId(), new OnCompleteListener() {
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