package com.example.smartguard;

import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworkMonitorFragment extends Fragment {

    private static final String TAG = "NetworkMonitorFragment";
    private ListView listView;
    private List<AppUsage> appUsageList;
    private AppUsageAdapter adapter;

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
        appUsageList = new ArrayList<>();
        adapter = new AppUsageAdapter(requireContext(), appUsageList);
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

                    appUsageList.clear();
                    Map<Integer, AppUsage> appUsageMap = findAppDataUsage(networkStatsManager, ConnectivityManager.TYPE_WIFI, startTime, currentTime);
                    appUsageMap.putAll(findAppDataUsage(networkStatsManager, ConnectivityManager.TYPE_MOBILE, startTime, currentTime));

                    for (Map.Entry<Integer, AppUsage> entry : appUsageMap.entrySet()) {
                        String appName = getAppNameForUid(entry.getKey(), requireContext());
                        Drawable appIcon = getAppIconForUid(entry.getKey(), requireContext());
                        AppUsage usage = entry.getValue();
                        appUsageList.add(new AppUsage(appName, appIcon,
                                usage.getRxBytes(), usage.getTxBytes(),
                                usage.getForegroundRxBytes(), usage.getForegroundTxBytes(),
                                usage.getBackgroundRxBytes(), usage.getBackgroundTxBytes()));
                    }

                    // Sortieren nach gesamtem Datenverbrauch
                    Collections.sort(appUsageList, new Comparator<AppUsage>() {
                        @Override
                        public int compare(AppUsage o1, AppUsage o2) {
                            return Long.compare(o2.getRxBytes() + o2.getTxBytes(), o1.getRxBytes() + o1.getTxBytes());
                        }
                    });

                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        }
    }

    private long calculateStartTime(String filter, long currentTime) {
        switch (filter) {
            case "Die letzte Stunde":
                return currentTime - (60 * 60 * 1000);
            case "Die letzten 24 Stunden":
                return currentTime - (24 * 60 * 60 * 1000);
            case "Die letzte Woche":
                return currentTime - (7 * 24 * 60 * 60 * 1000);
            case "Insgesamt":
            default:
                return 0;
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

                // Foreground and background separation is not directly available on all devices.
                long foregroundRxBytes = 0; // Placeholder logic
                long foregroundTxBytes = 0;
                long backgroundRxBytes = rxBytes - foregroundRxBytes;
                long backgroundTxBytes = txBytes - foregroundTxBytes;

                if (rxBytes > 0 || txBytes > 0) {
                    if (appUsageMap.containsKey(uid)) {
                        AppUsage usage = appUsageMap.get(uid);
                        usage.addUsage(rxBytes, txBytes, foregroundRxBytes, foregroundTxBytes, backgroundRxBytes, backgroundTxBytes);
                    } else {
                        appUsageMap.put(uid, new AppUsage("", null, rxBytes, txBytes, foregroundRxBytes, foregroundTxBytes, backgroundRxBytes, backgroundTxBytes));
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching app data usage: " + e.getMessage());
        }
        return appUsageMap;
    }

    public String getAppNameForUid(int uid, Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            String packageName = packageManager.getNameForUid(uid);
            if (packageName != null) {
                ApplicationInfo appInfo = packageManager.getApplicationInfo(packageName, 0);
                return packageManager.getApplicationLabel(appInfo).toString();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error resolving app name for UID: " + uid, e);
        }
        return "Unknown App (UID " + uid + ")";
    }

    public Drawable getAppIconForUid(int uid, Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            String packageName = packageManager.getNameForUid(uid);
            if (packageName != null) {
                ApplicationInfo appInfo = packageManager.getApplicationInfo(packageName, 0);
                return packageManager.getApplicationIcon(appInfo);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error resolving app icon for UID: " + uid, e);
        }
        return context.getDrawable(android.R.drawable.sym_def_app_icon);
    }

    static class AppUsageAdapter extends ArrayAdapter<AppUsage> {

        public AppUsageAdapter(@NonNull Context context, List<AppUsage> appUsageList) {
            super(context, 0, appUsageList);
        }

        @NonNull
        @Override
        public View getView(int position, @NonNull View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.network_usage_item, parent, false);
            }

            AppUsage appUsage = getItem(position);
            TextView appNameTextView = convertView.findViewById(R.id.appNameTextView);
            ImageView appIconImageView = convertView.findViewById(R.id.appIconImageView);
            TextView dataUsageTextView = convertView.findViewById(R.id.dataUsageTextView);
            TextView expandedDetailsTextView = convertView.findViewById(R.id.expandedDetailsTextView);

            appNameTextView.setText(appUsage.getName());
            appIconImageView.setImageDrawable(appUsage.getIcon());
            dataUsageTextView.setText(appUsage.getFormattedDataUsage());

            convertView.setOnClickListener(v -> {
                if (expandedDetailsTextView.getVisibility() == View.GONE) {
                    expandedDetailsTextView.setVisibility(View.VISIBLE);
                    expandedDetailsTextView.setText(appUsage.getDetailedUsage());
                } else {
                    expandedDetailsTextView.setVisibility(View.GONE);
                }
            });

            return convertView;
        }
    }
}