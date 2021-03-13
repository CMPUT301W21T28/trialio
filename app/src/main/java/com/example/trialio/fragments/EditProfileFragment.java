package com.example.trialio.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.trialio.R;

public class EditProfileFragment extends DialogFragment {
    private EditText username;
    private EditText phoneNumber;
    private EditText email;
    private OnFragmentInteractionListener listener;

    public interface OnFragmentInteractionListener {
        //void onConfirm(); change user info
    }

    //if some user info exists, send it to the dialog box as a default value

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

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.edit_profile_fragment_layout, null);
        username = view.findViewById(R.id.userNameText);
        phoneNumber = view.findViewById(R.id.editUserPhone);
        email = view.findViewById(R.id.editUserEmail);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Edit User Information")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String UserName = username.getText().toString();
                        String UserPhone = phoneNumber.getText().toString();
                        String UserEMail = email.getText().toString();
                        //confirm update to user profile
                    }}).create();
    }

}
