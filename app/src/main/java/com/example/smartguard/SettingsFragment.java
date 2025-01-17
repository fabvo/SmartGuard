package com.example.smartguard;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SettingsFragment extends Fragment {

    private static final String PREFS_NAME = "AppSettings";
    private static final String KEY_NOTIFICATIONS = "notifications_enabled";
    private static final String KEY_USAGE_WARNING = "usage_warning_enabled";
    private static final String KEY_WARNING_THRESHOLD = "warning_threshold";

    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, 0);

        Switch notificationsSwitch = view.findViewById(R.id.notificationsSwitch);
        Switch usageWarningSwitch = view.findViewById(R.id.usageWarningSwitch);
        TextView thresholdValue = view.findViewById(R.id.thresholdValue);
        TextView adjustThreshold = view.findViewById(R.id.adjustThreshold);
        TextView managePermissions = view.findViewById(R.id.managePermissions);
        TextView specialAppAccess = view.findViewById(R.id.specialAppAccess);
        TextView usageAccessPermission = view.findViewById(R.id.usageAccessPermission);

        boolean notificationsEnabled = sharedPreferences.getBoolean(KEY_NOTIFICATIONS, true);
        boolean usageWarningEnabled = sharedPreferences.getBoolean(KEY_USAGE_WARNING, true);
        int warningThreshold = sharedPreferences.getInt(KEY_WARNING_THRESHOLD, 500);

        notificationsSwitch.setChecked(notificationsEnabled);
        usageWarningSwitch.setChecked(usageWarningEnabled);
        thresholdValue.setText(String.format("%d MB", warningThreshold));

        notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean(KEY_NOTIFICATIONS, isChecked).apply();
            Toast.makeText(requireContext(),
                    isChecked ? "Benachrichtigungen aktiviert" : "Benachrichtigungen deaktiviert",
                    Toast.LENGTH_SHORT).show();
        });

        usageWarningSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean(KEY_USAGE_WARNING, isChecked).apply();
            Toast.makeText(requireContext(),
                    isChecked ? "Warnung bei hoher Datennutzung aktiviert" : "Warnung bei hoher Datennutzung deaktiviert",
                    Toast.LENGTH_SHORT).show();
        });

        adjustThreshold.setOnClickListener(v -> {
            ThresholdDialogFragment dialog = new ThresholdDialogFragment(currentThreshold -> {
                sharedPreferences.edit().putInt(KEY_WARNING_THRESHOLD, currentThreshold).apply();
                thresholdValue.setText(String.format("%d MB", currentThreshold));
                Toast.makeText(requireContext(), "Warnschwelle aktualisiert", Toast.LENGTH_SHORT).show();
            });
            dialog.show(getParentFragmentManager(), "ThresholdDialog");
        });

        managePermissions.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", requireContext().getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        });

        specialAppAccess.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(Settings.ACTION_IGNORE_BACKGROUND_DATA_RESTRICTIONS_SETTINGS);
                intent.setData(Uri.parse("package:" + requireContext().getPackageName()));
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(requireContext(),
                        "Diese Spezialberechtigungsseite ist auf Ihrem Gerät nicht verfügbar.",
                        Toast.LENGTH_SHORT).show();
            }
        });

        usageAccessPermission.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(requireContext(),
                        "Die Nutzungszugriffsberechtigung ist auf Ihrem Gerät nicht verfügbar.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}