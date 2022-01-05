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
import com.ohadshai.savta.data.utils.OnEmailUpdateCompleteListener;
import com.ohadshai.savta.entities.User;
import com.ohadshai.savta.utils.AndroidUtils;
import com.ohadshai.savta.utils.ValidationUtils;
import com.ohadshai.savta.utils.views.ProgressButton;

/**
 * Represents a dialog for updating the current user's email.
 */
public class UpdateEmailDialog extends DialogFragment {
    private final String DIALOG_TAG = "dialog_update_email";
    private final AppCompatActivity _activity;
    private User _user;
    private EditText _txtPassword, _txtEmail;
    private ProgressButton _progressBtnSave;
    private OnUpdateSuccessListener _onUpdateSuccessListener;

    private UpdateEmailDialog(@NonNull AppCompatActivity activity) {
        _activity = activity;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.dialog_update_email, null);

        // Gets the current user's info:
        _user = UsersModel.getInstance().getCurrentUser();
        if (_user == null) {
            throw new IllegalStateException("User cannot be null in UpdateEmailDialog.");
        }

        _txtPassword = view.findViewById(R.id.txtPassword);
        _txtPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                validateForm(false);
                return false;
            }
        });

        _txtEmail = view.findViewById(R.id.txtEmail);
        _txtEmail.setText(_user.getEmail());
        _txtEmail.setOnKeyListener(new View.OnKeyListener() {
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
        builder.setTitle(R.string.change_email);
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
    public static UpdateEmailDialog make(@NonNull AppCompatActivity activity) {
        return new UpdateEmailDialog(activity);
    }

    //endregion

    //region Public API

    /**
     * Sets a listener for update success from the dialog.
     *
     * @param listener The listener to set.
     * @return Returns the dialog instance.
     */
    public UpdateEmailDialog setOnUpdateSuccessListener(OnUpdateSuccessListener listener) {
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
        String password = _txtPassword.getText().toString().trim();
        String email = _txtEmail.getText().toString().trim();

        // Checks the new email is different from the current one:
        if (email.equals(_user.getEmail())) {
            _progressBtnSave.setEnabled(false);
            return false;
        }
        // Checks password is not empty:
        if (TextUtils.isEmpty(password)) {
            if (alert) {
                _txtPassword.setError(getString(R.string.password_required));
            }
            _progressBtnSave.setEnabled(false);
            return false;
        }
        // Checks password is minimum 6 letters:
        if (password.length() < 6) {
            if (alert) {
                _txtPassword.setError(getString(R.string.password_minimum_6_letters));
            }
            _progressBtnSave.setEnabled(false);
            return false;
        }
        // Checks email is not empty:
        if (TextUtils.isEmpty(email)) {
            if (alert) {
                _txtEmail.setError(getString(R.string.email_required));
            }
            _progressBtnSave.setEnabled(false);
            return false;
        }
        // Checks email format is valid:
        if (!ValidationUtils.isEmailValid(email)) {
            if (alert) {
                _txtEmail.setError(getString(R.string.email_invalid_format));
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

        String password = _txtPassword.getText().toString().trim();
        String email = _txtEmail.getText().toString().trim();

        UsersModel.getInstance().updateEmailAddress(password, email, new OnEmailUpdateCompleteListener() {
            @Override
            public void onSuccess(User user) {
                if (_onUpdateSuccessListener != null) {
                    _onUpdateSuccessListener.onUpdateSuccess(user.getEmail());
                }
                dismiss();
            }

            @Override
            public void onCollision() {
                _progressBtnSave.stopProgress();
                setCancelable(true);
                Toast.makeText(requireContext(), R.string.email_already_taken, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onInvalidCredentials() {
                _progressBtnSave.stopProgress();
                setCancelable(true);
                Toast.makeText(requireContext(), R.string.password_incorrect, Toast.LENGTH_SHORT).show();
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
        void onUpdateSuccess(String email);
    }

}
