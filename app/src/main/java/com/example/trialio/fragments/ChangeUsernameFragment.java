package com.example.trialio.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.trialio.R;

/**
 * Collects a new username string input from the user.
 */
public class ChangeUsernameFragment extends DialogFragment {

    private OnFragmentInteractionListener listener;
    private EditText usernameTextView;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_change_username, null);

        usernameTextView = view.findViewById(R.id.editUsername);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Change Username")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String newUsername = usernameTextView.getText().toString();
                        listener.onChangeUsernameConfirm(newUsername);
                    }
                }).create();
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

    /**
     * Interface for action to be performed by called when OK is clicked
     */
    public interface OnFragmentInteractionListener {
        void onChangeUsernameConfirm(String newUsername);
    }
}
