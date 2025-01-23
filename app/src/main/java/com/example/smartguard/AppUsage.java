package com.example.smartguard;

import android.graphics.drawable.Drawable;

public class AppUsage {
    private final String name;
    private final Drawable icon;
    private long rxBytes;
    private long txBytes;
    private long foregroundRxBytes;
    private long foregroundTxBytes;
    private long backgroundRxBytes;
    private long backgroundTxBytes;

    public AppUsage(String name, Drawable icon, long rxBytes, long txBytes,
                    long foregroundRxBytes, long foregroundTxBytes,
                    long backgroundRxBytes, long backgroundTxBytes) {
        this.name = name;
        this.icon = icon;
        this.rxBytes = rxBytes;
        this.txBytes = txBytes;
        this.foregroundRxBytes = foregroundRxBytes;
        this.foregroundTxBytes = foregroundTxBytes;
        this.backgroundRxBytes = backgroundRxBytes;
        this.backgroundTxBytes = backgroundTxBytes;
    }

    public void addUsage(long rxBytes, long txBytes, long foregroundRxBytes, long foregroundTxBytes,
                         long backgroundRxBytes, long backgroundTxBytes) {
        this.rxBytes += rxBytes;
        this.txBytes += txBytes;
        this.foregroundRxBytes += foregroundRxBytes;
        this.foregroundTxBytes += foregroundTxBytes;
        this.backgroundRxBytes += backgroundRxBytes;
        this.backgroundTxBytes += backgroundTxBytes;
    }

    public String getDetailedUsage() {
        return "Foreground:\n"
                + "  Download: " + formatDataSize(foregroundRxBytes) + "\n"
                + "  Upload: " + formatDataSize(foregroundTxBytes) + "\n"
                + "Background:\n"
                + "  Download: " + formatDataSize(backgroundRxBytes) + "\n"
                + "  Upload: " + formatDataSize(backgroundTxBytes) + "\n";
    }

    public String getName() {
        return name;
    }

    public Drawable getIcon() {
        return icon;
    }

    public String getFormattedDataUsage() {
        return "Total: " + formatDataSize(rxBytes + txBytes);
    }

    // Add getter methods for the missing fields
    public long getRxBytes() {
        return rxBytes;
    }

    public long getTxBytes() {
        return txBytes;
    }

    public long getForegroundRxBytes() {
        return foregroundRxBytes;
    }

    public long getForegroundTxBytes() {
        return foregroundTxBytes;
    }

    public long getBackgroundRxBytes() {
        return backgroundRxBytes;
    }

    public long getBackgroundTxBytes() {
        return backgroundTxBytes;
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