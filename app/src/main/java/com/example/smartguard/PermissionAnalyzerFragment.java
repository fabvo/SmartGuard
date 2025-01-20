package com.example.smartguard;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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
import java.util.List;

public class PermissionAnalyzerFragment extends Fragment {

    private ListView permissionsListView;
    private PackageManager packageManager;

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
                    permissions.append(permission).append("\n");
                }
            } else {
                permissions.append("Keine Berechtigungen");
            }

            appPermissionList.add(new AppPermissionInfo(appName, appIcon, permissions.toString()));
        }

        return appPermissionList;
    }
}