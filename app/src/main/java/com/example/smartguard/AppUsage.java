package com.example.smartguard;

import android.graphics.drawable.Drawable;

public class AppUsage {
    private String name;
    private Drawable icon;
    private long rxBytes;
    private long txBytes;
    private long foregroundBytes;
    private long backgroundBytes;

    public AppUsage(String name, Drawable icon, long rxBytes, long txBytes, long foregroundBytes, long backgroundBytes) {
        this.name = name;
        this.icon = icon;
        this.rxBytes = rxBytes;
        this.txBytes = txBytes;
        this.foregroundBytes = foregroundBytes;
        this.backgroundBytes = backgroundBytes;
    }

    public void addUsage(long rxBytes, long txBytes, long foregroundBytes, long backgroundBytes) {
        this.rxBytes += rxBytes;
        this.txBytes += txBytes;
        this.foregroundBytes += foregroundBytes;
        this.backgroundBytes += backgroundBytes;
    }

    public String getDetailedUsage() {
        return "Foreground: " + (foregroundBytes / (1024 * 1024)) + " MB\n"
                + "Background: " + (backgroundBytes / (1024 * 1024)) + " MB\n"
                + "Download: " + (rxBytes / (1024 * 1024)) + " MB\n"
                + "Upload: " + (txBytes / (1024 * 1024)) + " MB";
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

    public long getForegroundBytes() {
        return foregroundBytes;
    }

    public long getBackgroundBytes() {
        return backgroundBytes;
    }

    public String getFormattedDataUsage() {
        return (rxBytes + txBytes) / (1024 * 1024) + " MB";
    }
}