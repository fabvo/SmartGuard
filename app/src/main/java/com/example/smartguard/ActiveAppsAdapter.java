package com.example.smartguard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.ArrayAdapter;

import java.util.List;

public class ActiveAppsAdapter extends ArrayAdapter<AppInfo> {

    public ActiveAppsAdapter(@NonNull Context context, @NonNull List<AppInfo> apps) {
        super(context, 0, apps);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.app_list_item, parent, false);
        }

        AppInfo app = getItem(position);

        ImageView appIcon = convertView.findViewById(R.id.appIcon);
        TextView appName = convertView.findViewById(R.id.appName);

        appIcon.setImageResource(app.getIconResource());
        appName.setText(app.getAppName());

        return convertView;
    }
}