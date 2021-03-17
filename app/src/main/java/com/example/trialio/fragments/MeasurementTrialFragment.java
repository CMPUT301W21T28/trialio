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
import com.example.trialio.controllers.UserManager;
import com.example.trialio.models.CountTrial;
import com.example.trialio.models.Location;
import com.example.trialio.models.MeasurementTrial;
import com.example.trialio.models.Trial;
import com.example.trialio.models.User;

import java.util.Date;

public class MeasurementTrialFragment extends DialogFragment {
    private OnFragmentInteractionListener listener;

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_add_measurement_trial, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        Switch s = view.findViewById(R.id.switchSuccessIndicator);
        return builder
                .setView(view)
                .setTitle("Add Measurement Trial:")
                .setNegativeButton("Cancel",null)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        TextView tv = view.findViewById(R.id.edit_measurement);
                        Double measurement = Double.parseDouble(tv.getText().toString());

                        Location location = new Location();
                        location.getCurrentLocation(getContext(),getActivity());
                        Date date = new Date();
                        String unit = "UNIT";  // TODO: change this

                        UserManager userManager = new UserManager();
                        userManager.getCurrentUser(new UserManager.OnUserFetchListener() {
                            @Override
                            public void onUserFetch(User user) {
                                listener.onOkPressed(new MeasurementTrial(user.getId(), location, date, measurement , unit));
                            }
                        });
                    }}).create();
    }

    public interface OnFragmentInteractionListener {
        void onOkPressed(Trial newTrial);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnFragmentInteractionListener){
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
}
