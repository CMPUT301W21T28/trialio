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
import com.example.trialio.controllers.UserManager;
import com.example.trialio.models.Experiment;
import com.example.trialio.models.User;
import com.example.trialio.utils.ExperimentTypeUtility;

import java.io.Serializable;

public class EditProfileFragment extends DialogFragment {
    private EditText username;
    private EditText phoneNumber;
    private EditText email;
    private OnFragmentInteractionListener listener;
    private User user;
    private Integer integer;
    private Bundle bundle;
    public interface OnFragmentInteractionListener {
        void onConfirm();
    }



    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.edit_profile_fragment_layout, null);

        // get the experiment that was passed in
        Bundle bundle = getArguments();
        user = (User) bundle.getSerializable("UserProfile");

        username = view.findViewById(R.id.userNameText);
        phoneNumber = view.findViewById(R.id.editUserPhone);
        email = view.findViewById(R.id.editUserEmail);

        UserManager manager = new UserManager();
        manager.addCurrentUserUpdateListener(new UserManager.OnUserFetchListener() {
            @Override
            public void onUserFetch(User user) {
                username.setText(user.getUsername());
                phoneNumber.setText(user.getContactInfo().getPhone());
                email.setText(user.getContactInfo().getEmail());
            }
        });

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
