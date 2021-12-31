package com.ohadshai.savta.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.appcompat.app.AppCompatActivity;

import com.ohadshai.savta.R;
import com.ohadshai.savta.data.UsersModel;
import com.ohadshai.savta.databinding.ActivityMainBinding;
import com.ohadshai.savta.entities.User;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, NavController.OnDestinationChangedListener {

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

        // Gets the current user's info:
        User user = UsersModel.getInstance().getCurrentUser();
        if (user == null) {
            throw new IllegalStateException("User cannot be null in MainActivity.");
        }

        NavigationView navView = _binding.navView;
        // Passing each menu ID as a set of Ids because each menu should be considered as top level destinations:
        _AppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_feed)
                .setOpenableLayout(_binding.drawerLayout)
                .build();
        _navController = Navigation.findNavController(this, R.id.content_main_nav_host);
        _navController.addOnDestinationChangedListener(this);
        NavigationUI.setupActionBarWithNavController(this, _navController, _AppBarConfiguration);
        NavigationUI.setupWithNavController(navView, _navController);
        navView.setNavigationItemSelectedListener(this);

        // Shows the current user's info in the navigation drawer header:
        View navViewHeader = navView.getHeaderView(0);
        TextView navViewHeaderTxtName = navViewHeader.findViewById(R.id.nav_header_main_txtName);
        navViewHeaderTxtName.setText(user.getFullName());
        TextView navViewHeaderTxtEmail = navViewHeader.findViewById(R.id.nav_header_main_txtEmail);
        navViewHeaderTxtEmail.setText(user.getEmail());

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
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.content_main_nav_host);
        return (NavigationUI.navigateUp(navController, _AppBarConfiguration) || super.onSupportNavigateUp());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // In order to be able to listen on item selections, we need to override the Navigation Drawer item selection behavior:
        int id = item.getItemId();
        if (id == R.id.nav_logout) {
            this.userLogout();
        }
        // This is for maintaining the behavior of the Navigation Drawer:
        NavigationUI.onNavDestinationSelected(item, _navController);
        if (_binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            _binding.drawerLayout.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
        this.updateLayoutByNavScreen(destination.getId());
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
     * Logs out the current authenticated user.
     */
    private void userLogout() {
        UsersModel.getInstance().logoutCurrentUser();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    //endregion

}