package com.ohadshai.savta.ui.activities.splash;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.ohadshai.savta.data.UsersModel;
import com.ohadshai.savta.entities.User;
import com.ohadshai.savta.ui.activities.login.LoginActivity;
import com.ohadshai.savta.ui.activities.main.MainActivity;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Checks if the current user is authenticated or not:
        User user = UsersModel.getInstance().getCurrentUser().getValue();
        if (user != null) {
            startActivity(new Intent(this, MainActivity.class));
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
        finish();
    }

}
