package com.example.smartguard;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.ArrayAdapter;

import java.util.List;

public class PermissionListAdapter extends ArrayAdapter<AppPermissionInfo> {

    public PermissionListAdapter(@NonNull Context context, @NonNull List<AppPermissionInfo> appPermissions) {
        super(context, 0, appPermissions);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.permission_list_item, parent, false);
        }

        AppPermissionInfo appPermissionInfo = getItem(position);

        ImageView appIcon = convertView.findViewById(R.id.appIcon);
        TextView appName = convertView.findViewById(R.id.appName);
        TextView appPermissions = convertView.findViewById(R.id.appPermissions);

        appIcon.setImageDrawable(appPermissionInfo.getAppIcon());
        appName.setText(appPermissionInfo.getAppName());
        appPermissions.setText(appPermissionInfo.getPermissions());

        return convertView;
    }
}