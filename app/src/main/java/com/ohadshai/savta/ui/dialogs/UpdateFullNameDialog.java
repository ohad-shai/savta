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
import com.ohadshai.savta.data.utils.OnCompleteListener;
import com.ohadshai.savta.entities.User;
import com.ohadshai.savta.utils.AndroidUtils;
import com.ohadshai.savta.utils.views.ProgressButton;

/**
 * Represents a dialog for updating the current user's full name.
 */
public class UpdateFullNameDialog extends DialogFragment {
    private final String DIALOG_TAG = "dialog_update_full_name";
    private final AppCompatActivity _activity;
    private User _user;
    private EditText _txtFirstName, _txtLastName;
    private ProgressButton _progressBtnSave;
    private OnUpdateSuccessListener _onUpdateSuccessListener;

    private UpdateFullNameDialog(@NonNull AppCompatActivity activity) {
        _activity = activity;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.dialog_update_full_name, null);

        // Gets the current user's info:
        _user = UsersModel.getInstance().getCurrentUser().getValue();
        if (_user == null) {
            throw new IllegalStateException("User cannot be null in UpdateFullNameDialog.");
        }

        _txtFirstName = view.findViewById(R.id.txtFirstName);
        _txtFirstName.setText(_user.getFirstName());
        _txtFirstName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                validateForm(false);
                return false;
            }
        });

        _txtLastName = view.findViewById(R.id.txtLastName);
        _txtLastName.setText(_user.getLastName());
        _txtLastName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                validateForm(false);
                return false;
            }
        });

        _progressBtnSave = view.findViewById(R.id.progressBtnSave);
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
        builder.setTitle(R.string.change_full_name);
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
    public static UpdateFullNameDialog make(@NonNull AppCompatActivity activity) {
        return new UpdateFullNameDialog(activity);
    }

    //endregion

    //region Public API

    /**
     * Sets a listener for update success from the dialog.
     *
     * @param listener The listener to set.
     * @return Returns the dialog instance.
     */
    public UpdateFullNameDialog setOnUpdateSuccessListener(OnUpdateSuccessListener listener) {
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
        String firstName = _txtFirstName.getText().toString().trim();
        String lastName = _txtLastName.getText().toString().trim();

        // Checks first name is not empty:
        if (TextUtils.isEmpty(firstName)) {
            if (alert) {
                _txtFirstName.setError(getString(R.string.first_name_required));
            }
            _progressBtnSave.setEnabled(false);
            return false;
        }
        // Checks last name is not empty:
        if (TextUtils.isEmpty(lastName)) {
            if (alert) {
                _txtLastName.setError(getString(R.string.last_name_required));
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

        String firstName = _txtFirstName.getText().toString().trim();
        String lastName = _txtLastName.getText().toString().trim();

        UsersModel.getInstance().updateFullName(firstName, lastName, new OnCompleteListener() {
            @Override
            public void onSuccess() {
                if (_onUpdateSuccessListener != null) {
                    _onUpdateSuccessListener.onUpdateSuccess(firstName, lastName);
                }
                dismiss();
            }

            @Override
            public void onFailure() {
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
        void onUpdateSuccess(String firstName, String lastName);
    }

}
