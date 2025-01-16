package com.example.smartguard;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class AppListAdapter extends ArrayAdapter<HomeFragment.AppInfo> {

    private Context context;
    private List<HomeFragment.AppInfo> appList;

    public AppListAdapter(@NonNull Context context, List<HomeFragment.AppInfo> appList) {
        super(context, R.layout.app_list_item, appList);
        this.context = context;
        this.appList = appList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.app_list_item, parent, false);
        }

        HomeFragment.AppInfo appInfo = appList.get(position);

        ImageView appIcon = convertView.findViewById(R.id.appIcon);
        TextView appName = convertView.findViewById(R.id.appName);

        appIcon.setImageDrawable(appInfo.icon);
        appName.setText(appInfo.name);

        return convertView;
    }
}
