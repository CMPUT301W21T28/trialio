package com.example.trialio.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.trialio.R;
import com.example.trialio.activities.StatActivity;
import com.example.trialio.controllers.CreateMeasurementTrialCommand;
import com.example.trialio.models.Experiment;
import com.example.trialio.models.Trial;

/**
 * This fragment collects data from a user to upload a measurement type trial
 * it sends data back to the Experiment activity, which then uploads the trial to the firestore database
 */
public class MeasurementTrialFragment extends DialogFragment {
    private OnFragmentInteractionListener listener;
    private boolean geoLocationReq;
    private Experiment experiment;

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_add_measurement_trial, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Bundle bundle = getArguments();
        geoLocationReq = (Boolean) bundle.getBoolean("GeoLocationRequired");
        experiment = (Experiment) bundle.getSerializable("experiment");

        // determine whether or not unit exists, and should be displayed
        String unit = experiment.getUnit();
        if (unit == null || "".equals(unit)) {
            unit = "";
        } else {
            unit = "(" + unit + ")";
        }

        return builder
                .setView(view)
                .setTitle("Add Measurement Trial: " + unit)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        TextView tv = view.findViewById(R.id.edit_measurement);
                        try {
                            Double measurement = Double.parseDouble(tv.getText().toString());
                            CreateMeasurementTrialCommand command = new CreateMeasurementTrialCommand(
                                    getContext(),
                                    geoLocationReq,
                                    measurement,
                                    experiment.getUnit(),
                                    trial -> listener.onOkPressed(trial));
                            command.execute();
                        } catch(Exception e) {
                            Toast.makeText(getActivity(), "Measurement must be less than " + Double.MAX_VALUE, Toast.LENGTH_LONG).show();
                        }
                    }
                }).create();
    }

    public interface OnFragmentInteractionListener {
        void onOkPressed(Trial newTrial);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
}
