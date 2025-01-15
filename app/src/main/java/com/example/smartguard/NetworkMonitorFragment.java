package com.example.smartguard;

import android.app.AppOpsManager;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class NetworkMonitorFragment extends Fragment {

    private static final String TAG = "NetworkMonitor";
    private ListView listView;
    private List<String> usageList;
    private ArrayAdapter<String> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_network_monitor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = view.findViewById(R.id.networkUsageListView);
        Spinner timeFilterSpinner = view.findViewById(R.id.timeFilterSpinner);
        usageList = new ArrayList<>();
        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, usageList);
        listView.setAdapter(adapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NetworkStatsManager networkStatsManager =
                    (NetworkStatsManager) requireContext().getSystemService(Context.NETWORK_STATS_SERVICE);

            ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                    requireContext(),
                    R.array.time_filter_options,
                    android.R.layout.simple_spinner_item
            );
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            timeFilterSpinner.setAdapter(spinnerAdapter);

            timeFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedFilter = (String) parent.getItemAtPosition(position);
                    long currentTime = System.currentTimeMillis();
                    long startTime = calculateStartTime(selectedFilter, currentTime);

                    usageList.clear();
                    getAppNetworkUsage(networkStatsManager, ConnectivityManager.TYPE_WIFI, startTime, currentTime, usageList);
                    getAppNetworkUsage(networkStatsManager, ConnectivityManager.TYPE_MOBILE, startTime, currentTime, usageList);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }
    }

    private long calculateStartTime(String filter, long currentTime) {
        switch (filter) {
            case "Die letzte Stunde":
                return currentTime - (60 * 60 * 1000); // 1 Stunde
            case "Die letzten 24 Stunden":
                return currentTime - (24 * 60 * 60 * 1000); // 24 Stunden
            case "Die letzte Woche":
                return currentTime - (7 * 24 * 60 * 60 * 1000); // 7 Tage
            case "Insgesamt":
            default:
                return 0; // Gesamtdaten
        }
    }

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
                    String usageData = appName + " - Downloaded: " + formatDataSize(rxBytes) +
                            ", Uploaded: " + formatDataSize(txBytes);
                    usageList.add(usageData);
                    Log.d(TAG, "App: " + appName + ", RxBytes: " + rxBytes + ", TxBytes: " + txBytes);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching app network usage: " + e.getMessage());
        }
    }

    private String getAppNameForUid(int uid) {
        PackageManager packageManager = requireContext().getPackageManager();
        try {
            String[] packageNames = packageManager.getPackagesForUid(uid);
            if (packageNames != null && packageNames.length > 0) {
                String packageName = packageNames[0];
                return packageManager.getApplicationLabel(
                        packageManager.getApplicationInfo(packageName, 0)).toString();
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, "Package name not found for UID: " + uid, e);
        } catch (Exception e) {
            Log.e(TAG, "Error resolving app name for UID: " + uid, e);
        }

        return getFallbackAppName(uid);
    }

    private String getFallbackAppName(int uid) {
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

    private String formatDataSize(long bytes) {
        if (bytes >= 1_073_741_824) { // 1 GB
            return String.format("%.2f GB", bytes / 1_073_741_824.0);
        } else if (bytes >= 1_048_576) { // 1 MB
            return String.format("%.2f MB", bytes / 1_048_576.0);
        } else if (bytes >= 1024) { // 1 KB
            return String.format("%.2f KB", bytes / 1024.0);
        } else {
            return bytes + " Bytes";
        }
    }
}