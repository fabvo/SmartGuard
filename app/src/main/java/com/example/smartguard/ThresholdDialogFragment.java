package com.example.smartguard;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ThresholdDialogFragment extends DialogFragment {

    private final ThresholdDialogListener listener;

    public interface ThresholdDialogListener {
        void onThresholdSet(int threshold);
    }

    public ThresholdDialogFragment(ThresholdDialogListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        NumberPicker numberPicker = new NumberPicker(requireContext());
        numberPicker.setMinValue(100); // Minimum threshold in MB
        numberPicker.setMaxValue(5000); // Maximum threshold in MB
        numberPicker.setValue(500); // Default threshold

        return new AlertDialog.Builder(requireContext())
                .setTitle("Warnschwelle einstellen")
                .setView(numberPicker)
                .setPositiveButton("Speichern", (dialog, which) -> {
                    listener.onThresholdSet(numberPicker.getValue());
                })
                .setNegativeButton("Abbrechen", null)
                .create();
    }
}