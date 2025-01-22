package com.example.smartguard;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private ListView appListView;
    private TextView totalDataUsageTextView;
    private TextView activeAppsTextView;
    private List<AppInfo> appList;
    private AppListAdapter appListAdapter;

    private ActivityManager activityManager;

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
        totalDataUsageTextView = view.findViewById(R.id.totalDataUsage);
        activeAppsTextView = view.findViewById(R.id.activeAppsCount);

        appList = new ArrayList<>();
        appListAdapter = new AppListAdapter(requireContext(), appList);
        appListView.setAdapter(appListAdapter);

        activityManager = (ActivityManager) requireContext().getSystemService(Context.ACTIVITY_SERVICE);

        loadActiveApps();
    }

    private void loadActiveApps() {
        PackageManager packageManager = requireContext().getPackageManager();

        // Erhalte alle installierten Apps
        List<ApplicationInfo> installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        int activeAppsCount = 0;

        try {
            // Hole die Liste der aktuell laufenden Tasks
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = activityManager.getRunningAppProcesses();

            // Check, ob die App in der RunningAppProcess-Liste enthalten ist
            for (ApplicationInfo appInfo : installedApps) {
                if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    // Nur benutzerinstallierte Apps

                    boolean isAppRunning = false;

                    // Überprüfe, ob die App im Hintergrund läuft
                    for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                        if (processInfo.processName.equals(appInfo.packageName)) {
                            isAppRunning = true;
                            break;
                        }
                    }

                    if (isAppRunning) {
                        String appName = packageManager.getApplicationLabel(appInfo).toString();
                        Drawable appIcon = packageManager.getApplicationIcon(appInfo);

                        appList.add(new AppInfo(appName, appIcon));
                        activeAppsCount++;
                    }

                    Log.d(TAG, "App found: " + appInfo.packageName + " - Active: " + isAppRunning);
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "Error loading active apps: " + e.getMessage());
        }

        int totalAppsCount = installedApps.size();

        // Setze die Textansicht, um aktive und gesamte Apps anzuzeigen
        activeAppsTextView.setText("Aktive Apps: " + activeAppsCount + "/" + totalAppsCount);
        appListAdapter.notifyDataSetChanged();
    }

    // AppInfo-Klasse
    public static class AppInfo {
        String name;
        Drawable icon;

        public AppInfo(String name, Drawable icon) {
            this.name = name;
            this.icon = icon;
        }
    }

    // Methoden zur Berechtigungsprüfung für PACKAGE_USAGE_STATS
    public static boolean isUsageStatsPermissionGranted(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES).contains("PACKAGE_USAGE_STATS");
        }
        return false;
    }
}