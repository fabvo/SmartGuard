package com.example.smartguard;

import android.app.AppOpsManager;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class NetworkMonitorFragment extends Fragment {

    private static final String TAG = "NetworkMonitor";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_network_monitor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView listView = view.findViewById(R.id.networkUsageListView);
        List<String> usageList = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NetworkStatsManager networkStatsManager =
                    (NetworkStatsManager) requireContext().getSystemService(Context.NETWORK_STATS_SERVICE);

            long startTime = 0;
            long endTime = System.currentTimeMillis();

            // WLAN-Daten abrufen
            getAppNetworkUsage(networkStatsManager, ConnectivityManager.TYPE_WIFI, startTime, endTime, usageList);

            // Mobile Daten abrufen
            getAppNetworkUsage(networkStatsManager, ConnectivityManager.TYPE_MOBILE, startTime, endTime, usageList);

            // Daten an Adapter binden
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, usageList);
            listView.setAdapter(adapter);
        }
    }

    // **Daten des gesamten Geräts abrufen**
    private long getDeviceNetworkUsage(NetworkStatsManager networkStatsManager, int networkType, long startTime, long endTime) {
        try {
            NetworkStats.Bucket bucket = networkStatsManager.querySummaryForDevice(networkType, null, startTime, endTime);
            long totalUsage = bucket.getRxBytes() + bucket.getTxBytes();
            Log.d(TAG, "Device total usage: " + totalUsage + " Bytes for networkType: " + networkType);
            return totalUsage;
        } catch (Exception e) {
            Log.e(TAG, "Error fetching device network usage: " + e.getMessage());
            return 0;
        }
    }

    // **Daten für einzelne Apps abrufen**
    private void getAppNetworkUsage(NetworkStatsManager networkStatsManager, int networkType, long startTime, long endTime, List<String> usageList) {
        try {
            NetworkStats networkStats = networkStatsManager.querySummary(networkType, null, startTime, endTime);
            NetworkStats.Bucket bucket = new NetworkStats.Bucket();

            while (networkStats.hasNextBucket()) {
                networkStats.getNextBucket(bucket);
                int uid = bucket.getUid();
                long rxBytes = bucket.getRxBytes();
                long txBytes = bucket.getTxBytes();

                if (rxBytes > 0 || txBytes > 0) {
                    String appName = getAppNameForUid(uid);
                    String usageData = appName + " - Downloaded: " + rxBytes + " Bytes, Uploaded: " + txBytes + " Bytes";
                    usageList.add(usageData);
                    Log.d(TAG, "App: " + appName + ", RxBytes: " + rxBytes + ", TxBytes: " + txBytes);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching app network usage: " + e.getMessage());
        }
    }

    // **UID in App-Namen auflösen**
    private String getAppNameForUid(int uid) {
        try {
            String[] packageNames = requireContext().getPackageManager().getPackagesForUid(uid);
            if (packageNames != null && packageNames.length > 0) {
                // Hole den App-Namen
                return requireContext().getPackageManager().getApplicationLabel(
                        requireContext().getPackageManager().getApplicationInfo(packageNames[0], 0)).toString();
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, "Package name not found for UID: " + uid, e);
        } catch (Exception e) {
            Log.e(TAG, "Error resolving app name for UID: " + uid, e);
        }

        // Fallback für unbekannte UID-Werte
        if (uid == android.os.Process.SYSTEM_UID) {
            return "Android System";
        } else if (uid == android.os.Process.PHONE_UID) {
            return "Phone Service";
        } else if (uid >= 1000 && uid < 2000) {
            return "Core Android Service (UID " + uid + ")";
        } else if (uid >= 2000 && uid < 3000) {
            return "System App (UID " + uid + ")";
        } else {
            return "Unknown App (UID " + uid + ")";
        }
    }


    // **Berechtigungsprüfung**
    private boolean isUsageStatsPermissionGranted() {
        AppOpsManager appOps = (AppOpsManager) requireContext().getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), requireContext().getPackageName());
        boolean granted = (mode == AppOpsManager.MODE_ALLOWED);
        Log.d(TAG, "Usage stats permission granted: " + granted);
        return granted;
    }
}