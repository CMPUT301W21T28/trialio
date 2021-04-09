package com.example.trialio.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.trialio.R;
import com.example.trialio.activities.StatActivity;
import com.example.trialio.controllers.CreateMeasurementTrialCommand;
import com.example.trialio.controllers.CurrentUserHandler;
import com.example.trialio.controllers.UserManager;
import com.example.trialio.models.Location;
import com.example.trialio.models.MeasurementTrial;
import com.example.trialio.models.Trial;
import com.example.trialio.models.User;

import java.util.Date;

/**
 * This fragment allows an experimenter to change the number of data points / sections on the plots
 * The value is not stored, and is reset to the default 12 when experiment is re-loaded
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
                        int sections = Integer.parseInt(tv.getText().toString());
                        StatActivity.numData = sections;
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
