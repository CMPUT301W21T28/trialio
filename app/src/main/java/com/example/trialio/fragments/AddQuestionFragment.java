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

public class AddQuestionFragment extends DialogFragment {
    private EditText questionTitle;
    private EditText questionBody;
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
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_add_question, null);
        questionTitle = view.findViewById(R.id.editQuestionTitle);
        questionBody = view.findViewById(R.id.editQuestionBody);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("New Question")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String QuestionTitle = questionTitle.getText().toString();
                        String QuestionBody = questionBody.getText().toString();
                        //confirm update to user profile
                    }}).create();
    }

}
