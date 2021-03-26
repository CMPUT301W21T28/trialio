package com.example.trialio.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.trialio.R;

public class AddIgnoredFragment extends DialogFragment {
    private OnFragmentInteractionListener listener;

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_add_ignored, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        return builder
                .setView(view)
                .setTitle("Add Ignored User:")
                .setNegativeButton("Cancel",null)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        EditText tv = (EditText) view.findViewById(R.id.edit_add_ignored);

                        listener.onOkPressed(tv.getText().toString());
                    }}).create();
    }

    public interface OnFragmentInteractionListener {
        void onOkPressed(String userID);
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
