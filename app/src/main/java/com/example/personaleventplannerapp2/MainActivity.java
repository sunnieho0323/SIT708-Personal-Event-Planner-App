package com.example.personaleventplannerapp2;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. (NavHostFragment)
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            // 2.
            NavController navController = navHostFragment.getNavController();

            // 3.
            BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

            //
            bottomNav.setItemIconTintList(null);

            //
            NavigationUI.setupWithNavController(bottomNav, navController);
        }
    }
}