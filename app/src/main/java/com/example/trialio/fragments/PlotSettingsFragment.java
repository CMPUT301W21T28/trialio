package com.example.trialio.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.trialio.R;
import com.example.trialio.activities.StatActivity;
import com.example.trialio.controllers.CreateMeasurementTrialCommand;
import com.example.trialio.controllers.CurrentUserHandler;
import com.example.trialio.controllers.TrialManager;
import com.example.trialio.controllers.UserManager;
import com.example.trialio.models.Location;
import com.example.trialio.models.MeasurementTrial;
import com.example.trialio.models.Trial;
import com.example.trialio.models.User;

import java.util.ArrayList;
import java.util.Date;

/**
 * This fragment allows an experimenter to change the number of data points / sections on the plots
 * The value is not stored, and is reset to the default 12 when app is re-installed
 */
public class PlotSettingsFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_plot_settings, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //Bundle bundle = getArguments();
        //geoLocationReq = (Boolean) bundle.getBoolean("GeoLocationRequired");

        return builder
                .setView(view)
                .setTitle("Number of data points to display:")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        TextView tv = view.findViewById(R.id.number_of_sections);
                        try {
                            int sections = Integer.parseInt(tv.getText().toString());
                            if(sections < 50) {
                                StatActivity.numData = sections;
                                Toast.makeText(getActivity(), "Please refresh page to see changes", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getActivity(), "Number must be less than 50", Toast.LENGTH_LONG).show();
                            }
                        } catch(Exception e) {
                            Toast.makeText(getActivity(), "Number must be less than 50", Toast.LENGTH_LONG).show();
                        }
                    }
                }).create();
    }

    /*
    public interface OnFragmentInteractionListener {
        void onOkPressed(Trial newTrial);
    }


     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
}
