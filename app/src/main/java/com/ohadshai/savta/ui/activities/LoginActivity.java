package com.ohadshai.savta.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.ohadshai.savta.R;
import com.ohadshai.savta.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding _binding;
    private NavController _navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.checkUserAuthenticationToExit();

        // Forces the application to display in RTL:
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        _binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(_binding.getRoot());

        _navController = Navigation.findNavController(this, R.id.navHostLogin);

        _binding.btnLoginScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToScreen(R.id.nav_login);
            }
        });

        _binding.btnRegisterScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToScreen(R.id.nav_register);
            }
        });
    }

    //region Private Methods

    /**
     * Switches between the login and register screens in the navigation.
     *
     * @param id The id of the navigation screen in the nav graph, to navigate to.
     */
    private void switchToScreen(int id) {
        NavDestination navDestination = _navController.getCurrentDestination();
        if (navDestination != null) {
            // Switch from login to register:
            if (navDestination.getId() == R.id.nav_login && id == R.id.nav_register) {
                _navController.navigate(R.id.action_nav_login_to_nav_register);
                _binding.btnLoginScreen.setTypeface(null, Typeface.NORMAL);
                _binding.btnLoginScreen.setTextColor(getResources().getColor(R.color.secondary));
                _binding.btnRegisterScreen.setTypeface(null, Typeface.BOLD);
                _binding.btnRegisterScreen.setTextColor(getResources().getColor(R.color.text));
            }
            // Switch from register to login:
            else if (navDestination.getId() == R.id.nav_register && id == R.id.nav_login) {
                _navController.popBackStack();
                _binding.btnLoginScreen.setTypeface(null, Typeface.BOLD);
                _binding.btnLoginScreen.setTextColor(getResources().getColor(R.color.text));
                _binding.btnRegisterScreen.setTypeface(null, Typeface.NORMAL);
                _binding.btnRegisterScreen.setTextColor(getResources().getColor(R.color.secondary));
            }
        }
    }

    /**
     * Checks if the user is already authenticated - then navigates to the MainActivity.
     */
    private void checkUserAuthenticationToExit() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    //endregion

}