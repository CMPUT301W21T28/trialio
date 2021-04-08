package com.example.trialio.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trialio.R;
import com.example.trialio.adapters.ReplyAdapter;
import com.example.trialio.controllers.CurrentUserHandler;
import com.example.trialio.controllers.ExperimentManager;
import com.example.trialio.controllers.QuestionForumManager;
import com.example.trialio.controllers.UserManager;
import com.example.trialio.controllers.ViewUserProfileCommand;
import com.example.trialio.fragments.AddReplyFragment;
import com.example.trialio.models.Experiment;
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

    private Boolean isUserOwner = false;
    private Experiment experiment;


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
        experiment = (Experiment) bundle.getSerializable("experiment");
        associatedExperimentID = experiment.getExperimentID();
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

        // initialize the state of the activity
        initState();
    }

    /**
     * Initialize the state for the Activity
     */
    private void initState() {
        CurrentUserHandler.getInstance().getCurrentUser(new CurrentUserHandler.OnUserFetchCallback() {
            @Override
            public void onUserFetch(User user) {
                // determine if user is the owner
                isUserOwner = user.getId().equals(experiment.getSettings().getOwnerID());
                setUpOnClickListeners();
            }
        });
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

        // set up the listener to view the profile of the user who posted a question in the list view
        replyListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                // get the userID
                String userID = replyList.get(i).getUserId();

                // set the menu layout
                int popupViewID = R.layout.menu_view_profile;
                if (isUserOwner) {
                    popupViewID = R.layout.menu_replies_owner;
                }

                // create the popup menu
                PopupMenu popup = new PopupMenu(getApplicationContext(), view);
                popup.inflate(popupViewID);

                // listener for menu
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.item_view_profile) {
                            Log.d(TAG, "View profile: " + userID);

                            // create and execute a ViewUserProfileCommand
                            ViewUserProfileCommand command = new ViewUserProfileCommand(context, userID);
                            command.execute();
                        } else if (menuItem.getItemId() == R.id.item_delete_reply) {
                            Log.d(TAG, "Delete reply: " + replyList.get(i).getPostID() + " from " + associatedQuestionID + " from " + associatedExperimentID);

                            // delete reply
                            questionForumManager.deleteReply(associatedQuestionID, replyList.get(i).getPostID());
                            setReplyList();
                        } else {
                            Log.d(TAG, "onMenuItemClick: Invalid item.");
                        }
                        return false;
                    }
                });
                popup.show();

                // return true so that the regular on click does not occur
                return true;
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

        // sets a listener to view the author profile when their username is clicked
        TextView authorID = findViewById(R.id.selectedQuestionAuthorID);
        authorID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // create and execute a ViewUserProfileCommand
                ViewUserProfileCommand command = new ViewUserProfileCommand(context, selectedQuestion.getUserId());
                command.execute();
            }
        });

        // set the home button
        HomeButtonUtility.setHomeButtonListener(findViewById(R.id.button_home));
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

