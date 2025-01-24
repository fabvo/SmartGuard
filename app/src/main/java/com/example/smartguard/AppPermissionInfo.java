package com.example.smartguard;

import android.graphics.drawable.Drawable;

import android.content.pm.PermissionInfo;

public class AppPermissionInfo {
    private final String appName;
    private final Drawable appIcon;
    private final String permissions;

    public AppPermissionInfo(String appName, Drawable appIcon, String permissions) {
        this.appName = appName;
        this.appIcon = appIcon;
        this.permissions = permissions;
    }

    public String getAppName() {
        return appName;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public String getPermissions() {
        return permissions;
    }
}