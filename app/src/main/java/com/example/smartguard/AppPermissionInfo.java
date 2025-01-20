package com.example.smartguard;

import android.graphics.drawable.Drawable;

public class AppPermissionInfo {
    private final String name;
    private final Drawable icon;
    private final String permissions;

    public AppPermissionInfo(String name, Drawable icon, String permissions) {
        this.name = name;
        this.icon = icon;
        this.permissions = permissions;
    }

    public String getName() {
        return name;
    }

    public Drawable getIcon() {
        return icon;
    }

    public String getPermissions() {
        return permissions;
    }
}