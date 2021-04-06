package com.example.trialio.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trialio.R;
import com.example.trialio.adapters.ReplyAdapter;
import com.example.trialio.controllers.ExperimentManager;
import com.example.trialio.controllers.QuestionForumManager;
import com.example.trialio.controllers.UserManager;
import com.example.trialio.fragments.AddReplyFragment;
import com.example.trialio.models.Question;
import com.example.trialio.models.Reply;
import com.example.trialio.models.User;
import com.example.trialio.utils.HomeButtonUtility;

import java.util.ArrayList;
import java.util.List;


/**
 * This activity opens an question (w/ details) with all of it's replies. The activity is opened
 * when a question is clicked in the list view from QuestionForumActivity.
 * <p>
 * This activity navigates to no other activities
 */
public class QuestionRepliesActivity extends AppCompatActivity implements AddReplyFragment.OnFragmentInteractionListener {
    private final Context context = this;

    private final String TAG = "QuestionRepliesForumActivity";


    private ArrayList<Reply> replyList;
    private ReplyAdapter replyAdapter;


    private String associatedExperimentID;
    private String associatedQuestionID;

    private Question selectedQuestion;

    // managers
    private QuestionForumManager questionForumManager;

    private UserManager userManager;
    private ExperimentManager experimentManager;

    /**
     * the On create the takes in the saved instance from the question forum activity
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_forum_detailed);

        Bundle bundle = getIntent().getExtras();

        selectedQuestion = (Question) bundle.getSerializable("question");
        //TODO: why did this work, and how can it cause additional issues in the future ***TEST ME***
        associatedExperimentID = bundle.getString("experimentID");
        associatedQuestionID = selectedQuestion.getPostID();


        questionForumManager = new QuestionForumManager(associatedExperimentID);
        replyList = new ArrayList<>();
        replyAdapter = new ReplyAdapter(this, replyList);

        // TODO: do we need this ???
        experimentManager = new ExperimentManager();
        userManager = new UserManager();

        // get the important views in this activity
        TextView authorID = findViewById(R.id.selectedQuestionAuthorID);
        TextView selectedQuestionTitle = findViewById(R.id.selectedQuestionTitle);
        TextView selectedQuestionBody = findViewById(R.id.selectedQuestionBody);

        // set views with selectedQuestion details
        userManager.getUserById(selectedQuestion.getUserId(), new UserManager.OnUserFetchListener() {
            @Override
            public void onUserFetch(User user) {
                authorID.setText(user.getUsername());
            }
        });
        selectedQuestionTitle.setText(selectedQuestion.getTitle());
        selectedQuestionBody.setText(selectedQuestion.getBody());

        // Set up the adapter for the ListView
        ListView questionsListView = findViewById(R.id.replyListView);
        questionsListView.setAdapter(replyAdapter);

        setUpOnClickListeners();

        // set the home button
        HomeButtonUtility.setHomeButtonListener(findViewById(R.id.button_home));
    }

    @Override
    protected void onStart() {

        super.onStart();

        setReplyList();
    }

    private void setReplyList() {

//        Log.d("Selected Question ID", associatedQuestionID);
//        String associatedQuestionID = selectedQuestion.getPostID();

        questionForumManager.setOnAllRepliesFetchCallback(associatedQuestionID, new QuestionForumManager.OnManyRepliesFetchListener() {
            @Override
            public void onManyRepliesFetch(List<Reply> replies) {  // TODO: why not ArrayList ***
                replyList.clear();
                replyList.addAll(replies);
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
            @Override
            public void onClick(View v) {
                Log.d("REPLY", "Reply was clicked");

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
    public void onOkPressed(Reply newReply) {
        // Log.d(TAG, "Reply");
        questionForumManager.createReply(selectedQuestion.getPostID(), newReply);

        setReplyList();
    }


}

