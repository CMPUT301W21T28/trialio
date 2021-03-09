package com.example.trialio.ui.main;

import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

public class EditProfileFragment extends DialogFragment {
    private EditText username;
    private EditText phoneNumber;
    private EditText email;
    private OnFragmentInteractionListener listener;

    public interface OnFragmentInteractionListener {
        //void onConfirm(); change user info
    }

    //send existing user info to fragment and set as default

    //oncreate dialog to edit user profile information

}
