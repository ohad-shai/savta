package com.ohadshai.savta.ui.fragments.remedyEdit;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.transition.TransitionInflater;

import com.ohadshai.savta.R;
import com.ohadshai.savta.databinding.FragmentRemedyEditBinding;
import com.ohadshai.savta.entities.Remedy;
import com.ohadshai.savta.ui.fragments.remedyDetails.RemedyDetailsFragmentArgs;
import com.ohadshai.savta.utils.AlertDialogRtlHelper;
import com.squareup.picasso.Picasso;

public class RemedyEditFragment extends Fragment {

    private RemedyEditViewModel _viewModel;
    private FragmentRemedyEditBinding _binding;
    private boolean _isImageLoaded;
    private Remedy _remedy;

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
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _viewModel = new ViewModelProvider(this).get(RemedyEditViewModel.class);

        _binding = FragmentRemedyEditBinding.inflate(inflater, container, false);
        View rootView = _binding.getRoot();

        // Gets the remedy information from the details fragment:
        Remedy remedy = RemedyEditFragmentArgs.fromBundle(getArguments()).getRemedy();
        this.bindData(remedy);

        _binding.remedyEditFlPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageOptionsDialog();
            }
        });

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }

    //region Private Methods

    private void bindData(Remedy remedy) {
        _remedy = remedy;
        ViewCompat.setTransitionName(_binding.remedyEditImgPhoto, ("remedy_image_" + remedy.getId()));
        if (remedy.getImageUrl() != null) {
            _binding.remedyEditLlPhotoLabel.setVisibility(View.GONE);
            Picasso.get()
                    .load(remedy.getImageUrl())
                    .noFade()
                    .into(_binding.remedyEditImgPhoto);
            _isImageLoaded = true;
        } else {
            _binding.remedyEditLlPhotoLabel.setVisibility(View.VISIBLE);
            _isImageLoaded = false;
        }
        _binding.remedyEditTxtProblem.setText(remedy.getProblemDescription());
        _binding.remedyEditTxtName.setText(remedy.getName());
        _binding.remedyEditTxtTreatment.setText(remedy.getTreatmentDescription());
    }

    /**
     * Opens the image options dialog.
     */
    private void openImageOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        String[] imageOptions;
        if (_isImageLoaded) {
            builder.setTitle(R.string.image_change);
            imageOptions = new String[]{getString(R.string.camera), getString(R.string.gallery), getString(R.string.delete_image)};
        } else {
            builder.setTitle(R.string.image_upload);
            imageOptions = new String[]{getString(R.string.camera), getString(R.string.gallery)};
        }
        builder.setItems(imageOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int index) {
                switch (index) {
                    case 0:
                        // TODO
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    default:
                        throw new IndexOutOfBoundsException("The option index is not implemented in the image options dialog.");
                }
            }
        });
        AlertDialogRtlHelper.make(builder).show();
    }

    //endregion

}