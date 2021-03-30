package com.example.trialio.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.trialio.R;
import com.example.trialio.controllers.UserManager;
import com.example.trialio.models.CountTrial;
import com.example.trialio.models.Location;
import com.example.trialio.models.Trial;
import com.example.trialio.models.User;

import java.util.Date;
/**
 * This fragment collects data from a user to upload a count type trial
 * it sends data back to the Experiment activity, which then uploads the trial to the firestore database
 */
public class CountTrialFragment extends DialogFragment {
    private OnFragmentInteractionListener listener;
    private boolean geoLocationReq;

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_add_count_trial, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Bundle bundle = getArguments();
        geoLocationReq = (Boolean) bundle.getBoolean("GeoLocationRequired");

        Switch s = view.findViewById(R.id.switchSuccessIndicator);
        return builder
                .setView(view)
                .setTitle("Add Count Trial:")
                .setNegativeButton("Cancel",null)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        Location location = new Location();
                        if (geoLocationReq) {
                            location.getCurrentLocation(getContext(),getActivity());
                        }
                        Date date = new Date();

                        UserManager userManager = new UserManager();
                        userManager.getCurrentUser(new UserManager.OnUserFetchListener() {
                            @Override
                            public void onUserFetch(User user) {
                                listener.onOkPressed(new CountTrial(user.getId(), location, date));
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
