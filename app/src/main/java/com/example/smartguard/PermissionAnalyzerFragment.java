package com.example.smartguard;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PermissionAnalyzerFragment extends Fragment {

    private ListView permissionsListView;
    private PackageManager packageManager;

    // Liste sensibler Berechtigungen
    private static final List<String> SENSITIVE_PERMISSIONS = Arrays.asList(
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.ACCESS_COARSE_LOCATION",
            "android.permission.CAMERA",
            "android.permission.RECORD_AUDIO",
            "android.permission.READ_CONTACTS",
            "android.permission.WRITE_CONTACTS",
            "android.permission.READ_SMS",
            "android.permission.SEND_SMS",
            "android.permission.READ_PHONE_STATE",
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_permission_analyzer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        permissionsListView = view.findViewById(R.id.permissionsListView);
        packageManager = requireContext().getPackageManager();

        List<AppPermissionInfo> appPermissionList = getAppPermissions(requireContext());
        PermissionListAdapter adapter = new PermissionListAdapter(requireContext(), appPermissionList);
        permissionsListView.setAdapter(adapter);
    }

    private List<AppPermissionInfo> getAppPermissions(Context context) {
        List<AppPermissionInfo> appPermissionList = new ArrayList<>();
        List<PackageInfo> packages = packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS);

        for (PackageInfo packageInfo : packages) {
            Drawable appIcon = packageManager.getApplicationIcon(packageInfo.applicationInfo);
            String appName = packageManager.getApplicationLabel(packageInfo.applicationInfo).toString();
            StringBuilder permissions = new StringBuilder();

            if (packageInfo.requestedPermissions != null) {
                for (String permission : packageInfo.requestedPermissions) {
                    String readablePermission = getReadablePermissionName(permission);
                    if (SENSITIVE_PERMISSIONS.contains(permission)) {
                        // Sensible Berechtigungen fett markieren
                        permissions.append("• <b>⚠️ ").append(readablePermission).append("</b><br>");
                    } else {
                        // Normale Berechtigungen mit Bullet Point
                        permissions.append("• ").append(readablePermission).append("<br>");
                    }
                }
            } else {
                permissions.append("Keine Berechtigungen");
            }

            appPermissionList.add(new AppPermissionInfo(appName, appIcon, permissions.toString()));
        }

        return appPermissionList;
    }

    private String getReadablePermissionName(String permission) {
        try {
            PackageManager packageManager = requireContext().getPackageManager();

            // Hole Informationen zur Berechtigung
            PermissionInfo permissionInfo = packageManager.getPermissionInfo(permission, 0);

            // Versuche, das Label der Berechtigung zu laden
            CharSequence label = permissionInfo.loadLabel(packageManager);
            if (label != null && !label.toString().isEmpty()) {
                return label.toString();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // Wenn keine Beschreibung verfügbar ist, die Berechtigung direkt anzeigen
        return permission;
    }
}