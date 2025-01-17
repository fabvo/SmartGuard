package com.example.smartguard;

import static com.example.smartguard.R.*;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    HomeFragment homeFragment = new HomeFragment();
    NetworkMonitorFragment networkMonitorFragment = new NetworkMonitorFragment();
    PermissionAnalyzerFragment permissionAnalyzerFragment = new PermissionAnalyzerFragment();
    SettingsFragment settingsFragment = new SettingsFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();

        BadgeDrawable badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.home);
        badgeDrawable.setVisible(true);
        badgeDrawable.setNumber(2);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()){
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
    }
}
