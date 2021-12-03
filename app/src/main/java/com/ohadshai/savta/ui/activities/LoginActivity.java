package com.ohadshai.savta.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.ohadshai.savta.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Forces the application to display in RTL:
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        setContentView(R.layout.activity_login);
    }

}