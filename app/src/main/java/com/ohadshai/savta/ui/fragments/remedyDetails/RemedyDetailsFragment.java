package com.ohadshai.savta.ui.fragments.remedyDetails;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.transition.TransitionInflater;

import com.google.android.material.snackbar.Snackbar;
import com.ohadshai.savta.R;
import com.ohadshai.savta.databinding.FragmentRemedyDetailsBinding;
import com.ohadshai.savta.entities.Remedy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class RemedyDetailsFragment extends Fragment {

    private RemedyDetailsViewModel _viewModel;
    private FragmentRemedyDetailsBinding _binding;
    private ActionBar _actionBar;

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

        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _viewModel = new ViewModelProvider(this).get(RemedyDetailsViewModel.class);

        _binding = FragmentRemedyDetailsBinding.inflate(inflater, container, false);
        View rootView = _binding.getRoot();

        _actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        // Gets the remedy information based on the data from the list:
        Bundle bundle = getArguments();
        if (bundle == null) {
            throw new IllegalStateException("Bundle with [Remedy] object must be provided to this fragment.");
        }
        Remedy remedy = Remedy.fromBundle(bundle);
        this.bindData(remedy);

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
            Snackbar.make(getView(), "עריכה", Snackbar.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_delete) {
            Snackbar.make(getView(), "מחיקה", Snackbar.LENGTH_SHORT).show();
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

    private void bindData(Remedy remedy) {
        _actionBar.setTitle(remedy.getName());
        ViewCompat.setTransitionName(_binding.flContainer, ("remedy_container_" + remedy.getId()));
        ViewCompat.setTransitionName(_binding.imgRemedyPhoto, ("remedy_image_" + remedy.getId()));
        Picasso.get()
                .load(remedy.getImageUrl())
                .noFade()
                .into(_binding.imgRemedyPhoto);
        _binding.txtProblemDescription.setText(remedy.getProblemDescription());
        _binding.txtName.setText(remedy.getName());
        _binding.txtTreatmentDescription.setText(remedy.getTreatmentDescription());
        _binding.txtPostedBy.setText(remedy.getUserPosted().getFullName());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String dateString = dateFormat.format(remedy.getDatePosted());
        _binding.txtPostedOnDate.setText(dateString);
    }

    //endregion

}