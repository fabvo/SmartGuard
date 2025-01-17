package com.example.smartguard;

import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                    // Holen der Apps mit deren Netzwerkdaten
                    Map<Integer, AppUsage> appUsageMap = findAppDataUsage(networkStatsManager, ConnectivityManager.TYPE_WIFI, startTime, currentTime);
                    appUsageMap.putAll(findAppDataUsage(networkStatsManager, ConnectivityManager.TYPE_MOBILE, startTime, currentTime));

                    // Anzeige der Apps und ihrer Netzwerkdaten
                    for (Map.Entry<Integer, AppUsage> entry : appUsageMap.entrySet()) {
                        String appName = getAppNameForUid(entry.getKey(), requireContext());
                        AppUsage usage = entry.getValue();
                        String usageData = "UID: " + entry.getKey() + " - App: " + appName + " - Downloaded: " + formatDataSize(usage.rxBytes) +
                                ", Uploaded: " + formatDataSize(usage.txBytes);
                        usageList.add(usageData);
                        Log.d(TAG, "UID: " + entry.getKey() + ", App: " + appName + ", RxBytes: " + usage.rxBytes + ", TxBytes: " + usage.txBytes);
                    }

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

    @RequiresApi(api = Build.VERSION_CODES.M)
    public Map<Integer, AppUsage> findAppDataUsage(NetworkStatsManager networkStatsManager, int networkType, long startTime, long endTime) {
        Map<Integer, AppUsage> appUsageMap = new HashMap<>();
        try {
            NetworkStats networkStats = networkStatsManager.querySummary(networkType, null, startTime, endTime);
            NetworkStats.Bucket bucket = new NetworkStats.Bucket();

            while (networkStats.hasNextBucket()) {
                networkStats.getNextBucket(bucket);
                int uid = bucket.getUid();
                long rxBytes = bucket.getRxBytes();
                long txBytes = bucket.getTxBytes();

                if (rxBytes > 0 || txBytes > 0) {
                    if (appUsageMap.containsKey(uid)) {
                        AppUsage usage = appUsageMap.get(uid);
                        usage.rxBytes += rxBytes;
                        usage.txBytes += txBytes;
                    } else {
                        appUsageMap.put(uid, new AppUsage(rxBytes, txBytes));
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching app data usage: " + e.getMessage());
        }
        return appUsageMap;
    }

    // Methode um den App-Namen basierend auf der UID zu bekommen
    public String getAppNameForUid(int uid, Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            String packageName = packageManager.getNameForUid(uid);
            if (packageName != null) {
                PackageInfo pInfo = packageManager.getPackageInfo(packageName, 0);
                return packageManager.getApplicationLabel(pInfo.applicationInfo).toString();
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, "Package name not found for UID: " + uid, e);
        } catch (Exception e) {
            Log.e(TAG, "Error resolving app name for UID: " + uid, e);
        }
        return "Unknown App (UID " + uid + ")";
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

    static class AppUsage {
        long txBytes;
        long rxBytes;

        AppUsage(long rxBytes, long txBytes) {
            this.rxBytes = rxBytes;
            this.txBytes = txBytes;
        }
    }
}