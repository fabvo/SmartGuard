package com.example.smartguard;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    HomeFragment homeFragment = new HomeFragment();
    NetworkMonitorFragment networkMonitorFragment = new NetworkMonitorFragment();
    PermissionAnalyzerFragment permissionAnalyzerFragment = new PermissionAnalyzerFragment();
    SettingsFragment settingsFragment = new SettingsFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);

        // Set initial fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, homeFragment)
                .commit();

        // Handle bottom navigation item selection
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, homeFragment)
                                .commit();
                        return true;
                    case R.id.network_monitor:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, networkMonitorFragment)
                                .commit();
                        return true;
                    case R.id.permission_analyzer:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, permissionAnalyzerFragment)
                                .commit();
                        return true;
                    case R.id.settings:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, settingsFragment)
                                .commit();
                        return true;
                    default:
                        return false;
                }
            }
        });

        // Floating Action Button click
        floatingActionButton.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "FAB clicked!", Toast.LENGTH_SHORT).show();
            // Handle FAB action
        });
    }
}