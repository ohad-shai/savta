package com.ohadshai.savta.ui.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.ohadshai.savta.R;
import com.ohadshai.savta.data.UsersModel;
import com.ohadshai.savta.data.utils.OnLoginCompleteListener;
import com.ohadshai.savta.entities.User;
import com.ohadshai.savta.utils.AndroidUtils;
import com.ohadshai.savta.utils.views.ProgressButton;

/**
 * Represents a dialog for deleting the current user's account.
 */
public class DeleteAccountDialog extends DialogFragment {
    private final String DIALOG_TAG = "dialog_delete_account";
    private final AppCompatActivity _activity;
    private EditText _txtPassword;
    private ProgressButton _progressBtnDelete;
    private OnDeleteSuccessListener _onDeleteSuccessListener;

    private DeleteAccountDialog(@NonNull AppCompatActivity activity) {
        _activity = activity;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.dialog_delete_account, null);

        _txtPassword = view.findViewById(R.id.txtPassword);
        _txtPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                validateForm(false);
                return false;
            }
        });

        _progressBtnDelete = view.findViewById(R.id.progressBtnDelete);
        _progressBtnDelete.setEnabled(false);
        _progressBtnDelete.setOnClickListener(new ProgressButton.OnClickListener() {
            @Override
            public void onClick(ProgressButton progressButton) {
                if (validateForm(true) && !progressButton.isInProgress()) {
                    deleteAccount();
                }
            }
        });

        Button btnCancel = view.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCancelable()) {
                    dismiss();
                }
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(_activity);
        builder.setTitle(R.string.delete_account);
        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dlg) {
                // Sets the title and message direction to RTL:
                dialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        });
        return dialog;
    }

    //region Public Static API

    /**
     * Makes a new instance of the dialog.
     *
     * @param activity The caller activity.
     * @return Returns the instance of the dialog.
     */
    public static DeleteAccountDialog make(@NonNull AppCompatActivity activity) {
        return new DeleteAccountDialog(activity);
    }

    //endregion

    //region Public API

    /**
     * Sets a listener for delete success from the dialog.
     *
     * @param listener The listener to set.
     * @return Returns the dialog instance.
     */
    public DeleteAccountDialog setOnDeleteSuccessListener(OnDeleteSuccessListener listener) {
        _onDeleteSuccessListener = listener;
        return this;
    }

    /**
     * Shows the dialog.
     */
    public void show() {
        super.show(_activity.getSupportFragmentManager(), DIALOG_TAG);
    }

    //endregion

    //region Private Methods

    /**
     * Validates the form, and returns an indicator indicating whether the form is valid or not.
     *
     * @param alert An indicator indicating whether to alert the user on the validation errors or not.
     * @return Returns true if the form is valid, otherwise false.
     * @apiNote Also, enables/disables the submit button according to the validation state.
     */
    private boolean validateForm(boolean alert) {
        String password = _txtPassword.getText().toString().trim();

        // Checks password is not empty:
        if (TextUtils.isEmpty(password)) {
            if (alert) {
                _txtPassword.setError(getString(R.string.password_required));
            }
            _progressBtnDelete.setEnabled(false);
            return false;
        }
        // Checks password is minimum 6 letters:
        if (password.length() < 6) {
            if (alert) {
                _txtPassword.setError(getString(R.string.password_minimum_6_letters));
            }
            _progressBtnDelete.setEnabled(false);
            return false;
        }
        _progressBtnDelete.setEnabled(true);
        return true;
    }

    /**
     * Performs a delete account procedure.
     *
     * @apiNote NOTE: Make sure that the form is validated before calling this method.
     */
    private void deleteAccount() {
        _progressBtnDelete.startProgress();
        setCancelable(false);
        AndroidUtils.hideKeyboard(requireActivity());

        String password = _txtPassword.getText().toString().trim();

        UsersModel.getInstance().delete(password, new OnLoginCompleteListener() {
            @Override
            public void onSuccess(User user) {
                if (_onDeleteSuccessListener != null) {
                    _onDeleteSuccessListener.onDeleteSuccess();
                }
                dismiss();
            }

            @Override
            public void onInvalidCredentials() {
                _progressBtnDelete.stopProgress();
                setCancelable(true);
                Toast.makeText(requireContext(), R.string.password_incorrect, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception exception) {
                _progressBtnDelete.stopProgress();
                setCancelable(true);
                Toast.makeText(requireContext(), R.string.failure_message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //endregion

    /**
     * Represents a listener for delete success.
     */
    public interface OnDeleteSuccessListener {
        void onDeleteSuccess();
    }

}
