package com.example.trialio.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.trialio.R;
import com.example.trialio.adapters.QuestionArrayAdapter;
import com.example.trialio.adapters.ReplyArrayAdapter;
import com.example.trialio.controllers.ExperimentManager;
import com.example.trialio.controllers.QuestionForumManager;
import com.example.trialio.controllers.UserManager;
import com.example.trialio.fragments.AddQuestionFragment;
import com.example.trialio.fragments.AddReplyFragment;
import com.example.trialio.fragments.BinomialTrialFragment;
import com.example.trialio.fragments.CountTrialFragment;
import com.example.trialio.fragments.MeasurementTrialFragment;
import com.example.trialio.fragments.NonNegativeTrialFragment;
import com.example.trialio.models.Experiment;
import com.example.trialio.models.Question;
import com.example.trialio.models.Reply;
import com.example.trialio.models.Trial;
import com.example.trialio.models.User;
import com.example.trialio.utils.ExperimentTypeUtility;

import java.util.ArrayList;
import java.util.List;



/**
 * This activity opens an question (w/ details) with all of it's replies. The activity is opened when a question is clicked in the list view from QuestionForumActivity.
 */

public class QuestionRepliesActivity extends AppCompatActivity implements AddReplyFragment.OnFragmentInteractionListener{
    private final Context context = this;

    private final String TAG = "QuestionRepliesForumActivity";


    private Question selectedQuestion;
    private ArrayList<Reply> replyList;
    private ReplyArrayAdapter replyAdapter;


    private String associatedExperimentID;
    private String associatedQuestionID;

    // managers
    private QuestionForumManager questionForumManager;
    private UserManager userManager;
    private ExperimentManager experimentManager;

    /**
     * the On create the takes in the saved instance from the question forum activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_forum_detailed);



        Bundle bundle = getIntent().getExtras();

        associatedExperimentID = bundle.getString("experimentID");

        selectedQuestion = (Question) bundle.getSerializable("question_details");
        associatedQuestionID = selectedQuestion.getPostID();

        questionForumManager = new QuestionForumManager(associatedExperimentID);

        replyList = new ArrayList<>();
        replyAdapter = new ReplyArrayAdapter(this, replyList);

        experimentManager = new ExperimentManager();
        userManager = new UserManager();

        // get the important views in this activity
        TextView authorID = findViewById(R.id.selectedQuestionAuthorID);
        TextView selectedQuestionTitle = findViewById(R.id.selectedQuestionTitle);
        TextView selectedQuestionBody = findViewById(R.id.selectedQuestionBody);

        EditText replyBox = findViewById(R.id.regionEditText);
        Button replyButton = findViewById(R.id.replyButton);


        // set views with selectedQuestion details
        authorID.setText(selectedQuestion.getPostID());
        selectedQuestionTitle.setText(selectedQuestion.getTitle());
        selectedQuestionBody.setText(selectedQuestion.getBody());

    }

    @Override
    protected void onStart() {
        super.onStart();

        setReplyList();
    }

    private void setReplyList() {
        questionForumManager.setOnAllRepliesFetchCallback(associatedQuestionID, new QuestionForumManager.OnManyRepliesFetchListener() {
            @Override
            public void onManyRepliesFetch(List<Reply> replies) {  // TODO: why not ArrayList ***
                replyList.clear();
                if (replies.isEmpty()) {
                    //Log.d(TAG, "onManyRepliesFetch: No question exist, initiate an empty array list to avoid crash "); //TODO: this seems hacky
                    replyList = new ArrayList<>();
                } else {
                    //Log.d(TAG, "onManyQuestionsFetch: Succesfully fetched questions");
                    replyList.addAll(replies);
                }
                replyAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Sets up on click listeners for the activity.
     */
    private void setUpOnClickListeners() {

        // Called when the user clicks item in question list -> makes bundle to send to detailed question page
        ListView replyListView = findViewById(R.id.replyListView);

        replyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // what do on click ???
                /*
                a) expand view to see sub replies
                b) delete?
                 */
            }
        });


        /**
         * Adds new reply
         */

        Button postReplyButton = findViewById(R.id.replyButton);

        postReplyButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LongLogTag")   // TODO why does not having this cause an error
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Reply was clicked");

                AddReplyFragment newReply = new AddReplyFragment();
                Bundle args = new Bundle();
                args.putString("experimentID", associatedExperimentID);
                args.putSerializable("associated_question", selectedQuestion);
                newReply.setArguments(args);
                newReply.show(getSupportFragmentManager(), "addReply");

            }
        });
    }

    /**
     * This is called when the user presses confirm on one of the Question creation fragments
     *
     * @param newReply The new reply that was created in the fragment
     */

    @Override
    public void onOkPressed (Reply newReply) {
        // Log.d(TAG, "Reply");
        questionForumManager.createReply(selectedQuestion.getPostID(), newReply);
        setReplyList();
    }



}

