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
 * Represents a dialog for updating the current user's password.
 */
public class UpdatePasswordDialog extends DialogFragment {
    private final String DIALOG_TAG = "dialog_update_password";
    private final AppCompatActivity _activity;
    private EditText _txtCurrentPassword, _txtNewPassword, _txtNewPasswordConfirm;
    private ProgressButton _progressBtnSave;
    private OnUpdateSuccessListener _onUpdateSuccessListener;

    private UpdatePasswordDialog(@NonNull AppCompatActivity activity) {
        _activity = activity;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.dialog_update_password, null);

        _txtCurrentPassword = view.findViewById(R.id.txtCurrentPassword);
        _txtCurrentPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                validateForm(false);
                return false;
            }
        });

        _txtNewPassword = view.findViewById(R.id.txtNewPassword);
        _txtNewPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                validateForm(false);
                return false;
            }
        });

        _txtNewPasswordConfirm = view.findViewById(R.id.txtNewPasswordConfirm);
        _txtNewPasswordConfirm.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                validateForm(false);
                return false;
            }
        });

        _progressBtnSave = view.findViewById(R.id.progressBtnSave);
        _progressBtnSave.setEnabled(false);
        _progressBtnSave.setOnClickListener(new ProgressButton.OnClickListener() {
            @Override
            public void onClick(ProgressButton progressButton) {
                if (validateForm(true) && !progressButton.isInProgress()) {
                    saveChanges();
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
        builder.setTitle(R.string.change_password);
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
    public static UpdatePasswordDialog make(@NonNull AppCompatActivity activity) {
        return new UpdatePasswordDialog(activity);
    }

    //endregion

    //region Public API

    /**
     * Sets a listener for update success from the dialog.
     *
     * @param listener The listener to set.
     * @return Returns the dialog instance.
     */
    public UpdatePasswordDialog setOnUpdateSuccessListener(OnUpdateSuccessListener listener) {
        _onUpdateSuccessListener = listener;
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
        String currentPassword = _txtCurrentPassword.getText().toString().trim();
        String newPassword = _txtNewPassword.getText().toString().trim();
        String newPasswordConfirm = _txtNewPasswordConfirm.getText().toString().trim();

        // Checks current password is not empty:
        if (TextUtils.isEmpty(currentPassword)) {
            if (alert) {
                _txtCurrentPassword.setError(getString(R.string.password_required));
            }
            _progressBtnSave.setEnabled(false);
            return false;
        }
        // Checks current password is minimum 6 letters:
        if (currentPassword.length() < 6) {
            if (alert) {
                _txtCurrentPassword.setError(getString(R.string.password_minimum_6_letters));
            }
            _progressBtnSave.setEnabled(false);
            return false;
        }
        // Checks new password is not empty:
        if (TextUtils.isEmpty(newPassword)) {
            if (alert) {
                _txtNewPassword.setError(getString(R.string.password_required));
            }
            _progressBtnSave.setEnabled(false);
            return false;
        }
        // Checks current password is minimum 6 letters:
        if (newPassword.length() < 6) {
            if (alert) {
                _txtNewPassword.setError(getString(R.string.password_minimum_6_letters));
            }
            _progressBtnSave.setEnabled(false);
            return false;
        }
        // Checks new password confirm is not empty:
        if (TextUtils.isEmpty(newPasswordConfirm)) {
            if (alert) {
                _txtNewPasswordConfirm.setError(getString(R.string.password_required));
            }
            _progressBtnSave.setEnabled(false);
            return false;
        }
        // Checks current password is minimum 6 letters:
        if (newPasswordConfirm.length() < 6) {
            if (alert) {
                _txtNewPasswordConfirm.setError(getString(R.string.password_minimum_6_letters));
            }
            _progressBtnSave.setEnabled(false);
            return false;
        }
        // Checks current password and new password are different:
        if (currentPassword.equals(newPassword)) {
            if (alert) {
                _txtNewPassword.setError(getString(R.string.new_password_must_be_different));
            }
            _progressBtnSave.setEnabled(false);
            return false;
        }
        // Checks new password confirmed:
        if (!newPassword.equals(newPasswordConfirm)) {
            if (alert) {
                _txtNewPasswordConfirm.setError(getString(R.string.new_password_confirm_do_not_match));
            }
            _progressBtnSave.setEnabled(false);
            return false;
        }
        _progressBtnSave.setEnabled(true);
        return true;
    }

    /**
     * Performs a save procedure.
     *
     * @apiNote NOTE: Make sure that the form is validated before calling this method.
     */
    private void saveChanges() {
        _progressBtnSave.startProgress();
        setCancelable(false);
        AndroidUtils.hideKeyboard(requireActivity());

        String currentPassword = _txtCurrentPassword.getText().toString().trim();
        String newPassword = _txtNewPassword.getText().toString().trim();

        UsersModel.getInstance().updatePassword(currentPassword, newPassword, new OnLoginCompleteListener() {
            @Override
            public void onSuccess(User user) {
                if (_onUpdateSuccessListener != null) {
                    _onUpdateSuccessListener.onUpdateSuccess();
                }
                dismiss();
            }

            @Override
            public void onInvalidCredentials() {
                _progressBtnSave.stopProgress();
                setCancelable(true);
                Toast.makeText(requireContext(), R.string.current_password_incorrect, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception exception) {
                _progressBtnSave.stopProgress();
                setCancelable(true);
                Toast.makeText(requireContext(), R.string.failure_message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //endregion

    /**
     * Represents a listener for update success.
     */
    public interface OnUpdateSuccessListener {
        void onUpdateSuccess();
    }

}
