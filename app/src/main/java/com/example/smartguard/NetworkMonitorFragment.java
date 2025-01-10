package com.example.smartguard;

import android.app.AppOpsManager;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.content.pm.PackageManager;

public class NetworkMonitorFragment extends Fragment {

    private static final String TAG = "NetworkMonitor";
    private static final int REQUEST_READ_PHONE_STATE = 100;
    private TextView networkInfoTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_network_monitor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, "onViewCreated called");
        networkInfoTextView = view.findViewById(R.id.network_info);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!isUsageStatsPermissionGranted()) {
                Log.e(TAG, "Usage stats permission not granted. Redirecting to settings.");
                startActivity(new Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS));
                return;
            }

            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "READ_PHONE_STATE permission not granted.");
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_PHONE_STATE)) {
                    Log.d(TAG, "Showing permission rationale.");
                    ActivityCompat.requestPermissions(requireActivity(),
                            new String[]{Manifest.permission.READ_PHONE_STATE},
                            REQUEST_READ_PHONE_STATE);
                } else {
                    Log.d(TAG, "Permission permanently denied or first request. Redirecting to settings.");
                    networkInfoTextView.setText("Permission denied. Please enable it in settings.");
                    startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", requireContext().getPackageName(), null)));
                }
            } else {
                Log.d(TAG, "Permissions granted. Fetching network usage.");
                getNetworkUsage();
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_READ_PHONE_STATE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "READ_PHONE_STATE permission granted. Fetching network usage.");
                getNetworkUsage();
            } else {
                Log.e(TAG, "READ_PHONE_STATE permission denied.");
                networkInfoTextView.setText("Permission denied. Unable to fetch network usage data.");
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getNetworkUsage() {
        Log.d(TAG, "getNetworkUsage called");

        if (!isUsageStatsPermissionGranted()) {
            Log.e(TAG, "Usage stats permission not granted. Redirecting to settings.");
            startActivity(new Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS));
            return;
        }

        NetworkStatsManager networkStatsManager =
                (NetworkStatsManager) requireContext().getSystemService(Context.NETWORK_STATS_SERVICE);

        if (networkStatsManager == null) {
            Log.e(TAG, "NetworkStatsManager is not available.");
            return;
        }

        try {
            StringBuilder statsBuilder = new StringBuilder();
            statsBuilder.append("Fetching network usage data...\n");

            long startTime = 0; // Startzeit: Keine Einschränkung
            long endTime = System.currentTimeMillis();

            // **WLAN-Daten für das Gerät abrufen**
            statsBuilder.append("\nWLAN Data (Device):\n");
            long wifiDeviceUsage = getDeviceNetworkUsage(networkStatsManager, ConnectivityManager.TYPE_WIFI, startTime, endTime);
            statsBuilder.append("Downloaded + Uploaded: ").append(wifiDeviceUsage).append(" Bytes\n");

            // **Mobile Daten für das Gerät abrufen**
            statsBuilder.append("\nMobile Data (Device):\n");
            long mobileDeviceUsage = getDeviceNetworkUsage(networkStatsManager, ConnectivityManager.TYPE_MOBILE, startTime, endTime);
            statsBuilder.append("Downloaded + Uploaded: ").append(mobileDeviceUsage).append(" Bytes\n");

            // **Daten für einzelne Apps abrufen**
            statsBuilder.append("\nWLAN Data (Apps):\n");
            getAppNetworkUsage(networkStatsManager, ConnectivityManager.TYPE_WIFI, startTime, endTime, statsBuilder);

            statsBuilder.append("\nMobile Data (Apps):\n");
            getAppNetworkUsage(networkStatsManager, ConnectivityManager.TYPE_MOBILE, startTime, endTime, statsBuilder);

            // Ergebnis anzeigen
            networkInfoTextView.setText(statsBuilder.toString());
            Log.d(TAG, "Network usage data:\n" + statsBuilder.toString());

        } catch (Exception e) {
            Log.e(TAG, "Error fetching network statistics: " + e.getMessage(), e);
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
    private void getAppNetworkUsage(NetworkStatsManager networkStatsManager, int networkType, long startTime, long endTime, StringBuilder statsBuilder) {
        try {
            NetworkStats networkStats = networkStatsManager.querySummary(networkType, null, startTime, endTime);
            NetworkStats.Bucket bucket = new NetworkStats.Bucket();

            while (networkStats.hasNextBucket()) {
                networkStats.getNextBucket(bucket);
                int uid = bucket.getUid();
                long rxBytes = bucket.getRxBytes();
                long txBytes = bucket.getTxBytes();

                if (rxBytes > 0 || txBytes > 0) {
                    statsBuilder.append("UID: ").append(uid)
                            .append(", Downloaded: ").append(rxBytes)
                            .append(" Bytes, Uploaded: ").append(txBytes)
                            .append(" Bytes\n");
                    Log.d(TAG, "App UID: " + uid + ", RxBytes: " + rxBytes + ", TxBytes: " + txBytes);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching app network usage: " + e.getMessage());
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