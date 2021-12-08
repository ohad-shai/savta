package com.ohadshai.savta.ui.activities;

import android.content.Intent;
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
                // Navigates to the "Create Remedy" fragment, but performs the right action (transition animation):
                NavDestination navDestination = _navController.getCurrentDestination();
                if (navDestination != null) {
                    if (navDestination.getId() == R.id.nav_feed) {
                        _navController.navigate(R.id.action_nav_feed_to_nav_remedy_create);
                    } else if (navDestination.getId() == R.id.nav_user_remedies) {
                        _navController.navigate(R.id.action_nav_user_remedies_to_nav_remedy_create);
                    }
                    updateLayoutByNavScreen(R.id.nav_remedy_create);
                }
            }
        });

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
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
        this.updateLayoutByCurrentNavScreen();
        return result;
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.updateLayoutByCurrentNavScreen();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.updateLayoutByCurrentNavScreen();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Here we're overriding the Navigation Drawer item selection behavior, for our customization:
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_feed:
            case R.id.nav_user_remedies:
            case R.id.nav_user_settings:
                this.updateLayoutByNavScreen(id);
                break;
            case R.id.nav_logout:
                Snackbar.make(_binding.appBarMain.getRoot(), "התנתקות", Snackbar.LENGTH_LONG).show();
                break;
            default:
                throw new IndexOutOfBoundsException("Undefined menu item id in the navigation drawer was selected.");
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
     * Updates the activity's layout, according to the specified navigation screen (from the nav-graph).
     *
     * @param id The id of the navigation screen (from the nav-graph) to update the activity's layout to.
     */
    private void updateLayoutByNavScreen(int id) {
        if (id == R.id.nav_feed) {
            _binding.appBarMain.fabAdd.show();
            _binding.appBarMain.frameToolbarLogo.setVisibility(View.VISIBLE);
        } else if (id == R.id.nav_user_remedies) {
            _binding.appBarMain.fabAdd.show();
            _binding.appBarMain.frameToolbarLogo.setVisibility(View.GONE);
        } else {
            _binding.appBarMain.fabAdd.hide();
            _binding.appBarMain.frameToolbarLogo.setVisibility(View.GONE);
        }
    }

    /**
     * Updates the activity's layout, according to the current navigation screen displaying (from the nav-graph).
     */
    private void updateLayoutByCurrentNavScreen() {
        NavDestination navDestination = _navController.getCurrentDestination();
        if (navDestination != null) {
            this.updateLayoutByNavScreen(navDestination.getId());
        }
    }

    //endregion

}