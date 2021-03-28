package com.example.trialio.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.trialio.R;
import com.example.trialio.controllers.QuestionForumManager;
import com.example.trialio.controllers.UserManager;
import com.example.trialio.models.NonNegativeTrial;
import com.example.trialio.models.Question;
import com.example.trialio.models.Reply;
import com.example.trialio.models.Trial;
import com.example.trialio.models.User;

import java.util.ArrayList;
import java.util.List;

public class AddQuestionFragment extends DialogFragment {
    private EditText questionTitleInput;
    private EditText questionBodyInput;
    private OnFragmentInteractionListener listener;

    String associatedExperimentID;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_add_question, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        // Get owner experiment ID from QuestionForumActivity
        Bundle bundle = getArguments();
        associatedExperimentID = bundle.getString("experimentID");

        questionTitleInput = view.findViewById(R.id.editQuestionTitle);
        questionBodyInput = view.findViewById(R.id.editQuestionBody);


        return builder
            .setView(view)
            .setTitle("New Question")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String questionTitle = questionTitleInput.getText().toString();
                String questionBody = questionBodyInput.getText().toString();

                QuestionForumManager questionForumManager = new QuestionForumManager(associatedExperimentID);

                String newQuestionID = questionForumManager.getNewPostID();

                //confirm update to user profile
                UserManager userManager = new UserManager();
                userManager.getCurrentUser(new UserManager.OnUserFetchListener() {
                    @Override
                    public void onUserFetch(User user) {
                        listener.onOkPressed(new Question(newQuestionID, questionBody, user.getId(), questionTitle) );
                    }
                });
            }}).create();
    }

    public interface OnFragmentInteractionListener {
        void onOkPressed(Question newQuestion);
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
