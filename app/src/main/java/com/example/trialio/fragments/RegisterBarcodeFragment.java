package com.example.trialio.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Switch;

import com.example.trialio.R;
import com.example.trialio.controllers.UserManager;
import com.example.trialio.models.BinomialTrial;
import com.example.trialio.models.Location;
import com.example.trialio.models.User;

import java.util.Date;


public class RegisterBarcodeFragment extends DialogFragment {
    private BinomialTrialFragment.OnFragmentInteractionListener listener;
    private Spinner spinnerselectType;
    private String selectedType = "";



    // Adapted from class/division code.
    // DATE:	2021-03-18
    // LICENSE:	CC BY 4.0 [https://creativecommons.org/licenses/by/4.0/]
    // SOURCE:  Working with Spinners in Android [https://www.studytonight.com/android/spinner-example-in-android#]
    // AUTHOR: 	Studytonight tutorial developers
    public void selectType(View v){
        spinnerselectType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                selectedType = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }




    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_register_barcode, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(view);
        builder.setTitle("Register Barcode:");
        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {

            }
        });
        return builder.create();

    }
}