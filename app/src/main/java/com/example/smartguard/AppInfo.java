package com.example.smartguard;

public class AppInfo {
    private final String appName;
    private final String packageName;
    private final int iconResource;

    public AppInfo(String appName, String packageName, int iconResource) {
        this.appName = appName;
        this.packageName = packageName;
        this.iconResource = iconResource;
    }

    public String getAppName() {
        return appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public int getIconResource() {
        return iconResource;
    }
}