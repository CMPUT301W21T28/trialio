package com.example.trialio.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trialio.R;
import com.example.trialio.adapters.QuestionAdapter;
import com.example.trialio.controllers.QuestionForumManager;
import com.example.trialio.controllers.UserManager;
import com.example.trialio.fragments.AddQuestionFragment;
import com.example.trialio.models.Experiment;
import com.example.trialio.models.Question;
import com.example.trialio.models.User;
import com.example.trialio.utils.HomeButtonUtility;

import java.util.ArrayList;
import java.util.List;

/**
 * This activity displays a list of questions about a given experiment/
 * <p>
 * This activity navigates to:
 * <ul>
 *     <li>QuestionRepliesActivity</li>
 * </ul>
 */
public class QuestionForumActivity extends AppCompatActivity implements AddQuestionFragment.OnFragmentInteractionListener {

    private final String TAG = "QuestionForumActivity";

    private String associatedExperimentID;
    private Experiment experiment;
    private QuestionForumManager questionForumManager;
    private ArrayList<Question> questionList;
    private QuestionAdapter questionAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_forum_activity);

        // receive experiment info from main -> the question forum belongs to this experiment
        Bundle bundle = getIntent().getExtras();
        experiment = (Experiment) bundle.getSerializable("experiment");
        // get id here and pass it into the constructor of the quesitonForumManager
        associatedExperimentID = experiment.getExperimentID();

        // Initialize attributes for the activity
        questionForumManager = new QuestionForumManager(associatedExperimentID);
        questionList = new ArrayList<>();
        questionAdapter = new QuestionAdapter(this, questionList);

        // Set up the adapter for the ListView
        ListView questionsListView = findViewById(R.id.questionForumListView);
        questionsListView.setAdapter(questionAdapter);

        // Set up onClick listeners
        setUpOnClickListeners();

        // set the home button
        HomeButtonUtility.setHomeButtonListener(findViewById(R.id.button_home));
    }

    @Override
    protected void onResume() {
        super.onResume();
        setQuestionList();
        setFields();
    }


    /**
     * Sets the questionList and updates the ArrayAdapter of the activity
     */
    private void setQuestionList() {
        questionForumManager.setOnAllQuestionsFetchCallback(new QuestionForumManager.OnManyQuestionsFetchListener() {
            @Override
            public void onManyQuestionsFetch(List<Question> questions) {  // TODO: why not ArrayList ***
                Log.w(TAG, "Successfully fetched questions");
                questionList.clear();
                questionList.addAll(questions);
                questionAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Sets the fields of the TextViews of the activity
     */
    private void setFields() {
        TextView experimentTypeView = findViewById(R.id.experiment_text_type);
        experimentTypeView.setText(experiment.getTrialManager().getType());

        TextView statusView = findViewById(R.id.experiment_text_status);
        statusView.setText(experiment.getTrialManager().getIsOpen() ? R.string.experiment_status_open : R.string.experiment_status_closed);

        TextView ownerView = findViewById(R.id.experiment_text_owner);
        UserManager userManager = new UserManager();
        userManager.getUserById(experiment.getSettings().getOwnerID(), new UserManager.OnUserFetchListener() {
            @Override
            public void onUserFetch(User user) {
                ownerView.setText(user.getUsername());
            }
        });

    }

    /**
     * Sets up on click listeners for the activity.
     */
    private void setUpOnClickListeners() {

        // Called when the user clicks item in question list -> makes bundle to send to detailed question page
        ListView questionForumListView = findViewById(R.id.questionForumListView);
        questionForumListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), QuestionRepliesActivity.class);

                // PASSING THE OBJECT APPEARS TO BE THE PROBLEM

                // pass in experiment as an argument
                Bundle args = new Bundle();

                Question tempQuestion = questionList.get(i);

                args.putString("experimentID", associatedExperimentID);
                args.putSerializable("question", tempQuestion);

                Log.w("QUESTION ID: ", tempQuestion.getPostID());
                Log.w("QUESTION USERNAME: ", tempQuestion.getUserId());

                intent.putExtras(args);

                startActivity(intent);
            }
        });

        // adds new question
        Button newQuestion = findViewById(R.id.newQuestion);
        newQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Add question was clicked");

                AddQuestionFragment newQuestion = new AddQuestionFragment();
                Bundle args = new Bundle();
                args.putString("experimentID", associatedExperimentID);
                newQuestion.setArguments(args);
                newQuestion.show(getSupportFragmentManager(), "addQuestion");

            }
        });
    }


    /**
     * This is called when the user presses confirm on one of the Question creation fragments
     *
     * @param newQuestion The new question that was created in the fragment
     */
    @Override
    public void onOkPressed(Question newQuestion) {
        Log.d(TAG, "QuestionAdded");
        questionForumManager.createQuestion(newQuestion);
        setQuestionList();
    }


}
