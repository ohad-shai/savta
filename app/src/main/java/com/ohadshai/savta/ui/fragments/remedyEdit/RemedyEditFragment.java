package com.ohadshai.savta.ui.fragments.remedyEdit;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.transition.TransitionInflater;

import com.google.android.material.snackbar.Snackbar;
import com.ohadshai.savta.R;
import com.ohadshai.savta.data.RemediesModel;
import com.ohadshai.savta.data.utils.ImageActionRequest;
import com.ohadshai.savta.data.utils.OnCompleteListener;
import com.ohadshai.savta.databinding.FragmentRemedyEditBinding;
import com.ohadshai.savta.entities.Remedy;
import com.ohadshai.savta.utils.AlertDialogRtlHelper;
import com.ohadshai.savta.utils.AndroidUtils;
import com.ohadshai.savta.utils.IntentUtils;
import com.ohadshai.savta.utils.NetworkUtils;
import com.ohadshai.savta.utils.views.ProgressButton;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;

public class RemedyEditFragment extends Fragment implements DialogInterface.OnClickListener {

    private FragmentRemedyEditBinding _binding;
    private boolean _hasImage;
    private ImageActionRequest _imageActionRequest;
    private Bitmap _imageBitmapToUpdate;
    private Remedy _remedy;

    //region Activity Result Launchers

    private final ActivityResultLauncher<Intent> _cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                Bundle extras = result.getData().getExtras();
                Bitmap bitmap = (Bitmap) extras.get("data");
                setRemedyImage(bitmap);
            }
        }
    });

    private final ActivityResultLauncher<Intent> _galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                try {
                    Uri imageUri = result.getData().getData();
                    InputStream imageStream = requireActivity().getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                    setRemedyImage(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    //endregion

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
        _binding = FragmentRemedyEditBinding.inflate(inflater, container, false);

        // Gets the remedy information from the details fragment:
        Remedy remedy = RemedyEditFragmentArgs.fromBundle(getArguments()).getRemedy();
        this.bindData(remedy);

        _binding.remedyEditFlPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageOptionsDialog();
            }
        });

        _binding.remedyEditTxtProblem.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                validateForm(false);
                return false;
            }
        });
        _binding.remedyEditTxtName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                validateForm(false);
                return false;
            }
        });
        _binding.remedyEditTxtTreatment.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                validateForm(false);
                return false;
            }
        });

        _binding.remedyEditBtnUpdate.setOnClickListener(new ProgressButton.OnClickListener() {
            @Override
            public void onClick(ProgressButton progressButton) {
                if (validateForm(true) && !progressButton.isInProgress()) {
                    updateRemedy();
                }
            }
        });

        return _binding.getRoot();
    }

    @Override
    public void onClick(DialogInterface dialog, int index) {
        switch (index) {
            // Camera:
            case 0:
                Intent cameraIntent = IntentUtils.camera();
                if (cameraIntent != null) {
                    _cameraLauncher.launch(cameraIntent);
                } else {
                    Snackbar.make(requireView(), R.string.camera_intent_not_found, Snackbar.LENGTH_SHORT).show();
                }
                break;
            // Gallery:
            case 1:
                Intent galleryIntent = IntentUtils.gallery();
                if (galleryIntent != null) {
                    _galleryLauncher.launch(galleryIntent);
                } else {
                    Snackbar.make(requireView(), R.string.gallery_intent_not_found, Snackbar.LENGTH_SHORT).show();
                }
                break;
            // Delete Image:
            case 2:
                setRemedyImage(null);
                break;
            default:
                throw new IndexOutOfBoundsException("The option index is not implemented in the image options dialog.");
        }
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
        _imageActionRequest = ImageActionRequest.NONE;
        _imageBitmapToUpdate = null;
        _remedy = remedy;
        ViewCompat.setTransitionName(_binding.remedyEditImgPhoto, ("remedy_image_" + remedy.getId()));
        if (remedy.getImageUrl() != null) {
            _hasImage = true;
            _binding.remedyEditImgPhoto.setVisibility(View.VISIBLE);
            _binding.remedyEditLlPhotoLabel.setVisibility(View.GONE);
            Picasso.get()
                    .load(remedy.getImageUrl())
                    .noFade()
                    .into(_binding.remedyEditImgPhoto);
        } else {
            _hasImage = false;
            _binding.remedyEditImgPhoto.setVisibility(View.GONE);
            _binding.remedyEditLlPhotoLabel.setVisibility(View.VISIBLE);
        }
        _binding.remedyEditTxtProblem.setText(remedy.getProblemDescription());
        _binding.remedyEditTxtName.setText(remedy.getName());
        _binding.remedyEditTxtTreatment.setText(remedy.getTreatmentDescription());
    }

    /**
     * Opens the image options dialog.
     */
    private void openImageOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        String[] imageOptions;
        if (_hasImage) {
            builder.setTitle(R.string.image_change);
            imageOptions = new String[]{getString(R.string.camera), getString(R.string.gallery), getString(R.string.delete_image)};
        } else {
            builder.setTitle(R.string.image_upload);
            imageOptions = new String[]{getString(R.string.camera), getString(R.string.gallery)};
        }
        builder.setItems(imageOptions, this);
        AlertDialogRtlHelper.make(builder).show();
    }

    /**
     * Sets the specified image bitmap to update the current remedy image, and shows/hides the imageview according to the bitmap (null to hide).
     *
     * @param bitmap The image bitmap to set. If null hides the image, otherwise shows the image.
     */
    private void setRemedyImage(Bitmap bitmap) {
        _imageBitmapToUpdate = bitmap;
        _binding.remedyEditImgPhoto.setImageBitmap(bitmap);

        // Checks if to show/hide the image:
        if (bitmap != null) {
            _hasImage = true;
            _binding.remedyEditImgPhoto.setVisibility(View.VISIBLE);
            _binding.remedyEditLlPhotoLabel.setVisibility(View.GONE);
        } else {
            _hasImage = false;
            _binding.remedyEditImgPhoto.setVisibility(View.GONE);
            _binding.remedyEditLlPhotoLabel.setVisibility(View.VISIBLE);
        }

        // Checks the image action request logic:
        if (_remedy.getImageFilePath() != null) {
            if (bitmap != null) {
                _imageActionRequest = ImageActionRequest.REPLACE;
            } else {
                _imageActionRequest = ImageActionRequest.DELETE;
            }
        } else {
            if (bitmap != null) {
                _imageActionRequest = ImageActionRequest.CREATE;
            } else {
                _imageActionRequest = ImageActionRequest.NONE;
            }
        }
    }

    /**
     * Validates the form, and optionally notifies the user about the invalid fields, and returns an indicator indicating whether the form is valid or not.
     *
     * @param notify An indicator indicating whether to notify the user about the invalid fields or not.
     * @return Returns true if the form is valid, otherwise false.
     * @apiNote Also, enables/disables the submit button according to the validation state.
     */
    private boolean validateForm(boolean notify) {
        String problemDesc = _binding.remedyEditTxtProblem.getText().toString().trim();
        String name = _binding.remedyEditTxtName.getText().toString().trim();
        String treatmentDesc = _binding.remedyEditTxtTreatment.getText().toString().trim();

        // Checks problem description is not empty:
        if (TextUtils.isEmpty(problemDesc)) {
            if (notify) {
                _binding.remedyEditTxtProblem.setError(getString(R.string.remedy_problem_desc_required));
            }
            _binding.remedyEditBtnUpdate.setEnabled(false);
            return false;
        }
        // Checks name is not empty:
        if (TextUtils.isEmpty(name)) {
            if (notify) {
                _binding.remedyEditTxtName.setError(getString(R.string.remedy_name_required));
            }
            _binding.remedyEditBtnUpdate.setEnabled(false);
            return false;
        }
        // Checks treatment description is not empty:
        if (TextUtils.isEmpty(treatmentDesc)) {
            if (notify) {
                _binding.remedyEditTxtTreatment.setError(getString(R.string.remedy_treatment_desc_required));
            }
            _binding.remedyEditBtnUpdate.setEnabled(false);
            return false;
        }
        _binding.remedyEditBtnUpdate.setEnabled(true);
        return true;
    }

    /**
     * Performs an update procedure.
     *
     * @apiNote NOTE: Make sure that the form is validated before calling this method.
     */
    private void updateRemedy() {
        if (NetworkUtils.checkIfNoNetworkToShowSnackBar(requireActivity(), requireView())) {
            return;
        }
        _binding.remedyEditBtnUpdate.startProgress();
        AndroidUtils.hideKeyboard(requireActivity());

        _remedy.setName(_binding.remedyEditTxtName.getText().toString().trim());
        _remedy.setProblemDescription(_binding.remedyEditTxtProblem.getText().toString().trim());
        _remedy.setTreatmentDescription(_binding.remedyEditTxtTreatment.getText().toString().trim());
        _remedy.setDatePosted(new Date());

        // Updates the remedy with an image (upload/replace/delete - according to the action requested):
        RemediesModel.getInstance().updateWithImage(_remedy, _imageActionRequest, _imageBitmapToUpdate, new OnCompleteListener() {
            @Override
            public void onSuccess() {
                // Navigates to the previous fragment:
                Navigation.findNavController(requireView()).popBackStack();
            }

            @Override
            public void onFailure() {
                Snackbar.make(requireView(), R.string.failure_message, Snackbar.LENGTH_SHORT).show();
                _binding.remedyEditBtnUpdate.stopProgress();
            }
        });
    }

    //endregion

}