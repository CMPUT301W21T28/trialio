package com.example.trialio.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trialio.R;
import com.example.trialio.adapters.QuestionArrayAdapter;
import com.example.trialio.adapters.ReplyArrayAdapter;
import com.example.trialio.controllers.QuestionForumManager;
import com.example.trialio.fragments.AddQuestionFragment;
import com.example.trialio.models.Experiment;
import com.example.trialio.models.Question;

import java.util.ArrayList;
import java.util.List;

public class QuestionForumActivity extends AppCompatActivity implements AddQuestionFragment.OnFragmentInteractionListener {
    private final Context context = this;

    private Experiment associatedExperiment;

    private QuestionForumManager questionForumManager;
    private ArrayList<Question> questionList;
    private QuestionArrayAdapter questionAdapter;

    String associatedExperimentID;

    private final String TAG = "QuestionForumActivity";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_forum_activity);

        // receive experiment info from main -> the question forum belongs to this experiment
        Bundle bundle = getIntent().getExtras();
        associatedExperiment = (Experiment) bundle.getSerializable("experiment");
        // get id here and pass it into the constructor of the quesitonForumManager
        associatedExperimentID = associatedExperiment.getExperimentID();


        // Initialize attributes for the activity
        questionForumManager = new QuestionForumManager(associatedExperimentID);
        questionList = new ArrayList<>();
        questionAdapter = new QuestionArrayAdapter(this, questionList);

        // Set up the adapter for the ListView
        ListView questionsListView = findViewById(R.id.questionForumListView);
        questionsListView.setAdapter(questionAdapter);

        // Set up onClick listeners
        setUpOnClickListeners();

    }

    @Override
    protected void onStart() {
        super.onStart();
        setQuestionList();
    }


    private void setQuestionList() {
        questionForumManager.setOnAllQuestionsFetchCallback(new QuestionForumManager.OnManyQuestionsFetchListener() {
            @Override
            public void onManyQuestionsFetch(List<Question> questions) {  // TODO: why not ArrayList ***
                questionList.clear();
                if (questions.isEmpty()) {
                    Log.d(TAG, "onManyQuestionsFetch: No question exist, initiate an empty array list to avoid crash "); //TODO: this seems hacky
                    questionList = new ArrayList<>();
                    questionAdapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "onManyQuestionsFetch: Succesfully fetched questions");
                    questionList.addAll(questions);
                    questionAdapter.notifyDataSetChanged();
                }
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
                Intent intent = new Intent(context, QuestionRepliesActivity.class);

                // pass in experiment as an argument
                Bundle args = new Bundle();
                args.putString("experimentID", associatedExperimentID);
                args.putSerializable("question_details", questionList.get(i));
                intent.putExtras(args);

                // start an ExperimentActivity
                startActivity(intent);
            }
        });

        /**
         * Adds new question
         */
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
    public void onOkPressed (Question newQuestion) {
        Log.d(TAG, "QuestionAdded");
        questionForumManager.createQuestion(newQuestion);
        setQuestionList();
    }



}
