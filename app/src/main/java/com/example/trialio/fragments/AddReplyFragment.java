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

public class AddReplyFragment extends DialogFragment {

    private EditText replyBodyInput;
    private OnFragmentInteractionListener listener;

    Question associatedQuestion;
    String associatedExperimentID;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_add_reply, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        // Get owner experiment ID and associated question object from QuestionForumActivity
        Bundle bundle = getArguments();

        associatedExperimentID = bundle.getString("experimentID");
        associatedQuestion = (Question) bundle.getSerializable("associated_question");


        return builder
                .setView(view)
                .setTitle("New Reply")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String replyBody = replyBodyInput.getText().toString();

                        QuestionForumManager questionForumManager = new QuestionForumManager(associatedExperimentID, associatedQuestion.getPostID());

                        String newReplyID = questionForumManager.getNewPostID();

                        //confirm update to user profile
                        UserManager userManager = new UserManager();
                        userManager.getCurrentUser(new UserManager.OnUserFetchListener() {
                            @Override
                            public void onUserFetch(User user) {
                                listener.onOkPressed(new Reply(newReplyID, replyBody, user) );
                            }
                        });
                    }}).create();
    }

    public interface OnFragmentInteractionListener {
        void onOkPressed(Reply newReply);
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
