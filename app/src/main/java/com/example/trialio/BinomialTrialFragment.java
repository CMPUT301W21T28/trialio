package com.example.trialio;

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

import java.util.Date;

public class BinomialTrialFragment extends DialogFragment {
    private OnFragmentInteractionListener listener;

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_add_binomial_trial, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        Switch s = view.findViewById(R.id.switchSuccessIndicator);
        return builder
                .setView(view)
                .setTitle("Add Binomial Trial:")
                .setNegativeButton("Cancel",null)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        com.example.trialio.Location location = new com.example.trialio.Location();
                        Date date = new Date();
                        listener.onOkPressed(new BinomialTrial("experimenterID", location, date, s.isChecked()));
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
