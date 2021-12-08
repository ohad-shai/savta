package com.ohadshai.savta.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.snackbar.Snackbar;
import com.ohadshai.savta.R;
import com.ohadshai.savta.databinding.ActivityLoginBinding;
import com.ohadshai.savta.utils.views.ProgressButton;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding _binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Forces the application to display in RTL:
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        setContentView(R.layout.activity_login);

        ProgressButton progressButton = findViewById(R.id.progressBtnSubmit);
        progressButton.setEnabled(true);
        progressButton.setOnClickListener(new ProgressButton.OnClickListener() {
            @Override
            public void onClick(ProgressButton progressButton) {
                if (progressButton.checkCanStartProgress()) {
                    progressButton.startProgress();
                    Snackbar.make(progressButton, "התחלנו תהליך", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

}