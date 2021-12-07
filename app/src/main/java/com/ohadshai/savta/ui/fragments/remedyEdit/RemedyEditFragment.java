package com.ohadshai.savta.ui.fragments.remedyEdit;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.ohadshai.savta.R;
import com.ohadshai.savta.databinding.FragmentRemedyEditBinding;
import com.ohadshai.savta.utils.AlertDialogRtlHelper;

public class RemedyEditFragment extends Fragment {

    private RemedyEditViewModel _viewModel;
    private FragmentRemedyEditBinding _binding;
    private boolean _isImageLoaded;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _viewModel = new ViewModelProvider(this).get(RemedyEditViewModel.class);

        _binding = FragmentRemedyEditBinding.inflate(inflater, container, false);
        View root = _binding.getRoot();

        _binding.remedyEditFlPhotoButton.setOnClickListener(new View.OnClickListener() {
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
        builder.setTitle(R.string.remedy_image);
        String[] imageOptions;
        if (_isImageLoaded) {
            imageOptions = new String[]{getString(R.string.camera), getString(R.string.gallery), getString(R.string.delete_image)};
        } else {
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