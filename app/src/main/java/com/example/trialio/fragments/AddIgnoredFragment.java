package com.example.trialio.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.trialio.R;
import com.example.trialio.controllers.UserManager;
import com.example.trialio.models.User;

public class AddIgnoredFragment extends DialogFragment {
    private OnFragmentInteractionListener listener;
    public Context context;

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_add_ignored, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // get the context of the fragment
        context = this.getContext();

        return builder
                .setView(view)
                .setTitle("Add Ignored User:")
                .setNegativeButton("Cancel",null)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        // get the typed username
                        EditText tv = (EditText) view.findViewById(R.id.edit_add_ignored);
                        String username = tv.getText().toString();

                        // add the userID associated with the username
                        UserManager userManager = new UserManager();
                        userManager.getUserByUsername(username, new UserManager.OnUserFetchListener() {
                            @Override
                            public void onUserFetch(User user) {
                                // if we found a user with that username, ignore the associated ID
                                if (user != null) {
                                    listener.onOkPressed(user.getId());
                                } else {  // if we did not find a user with that username, tell the user
                                    String nullMessage = "There is no user with the username " + username + ".";
                                    Toast.makeText(context, nullMessage, Toast.LENGTH_LONG).show();
                                }
                            }
                        });
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
