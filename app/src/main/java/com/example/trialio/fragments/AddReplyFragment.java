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
import com.example.trialio.controllers.CurrentUserHandler;
import com.example.trialio.controllers.QuestionForumManager;
import com.example.trialio.models.Question;
import com.example.trialio.models.Reply;
import com.example.trialio.models.User;

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

        associatedQuestion = (Question) bundle.getSerializable("associated_question");
        associatedExperimentID = bundle.getString("experimentID");


        replyBodyInput = view.findViewById(R.id.editReplyBody);

        return builder
                .setView(view)
                .setTitle("New Reply")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String replyBody = replyBodyInput.getText().toString();
                        QuestionForumManager questionForumManager = new QuestionForumManager(associatedExperimentID);

                        String newReplyID = questionForumManager.getNewReplyID(associatedQuestion.getPostID());   // TODO error check this a lot

                        //confirm update to user profile
                        CurrentUserHandler.getInstance().getCurrentUser(new CurrentUserHandler.OnUserFetchCallback() {
                            @Override
                            public void onUserFetch(User user) {
                                listener.onOkPressed(new Reply(newReplyID, replyBody, user.getId()) );
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
