package com.example.smartguard;

import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private ListView appListView;
    private CustomPieChartView dataUsageChartView;
    private TextView totalDataUsageTextView;
    private TextView activeAppsTextView;
    private List<AppInfo> appList;
    private AppListAdapter appListAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        appListView = view.findViewById(R.id.appListView);
        dataUsageChartView = view.findViewById(R.id.dataUsageChartView);
        totalDataUsageTextView = view.findViewById(R.id.totalDataUsage);
        activeAppsTextView = view.findViewById(R.id.activeAppsCount);

        appList = new ArrayList<>();
        appListAdapter = new AppListAdapter(requireContext(), appList);
        appListView.setAdapter(appListAdapter);

        loadRunningApps();
        updateDataUsageChart();
    }

    private void loadRunningApps() {
        PackageManager packageManager = requireContext().getPackageManager();

        try {
            List<ApplicationInfo> installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

            for (ApplicationInfo appInfo : installedApps) {
                if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    // Nur benutzerinstallierte Apps
                    String appName = packageManager.getApplicationLabel(appInfo).toString();
                    Drawable appIcon = packageManager.getApplicationIcon(appInfo);

                    appList.add(new AppInfo(appName, appIcon));
                    Log.d(TAG, "App found: " + appName);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading running apps: " + e.getMessage());
        }

        activeAppsTextView.setText("Aktive Apps: " + appList.size());
        appListAdapter.notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void updateDataUsageChart() {
        long currentTime = System.currentTimeMillis();
        long startTime = currentTime - (60 * 60 * 1000); // Letzte Stunde

        Map<String, Float> dataUsage = new HashMap<>();
        float totalUsage = 0;

        NetworkStatsManager networkStatsManager = (NetworkStatsManager) requireContext().getSystemService(Context.NETWORK_STATS_SERVICE);
        NetworkMonitorFragment networkMonitorFragment = new NetworkMonitorFragment();

        Map<Integer, NetworkMonitorFragment.AppUsage> usageMap =
                networkMonitorFragment.findAppDataUsage(networkStatsManager, ConnectivityManager.TYPE_WIFI, startTime, currentTime);

        for (Map.Entry<Integer, NetworkMonitorFragment.AppUsage> entry : usageMap.entrySet()) {
            String appName = networkMonitorFragment.getAppNameForUid(entry.getKey(), requireContext());
            float totalUsageInMB = (entry.getValue().rxBytes + entry.getValue().txBytes) / 1_048_576f; // MB
            dataUsage.put(appName, totalUsageInMB);
            totalUsage += totalUsageInMB;
        }

        totalDataUsageTextView.setText(String.format("Gesamtnutzung: %.2f MB", totalUsage));
        if (totalUsage > 500) {
            totalDataUsageTextView.setTextColor(Color.RED);
        }

        dataUsageChartView.setData(dataUsage);
    }

    public static class AppInfo {
        String name;
        Drawable icon;

        public AppInfo(String name, Drawable icon) {
            this.name = name;
            this.icon = icon;
        }
    }
}