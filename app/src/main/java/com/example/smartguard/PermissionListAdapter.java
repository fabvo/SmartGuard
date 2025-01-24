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
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.permission_list_item, parent, false);
        }

        AppPermissionInfo appPermissionInfo = getItem(position);

        TextView appNameTextView = convertView.findViewById(R.id.appNameTextView);
        ImageView appIconImageView = convertView.findViewById(R.id.appIconImageView);
        TextView permissionsTextView = convertView.findViewById(R.id.permissionsTextView);

        appNameTextView.setText(appPermissionInfo.getAppName());
        appIconImageView.setImageDrawable(appPermissionInfo.getAppIcon());

        // Setze Berechtigungen mit HTML-Formatierung
        permissionsTextView.setText(android.text.Html.fromHtml(appPermissionInfo.getPermissions()));

        return convertView;
    }
}