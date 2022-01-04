package com.ohadshai.savta.ui.fragments.remedyCreate;

import androidx.appcompat.app.AlertDialog;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;
import com.ohadshai.savta.R;
import com.ohadshai.savta.data.RemediesModel;
import com.ohadshai.savta.data.UsersModel;
import com.ohadshai.savta.data.utils.OnCompleteListener;
import com.ohadshai.savta.databinding.FragmentRemedyCreateBinding;
import com.ohadshai.savta.entities.Remedy;
import com.ohadshai.savta.entities.User;
import com.ohadshai.savta.utils.AlertDialogRtlHelper;
import com.ohadshai.savta.utils.AndroidUtils;
import com.ohadshai.savta.utils.NetworkUtils;
import com.ohadshai.savta.utils.views.ProgressButton;

public class RemedyCreateFragment extends Fragment implements DialogInterface.OnClickListener {

    private FragmentRemedyCreateBinding _binding;
    private boolean _isImageLoaded;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _binding = FragmentRemedyCreateBinding.inflate(inflater, container, false);
        View rootView = _binding.getRoot();

        _binding.remedyCreateFlPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageOptionsDialog();
            }
        });

        _binding.remedyCreateTxtProblem.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                validateForm(false);
                return false;
            }
        });
        _binding.remedyCreateTxtName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                validateForm(false);
                return false;
            }
        });
        _binding.remedyCreateTxtTreatment.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                validateForm(false);
                return false;
            }
        });

        _binding.remedyCreateBtnAdd.setEnabled(false);
        _binding.remedyCreateBtnAdd.setOnClickListener(new ProgressButton.OnClickListener() {
            @Override
            public void onClick(ProgressButton progressButton) {
                if (validateForm(true) && !progressButton.isInProgress()) {
                    createRemedy();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onClick(DialogInterface dialog, int index) {
        switch (index) {
            // Camera:
            case 0:
                break;
            // Gallery:
            case 1:
                break;
            // Delete Image:
            case 2:
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
        builder.setItems(imageOptions, this);
        AlertDialogRtlHelper.make(builder).show();
    }

    /**
     * Validates the form, and optionally notifies the user about the invalid fields, and returns an indicator indicating whether the form is valid or not.
     *
     * @param notify An indicator indicating whether to notify the user about the invalid fields or not.
     * @return Returns true if the form is valid, otherwise false.
     * @apiNote Also, enables/disables the submit button according to the validation state.
     */
    private boolean validateForm(boolean notify) {
        String problemDesc = _binding.remedyCreateTxtProblem.getText().toString().trim();
        String name = _binding.remedyCreateTxtName.getText().toString().trim();
        String treatmentDesc = _binding.remedyCreateTxtTreatment.getText().toString().trim();

        // Checks problem description is not empty:
        if (TextUtils.isEmpty(problemDesc)) {
            if (notify) {
                _binding.remedyCreateTxtProblem.setError(getString(R.string.remedy_problem_desc_required));
            }
            _binding.remedyCreateBtnAdd.setEnabled(false);
            return false;
        }
        // Checks name is not empty:
        if (TextUtils.isEmpty(name)) {
            if (notify) {
                _binding.remedyCreateTxtName.setError(getString(R.string.remedy_name_required));
            }
            _binding.remedyCreateBtnAdd.setEnabled(false);
            return false;
        }
        // Checks treatment description is not empty:
        if (TextUtils.isEmpty(treatmentDesc)) {
            if (notify) {
                _binding.remedyCreateTxtTreatment.setError(getString(R.string.remedy_treatment_desc_required));
            }
            _binding.remedyCreateBtnAdd.setEnabled(false);
            return false;
        }
        _binding.remedyCreateBtnAdd.setEnabled(true);
        return true;
    }

    /**
     * Performs a create procedure.
     *
     * @apiNote NOTE: Make sure that the form is validated before calling this method.
     */
    private void createRemedy() {
        if (NetworkUtils.checkIfNoNetworkToShowSnackBar(requireActivity(), requireView())) {
            return;
        }
        _binding.remedyCreateBtnAdd.startProgress();
        AndroidUtils.hideKeyboard(requireActivity());

        User user = UsersModel.getInstance().getCurrentUser();
        if (user == null) {
            throw new IllegalStateException("User cannot be null in RemedyCreateFragment.");
        }

        Remedy remedy = new Remedy();
        remedy.setName(_binding.remedyCreateTxtName.getText().toString().trim());
        remedy.setProblemDescription(_binding.remedyCreateTxtProblem.getText().toString().trim());
        remedy.setTreatmentDescription(_binding.remedyCreateTxtTreatment.getText().toString().trim());
        remedy.setImageUrl(null); // TODO
        remedy.setPostedByUserId(user.getId());
        remedy.setPostedByUserName(user.getFullName());

        RemediesModel.getInstance().create(remedy, new OnCompleteListener() {
            @Override
            public void onSuccess() {
                // Navigates to the previous fragment:
                Navigation.findNavController(requireView()).popBackStack();
            }

            @Override
            public void onFailure() {
                Snackbar.make(requireView(), R.string.failure_message, Snackbar.LENGTH_SHORT).show();
                _binding.remedyCreateBtnAdd.stopProgress();
            }
        });
    }

    //endregion

}