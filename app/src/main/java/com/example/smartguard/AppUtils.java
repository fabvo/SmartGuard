package com.example.smartguard;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class AppUtils {

    private static final String TAG = "AppUtils";

    public static List<HomeFragment.AppInfo> getInstalledAppsWithIcons(Context context) {
        List<HomeFragment.AppInfo> appList = new ArrayList<>();
        PackageManager packageManager = context.getPackageManager();

        try {
            List<ApplicationInfo> installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

            for (ApplicationInfo appInfo : installedApps) {
                if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    String appName = packageManager.getApplicationLabel(appInfo).toString();
                    Drawable appIcon = packageManager.getApplicationIcon(appInfo);
                    appList.add(new HomeFragment.AppInfo(appName, appIcon));
                    Log.d(TAG, "App found: " + appName);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading apps: " + e.getMessage());
        }

        return appList;
    }
}