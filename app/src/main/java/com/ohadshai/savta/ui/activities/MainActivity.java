package com.ohadshai.savta.ui.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.appcompat.app.AppCompatActivity;

import com.ohadshai.savta.R;
import com.ohadshai.savta.databinding.ActivityMainBinding;
import com.ohadshai.savta.ui.dialogs.AboutDialog;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration _AppBarConfiguration;
    private ActivityMainBinding _binding;
    private NavController _navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Forces the application to display in RTL:
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        _binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(_binding.getRoot());
        setSupportActionBar(_binding.appBarMain.toolbar);

        NavigationView navigationView = _binding.navView;
        // Passing each menu ID as a set of Ids because each menu should be considered as top level destinations:
        _AppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_feed)
                .setOpenableLayout(_binding.drawerLayout)
                .build();
        _navController = Navigation.findNavController(this, R.id.content_main_nav_host);
        NavigationUI.setupActionBarWithNavController(this, _navController, _AppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, _navController);
        navigationView.setNavigationItemSelectedListener(this);

        _binding.appBarMain.fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

//        Intent intent = new Intent(this, LoginActivity.class);
//        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                AboutDialog.make(this).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.content_main_nav_host);
        boolean result = (NavigationUI.navigateUp(navController, _AppBarConfiguration) || super.onSupportNavigateUp());
        this.checkToUpdateLayoutByCurrentFragment();
        return result;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        // Checks to update the activity layout by the current fragment displaying:
        this.checkToUpdateLayoutByCurrentFragment();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Here we're overriding the Navigation Drawer item selection behavior, for our customization:
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_feed:
            case R.id.nav_user_remedies:
            case R.id.nav_user_settings:
                this.updateLayoutByFragment(id);
                break;
            case R.id.nav_logout:
                Snackbar.make(_binding.appBarMain.getRoot(), "התנתקות", Snackbar.LENGTH_LONG).show();
                break;
            default:
                throw new IndexOutOfBoundsException("Undefined menu item id in the navigation drawer.");
        }
        // This is for maintaining the behavior of the Navigation Drawer:
        NavigationUI.onNavDestinationSelected(item, _navController);
        if (_binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            _binding.drawerLayout.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    //region Private Methods

    /**
     * Updates the layout of the activity, according to the current fragment displaying.
     *
     * @param id The id of the fragment's menu item resource in the navigation drawer.
     */
    private void updateLayoutByFragment(int id) {
        switch (id) {
            case R.id.nav_feed:
                _binding.appBarMain.fabAdd.show();
                _binding.appBarMain.frameToolbarLogo.setVisibility(View.VISIBLE);
                break;
            case R.id.nav_user_remedies:
                _binding.appBarMain.fabAdd.show();
                _binding.appBarMain.frameToolbarLogo.setVisibility(View.GONE);
                break;
            case R.id.nav_user_settings:
                _binding.appBarMain.fabAdd.hide();
                _binding.appBarMain.frameToolbarLogo.setVisibility(View.GONE);
                break;
            default:
                throw new IndexOutOfBoundsException("Undefined menu item id in the navigation drawer, that is able to update the layout.");
        }
    }

    /**
     * Checks if to update the activity layout by the current fragment displaying.
     */
    private void checkToUpdateLayoutByCurrentFragment() {
        NavDestination navDestination = _navController.getCurrentDestination();
        if (navDestination != null) {
            this.updateLayoutByFragment(navDestination.getId());
        }
    }

    //endregion

}