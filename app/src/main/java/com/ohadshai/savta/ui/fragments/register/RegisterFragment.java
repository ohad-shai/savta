package com.ohadshai.savta.ui.fragments.register;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.snackbar.Snackbar;
import com.ohadshai.savta.R;
import com.ohadshai.savta.data.UsersModel;
import com.ohadshai.savta.data.utils.OnRegisterCompleteListener;
import com.ohadshai.savta.databinding.FragmentRegisterBinding;
import com.ohadshai.savta.entities.User;
import com.ohadshai.savta.ui.activities.main.MainActivity;
import com.ohadshai.savta.utils.AndroidUtils;
import com.ohadshai.savta.utils.NetworkUtils;
import com.ohadshai.savta.utils.ValidationUtils;
import com.ohadshai.savta.utils.views.ProgressButton;

public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding _binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false);
        View rootView = _binding.getRoot();

        _binding.txtFullName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                validateForm(false);
                return false;
            }
        });
        _binding.txtEmail.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                validateForm(false);
                return false;
            }
        });
        _binding.txtPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                validateForm(false);
                return false;
            }
        });

        _binding.progressBtnRegister.setEnabled(false);
        _binding.progressBtnRegister.setOnClickListener(new ProgressButton.OnClickListener() {
            @Override
            public void onClick(ProgressButton progressButton) {
                boolean isValid = validateForm(true);
                if (isValid && !progressButton.isInProgress()) {
                    register();
                }
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

    /**
     * Validates the form, and optionally notifies the user about the invalid fields, and returns an indicator indicating whether the form is valid or not.
     *
     * @param notify An indicator indicating whether to notify the user about the invalid fields or not.
     * @return Returns true if the form is valid, otherwise false.
     * @apiNote Also, enables/disables the submit button according to the validation state.
     */
    private boolean validateForm(boolean notify) {
        String fullName = _binding.txtFullName.getText().toString().trim();
        String email = _binding.txtEmail.getText().toString().trim();
        String password = _binding.txtPassword.getText().toString().trim();

        // Checks full name is not empty, and the format is valid:
        if (TextUtils.isEmpty(fullName) || !ValidationUtils.isFullNameValid(fullName)) {
            if (notify) {
                _binding.txtFullName.setError(getString(R.string.full_name_required));
                Snackbar.make(requireView(), R.string.full_name_required, Snackbar.LENGTH_SHORT).show();
            }
            _binding.progressBtnRegister.setEnabled(false);
            return false;
        }
        // Checks email is not empty:
        if (TextUtils.isEmpty(email)) {
            if (notify) {
                _binding.txtEmail.setError(getString(R.string.email_required));
                Snackbar.make(requireView(), R.string.email_required, Snackbar.LENGTH_SHORT).show();
            }
            _binding.progressBtnRegister.setEnabled(false);
            return false;
        }
        // Checks email format is valid:
        if (!ValidationUtils.isEmailValid(email)) {
            if (notify) {
                _binding.txtEmail.setError(getString(R.string.email_invalid_format));
                Snackbar.make(requireView(), R.string.email_invalid_format, Snackbar.LENGTH_SHORT).show();
            }
            _binding.progressBtnRegister.setEnabled(false);
            return false;
        }
        // Checks password is not empty:
        if (TextUtils.isEmpty(password)) {
            if (notify) {
                _binding.txtPassword.setError(getString(R.string.password_required));
                Snackbar.make(requireView(), R.string.password_required, Snackbar.LENGTH_SHORT).show();
            }
            _binding.progressBtnRegister.setEnabled(false);
            return false;
        }
        // Checks password is minimum 6 letters:
        if (password.length() < 6) {
            if (notify) {
                _binding.txtPassword.setError(getString(R.string.password_minimum_6_letters));
                Snackbar.make(requireView(), R.string.password_minimum_6_letters, Snackbar.LENGTH_SHORT).show();
            }
            _binding.progressBtnRegister.setEnabled(false);
            return false;
        }
        _binding.progressBtnRegister.setEnabled(true);
        return true;
    }

    /**
     * Performs a register procedure.
     *
     * @apiNote NOTE: Make sure the form is validated before calling this method.
     */
    private void register() {
        if (NetworkUtils.checkIfNoNetworkToShowSnackBar(requireActivity(), requireView())) {
            return;
        }
        _binding.progressBtnRegister.startProgress();
        AndroidUtils.hideKeyboard(requireActivity());

        String fullName = _binding.txtFullName.getText().toString().trim();
        int fullNameDividerIndex = fullName.indexOf(' ');
        String firstName = fullName.substring(0, fullNameDividerIndex);
        String lastName = fullName.substring(fullNameDividerIndex + 1);
        String email = _binding.txtEmail.getText().toString().trim();
        String password = _binding.txtPassword.getText().toString().trim();

        UsersModel.getInstance().register(firstName, lastName, email, password, new OnRegisterCompleteListener() {
            @Override
            public void onSuccess(User user) {
                // Navigates to the MainActivity:
                FragmentActivity activity = requireActivity();
                startActivity(new Intent(activity, MainActivity.class));
                activity.finish();
            }

            @Override
            public void onCollision() {
                Snackbar.make(requireView(), R.string.email_already_taken, Snackbar.LENGTH_SHORT).show();
                _binding.progressBtnRegister.stopProgress();
            }

            @Override
            public void onFailure(Exception exception) {
                Snackbar.make(requireView(), R.string.failure_message, Snackbar.LENGTH_SHORT).show();
                _binding.progressBtnRegister.stopProgress();
            }
        });
    }

    //endregion

}