package com.ensat.retika;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Main activity of the application.
 * This activity serves as the main entry point after the user logs in.
 * It includes a bottom navigation bar for navigating between different fragments
 * and an options menu with a logout button.
 */
public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth; // Firebase Authentication instance

    /**
     * Called when the activity is starting. This is where most initialization should go:
     * calling setContentView(int) to inflate the activity's UI, initializing fields, and
     * setting up navigation.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously
     *                           being shut down, this Bundle contains the data it most
     *                           recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Check if the user is signed in (currentUser is null if not signed in)
        if (currentUser == null) {
            // Redirect to the login activity if the user is not signed in
            redirectToLogin();
        } else {
            // Set the main activity layout
            setContentView(R.layout.activity_main);

            // Find the NavHostFragment
            NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.nav_host_fragment);

            // Get the NavController from the NavHostFragment
            NavController navController = navHostFragment.getNavController();

            // Set up the BottomNavigationView with the NavController
            BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
            NavigationUI.setupWithNavController(bottomNavigationView, navController);
        }
    }

    /**
     * Redirects the user to the login activity.
     * This method is called when the user is not signed in or logs out.
     */
    private void redirectToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Prevent the user from going back to the main screen without logging in
    }

    /**
     * Initializes the options menu for the activity.
     * This method inflates the menu resource and adds it to the action bar.
     *
     * @param menu The options menu in which items are placed.
     * @return true to display the menu, false otherwise.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * Handles item selections in the options menu.
     * This method is called when an item in the options menu is selected.
     *
     * @param item The menu item that was selected.
     * @return true if the item selection was handled, false otherwise.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            // Sign out the user and redirect to the login activity
            FirebaseAuth.getInstance().signOut();
            redirectToLogin();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}