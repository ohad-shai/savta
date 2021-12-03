package com.ohadshai.savta.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.ohadshai.savta.R;
import com.ohadshai.savta.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration _AppBarConfiguration;
    private ActivityMainBinding _binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Forces the application to display in RTL:
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        _binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(_binding.getRoot());

        setSupportActionBar(_binding.appBarMain.toolbar);
        _binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = _binding.drawerLayout;
        NavigationView navigationView = _binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        _AppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_feed, R.id.nav_user_remedies, R.id.nav_user_settings)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, _AppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

//        Intent intent = new Intent(this, LoginActivity.class);
//        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, _AppBarConfiguration) || super.onSupportNavigateUp();
    }
    
}