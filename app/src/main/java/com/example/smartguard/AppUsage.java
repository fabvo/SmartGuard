package com.example.smartguard;

import android.graphics.drawable.Drawable;

public class AppUsage {
    private String name;
    private Drawable icon;
    private long rxBytes;
    private long txBytes;

    public AppUsage(String name, Drawable icon, long rxBytes, long txBytes) {
        this.name = name;
        this.icon = icon;
        this.rxBytes = rxBytes;
        this.txBytes = txBytes;
    }

    public String getName() {
        return name;
    }

    public Drawable getIcon() {
        return icon;
    }

    public long getRxBytes() {
        return rxBytes;
    }

    public long getTxBytes() {
        return txBytes;
    }

    public long getTotalBytes() {
        return rxBytes + txBytes;
    }

    public String getFormattedDataUsage() {
        return formatDataSize(getTotalBytes());
    }

    private String formatDataSize(long bytes) {
        if (bytes >= 1_073_741_824) {
            return String.format("%.2f GB", bytes / 1_073_741_824.0);
        } else if (bytes >= 1_048_576) {
            return String.format("%.2f MB", bytes / 1_048_576.0);
        } else if (bytes >= 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else {
            return bytes + " Bytes";
        }
    }
}