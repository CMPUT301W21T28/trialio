package com.example.trialio.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.trialio.R;
import com.example.trialio.controllers.UserManager;
import com.example.trialio.models.User;

/**
 * This fragment allows a user to edit their profile information, including username, phone number and e-mail
 * it sends data back to the User manager, which then updates the user's information in the firebase database
 */
public class EditContactInfoFragment extends DialogFragment {
    private EditText phoneNumberView;
    private EditText emailView;
    private OnFragmentInteractionListener listener;
    private User user;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_edit_contact_info, null);

        // get the experiment that was passed in
        Bundle bundle = getArguments();
        user = (User) bundle.getSerializable("UserProfile");

        phoneNumberView = view.findViewById(R.id.editUserPhone);
        emailView = view.findViewById(R.id.editUserEmail);

        UserManager manager = new UserManager();
        manager.getCurrentUser(new UserManager.OnUserFetchListener() {
            @Override
            public void onUserFetch(User user) {
                manager.addUserUpdateListener(user.getUsername(), new UserManager.OnUserFetchListener() {
                    @Override
                    public void onUserFetch(User user) {
                        phoneNumberView.setText(user.getContactInfo().getPhone());
                        emailView.setText(user.getContactInfo().getEmail());
                    }
                });
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Edit Contact Information")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String phoneText = phoneNumberView.getText().toString();
                        String emailText = emailView.getText().toString();

                        user.getContactInfo().setPhone(phoneText);
                        user.getContactInfo().setEmail(emailText);

                        manager.updateUser(user);
                        //confirm update to user profile
                    }
                }).create();
    }

    public interface OnFragmentInteractionListener {
        void onConfirm();
    }

}
