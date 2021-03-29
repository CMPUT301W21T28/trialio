package com.example.trialio.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
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

    private OnFragmentInteractionListener listener;
    private EditText phoneNumberView;
    private EditText emailView;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_edit_contact_info, null);

        // get the user that was passed in
        Bundle bundle = getArguments();
        User user = (User) bundle.getSerializable("UserProfile");

        // set the text views with current user values
        phoneNumberView = view.findViewById(R.id.editUserPhone);
        emailView = view.findViewById(R.id.editUserEmail);
        phoneNumberView.setText(user.getContactInfo().getPhone());
        emailView.setText(user.getContactInfo().getEmail());

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Edit Contact Information")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String phone = phoneNumberView.getText().toString();
                        String email = emailView.getText().toString();
                        listener.onEditContactInfoConfirm(phone, email);
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
        void onEditContactInfoConfirm(String phone, String email);
    }

}
