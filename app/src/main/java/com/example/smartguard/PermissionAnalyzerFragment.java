package com.example.smartguard;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class PermissionAnalyzerFragment extends Fragment {

    private ListView permissionsListView;
    private PackageManager packageManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_permission_analyzer, container, false);

        // Initialize the PackageManager
        packageManager = getActivity().getPackageManager();

        // Initialize the ListView
        permissionsListView = rootView.findViewById(R.id.permissionsListView);

        // Get the list of installed apps and their permissions
        List<String> appPermissions = getAppPermissions();

        // Set up an adapter to display the permissions
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, appPermissions);
        permissionsListView.setAdapter(adapter);

        return rootView;
    }

    // Get list of apps and their permissions
    private List<String> getAppPermissions() {
        List<String> appPermissions = new ArrayList<>();

        // Get the list of installed apps
        List<PackageInfo> packages = packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS);

        // Loop through each app and get its permissions
        for (PackageInfo packageInfo : packages) {
            if (packageInfo.requestedPermissions != null) {
                StringBuilder appInfo = new StringBuilder(packageInfo.packageName + ":\n");
                for (String permission : packageInfo.requestedPermissions) {
                    appInfo.append(permission).append("\n");
                }
                appPermissions.add(appInfo.toString());
            }
        }

        return appPermissions;
    }
}
