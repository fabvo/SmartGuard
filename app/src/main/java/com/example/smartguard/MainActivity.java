package com.example.smartguard;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartguard.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Referenzen zu UI-Elementen
        Button networkMonitorButton = findViewById(R.id.btn_network_monitor);
        Button permissionAnalyzerButton = findViewById(R.id.btn_permission_analyzer);
        TextView statusText = findViewById(R.id.log_status);

        // Klick-Listener für Netzwerküberwachung
        networkMonitorButton.setOnClickListener(v ->
                statusText.setText("Netzwerküberwachung gestartet"));

        // Klick-Listener für Berechtigungsanalyse
        permissionAnalyzerButton.setOnClickListener(v ->
                statusText.setText("Berechtigungsanalyse gestartet"));
    }
}
