package com.ohadshai.savta.ui.fragments.userSettings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.material.snackbar.Snackbar;
import com.ohadshai.savta.R;
import com.ohadshai.savta.data.UsersModel;
import com.ohadshai.savta.entities.User;
import com.ohadshai.savta.ui.activities.login.LoginActivity;
import com.ohadshai.savta.ui.dialogs.AboutDialog;
import com.ohadshai.savta.ui.dialogs.DeleteAccountDialog;
import com.ohadshai.savta.ui.dialogs.UpdateEmailDialog;
import com.ohadshai.savta.ui.dialogs.UpdateFullNameDialog;
import com.ohadshai.savta.ui.dialogs.UpdatePasswordDialog;

public class UserSettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setHasOptionsMenu(true);
        setPreferencesFromResource(R.xml.user_settings, rootKey);

        User user = UsersModel.getInstance().getCurrentUser().getValue();
        if (user == null) {
            throw new IllegalStateException("User cannot be null in UserSettingsFragment.");
        }

        // Full Name:
        Preference prefFullName = findPreference(getString(R.string.pref_key_full_name));
        prefFullName.setSummary(user.getFullName());
        prefFullName.setOnPreferenceClickListener(this);

        // Email:
        Preference prefEmail = findPreference(getString(R.string.pref_key_email));
        prefEmail.setSummary(user.getEmail());
        prefEmail.setOnPreferenceClickListener(this);

        // Change Password:
        findPreference(getString(R.string.pref_key_change_password)).setOnPreferenceClickListener(this);

        // Delete Account:
        findPreference(getString(R.string.pref_key_delete_account)).setOnPreferenceClickListener(this);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_about) {
            AboutDialog.make((AppCompatActivity) requireActivity()).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        // Change Full Name:
        if (key.equals(getString(R.string.pref_key_full_name))) {
            UpdateFullNameDialog dialog = UpdateFullNameDialog.make((AppCompatActivity) requireActivity());
            dialog.setOnUpdateSuccessListener(new UpdateFullNameDialog.OnUpdateSuccessListener() {
                @Override
                public void onUpdateSuccess(String firstName, String lastName) {
                    preference.setSummary((firstName + " " + lastName));
                }
            });
            dialog.show();
        }
        // Change Email:
        else if (key.equals(getString(R.string.pref_key_email))) {
            UpdateEmailDialog dialog = UpdateEmailDialog.make((AppCompatActivity) requireActivity());
            dialog.setOnUpdateSuccessListener(new UpdateEmailDialog.OnUpdateSuccessListener() {
                @Override
                public void onUpdateSuccess(String email) {
                    preference.setSummary(email);
                }
            });
            dialog.show();
        }
        // Change Password:
        else if (key.equals(getString(R.string.pref_key_change_password))) {
            UpdatePasswordDialog dialog = UpdatePasswordDialog.make((AppCompatActivity) requireActivity());
            dialog.setOnUpdateSuccessListener(new UpdatePasswordDialog.OnUpdateSuccessListener() {
                @Override
                public void onUpdateSuccess() {
                    Snackbar.make(requireView(), R.string.password_changed_successfully, Snackbar.LENGTH_SHORT).show();
                }
            });
            dialog.show();
        }
        // Delete Account:
        else if (key.equals(getString(R.string.pref_key_delete_account))) {
            DeleteAccountDialog dialog = DeleteAccountDialog.make((AppCompatActivity) requireActivity());
            dialog.setOnDeleteSuccessListener(new DeleteAccountDialog.OnDeleteSuccessListener() {
                @Override
                public void onDeleteSuccess() {
                    Activity activity = requireActivity();
                    startActivity(new Intent(activity, LoginActivity.class));
                    activity.finish();
                    Toast.makeText(getContext(), R.string.account_deleted_successfully, Toast.LENGTH_SHORT).show();
                }
            });
            dialog.show();
        }
        return false;
    }

}