package com.example.smartguard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "AppSettings";
    private static final String KEY_FIRST_RUN = "first_run";

    private HomeFragment homeFragment = new HomeFragment();
    private NetworkMonitorFragment networkMonitorFragment = new NetworkMonitorFragment();
    private PermissionAnalyzerFragment permissionAnalyzerFragment = new PermissionAnalyzerFragment();
    private SettingsFragment settingsFragment = new SettingsFragment();

    private TextView headerTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Header-Referenz
        headerTitle = findViewById(R.id.headerTitle);

        // Bottom Navigation und Fragment-Initialisierung
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        loadFragment(homeFragment, "Home");

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        loadFragment(homeFragment, "Home");
                        return true;
                    case R.id.network_monitor:
                        loadFragment(networkMonitorFragment, "Network Monitor");
                        return true;
                    case R.id.permission_analyzer:
                        loadFragment(permissionAnalyzerFragment, "Permission Analyzer");
                        return true;
                    case R.id.settings:
                        loadFragment(settingsFragment, "Settings");
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

    private void loadFragment(androidx.fragment.app.Fragment fragment, String title) {
        // Fragment wechseln
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit();

        // Header-Titel aktualisieren
        if (headerTitle != null) {
            headerTitle.setText(title);
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