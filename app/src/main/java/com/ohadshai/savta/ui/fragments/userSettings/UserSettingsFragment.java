package com.ohadshai.savta.ui.fragments.userSettings;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.ohadshai.savta.R;

public class UserSettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.user_settings, rootKey);
    }

}