package com.ohadshai.savta.ui.activities.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.appcompat.app.AppCompatActivity;

import com.ohadshai.savta.R;
import com.ohadshai.savta.data.UsersModel;
import com.ohadshai.savta.data.utils.OnUserRefreshCompleteListener;
import com.ohadshai.savta.databinding.ActivityMainBinding;
import com.ohadshai.savta.entities.User;
import com.ohadshai.savta.ui.activities.login.LoginActivity;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, NavController.OnDestinationChangedListener {

    private AppBarConfiguration _AppBarConfiguration;
    private MainViewModel _viewModel;
    private ActivityMainBinding _binding;
    private NavController _navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Forces the application to display in RTL:
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        // Checks on the background that the user is still authenticated in the cloud (not locked out or something):
        this.refreshUserAuthentication();

        _viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        _binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(_binding.getRoot());
        setSupportActionBar(_binding.appBarMain.toolbar);

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
        TextView navViewHeaderTxtEmail = navViewHeader.findViewById(R.id.nav_header_main_txtEmail);

        // Gets the current user's info:
        LiveData<User> user = _viewModel.getCurrentUser();
        if (user == null) {
            throw new IllegalStateException("User cannot be null in MainActivity.");
        } else {
            // Listens to data changes (while the fragment is alive):
            user.observe(this, new Observer<User>() {
                @Override
                public void onChanged(User user) {
                    if (user != null) {
                        navViewHeaderTxtName.setText(user.getFullName());
                        navViewHeaderTxtEmail.setText(user.getEmail());
                    }
                }
            });
        }

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
     * Checks on the background that the user is still authenticated in the cloud (not locked out or deleted or something).
     */
    private void refreshUserAuthentication() {
        UsersModel.getInstance().refreshCurrentUser(new OnUserRefreshCompleteListener() {
            @Override
            public void onSuccess(User user) {
            }

            @Override
            public void onUnauthorized() {
                userLogout();
                Toast.makeText(getApplicationContext(), R.string.account_not_authorized, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Exception exception) {
            }
        });
    }

    /**
     * Logs out the current authenticated user.
     */
    private void userLogout() {
        UsersModel.getInstance().logoutCurrentUser(null);
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    //endregion

}