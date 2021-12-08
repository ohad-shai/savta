package com.ohadshai.savta.ui.fragments.remedyCreate;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.ohadshai.savta.R;
import com.ohadshai.savta.databinding.FragmentRemedyCreateBinding;
import com.ohadshai.savta.utils.AlertDialogRtlHelper;

public class RemedyCreateFragment extends Fragment {

    private RemedyCreateViewModel _viewModel;
    private FragmentRemedyCreateBinding _binding;
    private boolean _isImageLoaded;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _viewModel = new ViewModelProvider(this).get(RemedyCreateViewModel.class);

        _binding = FragmentRemedyCreateBinding.inflate(inflater, container, false);
        View root = _binding.getRoot();

        _binding.remedyCreateFlPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageOptionsDialog();
            }
        });

        return root;
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