package com.example.smartguard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "AppSettings";
    private static final String KEY_FIRST_RUN = "first_run";

    HomeFragment homeFragment = new HomeFragment();
    NetworkMonitorFragment networkMonitorFragment = new NetworkMonitorFragment();
    PermissionAnalyzerFragment permissionAnalyzerFragment = new PermissionAnalyzerFragment();
    SettingsFragment settingsFragment = new SettingsFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
                        return true;
                    case R.id.network_monitor:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, networkMonitorFragment).commit();
                        return true;
                    case R.id.permission_analyzer:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, permissionAnalyzerFragment).commit();
                        return true;
                    case R.id.settings:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, settingsFragment).commit();
                        return true;
                }
                return false;
            }
        });

        // Überprüfen, ob die App zum ersten Mal geöffnet wird
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean firstRun = sharedPreferences.getBoolean(KEY_FIRST_RUN, true);

        if (firstRun) {
            showPermissionSetupDialog();
            sharedPreferences.edit().putBoolean(KEY_FIRST_RUN, false).apply();
        }
    }

    private void showPermissionSetupDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Berechtigungen erforderlich")
                .setMessage("Um alle Funktionen der App nutzen zu können, müssen einige Berechtigungen erteilt werden. Möchten Sie diese jetzt einrichten?")
                .setPositiveButton("Einrichten", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                    startActivity(intent);
                })
                .setNegativeButton("Später", null)
                .show();
    }
}