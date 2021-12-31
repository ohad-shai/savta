package com.ohadshai.savta.ui.fragments.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;
import com.ohadshai.savta.R;
import com.ohadshai.savta.data.UsersModel;
import com.ohadshai.savta.data.utils.OnLoginCompleteListener;
import com.ohadshai.savta.databinding.FragmentLoginBinding;
import com.ohadshai.savta.entities.User;
import com.ohadshai.savta.ui.activities.MainActivity;
import com.ohadshai.savta.utils.AndroidUtils;
import com.ohadshai.savta.utils.ValidationUtils;
import com.ohadshai.savta.utils.views.ProgressButton;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding _binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _binding = FragmentLoginBinding.inflate(inflater, container, false);
        View rootView = _binding.getRoot();

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

        _binding.progressBtnLogin.setEnabled(false);
        _binding.progressBtnLogin.setOnClickListener(new ProgressButton.OnClickListener() {
            @Override
            public void onClick(ProgressButton progressButton) {
                boolean isValid = validateForm(true);
                if (isValid) {
                    login();
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
        String email = _binding.txtEmail.getText().toString().trim();
        String password = _binding.txtPassword.getText().toString().trim();

        // Checks email is not empty:
        if (TextUtils.isEmpty(email)) {
            if (notify) {
                _binding.txtEmail.setError(getString(R.string.email_required));
                Snackbar.make(requireView(), R.string.email_required, Snackbar.LENGTH_SHORT).show();
            }
            _binding.progressBtnLogin.setEnabled(false);
            return false;
        }
        // Checks email format is valid:
        if (!ValidationUtils.isEmailValid(email)) {
            if (notify) {
                _binding.txtEmail.setError(getString(R.string.email_invalid_format));
                Snackbar.make(requireView(), R.string.email_invalid_format, Snackbar.LENGTH_SHORT).show();
            }
            _binding.progressBtnLogin.setEnabled(false);
            return false;
        }
        // Checks password is not empty:
        if (TextUtils.isEmpty(password)) {
            if (notify) {
                _binding.txtPassword.setError(getString(R.string.password_required));
                Snackbar.make(requireView(), R.string.password_required, Snackbar.LENGTH_SHORT).show();
            }
            _binding.progressBtnLogin.setEnabled(false);
            return false;
        }
        // Checks password is minimum 6 letters:
        if (password.length() < 6) {
            if (notify) {
                _binding.txtPassword.setError(getString(R.string.password_minimum_6_letters));
                Snackbar.make(requireView(), R.string.password_minimum_6_letters, Snackbar.LENGTH_SHORT).show();
            }
            _binding.progressBtnLogin.setEnabled(false);
            return false;
        }
        _binding.progressBtnLogin.setEnabled(true);
        return true;
    }

    /**
     * Performs a login procedure.
     *
     * @apiNote NOTE: Make sure the form is validated before calling this method.
     */
    private void login() {
        _binding.progressBtnLogin.startProgress();
        AndroidUtils.hideKeyboard(requireActivity());

        String email = _binding.txtEmail.getText().toString().trim();
        String password = _binding.txtPassword.getText().toString().trim();

        UsersModel.getInstance().login(email, password, new OnLoginCompleteListener() {
            @Override
            public void onSuccess(User user) {
                // Navigates to the MainActivity:
                FragmentActivity activity = requireActivity();
                startActivity(new Intent(activity, MainActivity.class));
                activity.finish();
            }

            @Override
            public void onInvalidCredentials() {
                Snackbar.make(requireView(), R.string.login_auth_invalid, Snackbar.LENGTH_SHORT).show();
                _binding.progressBtnLogin.stopProgress();
            }

            @Override
            public void onFailure(Exception exception) {
                Snackbar.make(requireView(), R.string.failure_message, Snackbar.LENGTH_SHORT).show();
                _binding.progressBtnLogin.stopProgress();
            }
        });
    }

    //endregion

}